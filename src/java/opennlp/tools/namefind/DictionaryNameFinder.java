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

import java.util.LinkedList;
import java.util.List;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.dictionary.Index;
import opennlp.tools.ngram.Token;
import opennlp.tools.ngram.TokenList;
import opennlp.tools.util.Span;

/**
 * This is a dictionary based name finder, it scans text
 * for names inside a dictionary.
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.8 $, $Date: 2007/06/29 12:11:55 $
 */
public class DictionaryNameFinder implements TokenNameFinder {

  private Dictionary mDictionary;
  
  private Index mMetaDictionary;
  
  /**
   * Initializes the current instance.
   * 
   * @param dictionary
   */
  public DictionaryNameFinder(Dictionary dictionary) {
    mDictionary = dictionary;
    mMetaDictionary = new Index(dictionary.iterator());
  }
  
  public Span[] find(String[] tokenStrings) {
    List foundNames = new LinkedList();
    
    for (int startToken = 0; startToken < tokenStrings.length; startToken++) {
      
      Span foundName = null;
      
      Token  tokens[] = new Token[]{};
      
      for (int endToken = startToken; endToken < tokenStrings.length; endToken++) {
        
        Token token = Token.create(tokenStrings[endToken]);
        
        // TODO: improve performance here
        Token newTokens[] = new Token[tokens.length + 1];
        System.arraycopy(tokens, 0, newTokens, 0, tokens.length);
        newTokens[newTokens.length - 1] = token;
        tokens = newTokens;
        
        if (mMetaDictionary.contains(token)) {
          
          TokenList tokenList = new TokenList(tokens);
          
          if (mDictionary.contains(tokenList)) {
            foundName = new Span(startToken, endToken + 1);
          }
        }
        else {
          break;
        }
      }
      
      if (foundName != null) {
        foundNames.add(foundName);
      }
    }
    
    return (Span[]) foundNames.toArray(new Span[foundNames.size()]);
  }
  
}