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

import java.util.List;

import opennlp.tools.util.Span;

/**
 * Generates features if the tokens are contained in the dictionary. 
 */
public class DictionaryFeatureGenerator extends FeatureGeneratorAdaptor {

  private TokenNameFinder mFinder;

  private String mCurrentSentence[];

  private Span mCurrentNames[];

  /**
   * Initializes the current instance. Pass in an instance of 
   * the {@link DictionaryNameFinder}.
   * 
   * @param dictionary
   */
  public DictionaryFeatureGenerator(TokenNameFinder finder) {
    mFinder = finder;
  }

  public void createFeatures(List features, String[] tokens, int index, String[] preds) {
    // cache results sentence
    if (mCurrentSentence != tokens) {
      mCurrentSentence = tokens;
      mCurrentNames = mFinder.find(tokens);
    }

    // iterate over names and check if a span is contained
    for (int i = 0; i < mCurrentNames.length; i++) {
      if (mCurrentNames[i].contains(index)) {
        // found a span for the current token
        features.add("w=dic");
        features.add("w=dic=" + tokens[index]);
        
        // TODO: consider generation start and continuation features
        
        break;
      }
    }
  }
}