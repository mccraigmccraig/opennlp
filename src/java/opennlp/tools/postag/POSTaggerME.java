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

import java.io.*;
import java.util.*;

import opennlp.common.util.FilterFcn;
import opennlp.common.util.Pair;

import opennlp.maxent.*;
import opennlp.maxent.io.*;

/**
 * A part-of-speech tagger that uses maximum entropy.  Trys to predict whether
 * words are nouns, verbs, or any of 70 other POS tags depending on their
 * surrounding context.
 *
 * @author      Gann Bierner
 * @version $Revision: 1.1 $, $Date: 2003/11/05 03:31:04 $
 */
public class POSTaggerME implements Evalable, POSTagger {

  /**
   * The maximum entropy model to use to evaluate contexts.
   */
  protected MaxentModel _posModel;

  /**
   * The feature context generator.
   */
  protected ContextGenerator _contextGen = new POSContextGenerator();

  /**
   * Decides whether a word can be assigned a particular closed class tag.
   */
  protected FilterFcn _closedClassTagsFilter;

  /**
   * Says whether a filter should be used to check whether a tag assignment
   * is to a word outside of a closed class.
   */
  protected boolean _useClosedClassTagsFilter = false;

  private Sequence bestSequence;

  protected POSTaggerME() {}

  public POSTaggerME(MaxentModel mod) {
    this(mod, new POSContextGenerator());
  }

  public POSTaggerME(MaxentModel mod, ContextGenerator cg) {
    _posModel = mod;
    _contextGen = cg;
  }

  public String getNegativeOutcome() {
    return "";
  }

  public EventCollector getEventCollector(Reader r) {
    return new POSEventCollector(r, _contextGen);
  }

  public List tag(List sentence) {
    return bestSequence(sentence);
  }

  public String[] tag(String[] sentence) {
    List t = tag(Arrays.asList(sentence));
    return ((String[]) t.toArray(new String[t.size()]));
  }

  public void probs(double[] probs) {
    List dlist = bestSequence.getProbs();
    for (int pi = 0; pi < probs.length; pi++) {
      probs[pi] = ((Double) dlist.get(pi)).doubleValue();
    }
  }

  public double[] probs() {
    List dlist = bestSequence.getProbs();
    double[] probs = new double[dlist.size()];
    for (int pi = 0; pi < probs.length; pi++) {
      probs[pi] = ((Double) dlist.get(pi)).doubleValue();
    }
    return (probs);
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
        List tags = bestSequence(words);

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

  ///////////////////////////////////////////////////////////////////
  // Do a beam search to compute best sequence of results (as in pos)
  // taken from Ratnaparkhi (1998), PhD diss, Univ. of Pennsylvania
  ///////////////////////////////////////////////////////////////////
  private static class Sequence implements Comparable {
    double score = 1;
    List tagList;
    List probList;
    Sequence() {
      tagList = new ArrayList();
      probList = new ArrayList();
    };

    Sequence(double s) {
      this();
      score = s;
    }
    public int compareTo(Object o) {
      Sequence s = (Sequence) o;
      if (score < s.score)
        return 1;
      else if (score == s.score)
        return 0;
      else
        return -1;
    }
    public Sequence copy() {
      Sequence s = new Sequence(score);
      s.tagList.addAll(tagList);
      s.probList.addAll(probList);
      return s;
    }

    public void add(String t, double d) {
      tagList.add(t);
      probList.add(new Double(d));
      score *= d;
    }

    public List getTags() {
      return (tagList);
    }

    public List getProbs() {
      return (probList);
    }
    public String toString() {
      return super.toString() + " " + score;
    }
  }

  public List bestSequence(List words) {
    int n = words.size();
    int N = 3;
    SortedSet[] h = new SortedSet[n + 1];

    for (int i = 0; i < h.length; i++)
      h[i] = new TreeSet();

    h[0].add(new Sequence());

    for (int i = 0; i < n; i++) {
      int sz = Math.min(N, h[i].size());
      for (int j = 1; j <= sz; j++) {
        Sequence top = (Sequence) h[i].first();
        h[i].remove(top);
        Object[] params = { words, top.getTags(), new Integer(i)};
        double[] scores = _posModel.eval(_contextGen.getContext(params));
        for (int p = 0; p < scores.length; p++) {
          if (!_useClosedClassTagsFilter || _closedClassTagsFilter.filter((String) words.get(i), _posModel.getOutcome(p))) {
            Sequence newS = top.copy();
            newS.add(_posModel.getOutcome(p), scores[p]);
            h[i + 1].add(newS);
          }
        }
      }
    }
    bestSequence = (Sequence) h[n].first();
    return bestSequence.getTags();
  }

  public String[] getOrderedTags(List words, List tags, int index) {
    Object[] params = { words, tags, new Integer(index)};
    double[] probs = _posModel.eval(_contextGen.getContext(params));
    String[] orderedTags = new String[probs.length];
    for (int i = 0; i < probs.length; i++) {
      int max = 0;
      for (int ti = 1; ti < probs.length; ti++) {
        if (probs[ti] > probs[max]) {
          max = ti;
        }
      }
      orderedTags[i] = _posModel.getOutcome(max);
      probs[max] = 0;
    }
    return (orderedTags);
  }

  public static GISModel train(EventStream es, int iterations, int cut) throws IOException {
    return GIS.trainModel(es, iterations, cut);
  }

  /**
     * <p>Trains a new pos model.</p>
     *
     * <p>Usage: java opennlp.postag.POStaggerME data_file new_model_name (iterations cutoff)?</p>
     *
     */
  public static void main(String[] args) throws IOException {
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
