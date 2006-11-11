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
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.2 $, $Date: 2006/11/11 04:13:17 $
 */
public class TokenList {
  
  private Token mTokens[];
  
  public TokenList(Token tokens[]) {
    
    if (tokens == null || tokens.length == 0) {
      throw new IllegalArgumentException();
    }
    
    mTokens = new Token[tokens.length];
    
    System.arraycopy(tokens, 0, mTokens, 0, tokens.length);
  }
  
  public TokenList(String tokens[]) {
    
    if (tokens == null || tokens.length == 0) {
      throw new IllegalArgumentException();
    }
    
    mTokens = new Token[tokens.length];
    
    for (int i = 0; i < tokens.length; i++) {
      mTokens[i] = Token.create(tokens[i]);
    }
  }
  
  public Token getToken(int index) {
    return mTokens[index];
  }
  
  public int size() {
    return mTokens.length;
  }
  
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
  
  /**
   * TODO: implement it
   */
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
  
  // TODO: implement it
  public String toString() {
    return super.toString();
  }
}