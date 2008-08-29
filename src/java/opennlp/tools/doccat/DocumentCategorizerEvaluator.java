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

package opennlp.tools.doccat;

import java.util.Iterator;

import opennlp.tools.postag.POSSample;
import opennlp.tools.tokenize.TokenSample;
import opennlp.tools.util.Mean;

/**
 * The {@link DocumentCategorizerEvaluator} measures the performance of
 * the given {@link DocumentCategorizer} with the provided reference
 * {@link DocumentSample}s.
 * 
 * @see DocumentCategorizer
 * @see DocumentSample
 */
public class DocumentCategorizerEvaluator {

  private DocumentCategorizer categorizer;
  
  private Mean accuracy;
  
  /**
   * Initializes the current instance.
   * 
   * @param categorizer
   */
  public DocumentCategorizerEvaluator(DocumentCategorizer categorizer) {
    this.categorizer = categorizer;
  }
  
  /**
   * Evaluates the given reference {@link DocumentSample} object.
   * 
   * This is done by categorizing the document from the provided
   * {@link DocumentSample}. The detected category is then used
   * to calculate and update the score.
   * 
   * @param reference the reference {@link TokenSample}.
   */
  public void evaluteSample(DocumentSample sample) {
    
    String document[] = sample.getText();
    
    double probs[] = categorizer.categorize(document);
    
    String cat = categorizer.getBestCategory(probs);
    
    if (sample.getCategory().equals(cat)) {
      accuracy.add(1);
    }
    else {
      accuracy.add(0);
    }
  }
  
  /**
   * Reads all {@link DocumentSample} objects from the stream
   * and evaluates each {@link DocumentSample} object with 
   * {@link #evaluateSample(POSSample)} method.
   * 
   * @param samples the stream of reference {@link POSSample} which
   * should be evaluated.
   */
  public void evaluate(Iterator<DocumentSample> samples) {

    while (samples.hasNext()) {
      evaluteSample(samples.next());
    }
  }
  
  /**
   * Retrieves the accuracy of provided {@link DocumentCategorizer}.
   * 
   * accuracy = correctly categorized documents / total documents
   * 
   * @return the accuracy
   */
  public double getAccuracy() {
    return accuracy.mean();
  }
  
  /**
   * Represents this objects as human readable {@link String}.
   */
  public String toString() {
    return "Accuracy: " + accuracy.mean() + "\n" +
        "Number of documents: " + accuracy.count();
  }
}