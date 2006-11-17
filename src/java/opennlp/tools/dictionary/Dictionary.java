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

package opennlp.tools.dictionary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import opennlp.tools.dictionary.serializer.Attributes;
import opennlp.tools.dictionary.serializer.DictionarySerializer;
import opennlp.tools.dictionary.serializer.Entry;
import opennlp.tools.dictionary.serializer.EntryInserter;
import opennlp.tools.ngram.TokenList;

/**
 * This class is a dictionary.
 * 
 * TODO: it should be possible to specify the capacity
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.2 $, $Date: 2006/11/17 09:37:22 $
 */
public class Dictionary {
  
  private Set mEntrySet = new HashSet();
  
  /**
   * Iitalizes an empty {@link Dictionary}.
   */
  public Dictionary() {
  }

  /**
   * Initalize the {@link Dictionary} from an existing dictionary resource.
   * 
   * @param in
   * @throws IOException
   */
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
  
  /**
   * Removes the given tokens form the current instance.
   * 
   * @param tokens
   */
  public void remove(TokenList tokens) {
    mEntrySet.remove(tokens);
  }
  
  /**
   * Retrives an Interator over all tokens.
   * 
   * @return token-{@link Iterator}
   */
  public Iterator iterator() {
    return mEntrySet.iterator();
  }
  
  /**
   * Retrives the number of tokens in the current instance.
   * 
   * @return number of tokens
   */
  public int size() {
    return mEntrySet.size();
  }
  
  /**
   * Writes the current instance to the given {@link OutputStream}.
   * 
   * @param out
   * @throws IOException
   */
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