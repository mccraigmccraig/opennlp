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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.3 $, $Date: 2006/11/08 19:25:38 $
 */
public class Profile {

  private String mName;
  
  private Map mNGrams;
  
  /**
   * Initializes a new instance of the Profile.
   * @param ngrams
   */
  protected Profile(String name, Map ngrams) {
    this(name, ngrams, 0, Integer.MAX_VALUE);
  }
 
  /**
   * Initializes a new instance of the Profile.
   * 
   * @param cutoffUnder - use 0 to not cutoff anything
   * @param cutoffOver - use {@link Integer#MAX_VALUE} to not cutoff anything
   * 
   * @param name
   */
  protected Profile(String name, Map ngrams, int cutoffUnder, int cutoffOver) {
    
    if (name == null) {
      throw new IllegalArgumentException("Name parameter must not be null!");
    }
    
    mName = name;
    
    mNGrams = Collections.unmodifiableMap(ngrams);

    if (cutoffUnder > 0 || cutoffOver < Integer.MAX_VALUE) {
      
      for (Iterator it = ngrams.values().iterator(); it.hasNext();) {
        
        Ngram ngram = (Ngram) it.next();
        
        if (ngram.getOccurenceCount() <= cutoffUnder || 
            ngram.getOccurenceCount() >= cutoffOver) {
          it.remove();
        }
      }
    }
  }
  
  /**
   * Returns the number total number of grams (sum of all gram occurences).
   *  
   * @return total number of grams
   */
  public int numberOfGrams() {
    int counter = 0;
    
    for (Iterator it = iterator(); it.hasNext();) {
      
      Ngram ngram = (Ngram) it.next();
      
      counter += ngram.getOccurenceCount();
    }
    
    return counter;
  }
  
  /**
   * Merges the other profile with the current instance.
   * 
   * @param mergeProfile
   * 
   * @return the merged profile
   */
  public Profile merge(Profile mergeProfile) {
    return merge(new Profile[]{mergeProfile}, 0, Integer.MAX_VALUE);
  }

  /**
   * Merges the other profile with the current instance.
   * 
   * @param mergeProfile
   * @param cutoffUnder - use 0 to not cutoff anything
   * @param cutoffOver - use {@link Integer#MAX_VALUE} to not cutoff anything
   * 
   * @return the merged profile
   */
  public Profile merge(Profile mergeProfile, int cutoffUnder, int cutoffOver) {
    return merge(new Profile[]{mergeProfile});
  }
  
  /**
   * Merges n {@link Profile}s into one.
   * 
   * @param mergeProfiles
   * 
   * @return the merged {@link Profile}
   */
  public Profile merge(Profile mergeProfiles[]) {
    return merge(mergeProfiles, 0, Integer.MAX_VALUE);
  }
  
  /**
   * Merges n profiles into one. 
   * 
   * @param cutoffUnder - use 0 to not cutoff anything
   * @param cutoffOver - use {@link Integer#MAX_VALUE} to not cutoff anything
   * 
   * @param mergeProfiles
   * @return the merged {@link Profile}
   */
  public Profile merge(Profile mergeProfiles[], int cutoffUnder, 
      int cutoffOver) {
    
    // create a map with the current ngrams 
    Map newProfileMap = new HashMap();
    
    for (Iterator it = iterator(); it.hasNext();) {
      
      Ngram ngram = (Ngram) it.next();
      newProfileMap.put(ngram.getGramText(), ngram);
    }
    
    // insert the mereProfile into the map
    for (int i = 0; i < mergeProfiles.length; i++) {
      
      for (Iterator it = mergeProfiles[i].iterator(); it.hasNext(); ) {
        
        Ngram ngram = (Ngram) it.next();
        
        Ngram mergeNgram = (Ngram) newProfileMap.get(ngram.getGramText());
        
        if (mergeNgram != null) {
          
          String gramText = mergeNgram.getGramText();
          
          newProfileMap.put(gramText, new Ngram(gramText, 
              mergeNgram.getOccurenceCount() + ngram.getOccurenceCount())); 
        }
        else {
          newProfileMap.put(ngram.getGramText(), ngram);
        }
      }
    }
    
    return new Profile(mName, newProfileMap, cutoffUnder, cutoffOver);
  }
  
  /**
   * Removes ngrams which do occur less than the cutoffUnder value and more
   * often than cutoffOver.
   * 
   * @param cutoffUnder - use 0 to not cutoff anything
   * @param cutoffOver - use {@link Integer#MAX_VALUE} to not cutoff anything
   * 
   * @return the profiles without cutoff ngrams
   */
  public Profile cutoff(int cutoffUnder, int cutoffOver) {
    
    Map ngrams = new HashMap();
    ngrams.putAll(mNGrams);
    
    return new Profile(mName, ngrams, cutoffUnder, cutoffOver);
  }
  
  /**
   * Checks if the current instance contains the given ngram string.
   * @param searchGram 
   * 
   * @return true if the profile contains the ngram otherwise false
   */
  public boolean contains(String searchGram) {
    return mNGrams.containsKey(searchGram);
  }
  
  /**
   * Checks if the given grams are contained in this profile.
   * 
   * @param searchGrams
   * @return true if alle grams are containd, otherwise false
   */
  public boolean contains(String searchGrams[]) {
    for (int i = 0; i < searchGrams.length; i++) {
      if (contains(searchGrams[i])) {
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * Retrives the specified ngram.
   * 
   * @param gramText
   * @return the searched ngram
   */
  public Ngram getGram(String gramText) {
    return (Ngram) mNGrams.get(gramText.toLowerCase());
  }
  
  /**
   * Computes how likli two profiles are. This is implementation is based on
   * frequence summing.
   * 
   * @param profile
   * @return a value between 0 and 1 which indicates the matching areas.
   */
  public double likelihood(Profile profile) {
    double frequenceSum = 0;
    
    int numberOfReferneceGrams = numberOfGrams();
    
    
    for (Iterator it = profile.iterator(); it.hasNext();) {
      
      Ngram ngram = (Ngram) it.next();
      
      Ngram referenceNgram = (Ngram) mNGrams.get(ngram.getGramText());
      
      if (referenceNgram != null) {
        double referenceNgramFrequence = 
            (double) referenceNgram.getOccurenceCount() / 
            (double) numberOfReferneceGrams;
        
        frequenceSum += referenceNgramFrequence * ngram.getOccurenceCount();
      }
    }
    
    return frequenceSum;
  }
  
  /**
   * Retrives an {@link Iterator} over all contained {@link Ngram} instances.
   * 
   * @return the {@link Iterator}
   */
  public Iterator iterator() {
    return mNGrams.values().iterator();
  }
  
  /**
   * Returns true if both instances are equal.
   */
  public boolean equals(Object obj) {
    
    if (this == obj) {
      return true;
    }
    
    if (obj != null && obj instanceof Profile) {
         Profile compareProfile = (Profile) obj;
         
         return compareProfile.mNGrams.equals(mNGrams) &&
             compareProfile.mName.equals(mName);
    }
    else {
      return false;
    }
  }
  
  /**
   * Retrives a hash code of this instance.
   */
  public int hashCode() {
    return mNGrams.hashCode();
  }
  
  /**
   * Generates a human readable string representing the current instance.
   */
  public String toString() {
    return mNGrams.values().toString();
  }

  /**
   * The name of the profile.
   * 
   * @return profile name
   */
  public String getName() {
    return mName;
  }
}