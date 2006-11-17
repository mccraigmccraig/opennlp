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
 * @version $Revision: 1.5 $, $Date: 2006/11/17 12:33:54 $
 */
public class Token {

  private String mToken;
  
  /**
   * Initializes a new Token object.
   * 
   * Note: 
   * Tokens should only be created with {@link #create(String)}.
   * 
   * @param token
   */
  private Token(String token) {

    if (token == null || token.length() == 0) {
      throw new IllegalArgumentException("token parameter must not be null!");
    }

    mToken = token;
  }
  
  /**
   * Retrives the token string.
   * 
   * Note: Do not use {@link #toString()} for token text retrival.
   * 
   * @return the token text
   */
  public String getToken() {
    return mToken;
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
   * @return the new token
   */
  public static Token create(String token) {
    return TokenSet.getInstance().insert(new Token(token));
  }
  
  /**
   * Creates a token array from the provdided string array.
   * 
   * @param tokenStrings
   * 
   * @return the strings as tokens
   */
  public static TokenList create(String tokenStrings[]) {
    
    if (tokenStrings == null) {
      throw new IllegalArgumentException();
    }
    
    Token tokens[] = new Token[tokenStrings.length];
    
    for (int i = 0; i < tokenStrings.length; i++) {
      tokens[i] = Token.create(tokenStrings[i]);
    }
    
    return new TokenList(tokens);
  }
}