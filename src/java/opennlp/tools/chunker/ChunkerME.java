package opennlp.tools.chunker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import opennlp.maxent.ContextGenerator;
import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;

/**
 * @author Tom Morton
 *
 */
public class ChunkerME implements Chunker {

  protected MaxentModel _npModel;
  protected ContextGenerator _contextGen;
  private Sequence bestSequence;
  private int beamSize;

  public ChunkerME(MaxentModel mod) {
    this(mod, new DefaultChunkerContextGenerator(),10);
  }

  public ChunkerME(MaxentModel mod, ContextGenerator cg) {
    this(mod,cg,10);
  }
  
  public ChunkerME(MaxentModel mod,ContextGenerator cg, int beamSize) {
    _npModel = mod;
    _contextGen = cg;
    this.beamSize=beamSize;
   }

  public List chunk(List toks, List tags) {
    return bestSequence(toks, tags);
  }

  public String[] chunk(Object[] toks, String[] tags) {
    List c = bestSequence(Arrays.asList(toks), Arrays.asList(tags));
    return (String[]) c.toArray(new String[c.size()]);
  }
  
  /** 
   * This method determines wheter the outcome is valid for the preceeding sequence.  
   * This can be used to implement constraints on what sequences are valid.  
   * @param outcome The outcome.
   * @param sequence The precceding sequence of outcomes assignments. 
   * @return true is the outcome is valid for the sequence, false otherwise.
   */
  protected boolean validOutcome(String outcome, Sequence sequence) {
    return(true);
  }

  private List bestSequence(List words, List tags) {
    int n = words.size();
    SortedSet prev = new TreeSet();
    SortedSet next = new TreeSet();
    SortedSet tmp;
    prev.add(new Sequence());

    for (int i = 0; i < n; i++) {
      int sz = Math.min(beamSize, prev.size());
      for (int j = 1; j <= sz; j++) {
        Sequence top = (Sequence) prev.first();
        prev.remove(top);
        Object[] params = { new Integer(i), words, tags, top.getTags()};
        double[] scores = _npModel.eval(_contextGen.getContext(params));
        for (int p = 0; p < scores.length; p++) {
          Sequence newS = top.copy();
          String outcome = _npModel.getOutcome(p);
          if (validOutcome(outcome,top)) {
            newS.add(_npModel.getOutcome(p), scores[p]);
            next.add(newS);
          }
        }
      }
      // make prev = next; and re-init next (we reuse existing prev set once we clear it)
      prev.clear();
      tmp=prev;
      prev=next;
      next=tmp;
    }
    bestSequence = (Sequence) prev.first();
    return bestSequence.getTags();
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
  
  public static GISModel train(opennlp.maxent.EventStream es, int iterations, int cut) throws java.io.IOException {
    return opennlp.maxent.GIS.trainModel(es, iterations, cut);
  }

  /**
     * <p>Trains a new chunker model.</p>
     *
     * <p>Usage: java opennlp.chunker.ChunkerME data_file new_model_name (iterations cutoff)?</p>
     *
     */
  public static void main(String[] args) {
    try {
      java.io.File inFile = new java.io.File(args[0]);
      java.io.File outFile = new java.io.File(args[1]);
      GISModel mod;
      opennlp.maxent.EventStream es = new ChunkerEventStream(new opennlp.maxent.PlainTextByLineDataStream(new java.io.FileReader(inFile)));
      if (args.length > 3)
        mod = train(es, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
      else
        mod = train(es, 100, 5);

      System.out.println("Saving the model as: " + args[1]);
      new opennlp.maxent.io.SuffixSensitiveGISModelWriter(mod, outFile).persist();

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  protected static class Sequence implements Comparable {
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
}

