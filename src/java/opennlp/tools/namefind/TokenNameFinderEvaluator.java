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

package opennlp.tools.namefind;

import opennlp.tools.util.FMeasureEvaluator;
import opennlp.tools.util.Span;

/**
 * The {@link TokenNameFinderEvaluator} measures the performance
 * of the given {@link TokenNameFinder} with the provided 
 * reference {@link NameSample}s.
 * 
 * @see FMeasureEvaluator
 * @see TokenNameFinder
 * @see NameSample
 */
public class TokenNameFinderEvaluator extends FMeasureEvaluator<NameSample> {
  
  /**
   * The {@link TokenNameFinder} used to create the predicted
   * {@link NameSample} objects.
   */
  private TokenNameFinder nameFinder;
  
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
      precisionScore.add(FMeasureEvaluator.precision(reference.getNames(), 
          predictedNames));
    }
    
    if (reference.getNames().length > 0) {
      recallScore.add(FMeasureEvaluator.recall(reference.getNames(), 
          predictedNames));
    }
  }
}