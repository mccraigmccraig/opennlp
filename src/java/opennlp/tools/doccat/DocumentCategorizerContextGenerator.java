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

package opennlp.tools.doccat;

import java.util.Collection;
import java.util.LinkedList;

/**
 * 
 */
class DocumentCategorizerContextGenerator {
  
  private FeatureGenerator[] mFeatureGenerators;
  
  DocumentCategorizerContextGenerator(FeatureGenerator[] featureGenerators) {
    mFeatureGenerators = featureGenerators;
  }

  public String[] getContext(Object textObject) {
    
    String[] text = (String[]) textObject;
    
    Collection context = new LinkedList();
    
    for (int i = 0; i < mFeatureGenerators.length; i++) {
      Collection extractedFeatures = 
          mFeatureGenerators[i].extractFeatures(text);
      context.addAll(extractedFeatures);
    }
    
    return (String[]) context.toArray(new String[context.size()]);
  }
}
