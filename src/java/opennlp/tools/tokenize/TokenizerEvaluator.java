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

import opennlp.tools.util.FMeasureEvaluator;
import opennlp.tools.util.Span;

/**
 * The {@link TokenizerEvaluator} measures the performance of
 * the given {@link Tokenizer} with the provided reference
 * {@link TokenSample}s.
 * 
 * @see FMeasureEvaluator
 * @see Tokenizer
 * @see TokenSample
 */
public class TokenizerEvaluator extends FMeasureEvaluator<TokenSample> {

  /**
   * The {@link Tokenizer} used to create the
   * predicted tokens.
   */
  private Tokenizer tokenizer;
  
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
      precisionScore.add(FMeasureEvaluator.precision(reference.getTokenSpans(),
          predictedSpans));
    }
    
    if (reference.getTokenSpans().length > 0) {
      recallScore.add(FMeasureEvaluator.recall(reference.getTokenSpans(),
          predictedSpans));
    }
  }
}