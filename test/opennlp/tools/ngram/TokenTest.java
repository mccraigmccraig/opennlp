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

import opennlp.tools.ngram.Token;
import opennlp.tools.ngram.TokenTable;

import junit.framework.TestCase;

/**
 * Tests for the {@link Token} class.
 */
public class TokenTest extends TestCase {

  /**
   * Tests if the {@link TokenTable} really allows unreferenced 
   * objects to be collected.
   * 
   * Note: Maybe this test can fail.
   */
  public void testTokenTableGarbageCollection() {

    Token token = Token.parse("test");
    
    WeakReference weakToken = new WeakReference(token);
    token = null;
    
    System.gc();
    
    assertTrue(weakToken.get() == null);
  }
  
  /**
   * Tests if the {@link TokenTable} lookup really works.
   */
  public void testTokenTableLookup() {
    
    Token token1 = Token.parse("test");
    Token token2 = Token.parse("test");
    
    assertTrue(token1 == token2);
  }
  
  /**
   * Tests that {@link Token#hashCode()} does not throw an exception.
   */
  public void testHashCode() {
    Token token = Token.parse("HashCodeTest");
    token.hashCode();
  }

  /**
   * Tests that {@link Token#toString()} does not throw an exception and
   * the result is not null.
   */
  public void testToString() {
    Token token = Token.parse("HashCodeTest");
    assertTrue(token.toString() != null);
  }

}