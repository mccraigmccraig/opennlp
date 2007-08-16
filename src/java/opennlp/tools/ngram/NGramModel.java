///////////////////////////////////////////////////////////////////////////////
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
 * The {@link NGramModel} can be used to crate ngrams and character ngrams.
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.8 $, $Date: 2007/08/16 00:28:19 $
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
   * @throws InvalidFormatException 
   */
  public NGramModel(InputStream in) throws IOException, InvalidFormatException {
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

  /**
   * Retrives the count of the given ngram.
   * 
   * @param ngram
   * 
   * @return count of the ngram or 0 if it is not contained
   * 
   */
  public int getCount(TokenList ngram) {
    
    Integer count = (Integer) mNGrams.get(ngram);
    
    if (count == null) {
      return 0;
    }
    
    return count.intValue();
  }
  
  /**
   * Sets the count of an existing ngram.
   * 
   * @param ngram
   * @param count
   */
  public void setCount(TokenList ngram, int count) {
    
    Integer oldCount = (Integer) mNGrams.put(ngram, new Integer(count));
    
    if (oldCount == null) {
      mNGrams.remove(ngram);
      throw new NoSuchElementException();
    }
  }
  
  /**
   * Adds one NGram, if it already exists the count increase by one.
   * 
   * @param ngram
   */
  public void add(TokenList ngram) {
    if (contains(ngram)) {
      setCount(ngram, getCount(ngram) + 1);
    }
    else {
      mNGrams.put(ngram, new Integer(1));
    }
  }

  /**
   * Adds NGrams up to the specified length to the current instance.
   * 
   * @param ngram the tokens to build the uni-grams, bi-grams, tri-grams, ..
   *     from.
   * @param minLength - minimal length
   * @param maxLength - maximal length
   */
  public void add(TokenList ngram, int minLength, int maxLength) {
    
    for (int lengthIndex = minLength; lengthIndex < maxLength + 1; 
    lengthIndex++) {
      for (int textIndex = 0; 
          textIndex + lengthIndex - 1 < ngram.size(); textIndex++) {
        
        Token[] grams = new Token[lengthIndex];
        
        for (int i = textIndex; i < textIndex + lengthIndex; i++) {
          grams[i - textIndex] = ngram.getToken(i);
        }
        
        add(new TokenList(grams));
      }      
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
   * 
   * @return true if the ngram is contained
   */
  public boolean contains(TokenList tokens) {
    return mNGrams.containsKey(tokens);
  }
  
  /**
   * Retrives the number of {@link TokenList} entries in the current instance.
   * 
   * @return number of different grams
   */
  public int size() {
    return mNGrams.size();
  }
  
  /**
   * Retrives an {@link Iterator} over all {@link TokenList} entires.
   * 
   * @return iterator over all grams
   */
  public Iterator iterator() {
    return mNGrams.keySet().iterator();
  }
  
  /**
   * Retrives the total count of all Ngrams.
   * 
   * @return total count of all ngrams
   */
  public int numberOfGrams() {
    int counter = 0;
    
    for (Iterator it = iterator(); it.hasNext();) {
      
      TokenList ngram = (TokenList) it.next();
      
      counter += getCount(ngram);
    }
    
    return counter;
  }
  
  /**
   * Deletes all ngram which do appear less than the cutoffUnder value
   * and more often than the cutoffOver value.
   * 
   * @param cutoffUnder
   * @param cutoffOver
   */
  public void cutoff(int cutoffUnder, int cutoffOver) {
    
    if (cutoffUnder > 0 || cutoffOver < Integer.MAX_VALUE) {
      
      for (Iterator it = iterator(); it.hasNext();) {
        
        TokenList ngram = (TokenList) it.next();
        
        int count = getCount(ngram);
        
        if (count < cutoffUnder || 
            count > cutoffOver) {
          it.remove();
        }
      }
    }
  }
  
//  public double likelihood(NGramModel model) {
//    double frequenceSum = 0;
//    
//    int numberOfReferneceGrams = numberOfGrams();
//    
//    for (Iterator it = model.iterator(); it.hasNext();) {
//      
//      TokenList ngram = (TokenList) it.next();
//      
//      if (contains(ngram)) {
//        double referenceNgramFrequence = 
//            (double) getCount(ngram) / 
//            (double) numberOfReferneceGrams;
//        
//        frequenceSum += referenceNgramFrequence * model.getCount(ngram);
//      }
//    }
//    
//    return frequenceSum;
//  }
  
  
  public Dictionary toDictionary() {
    return toDictionary(false);
  }
  
  /**
   * Creates a dictionary which contains all {@link TokenList}s which
   * are in the current {@link NGramModel}.
   * @param caseSensitive Specifies whether case distinctions should be kept in the creation of the dictionary.
   * @return the new dictionary
   */
  public Dictionary toDictionary(boolean caseSensitive) {
    
    Dictionary dict = new Dictionary(caseSensitive);
    
    for (Iterator it = iterator(); it.hasNext();) {
      dict.put((TokenList)it.next());
    }
    
    return dict;
  }
  
  /**
   * Writes the ngram instance to the given {@link OutputStream}.
   * 
   * @param out
   * @throws IOException if an I/O Error during writing occures
   */
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
    else if (obj instanceof NGramModel) {
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