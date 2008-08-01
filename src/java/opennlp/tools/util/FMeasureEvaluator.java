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

package opennlp.tools.util;

import java.util.Iterator;

/**
 * The {@link FMeasureEvaluator} is an abstract base class for evaluators
 * which measure precision, recall and the resulting f-measure.
 */
public abstract class FMeasureEvaluator<T> {

  /**
   * The mean of all calculated precision scores.
   */
  protected Mean precisionScore = new Mean();
  
  /**
   * The mean of all calculated recall scores.
   */
  protected Mean recallScore = new Mean();
  
  /**
   * Evaluates the given reference object.
   * 
   * The implementation has to update the precisionScore and recallScore
   * after every invocation.
   * 
   * @param sample the sample to be evaluated
   */
  public abstract void evaluateSample(T sample);
  
  /**
   * Reads all sample objects from the stream
   * and evaluates each sample object with 
   * {@link #evaluateSample(Object)} method.
   * 
   * @param samples the stream of reference which
   * should be evaluated.
   */
  public void evaluate(Iterator<T> samples) {
    while (samples.hasNext()) {
      evaluateSample(samples.next());
    }
  }
  
  /**
   * Retrieves the arithmetic mean of the precision scores
   * calculated for each evaluated sample.
   * 
   * @return the arithmetic mean of all precision scores
   */
  public double getPrecisionScore() {
    return precisionScore.mean();
  }
  
  /**
   * Retrieves the arithmetic mean of the recall score
   * calculated for each evaluated sample.
   *
   * @return the arithmetic mean of all recall scores
   */
  public double getRecallScore() {
    return recallScore.mean();
  }
  
  // get f-measure
  
  /**
   * Creates a human read-able {@link String} representation.
   */
  @Override
  public String toString() {
    return "Precision: " + Double.toString(getPrecisionScore()) + "\n" +
        " Recall: " + Double.toString(getRecallScore());
  }
  
  /**
   * This method counts the number of objects which are equal and
   * occur in the references and predictions arrays.
   * 
   * These are the number of true positives.
   * 
   * @param references the gold standard 
   * @param predictions the predictions
   * 
   * @return number of true positives
   */
  public static int countTruePositives(Object references[], 
      Object predictions[]) {
    
    int truePositives = 0;
    
    // Maybe a map should be used to improve performance
    for (int referenceIndex = 0; referenceIndex < references.length; 
        referenceIndex++) {
      
      Object referenceName = references[referenceIndex];
      
      for (int predictedIndex = 0; predictedIndex < predictions.length; 
          predictedIndex++) {
        if (referenceName.equals(predictions[predictedIndex])) {
          truePositives++;
        }
      }
    }
    
    return truePositives;
  }
  
  /**
   * Calculates the precision score for the given reference and
   * predicted spans.
   * 
   * @param references the gold standard spans
   * @param predictions the predicted spans   
   * 
   * @return the precision score or -1 if there are no predicted spans
   */
  public static double precision(Object references[], Object predictions[]) {
    
    if (predictions.length > 0) {
      return countTruePositives(references, predictions) / 
          (double) predictions.length;
    }
    else {
      return -1;
    }
  }
  
  /**
   * Calculates the recall score for the given reference and
   * predicted spans.
   * 
   * @param references the gold standard spans
   * @param predictions the predicted spans
   * 
   * @return the recall score or -1 if there are no reference spans
   */
  public static double recall(Span references[], Span predictions[]) {
    
    if (references.length > 0) {
      return countTruePositives(references, predictions) /
          (double) references.length;
    }
    else {
        return -1;
    }
  }
}