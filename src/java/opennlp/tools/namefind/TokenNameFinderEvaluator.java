///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2007 OpenNlp
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

import java.util.Iterator;

import opennlp.tools.util.EvaluatorUtil;
import opennlp.tools.util.Mean;
import opennlp.tools.util.Span;

/**
 * The {@link TokenNameFinderEvaluator} measures the performance
 * of the given {@link TokenNameFinder} with the provided 
 * reference {@link NameSample}s.
 * 
 * Performance is measured with the precision and recall scores.
 * 
 * Evaluation results are the arithmetic mean of the precision
 * scores calculated for each reference {@link NameSample} and
 * the arithmetic mean of the recall scores calculated for
 * each reference {@link NameSample}.
 * 
 * @see TokenNameFinder
 */
public class TokenNameFinderEvaluator {
  
  /**
   * The {@link TokenNameFinder} used to create the predicted
   * {@link NameSample} objects.
   */
  private TokenNameFinder nameFinder;
  
  /**
   * The mean of all calculated precision scores.
   */
  private Mean precisionScore = new Mean();
  
  /**
   * The mean of all calculated recall scores.
   */
  private Mean recallScore = new Mean();
  
  /**
   * Initializes the current instance with the given 
   * {@link TokenNameFinder}. 
   * 
   * @param nameFinder the {@link TokenNameFinder} to evaluate.
   */
  public TokenNameFinderEvaluator(TokenNameFinder nameFinder) {
    this.nameFinder = nameFinder;
  }
  
  /**
   * Evaluates the given reference {@link NameSample} object.
   * 
   * This is done by finding the names with the 
   * {@link TokenNameFinder} in the sentence from the reference 
   * {@link NameSample}. The found names are then used to
   * calculate and update the scores.
   * 
   * @param reference the reference {@link NameSample}.
   */
  public void evaluateSample(NameSample reference) {
    
    Span predictedNames[] = nameFinder.find(reference.getSentence());
    
    if (predictedNames.length > 0) {
      precisionScore.add(EvaluatorUtil.precision(reference.getNames(), 
          predictedNames));
    }
    
    if (reference.getNames().length > 0) {
      recallScore.add(EvaluatorUtil.recall(reference.getNames(), 
          predictedNames));
    }
  }
  
  /**
   * Reads all {@link NameSample} objects from the stream
   * and evaluates each {@link NameSample} object with 
   * {@link #evaluateSample(NameSample)} method.
   * 
   * @param nameSamples the stream of reference {@link NameSample} which
   * should be evaluated.
   */
  public void evaluate(Iterator<NameSample> nameSamples) {
    while (nameSamples.hasNext()) {
      evaluateSample(nameSamples.next());
    }
  }
  
  /**
   * Retrieves the arithmetic mean of the precision scores
   * calculated for each evaluated {@link NameSample}.
   * 
   * @return the arithmetic mean of all precision scores
   */
  public double getPrecisionScore() {
    return precisionScore.mean();
  }
  
  /**
   * Retrieves the arithmetic mean of the recall score
   * calculated for each evaluated {@link NameSample}.
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