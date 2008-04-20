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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import opennlp.tools.dictionary.serializer.Attributes;
import opennlp.tools.dictionary.serializer.DictionarySerializer;
import opennlp.tools.dictionary.serializer.Entry;
import opennlp.tools.dictionary.serializer.EntryInserter;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.StringList;

/**
 * This class is a dictionary.
 */
public class Dictionary {
  
  
  private static class StringListWrapper {
    
    private final StringList stringList;
    private final boolean isCaseSensitive;
    
    private StringListWrapper(StringList stringList, boolean isCaseSensitive) {
      this.stringList = stringList;
      this.isCaseSensitive = isCaseSensitive;
    }
    
    private StringList getStringList() {
      return stringList;
    }
    
    public boolean equals(Object obj) {
     
      boolean result;
      
      if (obj == this) {
        result = true;
      }
      else if (obj instanceof StringListWrapper) {
        StringListWrapper other = (StringListWrapper) obj;
        
        if (isCaseSensitive) {
          result = this.stringList.equals(other.getStringList());
        }
        else {
          result = this.stringList.compareToIgnoreCase(other.getStringList());
        }
       }
      else {
        result = false;
      }
      
      return result;
    }
    
    public int hashCode() {
      // if lookup is too slow optimize this
      return this.stringList.toString().toLowerCase().hashCode();
    }
    
    public String toString() {
      return this.stringList.toString();
    }
  }
  
  private Set<StringListWrapper> entrySet = new HashSet<StringListWrapper>();
  private boolean caseSensitive;
  
  /**
   * Initializes an empty {@link Dictionary}.
   */
  public Dictionary() {
    this(false);
  }
  
  public Dictionary(boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
  }
  
  /**
   * Initializes the {@link Dictionary} from an existing dictionary resource.
   * 
   * @param in
   * @throws IOException
   * @throws InvalidFormatException 
   */
  public Dictionary(InputStream in) throws IOException, InvalidFormatException {
    this(in,false);
  }
  
  public Dictionary(InputStream in, boolean caseSensitive) throws IOException, InvalidFormatException {
    this.caseSensitive = caseSensitive;
    DictionarySerializer.create(in, new EntryInserter() 
    {
      public void insert(Entry entry) {
        put(entry.getTokens());
      }
    });
  }
  
  /**
   * Adds the tokens to the dictionary as one new entry. 
   * 
   * @param tokens the new entry
   */
  public void put(StringList tokens) {
      entrySet.add(new StringListWrapper(tokens, caseSensitive));
  }
  
  /**
   * Checks if this dictionary has the given entry.
   * 
   * @param tokens
   * 
   * @return true if it contains the entry otherwise false
   */
  public boolean contains(StringList tokens) {
      return entrySet.contains(new StringListWrapper(tokens, caseSensitive));      
  }
  
  /**
   * Removes the given tokens form the current instance.
   * 
   * @param tokens
   */
  public void remove(StringList tokens) {
      entrySet.remove(new StringListWrapper(tokens, caseSensitive));
  }
  
  /**
   * Retrieves an Iterator over all tokens.
   * 
   * @return token-{@link Iterator}
   */
  public Iterator<StringList> iterator() {
    final Iterator<StringListWrapper> entries = entrySet.iterator();
    
    return new Iterator<StringList>() {

      public boolean hasNext() {
        return entries.hasNext();
      }

      public StringList next() {
        return entries.next().getStringList();
      }

      public void remove() {
        entries.remove();
      }};
  }
  
  /**
   * Retrieves the number of tokens in the current instance.
   * 
   * @return number of tokens
   */
  public int size() {
    return entrySet.size();
  }
  
  /**
   * Writes the current instance to the given {@link OutputStream}.
   * 
   * @param out
   * @throws IOException
   */
  public void serialize(OutputStream out) throws IOException {
    
    Iterator<Entry> entryIterator = new Iterator<Entry>() 
      {
        private Iterator<StringList> dictionaryIterator = Dictionary.this.iterator();
        
        public boolean hasNext() {
          return dictionaryIterator.hasNext();
        }

        public Entry next() {
          
          StringList tokens = (StringList)
              dictionaryIterator.next();
          
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
      
      result = entrySet.equals(dictionary.entrySet);
    }
    else {
      result = false;
    }
    
    return result;
  }
  
  public int hashCode() {
    return entrySet.hashCode();
  }
  
  public String toString() {
    return entrySet.toString();
  }
  
  /**
   * Reads a dictionary which has one entry per line. The tokens inside an
   * entry are whitespace delimited. 
   * 
   * @param in
   * 
   * @return the parsed dictionary
   * 
   * @throws IOException
   */
  public static Dictionary parseOneEntryPerLine(Reader in) throws IOException {
    BufferedReader lineReader = new BufferedReader(in);
    
    Dictionary dictionary = new Dictionary();
    
    String line;
    
    while ((line = lineReader.readLine()) != null) {
      StringTokenizer whiteSpaceTokenizer = new StringTokenizer(line, " ");
      
      String tokens[] = new String[whiteSpaceTokenizer.countTokens()];
      
      if (tokens.length > 0) {
        int tokenIndex = 0;
        while (whiteSpaceTokenizer.hasMoreTokens()) {
          tokens[tokenIndex++] = whiteSpaceTokenizer.nextToken();
        }
        
        dictionary.put(new StringList(tokens));
      }
    }
    
    return dictionary;
  }
}