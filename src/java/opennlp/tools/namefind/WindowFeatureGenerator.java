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

public class WindowFeatureGenerator implements FeatureGenerator {
  
  private final FeatureGenerator generator;
  
  private final int prevWindowSize;
  private final int nextWindowSize;

  public WindowFeatureGenerator(FeatureGenerator generator, int prevWindowSize, 
      int nextWindowSize) {
        this.generator = generator;
        this.prevWindowSize = prevWindowSize;
        this.nextWindowSize = nextWindowSize;
  }
  
  public WindowFeatureGenerator(FeatureGenerator generator) {
    this(generator, 5, 5);
  }
  
  public void createFeatures(List features, String[] tokens, int index) {

    // current features
    generator.createFeatures(features, tokens, index);
    
    // previous features
    for (int i = 1; i < prevWindowSize + 1; i++) {
      if (index - i >= 0) {

        List prevFeatures = new ArrayList();
          
          generator.createFeatures(prevFeatures, tokens, index - i);

        for (Iterator it = prevFeatures.iterator(); it.hasNext();) {
          features.add("p" + i + it.next().toString());
        }
      }
    }

    // next features
    for (int i = 1; i < nextWindowSize + 1; i++) {
      if (i + index < tokens.length) {

        List nextFeatures = new ArrayList();

        generator.createFeatures(nextFeatures, tokens, index + i);
        
        for (Iterator it = nextFeatures.iterator(); it.hasNext();) {
          features.add("n" + i + it.next().toString());
        }

      }
    }
  }
}