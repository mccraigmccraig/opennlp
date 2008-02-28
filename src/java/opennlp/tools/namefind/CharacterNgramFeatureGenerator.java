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

import java.util.Iterator;
import java.util.List;

import opennlp.tools.ngram.NGramModel;
import opennlp.tools.ngram.TokenList;

/**
 * The {@link CharacterNgramFeatureGenerator} uses character ngrams to 
 * generate features about each token. 
 * The minimum and maximum length can be specified.
 */
public class CharacterNgramFeatureGenerator extends FeatureGeneratorAdapter {

  private final int minLength;
  private final int maxLength;

  public CharacterNgramFeatureGenerator(int minLength, int maxLength) {
    this.minLength = minLength;
    this.maxLength = maxLength;
  }
  
  /**
   * Initializes the current instance with min 2 length and max 5 length of ngrams.
   */
  public CharacterNgramFeatureGenerator() {
    this(2, 5);
  }
  
  public void createFeatures(List features, String[] tokens, int index, String[] preds) {

    NGramModel model = new NGramModel();
    model.add(tokens[index], minLength, maxLength);

    for (Iterator it = model.iterator(); it.hasNext();) {

      TokenList tokenList = (TokenList) it.next();

      if (tokenList.size() > 0) {
        features.add("ng=" + tokenList.getToken(0).getToken().toLowerCase());
      }
    }
  }
}