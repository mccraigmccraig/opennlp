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

package opennlp.tools.sentdetect;

import opennlp.tools.util.FMeasureEvaluator;

/**
 * The {@link SentenceDetectorEvaluator} measures the performance of
 * the given {@link SentenceDetector} with the provided reference
 * {@link SentenceSample}s.
 * 
 * @see FMeasureEvaluator
 * @see SentenceDetector
 * @see SentenceSample
 */
public class SentenceDetectorEvaluator extends FMeasureEvaluator<SentenceSample> {

  /**
   * The {@link SentenceDetector} used to predict sentences.
   */
  private SentenceDetector sentenceDetector;
  
  /**
   * Initializes the current instance.
   * 
   * @param sentenceDetector
   */
  public SentenceDetectorEvaluator(SentenceDetector sentenceDetector) {
    this.sentenceDetector = sentenceDetector;
  }
  
  private Integer[] convert(int starts[]) {
    Integer begins[] = new Integer[starts.length];
    
    for (int i = 0; i < starts.length; i++) {
      begins[i] = new Integer(starts[i]);
    }
    
    return begins;
  }
  
  public void evaluateSample(SentenceSample sample) {
    
    int starts[] = sentenceDetector.sentPosDetect(sample.getDocument());
    
    precisionScore.add(FMeasureEvaluator.precision(
        convert(sample.getSentences()), convert(starts)));
    recallScore.add(FMeasureEvaluator.recall(
        convert(sample.getSentences()), convert(starts)));
  }
}