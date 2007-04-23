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

package opennlp.tools.ngram;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * TODO: add a method for a subtoken list
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.7 $, $Date: 2007/04/23 03:06:22 $
 */
public class TokenList {
  
  private Token mTokens[];
  
  /**
   * Initializes the current instance.
   * 
   * @param tokens
   */
  public TokenList(Token tokens[]) {
    
    if (tokens == null || tokens.length == 0) {
      throw new IllegalArgumentException();
    }
    
    mTokens = new Token[tokens.length];
    
    System.arraycopy(tokens, 0, mTokens, 0, tokens.length);
  }
  
  public TokenList(String[] tokens) {
    if (tokens == null || tokens.length == 0) {
      throw new IllegalArgumentException();
    }
    mTokens = new Token[tokens.length];
    for (int ti=0;ti<tokens.length;ti++) {
      mTokens[ti]=Token.create(tokens[ti]);
    }
  }
  
  /**
   * Retrives a token from the given index.
   * 
   * @param index
   * 
   * @return token at the given index
   */
  public Token getToken(int index) {
    return mTokens[index];
  }
  
  /**
   * Retrives the number of tokens inside this list.
   *  
   * @return number of tokens
   */
  public int size() {
    return mTokens.length;
  }
  
  /**
   * Retrives an {@link Iterator} over all {@link Token}s.
   * 
   * @return iterator over tokens
   */
  public Iterator iterator() {
    return new Iterator() {
      
      private int mIndex;
      
      public boolean hasNext() {
        return mIndex < size();
      }

      public Object next() {
        
        if (hasNext()) {
          return getToken(mIndex++);
        }
        else {
          throw new NoSuchElementException();
        }
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
      
    };
  }
  
  /**
   * Compares to tokens list and ignores the case of the tokens.
   * 
   * Note: This can cause problems with some locals.
   * 
   * @param tokens
   * 
   * @return true if identicaly with ignore the case otherwise false
   */
  public boolean compareToIgnoreCase(TokenList tokens) {
    
    if (size() == tokens.size()) {
      for (int i = 0; i < size(); i++) {
        
        if (getToken(i).getToken().compareToIgnoreCase(
            tokens.getToken(i).getToken()) != 0) {
          return false;
        } 
      }
    }
    else {
      return false;
    }
    
    return true;
  }
  
  
  public boolean equals(Object obj) {
    
    boolean result;
    
    if (this == obj) {
      result = true;
    }
    else if (obj != null && obj instanceof TokenList) {
      TokenList tokenList = (TokenList) obj;
      
      result = Arrays.equals(mTokens, tokenList.mTokens);
    }
    else {
      result = false;
    }
    
    return result;
  }
  
  public int hashCode() {
    int numBitsRegular = 32 / size();
    int numExtra = 32 % size();
    int maskExtra = 0xFFFFFFFF >>> (32 - numBitsRegular + 1);
    int maskRegular = 0xFFFFFFFF >>> 32 - numBitsRegular;
    int code = 0x000000000;
    int leftMostBit = 0;

    for (int wi = 0; wi < size(); wi++) {
      int word;
      int mask;
      int numBits;
      if (wi < numExtra) {
        mask = maskExtra;
        numBits = numBitsRegular + 1;
      } else {
        mask = maskRegular;
        numBits = numBitsRegular;
      }
      word = getToken(wi).hashCode() & mask; // mask off top bits
      word <<= 32 - leftMostBit - numBits; // move to correct position
      leftMostBit += numBits; // set for next interation
      code |= word;
    }
    
    return code;
  }
  
  public String toString() {
    StringBuffer string = new StringBuffer();
    
    string.append('[');
    
    for (int i = 0; i < size(); i++) {
      Token token = getToken(i);
      string.append(token.getToken());
      
      if (i < size() - 1) {
        string.append(',');
      }
    }
    
    string.append(']');
    
    return string.toString();
  }
}