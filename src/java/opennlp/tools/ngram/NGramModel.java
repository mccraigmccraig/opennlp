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
import java.util.NoSuchElementException;

public class NGramModel {
  private Map mNGrams = new HashMap();
  
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
  
  public void add(String chars, int minLength, 
      int maxLength) {
    
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
  
  public void add(TokenList tokens) {
    if (contains(tokens)) {
      setCount(tokens, getCount(tokens) + 1);
    }
    else {
      mNGrams.put(tokens, new Integer(0));
    }
  }
  
  public void remove(TokenList tokens) {
    mNGrams.remove(tokens);
  }
  
  public boolean contains(TokenList tokens) {
    return mNGrams.containsKey(tokens);
  }
  
  public int size() {
    return mNGrams.size();
  }
  
  public Iterator iterator() {
    return mNGrams.keySet().iterator();
  }
  
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
  
  public String toString() {
    return "Size: " + size();
  }
  
  public int hashCode() {
    return mNGrams.hashCode();
  }
}