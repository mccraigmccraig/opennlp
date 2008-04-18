///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2008 OpenNlp
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

package opennlp.tools.namefind;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.Cache;

/**
 * Caches features of the aggregated {@link AdaptiveFeatureGenerator}s.
 */
public class CachedFeatureGenerator implements AdaptiveFeatureGenerator {

  private final AdaptiveFeatureGenerator generators[];

  private String[] prevTokens;

  private Cache contextsCache;

  private long numberOfCacheHits;
  private long numberOfCacheMisses;
  
  public CachedFeatureGenerator(AdaptiveFeatureGenerator generators[]) {
    this.generators = generators;
    contextsCache = new Cache(100);
  }

  public void createFeatures(List features, String[] tokens, int index,
      String[] previousOutcomes) {

    List cacheFeatures;

    if (tokens == prevTokens) {
      cacheFeatures = (List) contextsCache.get(new Integer(index));
      
      if (cacheFeatures != null) {
        numberOfCacheHits++;
        features.addAll(cacheFeatures);
        return;
      }
      
    } else {
      contextsCache.clear();
      prevTokens = tokens;
    }

    cacheFeatures = new ArrayList();
    
    numberOfCacheMisses++;
    
    for (int i = 0; i < generators.length; i++)
      generators[i].createFeatures(cacheFeatures, tokens, index, previousOutcomes);

    contextsCache.put(new Integer(index), cacheFeatures);
    features.addAll(cacheFeatures);
  }

  public void updateAdaptiveData(String[] tokens, String[] outcomes) {
    for (int i = 0; i < generators.length; i++)
      generators[i].updateAdaptiveData(tokens, outcomes);
  }

  public void clearAdaptiveData() {
    for (int i = 0; i < generators.length; i++)
      generators[i].clearAdaptiveData();
  }
  
  /**
   * Retrieves the number of times a cache hit occurred.
   * 
   * @return number of cache hits
   */
  public long getNumberOfCacheHits() {
    return numberOfCacheHits;
  }
  
  /**
   * Retrieves the number of times a cache miss occurred.
   * 
   * @return number of cache misses
   */
  public long getNumberOfCacheMisses() {
    return numberOfCacheMisses;
  }
  
  public String toString() {
    return super.toString()+": hits=" + numberOfCacheHits+" misses="+ numberOfCacheMisses+" hit%"+ (numberOfCacheHits > 0 ? 
        (double)numberOfCacheHits/(numberOfCacheMisses+numberOfCacheHits) : 0);
  }
}