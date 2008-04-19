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
import opennlp.tools.ngram.Token;
import opennlp.tools.ngram.TokenList;
import opennlp.tools.util.InvalidFormatException;

/**
 * This class is a dictionary.
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.7 $, $Date: 2008/04/19 12:26:46 $
 */
public class Dictionary {
  
  
  private static class IgnoreCaseTokenList {
    
    private TokenList mTokenList;
    
    private IgnoreCaseTokenList(TokenList tokenList) {
      mTokenList = tokenList;
    }
    
    private TokenList getTokenList() {
      return mTokenList;
    }
    
    public boolean equals(Object obj) {
     
      boolean result;
      
      if (obj == this) {
        result = true;
      }
      else if (obj instanceof IgnoreCaseTokenList) {
        IgnoreCaseTokenList other = (IgnoreCaseTokenList) obj;
        
        result = mTokenList.compareToIgnoreCase(other.getTokenList());
      }
      else {
        result = false;
      }
      
      return result;
    }
    
    public int hashCode() {
      // if lookup is too slow optimize this
      return mTokenList.toString().toLowerCase().hashCode();
    }
    
    public String toString() {
      return mTokenList.toString();
    }
  }
  
  private Set mEntrySet = new HashSet();
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
  public void put(TokenList tokens) {
    if (caseSensitive) {
      mEntrySet.add(tokens);
    }
    else {
      mEntrySet.add(new IgnoreCaseTokenList(tokens));
    }
  }
  
  /**
   * Checks if this dictionary has the given entry.
   * 
   * @param tokens
   * 
   * @return true if it contains the entry otherwise false
   */
  public boolean contains(TokenList tokens) {
    if (caseSensitive) {
      return mEntrySet.contains(tokens);      
    }
    else {
      return mEntrySet.contains(new IgnoreCaseTokenList(tokens));      
    }
  }
  
  /**
   * Removes the given tokens form the current instance.
   * 
   * @param tokens
   */
  public void remove(TokenList tokens) {
    if (caseSensitive) {
      mEntrySet.remove(tokens);
    }
    else {
      mEntrySet.remove(new IgnoreCaseTokenList(tokens));
    }
  }
  
  /**
   * Retrieves an Iterator over all tokens.
   * 
   * @return token-{@link Iterator}
   */
  public Iterator iterator() {
    final Iterator entries = mEntrySet.iterator();
    
    return new Iterator() {

      public boolean hasNext() {
        return entries.hasNext();
      }

      public Object next() {
        Object o = entries.next();
        if (o instanceof IgnoreCaseTokenList) {
          return ((IgnoreCaseTokenList) o).getTokenList();
        }
        return o; 
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
          
          TokenList tokens = (TokenList)
              mDictionaryIterator.next();
          
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
      
      Token tokens[] = new Token[whiteSpaceTokenizer.countTokens()];
      
      if (tokens.length > 0) {
        int tokenIndex = 0;
        while (whiteSpaceTokenizer.hasMoreTokens()) {
          tokens[tokenIndex++] = Token.create(whiteSpaceTokenizer.nextToken());
        }
        
        dictionary.put(new TokenList(tokens));
      }
    }
    
    return dictionary;
  }
}