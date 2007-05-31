/**
 * 
 */
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