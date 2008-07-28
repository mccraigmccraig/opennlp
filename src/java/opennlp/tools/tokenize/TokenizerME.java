///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2002 Jason Baldridge, Gann Bierner, and Tom Morton
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

package opennlp.tools.tokenize;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import opennlp.maxent.EventStream;
import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.TwoPassDataIndexer;
import opennlp.maxent.io.SuffixSensitiveGISModelWriter;
import opennlp.tools.util.Span;

/**
 * A Tokenizer for converting raw text into separated tokens.  It uses
 * Maximum Entropy to make its decisions.  The features are loosely
 * based off of Jeff Reynar's UPenn thesis "Topic Segmentation:
 * Algorithms and Applications.", which is available from his
 * homepage: <http://www.cis.upenn.edu/~jcreynar>.
 *
 * @author      Tom Morton
 * @version $Revision: 1.23 $, $Date: 2008/07/28 15:41:36 $
 */
public class TokenizerME extends AbstractTokenizer {

  /**
   * Constant indicates a token split.
   */
  public static final String SPLIT ="T";
  
  /**
   * Constant indicates no token split.
   */
  public static final String NO_SPLIT ="F";
  
  /**
   * The maximum entropy model to use to evaluate contexts.
   */
  private MaxentModel model;

  /**
   * The context generator.
   */
  private final TokenContextGenerator cg = new DefaultTokenContextGenerator();

  private static final Double ONE = new Double(1.0);
  
  /**
   * Alpha-Numeric Pattern
   */
  public static final Pattern alphaNumeric = Pattern.compile("^[A-Za-z0-9]+$");

  /** optimization flag to skip alpha numeric tokens for further
   * tokenization 
   */
  private boolean ALPHA_NUMERIC_OPTIMIZATION;

  /** list of probabilities for each token returned from call to
   * tokenize() */
  private List<Double> tokProbs;
  private List<Span> newTokens;

  /**
   * Class constructor which takes the string locations of the
   * information which the maxent model needs.
   * 
   * @param mod 
   */
  public TokenizerME(MaxentModel mod) {
    setAlphaNumericOptimization(false);
    model = mod;
    newTokens = new ArrayList<Span>();
    tokProbs = new ArrayList<Double>(50);
  }

  /** Returns the probabilities associated with the most recent
   * calls to tokenize() or tokenizePos().
   * @return probability for each token returned for the most recent
   * call to tokenize.  If not applicable an empty array is
   * returned.
   */
  public double[] getTokenProbabilities() {
    double[] tokProbArray = new double[tokProbs.size()];
    for (int i = 0; i < tokProbArray.length; i++) {
      tokProbArray[i] = ((Double) tokProbs.get(i)).doubleValue();
    }
    return tokProbArray;
  }

  /**
   * Tokenizes the string.
   *
   * @param d  The string to be tokenized.
   * @return   A span array containing individual tokens as elements.
   *           
   */
  public Span[] tokenizePos(String d) {
    Span[] tokens = WhitespaceTokenizer.INSTANCE.tokenizePos(d);
    newTokens.clear();
    tokProbs.clear();
    for (int i = 0, il = tokens.length; i < il; i++) {
      Span s = tokens[i];
      String tok = d.substring(s.getStart(), s.getEnd());
      // Can't tokenize single characters
      if (tok.length() < 2) {
        newTokens.add(s);
        tokProbs.add(ONE);
      }
      else if (useAlphaNumericOptimization() && alphaNumeric.matcher(tok).matches()) {
        newTokens.add(s);
        tokProbs.add(ONE);
      }
      else {
        int start = s.getStart();
        int end = s.getEnd();
        final int origStart = s.getStart();
        double tokenProb = 1.0;
        for (int j = origStart + 1; j < end; j++) {
          double[] probs =
            model.eval(cg.getContext(tok, j - origStart));
          String best = model.getBestOutcome(probs);
          //System.err.println("TokenizerME: "+tok.substring(0,j-origStart)+"^"+tok.substring(j-origStart)+" "+best+" "+probs[model.getIndex(best)]);
          tokenProb *= probs[model.getIndex(best)];
          if (best.equals(TokenizerME.SPLIT)) {
            newTokens.add(new Span(start, j));
            tokProbs.add(new Double(tokenProb));
            start = j;
            tokenProb = 1.0;
          }
        }
        newTokens.add(new Span(start, end));
        tokProbs.add(new Double(tokenProb));
      }
    }

    Span[] spans = new Span[newTokens.size()];
    newTokens.toArray(spans);
    return spans;
  }

  /**
   * Trains the {@link TokenizerME}, use this to create a new model.
   * 
   * @param evc
   * @return the new model
   */
  public static GISModel train(EventStream evc) throws IOException {
    return opennlp.maxent.GIS.trainModel(100, new TwoPassDataIndexer(evc, 5));
  }
  
  /**
   * Trains the {@link TokenizerME}, use this to create a new model.
   * 
   * @param evc
   * @param output
   * @throws IOException
   */
  public static void train(EventStream evc, File output, String encoding) throws IOException {
    new SuffixSensitiveGISModelWriter(TokenizerME.train(evc), output).persist();
  }

  /**
   * Used to have the tokenizer ignore tokens which only contain alpha-numeric characters.
   * @param opt set to true to use the optimization, false otherwise.
   */
  public void setAlphaNumericOptimization(boolean opt) {
    ALPHA_NUMERIC_OPTIMIZATION = opt;
  }

/**
 * Returns the value of the alpha-numeric optimization flag.
 * @return true if the tokenizer should use alpha-numeric optization, false otherwise.
 */
  public boolean useAlphaNumericOptimization() {
    return ALPHA_NUMERIC_OPTIMIZATION;
  }
}