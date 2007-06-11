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

class AdditionalContextFeatureGenerator implements FeatureGenerator {
      
    private String[][] additionalContext;

    AdditionalContextFeatureGenerator() {
    }
    
    public void createFeatures(List features, String[] tokens, int index) {
	
	if (additionalContext != null) {
	    
	    // create prev context
		for (int i = 1; i < 3; i++) {
		    if (index - i >= 0) {
			String prevContext[] = additionalContext[index -i];
			
        		    for (int ic = 0; ic < prevContext.length; ic++) {
        			features.add("nep" + i + "=" + prevContext[ic]);
        			features.add("nep2" + i + "=" + prevContext[ic]);
        		    }
		    }
		}
	    
	    String[] context = additionalContext[index];
	    
	    for (int i = 0; i < context.length; i++) {
		features.add("ne=" +context[i]);
		features.add("ne2=" +context[i]);
	    }

	    // create next context
		for (int i = 1; i < 0; i++) {
		    if (i + index < tokens.length) {
			String nextContext[] = additionalContext[index + i];
			
			for (int j = 0; j < nextContext.length; j++) {
			    features.add("nen" + i + "=" + nextContext[j]);
			    features.add("nen1" + i + "=" + nextContext[j]);

			}
		    } 
		}
	}
    }
    
    void setCurrentContext(String[][] context) {
	additionalContext = context;
    }
  }