///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2002 Jason Baldridge and Gann Bierner
// 
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////

package opennlp.tools.postag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import opennlp.maxent.Evalable;
import opennlp.maxent.EventCollector;
import opennlp.maxent.EventStream;
import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.PlainTextByLineDataStream;
import opennlp.maxent.TwoPassDataIndexer;
import opennlp.maxent.io.SuffixSensitiveGISModelWriter;
import opennlp.tools.util.BeamSearch;
import opennlp.tools.util.Pair;
import opennlp.tools.util.Sequence;

/**
 * A part-of-speech tagger that uses maximum entropy.  Trys to predict whether
 * words are nouns, verbs, or any of 70 other POS tags depending on their
 * surrounding context.
 *
 * @author      Gann Bierner
 * @version $Revision: 1.9 $, $Date: 2004/08/13 16:59:43 $
 */
public class POSTaggerME implements Evalable, POSTagger {

  /**
   * The maximum entropy model to use to evaluate contexts.
   */
  protected MaxentModel _posModel;

  /**
   * The feature context generator.
   */
  protected POSContextGenerator _contextGen;

  /**
   * Tag dictionary used for restricting words to a fixed set of tags.
   */
  protected POSDictionary dictionary;

  /**
   * Says whether a filter should be used to check whether a tag assignment
   * is to a word outside of a closed class.
   */
  protected boolean _useClosedClassTagsFilter = false;
  
  private static final int DEFAULT_BEAM_SIZE =3;

  /** The size of the beam to be used in determining the best sequence of pos tags.*/
  protected int size;

  private Sequence bestSequence;
  
  /** The search object used for search multiple sequences of tags. */
  protected  BeamSearch beam;

  public POSTaggerME(MaxentModel mod) {
    this(mod, new DefaultPOSContextGenerator());
  }
  
  public POSTaggerME(MaxentModel mod,POSDictionary dict) {
      this(DEFAULT_BEAM_SIZE,mod, new DefaultPOSContextGenerator(),dict);
    }
  
  public POSTaggerME(MaxentModel mod, POSContextGenerator cg) {
    this(DEFAULT_BEAM_SIZE,mod,cg,null);
  }
  
  public POSTaggerME(MaxentModel mod, POSContextGenerator cg, POSDictionary dict) {
      this(DEFAULT_BEAM_SIZE,mod,cg,dict);
    }

  public POSTaggerME(int beamSize, MaxentModel mod, POSContextGenerator cg,POSDictionary dict) {
    size = beamSize;
    _posModel = mod;
    _contextGen = cg;
    beam = new PosBeamSearch(size, cg, mod);
    dictionary = dict;
  }

  public String getNegativeOutcome() {
    return "";
  }
  
  /**
   * Returns the number of different tags predicted by this model.
   * @return the number of different tags predicted by this model.
   */
  public int getNumTags() {
    return _posModel.getNumOutcomes();
  }

  public EventCollector getEventCollector(Reader r) {
    return new POSEventCollector(r, _contextGen);
  }

  public List tag(List sentence) {
    bestSequence = beam.bestSequence(sentence,null);
    return bestSequence.getOutcomes();
  }

  public String[] tag(String[] sentence) {
    List t = tag(Arrays.asList(sentence));
    return ((String[]) t.toArray(new String[t.size()]));
  }

  public void probs(double[] probs) {
    bestSequence.getProbs(probs);
  }

  public double[] probs() {
    return bestSequence.getProbs();
  }

  public String tag(String sentence) {
    ArrayList toks = new ArrayList();
    StringTokenizer st = new StringTokenizer(sentence);
    while (st.hasMoreTokens())
      toks.add(st.nextToken());
    List tags = tag(toks);
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < tags.size(); i++)
      sb.append(toks.get(i) + "/" + tags.get(i) + " ");
    return sb.toString().trim();
  }

  public void localEval(MaxentModel posModel, Reader r, Evalable e, boolean verbose) {

    _posModel = posModel;
    float total = 0, correct = 0, sentences = 0, sentsCorrect = 0;
    BufferedReader br = new BufferedReader(r);
    String line;
    try {
      while ((line = br.readLine()) != null) {
        sentences++;
        Pair p = POSEventCollector.convertAnnotatedString(line);
        List words = (List) p.a;
        List outcomes = (List) p.b;
        List tags = beam.bestSequence(words, null).getOutcomes();

        int c = 0;
        boolean sentOk = true;
        for (Iterator t = tags.iterator(); t.hasNext(); c++) {
          total++;
          String tag = (String) t.next();
          if (tag.equals(outcomes.get(c)))
            correct++;
          else
            sentOk = false;
        }
        if (sentOk)
          sentsCorrect++;
      }
    }
    catch (IOException E) {
      E.printStackTrace();
    }

    System.out.println("Accuracy         : " + correct / total);
    System.out.println("Sentence Accuracy: " + sentsCorrect / sentences);

  }

  private class PosBeamSearch extends BeamSearch {

    public PosBeamSearch(int size, POSContextGenerator cg, MaxentModel model) {
      super(size, cg, model);
    }
    
    public PosBeamSearch(int size, POSContextGenerator cg, MaxentModel model, int cacheSize) {
      super(size, cg, model, cacheSize);
    }

    
    protected boolean validSequence(int i, Object[] inputSequence, String[] outcomesSequence, String outcome) {
      if (dictionary == null) {
        return true;
      }
      else {
        String[] tags = dictionary.getTags(inputSequence[i].toString());
        if (tags == null) {
          return true;
        }
        else {
          return Arrays.asList(tags).contains(outcome);
        }
      }
    }
    
    protected boolean validSequence(int i, List inputSequence, Sequence outcomesSequence, String outcome) {
      if (dictionary == null) {
        return true;
      }
      else {
        String[] tags = dictionary.getTags(inputSequence.get(i).toString());
        if (tags == null) {
          return true;
        }
        else {
          return Arrays.asList(tags).contains(outcome);
        }
      }
    }
  }
  
  public String[] getOrderedTags(List words, List tags, int index) {
    return getOrderedTags(words,tags,index,null);
  }
  
  public String[] getOrderedTags(List words, List tags, int index,double[] tprobs) {
    double[] probs = _posModel.eval(_contextGen.getContext(index,words.toArray(),(String[]) tags.toArray(new String[tags.size()]),null));
    String[] orderedTags = new String[probs.length];
    for (int i = 0; i < probs.length; i++) {
      int max = 0;
      for (int ti = 1; ti < probs.length; ti++) {
        if (probs[ti] > probs[max]) {
          max = ti;
        }
      }
      orderedTags[i] = _posModel.getOutcome(max);
      if (tprobs != null){
        tprobs[i]=probs[max];
      }
      probs[max] = 0;
    }
    return (orderedTags);
  }

  public static GISModel train(EventStream es, int iterations, int cut) throws IOException {
    return opennlp.maxent.GIS.trainModel(iterations, new TwoPassDataIndexer(es, cut));
  }

  /**
     * <p>Trains a new pos model.</p>
     *
     * <p>Usage: java opennlp.postag.POStaggerME data_file new_model_name (iterations cutoff)?</p>
     *
     */
  public static void main(String[] args) throws IOException {
    if (args.length == 0){
      System.err.println("Usage: POSTaggerME training model");
      System.err.println("This trains a new model on the specified training file and writes the trained model to the model file.");
      System.exit(1);
    }
    try {
      File inFile = new File(args[0]);
      File outFile = new File(args[1]);
      GISModel mod;
      
      
      EventStream es = new POSEventStream(new PlainTextByLineDataStream(new FileReader(inFile)));
      if (args.length > 3)
        mod = train(es, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
      else
        mod = train(es, 100, 5);

      System.out.println("Saving the model as: " + args[1]);
      new SuffixSensitiveGISModelWriter(mod, outFile).persist();

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
