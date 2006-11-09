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


/**
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.1 $, $Date: 2006/11/09 15:34:16 $
 */
public class TokenList {
  
  private Token mTokens[];
  
  public TokenList(Token token) {
    
    if (token == null) {
      throw new IllegalArgumentException();
    }
    
    mTokens = new Token[] {token};
  }
  
  public TokenList(Token tokens[]) {
    
    if (tokens == null || tokens.length == 1) {
      throw new IllegalArgumentException();
    }
    
    mTokens = new Token[tokens.length];
    
    System.arraycopy(tokens, 0, mTokens, 0, tokens.length);
  }
  
  public TokenList(String tokens[]) {
    mTokens = new Token[tokens.length];
    
    for (int i = 0; i < tokens.length; i++) {
      mTokens[i] = Token.parse(tokens[i]);
    }
  }
  
  public Token getToken(int index) {
    return mTokens[index];
  }
  
  public int numberOfTokens() {
    return mTokens.length;
  }
  
  Iterator iterator() {
    return null;
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
    return 0; 
  }
  
  public String toString() {
    return super.toString();
  }
}