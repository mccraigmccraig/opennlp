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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import opennlp.maxent.EventStream;
import opennlp.maxent.GIS;
import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.PlainTextByLineDataStream;
import opennlp.maxent.TwoPassDataIndexer;
import opennlp.maxent.io.SuffixSensitiveGISModelWriter;
import opennlp.tools.util.BeamSearch;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.Span;

/**
 * Class for creating a maximum-entropy-based name finder.  
 */
public class NameFinderME implements NameFinder {

  protected MaxentModel _npModel;
  protected NameContextGenerator _contextGen;
  private Sequence bestSequence;
  private BeamSearch beam;

  public static final String START = "start";
  public static final String CONTINUE = "cont";
  public static final String OTHER = "other";

  /**
   * Creates a new name finder with the specified model.
   * @param mod The model to be used to find names.
   */
  public NameFinderME(MaxentModel mod) {
    this(mod, new DefaultNameContextGenerator(10), 10);
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
    _npModel = mod;
    _contextGen = cg;
    beam = new NameBeamSearch(beamSize, cg, mod, beamSize);
  }
  
  /* inherieted javadoc */
  public List find(List toks, Map prevTags) {
    bestSequence = beam.bestSequence(toks, new Object[] { prevTags });
    return bestSequence.getOutcomes();
  }

  /* inherieted javadoc */
  public String[] find(Object[] toks, Map prevTags) {
    bestSequence = beam.bestSequence(toks, new Object[] { prevTags });
    List c = bestSequence.getOutcomes();
    return (String[]) c.toArray(new String[c.size()]);
  }

  /* inherieted javadoc */
  public List find(String sentence, List toks, Map prevMap) {

    List tokenStrings = new LinkedList();
    Iterator tokenIterator = toks.iterator();

    while (tokenIterator.hasNext()) {
      Span tokenSpan = (Span) tokenIterator.next();
      tokenStrings.add(sentence.substring(tokenSpan.getStart(), 
          tokenSpan.getEnd()));
    }

    List result = find(tokenStrings, prevMap);

    List detectedNames = new LinkedList();

    Span startSpan = null;
    Span endSpan = null;

    boolean insideName = false;

    int length = tokenStrings.size();

    for (int i = 0; i < length; i++) {

      Span annotation = (Span) toks.get(i);

      if (insideName) {

        // check if insideName ends here
        if (!result.get(i).equals(NameFinderME.CONTINUE)) {

          Span entitySpan = new Span(startSpan.getStart(), endSpan.getEnd());

          detectedNames.add(entitySpan);

          startSpan = null;
          insideName = false;
          endSpan = null;
        }
      } 
      else {
        if (result.get(i).equals(NameFinderME.START)) {
          startSpan = annotation;
          insideName = true;
        }
      }

      if (insideName) {
        endSpan = annotation;
      }
    }

    // is last start in sent
    if (insideName) {
      detectedNames.add(startSpan);
    }

    return detectedNames;
  }
  
  /* inherieted javadoc */
  public Span[] find(String sentence, Span[] toks, Map prevMap) {
    
    List tokList = new LinkedList();
    Collections.addAll(tokList, toks);
    
    List resultList = find(sentence, tokList, prevMap);
    
    Span[] result = new Span[toks.length];
    resultList.toArray(result);
    
    return result;
  }
  /** 
   * This method determines wheter the outcome is valid for the preceeding sequence.  
   * This can be used to implement constraints on what sequences are valid.  
   * @param outcome The outcome.
   * @param sequence The precceding sequence of outcomes assignments. 
   * @return true is the outcome is valid for the sequence, false otherwise.
   */
  protected boolean validOutcome(String outcome, Sequence sequence) {
    if (outcome.equals(CONTINUE)) {
      List tags = sequence.getOutcomes();
      int li = tags.size() - 1;
      if (li == -1) {
        return false;
      }
      else if (((String) tags.get(li)).equals(OTHER)) {
        return false;
      }
    }
    return true;
  }

  /** 
   * Implementation of the abstract beam search to allow the name finder to use the common beam search code. 
   *
   */
  private class NameBeamSearch extends BeamSearch {

    /**
     * Creams a beam seach of the specified size sing the specified model with the specified context generator.
     * @param size The size of the beam.
     * @param cg The context generator used with the specified model.
     * @param model The model used to determine names.
     * @param beamSize 
     */
    public NameBeamSearch(int size, NameContextGenerator cg, MaxentModel model, int beamSize) {
      super(size, cg, model, beamSize);
    }

    protected boolean validSequence(int i, List sequence, Sequence s, String outcome) {
      return validOutcome(outcome, s);
    }
  }

  /**
     * Populates the specified array with the probabilities of the last decoded sequence.  The
     * sequence was determined based on the previous call to <code>chunk</code>.  The 
     * specified array should be at least as large as the numbe of tokens in the previous call to <code>chunk</code>.
     * @param probs An array used to hold the probabilities of the last decoded sequence.
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
   * Creates the map with the previous result.
   * 
   * @param tokens - the previous tokens as array of String or 
   * null (if first time)
   * @param outcomes - the previous outcome as array of String or null 
   * (if first time)
   * @return - the previous map
   */
  public static Map createPrevMap(String[] tokens, String[] outcomes) {
    Map prevMap = new HashMap();

    if (tokens != null | outcomes != null) {

      if (tokens.length != outcomes.length) {
        throw new IllegalArgumentException(
            "The sent and outcome arrays MUST have the same size!");
      }

      for (int i = 0; i < tokens.length; i++) {
        prevMap.put(tokens[i], outcomes[i]);
      }
    } 
    else {
      prevMap = Collections.EMPTY_MAP;
    }

    return prevMap;
  }
  
  /**
   * Creates the prevMap with the previous result.
   * 
   * @param tokens - the previous tokens as List of String or null
   * @param outcomes - the previous outcome as List of Strings or null
   * @return - the previous map or an empty map if token or outcome is null
   */
  public static Map createPrevMap(List tokens, List outcomes) {

    Map prevMap = new HashMap();

    if (tokens != null | outcomes != null) {

      if (tokens.size() != outcomes.size()) {
        throw new IllegalArgumentException(
            "The sent and outcome arrays MUST have the same size!");
      }

      Iterator tokenIterator = tokens.iterator();
      Iterator outcomeIterator = outcomes.iterator();

      while (tokenIterator.hasNext() && outcomeIterator.hasNext()) {
        prevMap.put(tokenIterator.next(), outcomeIterator.next());
      }
    } 
    else {
      prevMap = Collections.EMPTY_MAP;
    }

    return prevMap;
  }

  /**
   * Creates the prevMap with the previous result.
   * 
   * @param sentence
   * @param tokens - the previous tokens as list of Span or 
   * null (if first time)
   * @param outcomes - the previous outcome as list of Span or null 
   * (if first time)
   * @return - the previous map
   */
  public static Map createPrevMap(String sentence, List tokens, List outcomes) {
    Map prevMap;
    
    if (sentence != null && tokens != null && 
        outcomes != null & tokens.size() > 0) {
      
      if (tokens.size() < outcomes.size()) {
        throw new IllegalArgumentException("The number of tokens must be " +
            "less or equal compared to the number of outcomes");
      }
      
      Iterator outcomeIterator = outcomes.iterator();
           
      Span outcomeSpan;
      
      if (outcomeIterator.hasNext()) {
        outcomeSpan = (Span) outcomeIterator.next();
      }
      else {
        outcomeSpan = new Span(0, 0);
      }
 
      boolean isInsideSpan = false;
      
      prevMap = new HashMap();
      
      for (Iterator i = tokens.iterator(); i.hasNext();) {
        
        Span token = (Span) i.next();
        
        if (!outcomeSpan.contains(token)) {
          prevMap.put(token.getCoveredText(sentence), 
              NameFinderME.OTHER);
          
          if (isInsideSpan) {
            if (outcomeIterator.hasNext()) {
              outcomeSpan = (Span) outcomeIterator.next();
            }
            isInsideSpan = false;
          }
        } 
        else if (outcomeSpan.startsWith(token)) {
          prevMap.put(token.getCoveredText(sentence), 
              NameFinderME.START);
          
          isInsideSpan = true;
        }
        // isContained
        else {
          prevMap.put(token.getCoveredText(sentence), 
              NameFinderME.CONTINUE);
        }
      }
    }
    else {
      prevMap = Collections.EMPTY_MAP;
    }
    return prevMap;
  }
  
  private static GISModel train(EventStream es, int iterations, int cut) throws IOException {
    return GIS.trainModel(iterations, new TwoPassDataIndexer(es, cut));
  }
  
  public static void usage(){
    System.err.println("Usage: opennlp.tools.namefind.NameFinderME -encoding encoding training_file model");
    System.exit(1);
  }

  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      usage();
    }
    try {
      int ai=0;
      String encoding = null;
      while (args[ai].startsWith("-")) {
        if (args[ai].equals("-encoding")) {
          ai++;
          if (ai < args.length) {
            encoding = args[ai];
            ai++;
          }
          else {
            usage();
          }
        }
      }
      File inFile = new File(args[ai++]);
      File outFile = new File(args[ai++]);
      GISModel mod;

      EventStream es = new NameFinderEventStream(new PlainTextByLineDataStream(new InputStreamReader(new FileInputStream(inFile),encoding)));
      if (args.length > ai)
        mod = train(es, Integer.parseInt(args[ai++]), Integer.parseInt(args[ai++]));
      else
        mod = train(es, 100, 5);

      System.out.println("Saving the model as: " + outFile);
      new SuffixSensitiveGISModelWriter(mod, outFile).persist();

    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }
}