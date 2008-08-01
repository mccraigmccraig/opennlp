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

package opennlp.tools.postag;

import java.util.Iterator;

import opennlp.tools.util.Mean;

/**
 * The {@link POSEvaluator} measures the performance of
 * the given {@link POSTagger} with the provided reference
 * {@link POSSamplee}s.
 */
public class POSEvaluator {

  private POSTagger tagger;
  
  private Mean wordAccuracy = new Mean();
  
  /**
   * Initializes the current instance.
   * 
   * @param tagger
   */
  POSEvaluator(POSTagger tagger) {
    this.tagger = tagger;
  }
  
  /**
   * Evaluates the given reference {@link POSSample} object.
   * 
   * This is done by tagging the sentence from the reference
   * {@link POSSample} with the {@link POSTagger}. The
   * tags are then used to update the word accuracy score.
   * 
   * @param reference the reference {@link POSSample}.
   */
  public void evaluateSample(POSSample reference) {
    
    String predictedTags[] = tagger.tag(reference.getSentence());
    
    for (int i = 0; i < reference.getTags().length; i++) {
      if (reference.getTags()[i].equals(predictedTags[i])) {
        wordAccuracy.add(1);
      }
      else {
        wordAccuracy.add(0);
      }
    }
  }
  
  /**
   * Reads all {@link POSSample} objects from the stream
   * and evaluates each {@link POSSample} object with 
   * {@link #evaluateSample(POSSample)} method.
   * 
   * @param samples the stream of reference {@link POSSample} which
   * should be evaluated.
   */
  public void evaluate(Iterator<POSSample> samples) {
    while (samples.hasNext()) {
      evaluateSample(samples.next());
    }
  }
  
  /**
   * Retrieves the word accuracy. 
   * 
   * This is defined as:
   * word accuracy = correctly detected tags / total words
   * 
   * @return the word accuracy
   */
  public double getWordAccuracy() {
    return wordAccuracy.mean();
  }
  
  /**
   * Represents this objects as human readable {@link String}.
   */
  public String toString() {
    return "Accuracy:" + wordAccuracy.mean() + 
        " Number of Samples: " + wordAccuracy.count();
  }
}