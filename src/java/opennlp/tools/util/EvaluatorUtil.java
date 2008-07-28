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

/**
 * This is a utility class for the calculation of
 * evaluation scores.
 */
public class EvaluatorUtil {
  
  private EvaluatorUtil() {
    // should not be instantiated 
  }
  /**
   * This method counts the number of spans which are
   * contained in the reference and predicted spans arrays.
   * These are the number of true positives.
   * 
   * @param referenceSpans the gold standard spans
   * @param predictedSpans the predicted spans
   * 
   * @return number of true positives
   */
  public static int countTruePositives(Span referenceSpans[], 
      Span predictedSpans[]) {
    
    int truePositives = 0;
    
    // Maybe a map should be used to improve performance
    for (int referenceIndex = 0; referenceIndex < referenceSpans.length; 
        referenceIndex++) {
      
      Span referenceName = referenceSpans[referenceIndex];
      
      for (int predictedIndex = 0; predictedIndex < predictedSpans.length; 
          predictedIndex++) {
        if (referenceName.equals(predictedSpans[predictedIndex])) {
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
   * @param referenceSpans the gold standard spans
   * @param predictedSpans the predicted spans   
   * 
   * @return the precision score or -1 if there are no predicted spans
   */
  public static double precision(Span referenceSpans[], Span predictedSpans[]) {
    
    if (predictedSpans.length > 0) {
      return countTruePositives(referenceSpans, predictedSpans) / 
          (double) predictedSpans.length;
    }
    else {
      return -1;
    }
  }
  
  /**
   * Calculates the recall score for the given reference and
   * predicted spans.
   * 
   * @param referenceSpans the gold standard spans
   * @param predictedSpans the predicted spans
   * 
   * @return the recall score or -1 if there are no reference spans
   */
  public static double recall(Span referenceSpans[], Span predictedSpans[]) {
    
    if (referenceSpans.length > 0) {
      return countTruePositives(referenceSpans, predictedSpans) / 
          (double) referenceSpans.length;
    }
    else {
        return -1;
    }
  }
}