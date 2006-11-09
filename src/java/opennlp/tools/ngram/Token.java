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


/**
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.1 $, $Date: 2006/11/09 15:34:16 $
 */
public class Token {

  private String mToken;
  
  /**
   * Initializes a new Token object.
   * 
   * Note: 
   * Tokens should only be created with {@link #parse(String)}.
   * 
   * @param token
   */
  private Token(String token) {
    mToken = token;
  }
  
  public int hashCode() {
    return mToken.hashCode();
  }
  
  public boolean equals(Object obj) {
    boolean result;
    
    if (this == obj) {
      result = true;
    }
    else if (obj != null && obj instanceof Token) {
      Token token = (Token) obj;
      
      result = mToken.equals(token.mToken);
    }
    else {
      result = false;
    }
    
    return result;
  }
  
  /**
   * Represents the token as human-readable string.
   */
  public String toString() {
    return mToken;
  }
  
  /**
   * Creates a new Token instance.
   * 
   * TODO: use a TokenCache or NameTable to store already created tokens
   * 
   * @param token
   * 
   * @return
   */
  public static Token parse(String token) {
    
    if (token == null) {
      throw new IllegalArgumentException("token parameter must not be null!");
    }
    
    return TokenTable.getInstance().insert(new Token(token));
  }
}