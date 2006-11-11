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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class is a lookup dictionary.
 * 
 * TODO: it should be possible to specify the capacity
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.2 $, $Date: 2006/11/11 04:13:17 $
 */
public class MultiTokenDictionary {
  
  private Map mEntryMap = new HashMap();
  
  /**
   * Adds the tokens to the dicitionary as one new entry. 
   * If the tokens already exists its {@link Attributes} are replaced
   * with an empty {@link Attributes} object.
   * 
   * @param tokens the new entry
   */
  public void put(TokenList tokens) {
    put(tokens, new Attributes());
  }
  
  /**
   * Adds the tokens to the dicitionary combinded with {@link Attributes} 
   * as one new entry. If the tokens already exists the {@link Attributes}
   * are updated.
   * 
   * @param tokens the new entry
   * @param attributes the attributes of the entry
   */
  public void put(TokenList tokens, Attributes attributes) {
    mEntryMap.put(tokens, attributes);
  }
  
  /**
   * Checks if this dictionary has the given entry.
   * 
   * @param tokens
   * 
   * @return true if it contains the entry otherwise false
   */
  public boolean contains(TokenList tokens) {
    return mEntryMap.containsKey(tokens);
  }
  
  public Attributes get(TokenList tokens) {
    return (Attributes) mEntryMap.get(tokens);
  }
  
  public void remove(TokenList tokens) {
    mEntryMap.remove(tokens);
  }
  
  public Iterator iterator() {
    return mEntryMap.keySet().iterator();
  }
  
  public int size() {
    return mEntryMap.size();
  }
  
  // TODO: add tokens to the string
  public String toString() {
    return "Size: " + size();
  }
}