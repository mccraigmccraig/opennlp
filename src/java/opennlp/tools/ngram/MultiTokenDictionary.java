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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * This class is an orderd lookup dictionary.
 * TODO: it should be possible to specify the capacity
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.1 $, $Date: 2006/11/09 22:32:19 $
 */
public class MultiTokenDictionary {
  
  /**
   * The {@link Entry} contains the entry itself and its {@link Attributes}.
   * 
   */
  private static class Entry {
    
    private TokenList mTokens;
    private Attributes mAttributes;

    /**
     * Creates a new {@link Entry} instance.
     * 
     * @param entry the entry
     * @param attributes the attributes of the entry, or if none null
     */
    private Entry(TokenList tokens, Attributes attributes) {
      
      if (tokens == null) {
        throw new IllegalArgumentException();
      }
      
      mTokens = tokens;
      
      mAttributes = attributes;
    }
    
    /**
     * Retrives the entry.
     * 
     * @return the entry
     */
    private TokenList getEntry() {
      return mTokens;
    }
    
    /**
     * Retrives the value of a given attribute key.
     * 
     * @return attribute value, if not set null is returned
     */
    private Attributes getAttribute() {
      return mAttributes;
    }
    
  }
  
  private String mName;
  
  private Map mEntryMap = new HashMap();
  
  private List mEntryList = new ArrayList();
  
  /**
   * Creates a new {@link MultiTokenDictionary} object.
   * 
   * @param name the name of the dictionary
   */
  public MultiTokenDictionary(String name) {
    
    if (name == null) {
      throw new IllegalArgumentException("name must not be null!");
    }
    
    mName = name;
  }
  
  /**
   * Retrives the name of the current dictionary instance.
   * 
   * @return the name of the dictionary
   */
  public String getName() {
    return mName;
  }
  
  /**
   * Adds the tokens to the dicitionary as one new entry. 
   * If the tokens already exists nothing happens. 
   * 
   * The new entry is added to the end of the dicitionary.
   * 
   * Note: Make sure that the entry was not previously added 
   * to the dictionary, otherwise an {@link IllegalArgumentException} will be
   * thrown.
   * 
   * @param tokens the new entry
   */
  public void add(TokenList tokens) {
    add(tokens, null);
  }
  
  /**
   * Adds the tokens to the dicitionary combinded with {@link Attributes} 
   * as one new entry. If the tokens already exists nothing happens.
   * 
   * The new entry is added to the end of the dicitionary.
   * 
   * Note: Make sure that the entry was not previously added 
   * to the dictionary, otherwise an {@link IllegalArgumentException} will be
   * thrown.
   * 
   * @param tokens the new entry
   * @param attributes the attributes of the entry
   */
  public void add(TokenList tokens, Attributes attributes) {
    add(tokens, attributes, mEntryList.size());
  }
  
  /**
   * Adds the tokens to the dicitionary combinded with {@link Attributes} 
   * as one new entry. 
   * If the tokens already exists nothing happens.
   * The new entry is inserted at the given index.
   * 
   * Note: Make sure that the entry was not previously added 
   * to the dictionary, otherwise an {@link IllegalArgumentException} will be
   * thrown.
   * 
   * @param tokens the new entry
   * @param attributes the attributes of the entry
   * @param index position where the entry will be inserted
   * 
   * @throws IllegalArgumentException is thrown if the entry exist already.
   */
  public void add(TokenList tokens, Attributes attributes, int index) {
    
    Entry newEntry = new Entry(tokens, attributes);
    
    Attributes oldAttributes = (Attributes) mEntryMap.put(
        tokens, newEntry);

    if (oldAttributes != null) {
      // restore old value
      mEntryMap.put(tokens, oldAttributes);
      
      throw new IllegalArgumentException();
    }
    
    mEntryList.add(index, newEntry);
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
    return ((Entry) mEntryMap.get(tokens)).getAttribute();
  }
  
  public String get(TokenList tokens, String key) {
    return get(tokens).getValue(key);
  }
  
  public Attributes get(int index) {
    return ((Entry) mEntryList.get(index)).getAttribute();
  }
  
  public TokenList getToken(int index) {
    return ((Entry) mEntryList.get(index)).getEntry();
  }
  
  public void remove(TokenList tokens) {
    Entry removedEntry = (Entry) mEntryMap.remove(tokens);
    
    mEntryList.remove(removedEntry);
  }
  
  public void remove(int index) {
    Entry removedEntry = (Entry) mEntryList.remove(index);
    
    mEntryMap.remove(removedEntry.getEntry());
  }
  
  /**
   * Iterates over all entires, this is only used internal.
   * @return
   */
  Iterator entryIterator() {
    return mEntryMap.values().iterator();
  }

  public int size() {
    return mEntryList.size();
  }
  
  // TODO: add tokens to the string ???
  public String toString() {
    return "Name: " + getName() + " size: " +size();
  }
}