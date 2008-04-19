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
import java.util.List;

import opennlp.maxent.EventStream;
import opennlp.maxent.GIS;
import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.TwoPassDataIndexer;
import opennlp.tools.util.BeamSearch;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.Span;
import opennlp.tools.util.featuregen.AdditionalContextFeatureGenerator;
import opennlp.tools.util.featuregen.WindowFeatureGenerator;

/**
 * Class for creating a maximum-entropy-based name finder.  
 */
public class NameFinderME implements TokenNameFinder {

  private static String[][] EMPTY = new String[0][0];
  
  /**
   * Implementation of the abstract beam search to allow the name finder to use
   * the common beam search code.
   */
  private class NameBeamSearch extends BeamSearch {

    /**
     * Creams a beam search of the specified size sing the specified model with
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
     * This method determines whether the outcome is valid for the preceding
     * sequence. This can be used to implement constraints on what sequences are
     * valid.
     * 
     * @param outcome The outcome.
     * @param inputSequence The preceding sequence of outcomes assignments.
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
    this(mod, new DefaultNameContextGenerator(), 3);
  }

  /**
   * Creates a new name finder with the specified model and context generator.
   * @param mod The model to be used to find names.
   * @param cg The context generator to be used with this name finder.
   */
  public NameFinderME(MaxentModel mod, NameContextGenerator cg) {
    this(mod, cg, 3);
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

  public Span[] find(String[] tokens) {
    return find(tokens, EMPTY);
  }
  
  /** Generates name tags for the given sequence, typically a sentence, returning token spans for any identified names.
   * @param tokens an array of the tokens or words of the sequence, typically a sentence.
   * @param additionalContext features which are based on context outside of the sentence but which should also be used.
   * @return an array of spans for each of the names identified.
   */
  public Span[] find(String[] tokens, String[][] additionalContext) {
    additionalContextFeatureGenerator.setCurrentContext(additionalContext);
    bestSequence = beam.bestSequence(tokens, additionalContext);
    List c = bestSequence.getOutcomes();
    
    contextGenerator.updateAdaptiveData(tokens, (String[]) c.toArray(new String[c.size()]));
    
    int start = -1;
    int end = -1;
    List spans = new ArrayList(tokens.length);
    for (int li = 0; li < c.size(); li++) {
      String chunkTag = (String) c.get(li);
      if (chunkTag.equals(NameFinderME.START)) {
        if (start != -1) {
          spans.add(new Span(start, end));
        }
        
        start = li;
        end = li + 1;
        
      }
      else if (chunkTag.equals(NameFinderME.CONTINUE)) {
        end = li + 1;
      }
      else if (chunkTag.equals(NameFinderME.OTHER)) {
        if (start != -1) {
          spans.add(new Span(start, end));
          start = -1;
          end = -1;
        }
      }
    }
    
    if (start != -1) {
      spans.add(new Span(start,end));
    }
    
    return (Span[]) spans.toArray(new Span[spans.size()]);
  }
  
  /**
   * Forgets all adaptive data which was collected during previous
   * calls to one of the find methods.
   * 
   * This method is typical called at the end of a document.
   */
  public void clearAdaptiveData() {
   contextGenerator.clearAdaptiveData();
  }
  
  /**
   * Populates the specified array with the probabilities of the last decoded
   * sequence. The sequence was determined based on the previous call to
   * <code>chunk</code>. The specified array should be at least as large as
   * the number of tokens in the previous call to <code>chunk</code>.
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
   
   /**
    * Returns an array of probabilities for each of the specified spans which is the product 
    * the probabilities for each of the outcomes which make up the span. 
    * @param spans The spans of the names for which probabilities are desired.
    * @return an array of probabilities for each of the specified spans.
    */
   public double[] probs(Span[] spans) {
     double[] sprobs = new double[spans.length];
     double[] probs = bestSequence.getProbs();
     for (int si=0;si<spans.length;si++) {
       double p = 1;
       for (int oi=spans[si].getStart();oi<spans[si].getEnd();oi++) {
         p*=probs[oi];
       }
       sprobs[si] = p;
     }
     return sprobs;
   }
  
  public static GISModel train(EventStream es, int iterations, int cut) throws IOException {
    return GIS.trainModel(iterations, new TwoPassDataIndexer(es, cut));
  }
  
  public static void usage(){
    System.err.println("Usage: opennlp.tools.namefind.NameFinderME -encoding encoding training_file model");
    System.exit(1);
  }

  /**
   * Trains a new named entity model on the specified training file using the specified encoding to read it in. 
   * @param args [-encoding encoding] training_file model_file
   * @throws java.io.IOException
   */
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
    System.out.println("Saving the model as: " + outFile.toString());
    new opennlp.maxent.io.SuffixSensitiveGISModelWriter(mod, outFile).persist();
  }
}