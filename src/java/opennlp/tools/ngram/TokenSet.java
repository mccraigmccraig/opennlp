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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The token cache is based on a {@link WeakHashMap}. 
 * 
 * It uses weak references to allow garbage collection of tokens which are 
 * not used anymore.
 */
public final class TokenSet {
  
  private final class StatisticLogger implements Runnable {
    
    private static final int INTERVAL = 5000;
    public void run() {
      
      int lastSize = -1;
      
      while (true) {
        try {
          Thread.sleep(INTERVAL); 
        } catch (InterruptedException e) {
          // quit statistic logger thread
          return;
        }
        
        synchronized (TokenSet.this) {
          // log only if it was changed
          int currentSize = mTokenTable.size();
          if (lastSize != currentSize) {
            sLogger.finest("Size: " + currentSize);
            lastSize = currentSize;
          }
        }
      }
    }
  }

  private static Logger sLogger = Logger.getLogger(TokenSet.class.getName());
  
  private static TokenSet sInstance;
  
  private Map mTokenTable = new WeakHashMap();
  
  private TokenSet() {
    if (sLogger.isLoggable(Level.FINEST)) {
      Thread statisticLogger = 
          new Thread(new StatisticLogger(), "TokenSet Statistics Logger");

      statisticLogger.setDaemon(true);
      
      statisticLogger.start();
    }
  }
  
  synchronized Token insert(Token token) {
    WeakReference weakCachedToken = 
      (WeakReference) mTokenTable.get(token.getToken());
    
    // Note: cachedToken == null is possible even if weakCachedToken != null,
    // then the referent was concurrently collected
    Token cachedToken = (Token) 
        (weakCachedToken != null ? weakCachedToken.get() : null );
    
    if (cachedToken != null) {
      return cachedToken;
    }
    else {
      mTokenTable.put(token.getToken(), new WeakReference(token));
      return token;
    }
  }
  
  /**
   * Retrieves the one and only instance of the {@link TokenSet}.
   * 
   * Note: Instance is created on the first call to this method.
   * 
   * @return the single {@link TokenSet} instance.
   */
  public static synchronized TokenSet getInstance() {
    if (sInstance == null) {
      sInstance = new TokenSet();
    }
    
    return sInstance;
  }
}