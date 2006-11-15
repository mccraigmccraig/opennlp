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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class is a dictionary.
 * 
 * TODO: it should be possible to specify the capacity
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.11 $, $Date: 2006/11/15 17:35:35 $
 */
public class Dictionary {
  
  private Set mEntrySet = new HashSet();
  
  public Dictionary() {
  }

  public Dictionary(InputStream in) throws IOException {
    DictionarySerializer.create(in, new EntryInserter() 
        {
          public void insert(Entry entry) {
            put(entry.getTokens());
          }
        });
  }
  
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
  
  public void serialize(OutputStream out) throws IOException {
    
    Iterator entryIterator = new Iterator() 
      {
        private Iterator mDictionaryIterator = Dictionary.this.iterator();
        
        public boolean hasNext() {
          return mDictionaryIterator.hasNext();
        }

        public Object next() {
          
          TokenList tokens = (TokenList) mDictionaryIterator.next();
          
          return new Entry(tokens, new Attributes());
        }

        public void remove() {
          throw new UnsupportedOperationException();
        }
      
      };
      
    DictionarySerializer.serialize(out, entryIterator);
  }
  
  public boolean equals(Object obj) {
    
    boolean result;
    
    if (obj == this) {
      result = true;
    }
    else if (obj != null && obj instanceof Dictionary) {
      Dictionary dictionary  = (Dictionary) obj;
      
      result = mEntrySet.equals(dictionary.mEntrySet);
    }
    else {
      result = false;
    }
    
    return result;
  }
  
  public int hashCode() {
    return mEntrySet.hashCode();
  }
  
  public String toString() {
    return mEntrySet.toString();
  }
}