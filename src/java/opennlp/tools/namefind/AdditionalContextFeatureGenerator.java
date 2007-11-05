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

import java.util.List;

/**
 * The {@link AdditionalContextFeatureGenerator} generates the context from the passed
 * in additional context.
 */
class AdditionalContextFeatureGenerator extends FeatureGenerator {

  private String[][] additionalContext;

  AdditionalContextFeatureGenerator() {
  }

  public void createFeatures(List features, String[] tokens, String[] preds, int index) {

    if (additionalContext != null && additionalContext.length != 0) {

      String[] context = additionalContext[index];

      for (int i = 0; i < context.length; i++) {
        features.add("ne=" + context[i]);
      }
    }
  }

  void setCurrentContext(String[][] context) {
    additionalContext = context;
  }
  
  public void clearAdaptiveData() {
    // has no adaptive data
  }
}