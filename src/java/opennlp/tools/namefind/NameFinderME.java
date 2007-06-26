///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2003 Thomas Morton
// 
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.
// 
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
// 
//You should have received a copy of the GNU Lesser General Public
//License along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////
package opennlp.tools.namefind;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import opennlp.maxent.EventStream;
import opennlp.maxent.GIS;
import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.TwoPassDataIndexer;
import opennlp.tools.util.BeamSearch;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.Span;

/**
 * Class for creating a maximum-entropy-based name finder.  
 */
public class NameFinderME implements NameFinder {

  /**
   * Implementation of the abstract beam search to allow the name finder to use
   * the common beam search code.
   */
  private class NameBeamSearch extends BeamSearch {

    /**
     * Creams a beam seach of the specified size sing the specified model with
     * the specified context generator.
     * 
     * @param size
     *          The size of the beam.
     * @param cg
     *          The context generator used with the specified model.
     * @param model
     *          The model used to determine names.
     * @param beamSize
     */
    public NameBeamSearch(int size, NameContextGenerator cg, MaxentModel model,
        int beamSize) {
      super(size, cg, model, beamSize);
    }

    /**
     * This method determines wheter the outcome is valid for the preceeding
     * sequence. This can be used to implement constraints on what sequences are
     * valid.
     * 
     * @param outcome
     *          The outcome.
     * @param sequence
     *          The precceding sequence of outcomes assignments.
     * @return true is the outcome is valid for the sequence, false otherwise.
     */
    protected boolean validSequence(int size, Object[] inputSequence,
        String[] outcomesSequence, String outcome) {
      if (outcome.equals(CONTINUE)) {
        int li = outcomesSequence.length - 1;
        if (li == -1) {
          return false;
        } else if (outcomesSequence[li].equals(OTHER)) {
          return false;
        }
      }
      return true;
    }
  }
  
  public static final String START = "start";
  public static final String CONTINUE = "cont";
  public static final String OTHER = "other";
  
  protected MaxentModel model;
  protected NameContextGenerator contextGenerator;
  private Sequence bestSequence;
  private BeamSearch beam;

  private AdditionalContextFeatureGenerator additionalContextFeatureGenerator =
      new AdditionalContextFeatureGenerator();
  
  /**
   * Creates a new name finder with the specified model.
   * @param mod The model to be used to find names.
   */
  public NameFinderME(MaxentModel mod) {
    this(mod, new NameContextGenerator(10), 10);
  }

  /**
   * Creates a new name finder with the specified model and context generator.
   * @param mod The model to be used to find names.
   * @param cg The context generator to be used with this name finder.
   */
  public NameFinderME(MaxentModel mod, NameContextGenerator cg) {
    this(mod, cg, 10);
  }

  /**
   * Creates a new name finder with the specified model and context generator.
   * @param mod The model to be used to find names.
   * @param cg The context generator to be used with this name finder.
   * @param beamSize The size of the beam to be used in decoding this model.
   */
  public NameFinderME(MaxentModel mod, NameContextGenerator cg, int beamSize) {
    model = mod;
    contextGenerator = cg;
    
    contextGenerator.addFeatureGenerator(new WindowFeatureGenerator(additionalContextFeatureGenerator, 8, 8));
    beam = new NameBeamSearch(beamSize, cg, mod, beamSize);
  }
  
  /**
   * Returns tokens span for the specified document of sentences and their tokens.  
   * Span start and end indices are relitive to the sentence they are in.
   * For example, a span identifying a name consisting of the first and second word of the second sentence would
   * be 0..2 and be referenced as spans[1][0].
   * @param document An array of tokens for each sentence of a document.
   * @return The token spans for each sentence of the specified document.  
   */
  public Span[][] find(String[][] document) {
    Map prevMap = new HashMap();
    Span[][] spans = new Span[document.length][];
    for (int si=0;si<document.length;si++) {
      Span[] names = find(document[si],NameFinderEventStream.additionalContext(document[si],prevMap));
      spans[si] = names;
    }
    return spans;
  }
  
  public Span[] find(String tokens[]) {
    return find(tokens,null);
  }
  
  public Span[] find(String[] tokens, String[][] additionalContext) {
    additionalContextFeatureGenerator.setCurrentContext(additionalContext);
    bestSequence = beam.bestSequence(tokens, additionalContext);
    List c = bestSequence.getOutcomes();
    
    int start = -1;
    int end = -1;
    List spans = new ArrayList(tokens.length);
    for (int li=0;li<c.size();li++) {
      String chunkTag = (String) c.get(li);
      if (chunkTag.equals(NameFinderME.START)) {
        if (start != -1) {
          spans.add(new Span(start,end));
          start = li;
          end = li+1;
        }
      }
      else if (chunkTag.equals(NameFinderME.CONTINUE)) {
        end = li+1;
      }
      else if (chunkTag.equals(NameFinderME.OTHER)) {
        if (start != -1) {
          spans.add(new Span(start,end));
          start = -1;
          end = -1;
        }
      }
    }
    if (start != -1) {
      spans.add(new Span(start,end));
    }
    return (Span[]) c.toArray(new Span[spans.size()]);
  }
  
  
  /**
   * Populates the specified array with the probabilities of the last decoded
   * sequence. The sequence was determined based on the previous call to
   * <code>chunk</code>. The specified array should be at least as large as
   * the numbe of tokens in the previous call to <code>chunk</code>.
   * 
   * @param probs
   *          An array used to hold the probabilities of the last decoded
   *          sequence.
   */
   public void probs(double[] probs) {
     bestSequence.getProbs(probs);
   }
  
  /**
    * Returns an array with the probabilities of the last decoded sequence.  The
    * sequence was determined based on the previous call to <code>chunk</code>.
    * @return An array with the same number of probabilities as tokens were sent to <code>chunk</code>
    * when it was last called.   
    */
   public double[] probs() {
     return bestSequence.getProbs();
   }
  
  public static GISModel train(EventStream es, int iterations, int cut) throws IOException {
    return GIS.trainModel(iterations, new TwoPassDataIndexer(es, cut));
  }
  
  public static void usage(){
    System.err.println("Usage: opennlp.tools.namefind.NameFinderME -encoding encoding training_file model");
    System.exit(1);
  }

  
  public static void main(String[] args) throws java.io.IOException {
    if (args.length == 0) {
      usage();
    }
    int ai = 0;
    String encoding = null;
    while (args[ai].startsWith("-")) {
      if (args[ai].equals("-encoding") && ai+1 < args.length) {
        ai++;
        encoding = args[ai];
      }
      else {
        System.err.println("Unknown option: "+args[ai]);
        usage();
      }
      ai++;
    }
    java.io.File inFile = null;
    java.io.File outFile = null;
    if (ai < args.length) {
      inFile = new java.io.File(args[ai++]);
    }
    else {
      usage();
    }
    if (ai < args.length) {
      outFile = new java.io.File(args[ai++]);
    }
    else {
      usage();
    }
    int iterations = 100;
    int cutoff = 5;
    if (args.length > ai) {
      iterations = Integer.parseInt(args[ai++]);
    }
    if (args.length > ai) {
      cutoff = Integer.parseInt(args[ai++]); 
    }
    GISModel mod;
    opennlp.maxent.EventStream es;
    if (encoding != null) {
       es = new NameFinderEventStream(new NameSampleDataStream(new opennlp.maxent.PlainTextByLineDataStream(new InputStreamReader(new FileInputStream(inFile),encoding))));
    }
    else {
      es = new NameFinderEventStream(new NameSampleDataStream(new opennlp.maxent.PlainTextByLineDataStream(new java.io.FileReader(inFile))));
    }
    mod = train(es, iterations, cutoff);
    System.out.println("Saving the model as: " + args[1]);
    new opennlp.maxent.io.SuffixSensitiveGISModelWriter(mod, outFile).persist();
  }
}