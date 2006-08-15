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
 * Iterate over the ngrams, immbutable, compute likeliehood 
 * 
 *  Optional: merge
 * 
 * A Profile 
 *
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.1 $, $Date: 2006/08/15 21:08:29 $
 */
public class Profile {

  private String mName;
  
  private Map mNGrams;
  
  
  /**
   * Initializes a new instance of the Profile.
   * @param ngrams
   */
  Profile(String name, Map ngrams) {
    this(name, ngrams, 0);
  }
 
  
  /**
   * Initializes a new instance of the Profile.
   * 
   * @param name
   */
  Profile(String name, Map ngrams, int cutoff) {
    
    mNGrams = Collections.unmodifiableMap(ngrams);
    
    if (cutoff > 0) {
      
      for (Iterator it = iterator(); it.hasNext();) {
        
        Ngram ngram = (Ngram) it.next();
        
        if (ngram.getOccurenceCount() <= cutoff) {
          ngrams.remove(ngram.getGramText());
        }
      }
    }
    
    if (name == null) {
      throw new IllegalArgumentException("Name parameter must not be null!");
    }
    mName = name;
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
   * Merges n {@link Profile}s into one.
   * 
   * @param mergeProfiles
   * 
   * @return the merged {@link Profile}
   */
  public Profile merge(Profile mergeProfiles[]) {
    return merge(0, mergeProfiles);
  }
  
  /**
   * Merges n profiles into one. 
   * 
   * @param cutoff 
   * 
   * @param mergeProfiles
   * @return the merged {@link Profile}
   */
  public Profile merge(int cutoff, Profile mergeProfiles[]) {
    
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
    
    return new Profile(mName, newProfileMap, cutoff);
    
  }
  
  /**
   * Removes ngrams which do occur less than the cutoff value.
   * 
   * @param cutoff
   * @return the profiles without cutoff ngrams
   */
  public Profile cutoff(int cutoff) {
    
    Map ngrams = new HashMap();
    ngrams.putAll(mNGrams);
    
    return new Profile(mName, ngrams, cutoff);
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
  
  public boolean contains(String searchGrams[])
  {
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
    return (Ngram) mNGrams.get(gramText);
  }
  
  /**
   * Computes how likli two profiles are. This is implementation is based on
   * frequence summing.
   * 
   * @param profile
   * @return a value between 0 and 1 which indicates thw matching areas.
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