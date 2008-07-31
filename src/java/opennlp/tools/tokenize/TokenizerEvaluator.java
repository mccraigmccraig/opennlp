///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2008 OpenNlp
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

package opennlp.tools.tokenize;

import opennlp.tools.util.EvaluatorUtil;
import opennlp.tools.util.Mean;
import opennlp.tools.util.Span;

/**
 * The {@link TokenizerEvaluator} measures the performance of
 * the given {@link Tokenizer} with the provided reference
 * {@link TokenSample}s.
 * 
 * Performance is measured with the precision and recall scores.
 *  
 * Evaluation results are the arithmetic mean of the precision
 * scores calculated for each reference {@link TokenSample} and
 * the arithmetic mean of the recall scores calculated for
 * each reference {@link TokenSample}.
 */
public class TokenizerEvaluator {

  /**
   * The {@link Tokenizer} used to create the 
   * predicted tokens.
   */
  private Tokenizer tokenizer;
  
  /**
   * The mean of all calculated precision scores.
   */
  private Mean precisionScore = new Mean();
  
  /**
   * The mean of all calculated recall scores.
   */
  private Mean recallScore = new Mean();
  
  /**
   * Initializes the current instance with the
   * given {@link Tokenizer}.
   * 
   * @param tokenizer the {@link Tokenizer} to evaluate.
   */
  public TokenizerEvaluator(Tokenizer tokenizer) {
    this.tokenizer = tokenizer;
  }
  
  /**
   * Evaluates the given reference {@link TokenSample} object.
   * 
   * This is done by detecting the token spans with the
   * {@link Tokenizer}. The detected token spans are then
   * used to calculate calculate and update the scores.
   * 
   * @param reference the reference {@link TokenSample}.
   */
  public void evaluateSample(TokenSample reference) {
    Span predictedSpans[] = tokenizer.tokenizePos(reference.getText());

    if (predictedSpans.length > 0) {
      precisionScore.add(EvaluatorUtil.precision(reference.getTokenSpans(), 
          predictedSpans));
    }
    
    if (reference.getTokenSpans().length > 0) {
      recallScore.add(EvaluatorUtil.recall(reference.getTokenSpans(), 
          predictedSpans));
    }
  }
  
  /**
   * Retrieves the arithmetic mean of the precision scores
   * calculated for each evaluated {@link TokenSample}.
   * 
   * @return the arithmetic mean of all precision scores
   */
  public double getPrecisionScore() {
    return precisionScore.mean();
  }
  
  /**
   * Retrieves the arithmetic mean of the recall score
   * calculated for each evaluated {@link TokenSample}.
   *
   * @return the arithmetic mean of all recall scores
   */
  public double getRecallScore() {
    return recallScore.mean();
  }
  
  /**
   * Creates a human read-able {@link String} representation.
   */
  @Override
  public String toString() {
    return "Precision: " + Double.toString(getPrecisionScore()) + "\n" +
        " Recall: " + Double.toString(getRecallScore());
  }
}