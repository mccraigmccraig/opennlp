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

/**
 * Generates previous and next features for a given {@link AdaptiveFeatureGenerator}.
 * The window size can be specified.
 */
public class WindowFeatureGenerator implements AdaptiveFeatureGenerator {
  
  private final AdaptiveFeatureGenerator generator;
  
  private final int prevWindowSize;
  private final int nextWindowSize;

  public WindowFeatureGenerator(AdaptiveFeatureGenerator generator, int prevWindowSize, 
      int nextWindowSize) {
        this.generator = generator;
        this.prevWindowSize = prevWindowSize;
        this.nextWindowSize = nextWindowSize;
  }
  
  public WindowFeatureGenerator(AdaptiveFeatureGenerator generator) {
    this(generator, 5, 5);
  }
  
  public void createFeatures(List features, String[] tokens, String[] preds, int index) {

    // current features
    generator.createFeatures(features, tokens, preds, index);
    
    // previous features
    for (int i = 1; i < prevWindowSize + 1; i++) {
      if (index - i >= 0) {

        List prevFeatures = new ArrayList();
          
          generator.createFeatures(prevFeatures, tokens, preds, index - i);

        for (Iterator it = prevFeatures.iterator(); it.hasNext();) {
          features.add("p" + i + it.next().toString());
        }
      }
    }

    // next features
    for (int i = 1; i < nextWindowSize + 1; i++) {
      if (i + index < tokens.length) {

        List nextFeatures = new ArrayList();

        generator.createFeatures(nextFeatures, tokens, preds, index + i);
        
        for (Iterator it = nextFeatures.iterator(); it.hasNext();) {
          features.add("n" + i + it.next().toString());
        }
      }
    }
  }
  
  public void updateAdaptiveData(String[] tokens, String[] outcomes) {
    generator.updateAdaptiveData(tokens, outcomes);
  }
  
  public void clearAdaptiveData() {
      generator.clearAdaptiveData();
  }
}