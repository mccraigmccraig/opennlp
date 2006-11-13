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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class is a dictionary.
 * 
 * TODO: it should be possible to specify the capacity
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.10 $, $Date: 2006/11/13 21:10:57 $
 */
public class Dictionary {
  
  private Set mEntrySet = new HashSet();
  
  /**
   * Adds the tokens to the dicitionary as one new entry. 
   * 
   * @param tokens the new entry
   */
  public void put(TokenList tokens) {
    mEntrySet.add(tokens);
  }
  
  /**
   * Checks if this dictionary has the given entry.
   * 
   * @param tokens
   * 
   * @return true if it contains the entry otherwise false
   */
  public boolean contains(TokenList tokens) {
    return mEntrySet.contains(tokens);
  }
  
  public void remove(TokenList tokens) {
    mEntrySet.remove(tokens);
  }
  
  public Iterator iterator() {
    return mEntrySet.iterator();
  }
  
  public int size() {
    return mEntrySet.size();
  }
  
  public int hashCode() {
    return mEntrySet.hashCode();
  }
  
  // TODO: add tokens to the string
  public String toString() {
    return "Size: " + size();
  }
}