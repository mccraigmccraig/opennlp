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

/**
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.2 $, $Date: 2006/11/17 09:37:42 $
 */
public class NGramModel {
  private Map mNGrams = new HashMap();
  
  public NGramModel() {
  }
  
  public NGramModel(InputStream stream) throws IOException {
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
  
  public void serialize(OutputStream stream) throws IOException {
  }

  public String toString() {
    return "Size: " + size();
  }
  
  public int hashCode() {
    return mNGrams.hashCode();
  }
}