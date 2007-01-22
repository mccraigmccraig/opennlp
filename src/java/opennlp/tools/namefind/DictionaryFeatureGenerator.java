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

package opennlp.tools.namefind;

import java.util.List;

import opennlp.tools.dictionary.Dictionary;

/**
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.1 $, $Date: 2007/01/22 06:51:14 $
 */
public class DictionaryFeatureGenerator implements FeatureGenerator {
  
  private DictionaryNameFinder mDictionary;
  
  private String mCurrentSentence[];
  
  private String mCurrentNames[];
  
  public DictionaryFeatureGenerator(Dictionary dictionary) {
    mDictionary = new DictionaryNameFinder(dictionary);
  }
  
  public void createFeatures(List features, String[] tokens, int index) {
    // cache results sentence wise
    if (mCurrentSentence != tokens) {
      mCurrentNames = mDictionary.find(tokens, null);
    }
    
    if (mCurrentNames[index].equals(NameFinderME.START) || 
        mCurrentNames[index].equals(NameFinderME.CONTINUE)) {
      features.add("w=dic");
    }
  }
}