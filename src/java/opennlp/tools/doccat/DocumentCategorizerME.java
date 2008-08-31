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

import java.io.IOException;

import opennlp.maxent.GIS;
import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.TwoPassDataIndexer;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;

/**
 * Maxent implementation of {@link DocumentCategorizer}.
 */
public class DocumentCategorizerME implements DocumentCategorizer {
  
  MaxentModel mModel;
  private DocumentCategorizerContextGenerator mContextGenerator;
  
  /**
   * Initializes the current instance with the given {@link MaxentModel}.
   * 
   * @param model
   */
  public DocumentCategorizerME(MaxentModel model) {
    this(model, new FeatureGenerator[]{new BagOfWordsFeatureGenerator()});
  }
  
  /**
   * Initializes the current instance with a the given {@link MaxentModel}
   * and {@link FeatureGenerator}s.
   * 
   * @param model
   * @param featureGenerators
   */
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
  
  /**
   * Trains a new model for the {@link DocumentCategorizerME}.
   * 
   * @param eventStream
   * 
   * @return the new model
   */
  public static GISModel train(DocumentCategorizerEventStream eventStream) throws IOException {
    return GIS.trainModel(100, new TwoPassDataIndexer(eventStream, 5));
  }
}