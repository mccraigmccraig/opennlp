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
import java.util.Iterator;
import java.util.List;

/**
 * Generates previous and next features for a given {@link AdaptiveFeatureGenerator}.
 * The window size can be specified.
 * 
 * Features:
 * Current token is always included unchanged
 * Previous tokens are prefixed with p distance
 * Next tokens are prefix with n distance
 */
public class WindowFeatureGenerator implements AdaptiveFeatureGenerator {
  
  public static final String PREV_PREFIX = "p";
  public static final String NEXT_PREFIX = "n";
  
  private final AdaptiveFeatureGenerator generator;
  
  private final int prevWindowSize;
  private final int nextWindowSize;
  
  /**
   * Initializes the current instance with the given parameters.
   * 
   * @param generator Feature generator to apply to the window.
   * @param prevWindowSize Size of the window to the left of the current token.
   * @param nextWindowSize Size of the window to the right of the current token.
   */
  public WindowFeatureGenerator(AdaptiveFeatureGenerator generator, int prevWindowSize,  int nextWindowSize) {
    this.generator = generator;
    this.prevWindowSize = prevWindowSize;
    this.nextWindowSize = nextWindowSize;
  }
  
  /**
   * Initializes the current instance. The previous and next window size is 5.
   * 
   * @param generator
   */
  public WindowFeatureGenerator(AdaptiveFeatureGenerator generator) {
    this(generator, 5, 5);
  }
  
  public void createFeatures(List features, String[] tokens, int index, String[] preds) {
    // current features
    generator.createFeatures(features, tokens, index, preds);

    // previous features
    for (int i = 1; i < prevWindowSize + 1; i++) {
      if (index - i >= 0) {

        List prevFeatures = new ArrayList();

        generator.createFeatures(prevFeatures, tokens, index - i, preds);

        for (Iterator it = prevFeatures.iterator(); it.hasNext();) {
          features.add(PREV_PREFIX + i + it.next().toString());
        }
      }
    }

    // next features
    for (int i = 1; i < nextWindowSize + 1; i++) {
      if (i + index < tokens.length) {

        List nextFeatures = new ArrayList();

        generator.createFeatures(nextFeatures, tokens, index + i, preds);

        for (Iterator it = nextFeatures.iterator(); it.hasNext();) {
          features.add(NEXT_PREFIX + i + it.next().toString());
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
  
  public String toString() {
    return super.toString()+": Prev windwow size: " + prevWindowSize +", Next window size: " + nextWindowSize;
  }
}