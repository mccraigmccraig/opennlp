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

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * The token cache is based on a {@link WeakHashMap}. 
 * 
 * It uses weak references to allow garbage collection of tokens which are 
 * not used anymore.
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.1 $, $Date: 2006/11/11 04:13:17 $
 */
class TokenSet {
  
  private static TokenSet sInstance;
  
  private Map mTokenTable = new WeakHashMap();
  
  private TokenSet() {
  }
  
  Token insert(Token token) {
    WeakReference weakCachedToken = (WeakReference) mTokenTable.get(token);
    
    // Note: cachedToken == null is possible even if weakCachedToken != null,
    // then the referent was concurrently collected
    Token cachedToken = (Token) 
        (weakCachedToken != null ? weakCachedToken.get() : null );
    
    if (cachedToken != null) {
      return cachedToken;
    }
    else {
      mTokenTable.put(token, new WeakReference(token));
      return token;
    }
  }
  
  static TokenSet getInstance() {
    if (sInstance == null) {
      sInstance = new TokenSet();
    }
    
    return sInstance;
  }
}