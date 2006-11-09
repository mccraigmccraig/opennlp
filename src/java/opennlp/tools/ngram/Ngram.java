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
//GNU General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
////////////////////////////////////////////////////////////////////////////// 

package opennlp.tools.ngram;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents an n-gram. Each ngram has a gram string and
 * an occurence counter.
 * 
 * Use the create(...) methods to create {@link Ngram} instances from
 * a text.
 *
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.3 $, $Date: 2006/11/09 15:34:16 $
 */
public class Ngram implements Comparable {
  private String mGram;
  private int mNumberOfOccurence = 1;
  
  /**
   * Intializes a new {@link Ngram} object. This consructor
   * is only called from the create factory method.
   * 
   * @param gram
   */
  private Ngram(String gram) {
   if (gram == null) {
     throw new IllegalArgumentException("The gram parameter must not be null!");
   }
   
   mGram = gram;
  }
  
  /**
   * Initializes a new {@link Ngram} instance. This constructor should 
   * only be called from the {@link ProfileSerializer}.
   * 
   * @param gram
   * @param occurenceCount
   */
  protected Ngram(String gram, int occurenceCount) {
    mGram = gram;
    mNumberOfOccurence = occurenceCount;
  }
  
  /**
   * Retrives the occurence count, specifies how often the n-gram was seen
   * in the text.
   * 
   * @return the occurence ount
   */
  public int getOccurenceCount() {
    return mNumberOfOccurence;
  }
  
  /**
   * Returns the length of the contained n-gram.
   * 
   * @return the n-gram length
   */
  public int length() {
    return mGram.length();
  }

  /**
   * Retrives the gram text. 
   * 
   * @return gram text
   */
  public String getGramText() {
    return mGram;
  }
  
  /**
   * Compares the given instance to the current one.
   */
  public int compareTo(Object ngramObject) {
    
    if (ngramObject instanceof Ngram) {
      return new Integer(mNumberOfOccurence).compareTo(new Integer(
          ((Ngram) ngramObject).getOccurenceCount()));
    }
    
    return -1;
  }
  
  /**
   * Returns a human-readbale string representing the current instance.
   */
  public String toString() {
    return mGram;
  }
  
  /**
   * Return true if the given object is equal to the current instance.
   */
  public boolean equals(Object obj) {
    
    boolean result;
    
    if (obj == this) {
      result = true;
    }
    else if (obj != null && obj instanceof Ngram) {
      Ngram ngram = (Ngram) obj;
        
      result = mGram.equals(ngram.mGram) && 
          mNumberOfOccurence == ngram.mNumberOfOccurence;
      }
      else {
        result = false;
      }
      
    return result;
  }
  
  /**
   * Returns a hash value which represents the current instance.
   */
  public int hashCode() {
    return mGram.hashCode();
  }
  
  /**
   * Creates all {@link Ngram}s with the speciefied (string) length.
   * 
   * @param profileName the name of the created profile
   * @param text
   * @param length
   * @return the {@link Profile} containing the {@link Ngram} objects.
   */
  public static Profile create(String profileName, String text[], int length) {
    return create(profileName, 0, Integer.MAX_VALUE, text, length);
  }
  
  /**
   * Creates all {@link Ngram}s with the speciefied (string) length 
   * and cutoff (remove ngrams which do not coccur less than cutoff value).
   * 
   * @param profileName the name of the created profile
   * @param cutoffUnder the number how often a ngram must be seen to be included
   * @param cutoffOver the number how a ngram must be seen to not be included
   * @param text
   * @param length
   * @return the {@link Profile} containing the {@link Ngram} objects.
   */
  public static Profile create(String profileName, int cutoffUnder, 
      int cutoffOver, String text[], int length) {
    return create(profileName, cutoffUnder, cutoffOver, text, length, length);
  }
  
  /**
   * Creates all {@link Ngram}s with the speciefied min until max
   * (string) length.
   * 
   * @param profileName the name of the created profile
   * @param text
   * @param minLength
   * @param maxLength
   * @return the {@link Profile} containing the {@link Ngram} objects.
   */
  public static Profile create(String profileName, String text[], int minLength, 
      int maxLength) {
    return create(profileName, 0, Integer.MAX_VALUE, 
        text, minLength, maxLength);
  }
  
  /**
   * Creates all n-grams from the geiven piece of text, with the speciefied
   * lengths and cutoff.
   * 
   * @param profileName the name of the created profile
   * @param text the text to create ngrams form
   * @param cutoffUnder the number how often a ngram must be seen to be included
   * @param cutoffOver the number how a ngram must be seen to not be included
   * @param minLength the minimal ngram length
   * @param maxLength the maximal ngram length
   * 
   * @return the {@link Profile} containing the {@link Ngram} objects.
   */
  public static Profile create(String profileName, int cutoffUnder, 
      int cutoffOver, String text[], int minLength, int maxLength) {
    
    Map ngrams = new HashMap();
    
    for (int i = 0; i < text.length; i++) {
      createNgram(ngrams, text[i], minLength, maxLength);
    }
    
    return new Profile(profileName, ngrams, cutoffUnder, cutoffOver);
  }
  
  /**
   * Creates all {@link Ngram}s with the speciefied (string) length.
   * 
   * @param profileName the name of the created profile
   * @param text
   * @param length
   * @return the {@link Profile} containing the {@link Ngram} objects.
   */
  public static Profile create(String profileName, String text, int length) {
    return create(profileName, 0, Integer.MAX_VALUE, text, length);
  }
  
  /**
   * Creates all {@link Ngram}s with the speciefied (string) length 
   * and cutoff (remove ngrams which do not coccur less than cutoff value).
   * 
   * @param profileName the name of the created profile
   * @param cutoffUnder the number how often a ngram must be seen to be included
   * @param cutoffOver the number how a ngram must be seen to not be included
   * @param text
   * @param length
   * @return the {@link Profile} containing the {@link Ngram} objects.
   */
  public static Profile create(String profileName, int cutoffUnder, 
      int cutoffOver, String text, int length) {
    return create(profileName, cutoffUnder, cutoffOver, text, length, length);
  }
  
  /**
   * Creates all {@link Ngram}s with the speciefied min until max
   * (string) length.
   * 
   * @param profileName the name of the created profile
   * @param text
   * @param minLength
   * @param maxLength
   * @return the {@link Profile} containing the {@link Ngram} objects.
   */
  public static Profile create(String profileName, String text, int minLength, 
      int maxLength) {
    return create(profileName, 0, Integer.MAX_VALUE, 
        text, minLength, maxLength);
  }
  
  /**
   * Creates all n-grams from the geiven piece of text, with the speciefied
   * lengths and cutoff.
   * 
   * @param profileName the name of the created profile
   * @param text the text to create ngrams form
   * @param cutoffUnder the number how often a ngram must be seen to be included
   * @param cutoffOver the number how a ngram must be seen to not be included
   * @param minLength the minimal ngram length
   * @param maxLength the maximal ngram length
   * 
   * @return the {@link Profile} containing the {@link Ngram} objects.
   */
  public static Profile create(String profileName, int cutoffUnder, 
      int cutoffOver, String text, int minLength, int maxLength) {
    
    Map ngrams = new HashMap();
    
    createNgram(ngrams, text, minLength, maxLength);
    
    return new Profile(profileName, ngrams, cutoffUnder, cutoffOver);
  }

  /**
   * Creates n-grams and adds them to the map. The map, maps the gram text
   * to the gram object. The gram can then be looked up to increase the 
   * occurence counter.
   * 
   * @param ngrams
   * @param cutoff
   * @param text
   * @param minLength
   * @param maxLength
   */
  private static void createNgram(Map ngrams, String text, int minLength, 
      int maxLength) {
    
    for (int lengthIndex = minLength; lengthIndex < maxLength + 1; 
        lengthIndex++) {
      for (int i = 0; i + lengthIndex - 1 < text.length(); i++) {

        String gram = text.substring(i, i + lengthIndex).toLowerCase();

        Ngram existingNGram = (Ngram) ngrams.get(gram);

        if (existingNGram != null) {
          existingNGram.mNumberOfOccurence++;
        }
        else {
          ngrams.put(gram, new Ngram(gram));
        }
      }
    }
  }
}