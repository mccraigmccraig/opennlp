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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

/**
  * 
  * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
  * @version $Revision: 1.1 $, $Date: 2006/11/15 17:47:40 $
  */
public class DictionaryTest extends TestCase  {
  
  public void testDictionary() throws IOException {
    Dictionary reference = new Dictionary();
    
    Token a1 = Token.create("a1");
    Token a2 = Token.create("a2");
    Token a3 = Token.create("a3");
    Token a5 = Token.create("a5");
    
    reference.put(new TokenList(new Token[]{a1, a2, a3, a5,}));
    
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    reference.serialize(out);
  
    Dictionary recreated = new Dictionary(
        new ByteArrayInputStream(out.toByteArray()));
    
    assertTrue(reference.equals(recreated));
  }
}
