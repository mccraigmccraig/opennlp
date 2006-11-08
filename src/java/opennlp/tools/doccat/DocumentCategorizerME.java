///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2006 Calcucare GmbH
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

import opennlp.maxent.ContextGenerator;
import opennlp.maxent.GIS;
import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.TwoPassDataIndexer;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;

/**
*
* @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
* @version $Revision: 1.1 $, $Date: 2006/11/08 18:43:25 $
*/
public class DocumentCategorizerME implements DocumentCategorizer {
  
  private MaxentModel mModel;
  private ContextGenerator mContextGenerator;
  
  public DocumentCategorizerME(MaxentModel model) {
    this(model, new FeatureGenerator[]{new BagOfWordsFeatureGenerator()});
  }
  
  public DocumentCategorizerME(MaxentModel model, 
      FeatureGenerator[] featureGenerators) {
    
    mModel = model;
    mContextGenerator = 
        new DocumentCategorizerContextGenerator(featureGenerators);
  }
  
  /**
   * Categorizes the given text. 
   * 
   * @param text
   */
  public double[] categorize(String text[]) {
    return mModel.eval(mContextGenerator.getContext(text));
  }
  
  public double[] categorize(String documentText) {
    Tokenizer tokenizer = new SimpleTokenizer();
    return categorize(tokenizer.tokenize(documentText));
  }
  
  public String getBestCategory(double[] outcome) {
    return mModel.getBestOutcome(outcome);
  }
  
  public int getIndex(String category) {
    return mModel.getIndex(category);
  }
  
  public String getCategory(int index) {
    return mModel.getOutcome(index);
  }
  
  public int getNumberOfCategories() {
    return mModel.getNumOutcomes();
  }
  
  public String getAllResults(double results[]) {
    return mModel.getAllOutcomes(results);
  }
  
  public static GISModel train(DocumentCategorizerEventStream eventStream) {
    return GIS.trainModel(100, new TwoPassDataIndexer(eventStream, 5),
        true, false);
  }
}