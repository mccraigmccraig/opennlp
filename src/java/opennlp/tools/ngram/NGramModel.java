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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.dictionary.serializer.Attributes;
import opennlp.tools.dictionary.serializer.DictionarySerializer;
import opennlp.tools.dictionary.serializer.Entry;
import opennlp.tools.dictionary.serializer.EntryInserter;
import opennlp.tools.util.InvalidFormatException;

/**
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.4 $, $Date: 2006/12/01 00:20:00 $
 */
public class NGramModel {
  
  protected static final String COUNT = "count";
  
  private Map mNGrams = new HashMap();
  
  /**
   * Initializes an empty instance.
   */
  public NGramModel() {
  }
  
  /**
   * Initializes the current instance.
   * 
   * @param in
   * @throws IOException
   */
  public NGramModel(InputStream in) throws IOException {
    DictionarySerializer.create(in, new EntryInserter() {
      public void insert(Entry entry) throws InvalidFormatException {

        int count;

        try {
          String countValueString = entry.getAttributes().getValue(COUNT);
          
          if (countValueString == null) {
        	  throw new InvalidFormatException(
        	      "The count attribute must be set!");
          }
          
          count = Integer.parseInt(countValueString);
        } catch (NumberFormatException e) {
          throw new InvalidFormatException(
              "The count attribute must be a nubmer!");
        }
        
        add(entry.getTokens());
        setCount(entry.getTokens(), count);
      }
    });
  }

  public int getCount(TokenList tokens) throws NoSuchElementException{
    
    Integer count = (Integer) mNGrams.get(tokens);
    
    if (count == null) {
      throw new NoSuchElementException();
    }
    
    return count.intValue();
  }
  
  public void setCount(TokenList tokens, int count) 
      throws NoSuchElementException {
    
    Integer oldCount = (Integer) mNGrams.put(tokens, new Integer(count));
    
    if (oldCount == null) {
      mNGrams.remove(tokens);
      throw new NoSuchElementException();
    }
  }
  
  /**
   * Adds one NGram, if it already exists the count increase by one.
   * 
   * @param tokens
   */
  public void add(TokenList tokens) {
    if (contains(tokens)) {
      setCount(tokens, getCount(tokens) + 1);
    }
    else {
      mNGrams.put(tokens, new Integer(0));
    }
  }

  /**
   * Adds character NGrams to the current instance.
   * 
   * @param chars
   * @param minLength
   * @param maxLength
   */
  public void add(String chars, int minLength, int maxLength) {
    
    for (int lengthIndex = minLength; lengthIndex < maxLength + 1; 
    lengthIndex++) {
      for (int textIndex = 0; 
          textIndex + lengthIndex - 1 < chars.length(); textIndex++) {
        
        String gram = 
            chars.substring(textIndex, textIndex + lengthIndex).toLowerCase();
        
        add(new TokenList(new Token[]{Token.create(gram)}));
      }      
    }
  }
  
  /**
   * Adds NGrams up to the specifed length to the current instance.
   * 
   * @param tokens the tokens to build the uni-grams, bi-grams, tri-grams, ..
   *     from.
   * @param minLength - minimal length
   * @param maxLength - maximal length
   */
  public void add(TokenList tokens, int minLength, int maxLength) {
    
    for (int lengthIndex = minLength; lengthIndex < maxLength + 1; 
    lengthIndex++) {
      for (int textIndex = 0; 
          textIndex + lengthIndex - 1 < tokens.size(); textIndex++) {
        
        Token[] grams = new Token[lengthIndex];
        
        for (int i = textIndex; i < textIndex + lengthIndex; i++) {
          grams[i - textIndex] = tokens.getToken(i);
        }
        
        add(new TokenList(grams));
      }      
    }    
  }
  
  /**
   * Removes the specified tokens form the NGram model, they are just dropped.
   * 
   * @param tokens
   */
  public void remove(TokenList tokens) {
    mNGrams.remove(tokens);
  }
  
  /**
   * Checks fit he given tokens are contained by the current instance.
   * 
   * @param tokens
   * @return
   */
  public boolean contains(TokenList tokens) {
    return mNGrams.containsKey(tokens);
  }
  
  /**
   * Retrives the number of {@link TokenList} entries in the current instance.
   * 
   * @return
   */
  public int size() {
    return mNGrams.size();
  }
  
  /**
   * Retrives an {@link Iterator} over all {@link TokenList} entires.
   * 
   * @return
   */
  public Iterator iterator() {
    return mNGrams.keySet().iterator();
  }
  
  /**
   * Retrives the total count of all Ngrams.
   * 
   * @return
   */
  public int numberOfGrams() {
    int counter = 0;
    
    for (Iterator it = iterator(); it.hasNext();) {
      
      TokenList ngram = (TokenList) it.next();
      
      counter += getCount(ngram);
    }
    
    return counter;
  }
  
  public void cutoff(int cutoffUnder, int cutoffOver) {
    
    if (cutoffUnder > 0 || cutoffOver < Integer.MAX_VALUE) {
      
      for (Iterator it = iterator(); it.hasNext();) {
        
        TokenList ngram = (TokenList) it.next();
        
        int count = getCount(ngram);
        
        if (count <= cutoffUnder || 
            count >= cutoffOver) {
          it.remove();
        }
      }
    }
  }
  
  public double likelihood(NGramModel model) {
    double frequenceSum = 0;
    
    int numberOfReferneceGrams = numberOfGrams();
    
    for (Iterator it = model.iterator(); it.hasNext();) {
      
      TokenList ngram = (TokenList) it.next();
      
      if (contains(ngram)) {
        double referenceNgramFrequence = 
            (double) getCount(ngram) / 
            (double) numberOfReferneceGrams;
        
        frequenceSum += referenceNgramFrequence * model.getCount(ngram);
      }
    }
    
    return frequenceSum;
  }
  
  public Dictionary toDictionary() {
    return null;
  }
  
  public void serialize(OutputStream out) throws IOException {
	    Iterator entryIterator = new Iterator() 
	      {
	        private Iterator mDictionaryIterator = NGramModel.this.iterator();
	        
	        public boolean hasNext() {
	          return mDictionaryIterator.hasNext();
	        }

	        public Object next() {
	          
	          TokenList tokens = (TokenList) mDictionaryIterator.next();
	          
	          Attributes attributes = new Attributes();
	          
	          attributes.setValue(COUNT, Integer.toString(getCount(tokens)));
	          
	          return new Entry(tokens, attributes);
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
    else if (obj != null && obj instanceof NGramModel) {
      NGramModel model  = (NGramModel) obj;
      
      result = mNGrams.equals(model.mNGrams);
    }
    else {
      result = false;
    }
    
    return result;  
    
    }
  
  public String toString() {
    return "Size: " + size();
  }
  
  public int hashCode() {
    return mNGrams.hashCode();
  }
}