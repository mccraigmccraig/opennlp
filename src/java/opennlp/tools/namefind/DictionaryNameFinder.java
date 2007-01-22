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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.ngram.Token;
import opennlp.tools.ngram.TokenList;
import opennlp.tools.util.Span;

/**
 * This is a dictionary based name finder, it scans text
 * for names inside a dictionary.
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.3 $, $Date: 2007/01/22 06:50:59 $
 */
public class DictionaryNameFinder implements NameFinder {

  /**
   * This class indexes an dictionary, each token is one element in
   * the index.
   */
  private class DictionaryIndex {
    
    private Set mTokens = new HashSet();
    
    private DictionaryIndex(Dictionary dictionary) {
      
      for (Iterator it = dictionary.iterator(); it.hasNext();) {
      
        TokenList tokens = (TokenList) it.next();
        
        for (int i = 0; i < tokens.size(); i++) {
          mTokens.add(tokens.getToken(i));
        }
      }
    }
    
    private boolean contains(Token token) {
      return mTokens.contains(token);
    }
  }

  private Dictionary mDictionary;
  
  private DictionaryIndex mMetaDictionary;
  
  /**
   * Initializes the current instance.
   * 
   * @param dictionary
   */
  public DictionaryNameFinder(Dictionary dictionary) {
    mDictionary = dictionary;
    mMetaDictionary = new DictionaryIndex(dictionary);
  }
  
  public List find(List toks, Map prevTags) {
    
    Span tokenSpans[] = new Span[toks.size()];
    
    StringBuffer sentence = new StringBuffer();
    
    int index = 0;
    for (Iterator it = toks.iterator(); it.hasNext();) {
      String token = (String) it.next();
      
      int startIndex = sentence.length();
      
      sentence.append(token);
      
      int endIndex = sentence.length();
      
      tokenSpans[index] = new Span(startIndex, endIndex);
      
      if (index < toks.size() - 1) {
        sentence.append(' ');
      }
      
      index++;
    }
    
    Span names[] = find(sentence.toString(), tokenSpans, null);
    
    List tokens = new LinkedList();
    
    for (int i = 0; i < names.length; i++) {
      tokens.add(names[i].getCoveredText(sentence.toString()));
    }
    
    return Collections.unmodifiableList(tokens);
  }

  public String[] find(Object[] toks, Map prevTags) {
    
    List tokenList = new LinkedList();
    
    for (int i = 0; i < toks.length; i++) {
      tokenList.add(toks[i]);
    }
    
    List nameList = find(tokenList, prevTags);
    
    return (String[]) nameList.toArray(new String[nameList.size()]);
  }

  public List find(String sentence, List toks, Map prevMap) {
    
    Span tokenSpan[] = (Span[]) toks.toArray(new Span[toks.size()]);
    
    Span names[] = find(sentence, tokenSpan, prevMap);
    
    List nameList = new LinkedList();
    
    for (int i = 0; i < names.length; i++) {
      nameList.add(names[i]);
    }
    
    return Collections.unmodifiableList(nameList);
  }

  public Span[] find(String sentence, Span[] toks, Map prevMap) {
    
    List foundNames = new LinkedList();
    
    for (int startToken = 0; startToken < toks.length; startToken++) {
      
      Span foundName = null;
      
      Token  tokens[] = new Token[]{};
      
      for (int endToken = startToken; endToken < toks.length; endToken++) {
        
        Token token = Token.create(
            sentence.substring(toks[endToken].getStart(), 
            toks[endToken].getEnd()));
        
        // TODO: improve performence here
        Token newTokens[] = new Token[tokens.length + 1];
        System.arraycopy(tokens, 0, newTokens, 0, tokens.length);
        newTokens[newTokens.length - 1] = token;
        tokens = newTokens;
        
        if (mMetaDictionary.contains(token)) {
          
          TokenList tokenList = new TokenList(tokens);
          
          if (mDictionary.contains(tokenList)) {
            foundName = new Span(toks[startToken].getStart(), 
                toks[endToken].getEnd());
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