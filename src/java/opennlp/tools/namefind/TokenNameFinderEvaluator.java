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
   * @param nameFinder the {@link TokenNameFinder} which should
   * be used to create the predicted {@link NameSample}s.
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
   */
  public void evaluateSample(NameSample reference) {
    
    String sentence[] = reference.getSentence();
    
    Span predictedNames[] = nameFinder.find(sentence);
    
    Span referenceNames[] = reference.getNames();
    
    // The reference name is used to lookup
    // the corresponding predicted name.
    // If the lookup was successful the predicted name
    // is counted as true positive
    // If the lookup failed the predicted name is counted
    // as false negative
    
    int truePositives = 0;
    int falseNegatives = 0;
    
    // Maybe a map should be used to improve performance on long sentences
    for (int referenceIndex = 0; referenceIndex < referenceNames.length; 
        referenceIndex++) {
      
      Span referenceName = referenceNames[referenceIndex];
      
      for (int predictedIndex = 0; predictedIndex < predictedNames.length; 
          predictedIndex++) {
        if (referenceName.equals(predictedNames[predictedIndex])) {
          truePositives++;
        }
        else {
          falseNegatives++;
        }
      }
    }
    
    // Each predicted name is used to lookup the
    // corresponding reference name.
    // If the lookup fails the predicted name is counted
    // as false positive
    
    int falsePositives = 0;
    
    for (int predictedIndex = 0; predictedIndex < predictedNames.length; 
        predictedIndex++) {
      
      Span predictedName = predictedNames[predictedIndex];
      
      for (int referenceIndex = 0; referenceIndex < referenceNames.length; 
          referenceIndex++) {
        if (!predictedName.equals(referenceNames[referenceIndex])) {
          falsePositives++;
        }
      }
    }
    
    // calculate and update the scores
    if (truePositives + falsePositives > 0) {
      
      double precisionScoreValue = truePositives / 
          (truePositives + falsePositives);
      precisionScore.add(precisionScoreValue);
          
      double recallScoreValue = truePositives / 
          (truePositives + falseNegatives);
      recallScore.add(recallScoreValue);
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
  public void evaluate(NameSampleStream nameSamples) {
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
    // print out both scores, and number of evaluated name samples
    return super.toString();
  }
}