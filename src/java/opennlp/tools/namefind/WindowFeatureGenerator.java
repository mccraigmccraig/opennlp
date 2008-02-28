///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2007 OpenNlp
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
import java.util.Iterator;
import java.util.List;

import opennlp.tools.util.Cache;

/**
 * Generates previous and next features for a given {@link AdaptiveFeatureGenerator}.
 * The window size can be specified.
 */
public class WindowFeatureGenerator implements AdaptiveFeatureGenerator {
  
  private final AdaptiveFeatureGenerator generator;
  
  private final int prevWindowSize;
  private final int nextWindowSize;
  
  private String[] prevTokens;
  private boolean caching;
  private Cache contextsCache;  

  public WindowFeatureGenerator(AdaptiveFeatureGenerator generator, int prevWindowSize,  int nextWindowSize, boolean caching) {
    this.generator = generator;
    this.prevWindowSize = prevWindowSize;
    this.nextWindowSize = nextWindowSize;
    this.caching = caching;
    contextsCache = new Cache(100);
  }
  
  public WindowFeatureGenerator(AdaptiveFeatureGenerator generator) {
    this(generator, 5, 5,true);
  }
  
  public void createFeatures(List features, String[] tokens, int index, String[] preds) {
    List cacheFeatures;
    if (caching) {
      if (tokens == prevTokens) {
        cacheFeatures = (List) contextsCache.get(new Integer(index));
        if (cacheFeatures != null) {
          features.addAll(cacheFeatures);
          return;
        }
      }
      else {
        contextsCache.clear();
        prevTokens = tokens;
      }
    }
    if (caching) {
      cacheFeatures = new ArrayList();
    }   
    else {
      cacheFeatures = features;
    }
    // current features
    generator.createFeatures(cacheFeatures, tokens, index, preds);

    // previous features
    for (int i = 1; i < prevWindowSize + 1; i++) {
      if (index - i >= 0) {

        List prevFeatures = new ArrayList();

        generator.createFeatures(prevFeatures, tokens, index - i, preds);

        for (Iterator it = prevFeatures.iterator(); it.hasNext();) {
          cacheFeatures.add("p" + i + it.next().toString());
        }
      }
    }

    // next features
    for (int i = 1; i < nextWindowSize + 1; i++) {
      if (i + index < tokens.length) {

        List nextFeatures = new ArrayList();

        generator.createFeatures(nextFeatures, tokens, index + i, preds);

        for (Iterator it = nextFeatures.iterator(); it.hasNext();) {
          cacheFeatures.add("n" + i + it.next().toString());
        }
      }
    }
    if (caching) {
      contextsCache.put(new Integer(index),cacheFeatures);
      features.addAll(cacheFeatures);
    }
  }
  
  public void updateAdaptiveData(String[] tokens, String[] outcomes) {
    generator.updateAdaptiveData(tokens, outcomes);
  }
  
  public void clearAdaptiveData() {
      generator.clearAdaptiveData();
  }
}