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

import java.util.Iterator;

import opennlp.tools.util.EvaluatorUtil;
import opennlp.tools.util.Mean;
import opennlp.tools.util.Span;

public class SentenceDetectorEvaluator {

  private SentenceDetector sentenceDetector;
  
  /**
   * The mean of all calculated precision scores.
   */
  private Mean precisionScore = new Mean();
  
  /**
   * The mean of all calculated recall scores.
   */
  private Mean recallScore = new Mean();
  
  public SentenceDetectorEvaluator(SentenceDetector sentenceDetector) {
    this.sentenceDetector = sentenceDetector;
  }
  
  private Span[] convert(int starts[]) {
    Span spans[] = new Span[starts.length];
    
    for (int i = 0; i < starts.length; i++) {
      spans[i] = new Span(starts[i], starts[i]);
    }
    
    return spans;
  }
  
  public void evaluateSample(SentenceSample sample) {
    
    int starts[] = sentenceDetector.sentPosDetect(sample.getDocument());
    
    precisionScore.add(EvaluatorUtil.precision(
        convert(sample.getSentences()), convert(starts)));
    recallScore.add(EvaluatorUtil.recall(
        convert(sample.getSentences()), convert(starts)));
  }
  
  public void evaluate(Iterator<SentenceSample> samples) {
    while (samples.hasNext())
      evaluateSample(samples.next());
  }
  
  /**
   * Retrieves the arithmetic mean of the precision scores
   * calculated for each evaluated {@link SentenceSample}.
   * 
   * @return the arithmetic mean of all precision scores
   */
  public double getPrecisionScore() {
    return precisionScore.mean();
  }
  
  /**
   * Retrieves the arithmetic mean of the recall score
   * calculated for each evaluated {@link SentenceSample}.
   *
   * @return the arithmetic mean of all recall scores
   */
  public double getRecallScore() {
    return recallScore.mean();
  }
}