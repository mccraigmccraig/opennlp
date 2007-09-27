///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2003 Thomas Morton
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

import opennlp.tools.util.BeamSearchContextGenerator;
import opennlp.tools.util.Cache;
import opennlp.tools.util.Sequence;


/** 
 * Class for determining contextual features for a tag/chunk style 
 * named-entity recognizer.
 * 
 * @version $Revision: 1.10 $, $Date: 2007/09/27 06:19:15 $
 */
public class NameContextGenerator implements BeamSearchContextGenerator {
  
  private Cache contextsCache;
  private Object wordsKey;
  private int pi = -1;
  private List prevStaticFeatures;
  
  private AdaptiveFeatureGenerator featureGenerators[];
  
  /**
   * Creates a name context generator.
   */
  public NameContextGenerator() {
    this(0, null);
  }

  public NameContextGenerator(int cacheSize) {
    this(cacheSize, null);
  }
  
  /**
   * Creates a name context generator with the specified cache size.
   */
  public NameContextGenerator(int cacheSize, 
      AdaptiveFeatureGenerator featureGenerators[]) {
    
    if (featureGenerators != null) {
      this.featureGenerators = featureGenerators;
    }
    else {
      this.featureGenerators =  new AdaptiveFeatureGenerator[] 
        {
          new WindowFeatureGenerator(new TokenFeatureGenerator(), 2, 2),
          new WindowFeatureGenerator(new TokenClassFeatureGenerator(), 2, 2)
        };
    }
    
    if (cacheSize > 0) {
      contextsCache = new Cache(cacheSize);
    }
  }
  
  void addFeatureGenerator(AdaptiveFeatureGenerator generator) {
      AdaptiveFeatureGenerator generators[] = featureGenerators;
      
      featureGenerators = new AdaptiveFeatureGenerator[featureGenerators.length + 1];
      
      System.arraycopy(generators, 0, featureGenerators, 0, generators.length);
      
      featureGenerators[featureGenerators.length - 1] = generator;
  }
  
  void updateAdaptiveData(String[] tokens, String[] outcomes) {
    
    if (tokens != null && outcomes != null && tokens.length != outcomes.length) {
        throw new IllegalArgumentException(
            "The tokens and outcome arrays MUST have the same size!");
      }
    
    for (int i = 0; i < featureGenerators.length; i++) {
      featureGenerators[i].updateAdaptiveData(tokens, outcomes);
    }    
  }
  
  void clearAdaptiveData() {
    for (int i = 0; i < featureGenerators.length; i++) {
      featureGenerators[i].clearAdaptiveData();
    }
  }
  
  public String[] getContext(Object o) {
    Object[] data = (Object[]) o;
    return getContext(((Integer) data[0]).intValue(), (List) data[1], (List) data[2], (String[][]) data[3]);
  }
  
  public String[] getContext(int index, List sequence, Sequence s, Object[] additionalContext) {
    return getContext(index,sequence,s.getOutcomes(),(String[][]) additionalContext);
  }

  public String[] getContext(int i, List toks, List preds, String[][] additionalContext) {
    return getContext(i, toks.toArray(), (String[]) preds.toArray(new String[preds.size()]),additionalContext);
  }
  
  public String[] getContext(int index, Object[] sequence, String[] priorDecisions, Object[] additionalContext) {
    return getContext(index,sequence,priorDecisions,(String[][]) additionalContext);
  }

  /**
   * Return the context for finding names at the specified index.
   * @param i The index of the token in the specified toks array for which the context should be constructed. 
   * @param toks The tokens of the sentence.  The <code>toString</code> methods of these objects should return the token text.
   * @param preds The previous decisions made in the tagging of this sequence.  Only indices less than i will be examined.
   * @param prevTags  A mapping between tokens and the previous outcome for these tokens. 
   * @return the context for finding names at the specified index.
   */
  public String[] getContext(int i, Object[] toks, String[] preds, String[][] additionalContext) {
    String po=NameFinderME.OTHER;
    String ppo=NameFinderME.OTHER;
    
    if (i > 1){
      ppo = preds[i-2];
    }
    
    if (i > 0) {
      po = preds[i-1];
    }
    
    String cacheKey = i + po + ppo;
    if (contextsCache != null) {
      if (wordsKey == toks) {
        String[] cachedContexts = (String[]) contextsCache.get(cacheKey);
        if (cachedContexts != null) {
          return cachedContexts;
        }
      } else {
        contextsCache.clear();
        wordsKey = toks;
      }
    }
    List features;
    if (wordsKey == toks && i == pi) {
      features = prevStaticFeatures;
    } else {
      features = getStaticFeatures(toks, preds, i);
      if (additionalContext != null) {
        for (int aci = 0; aci < additionalContext[i].length; aci++) {
          features.add(additionalContext[i][aci]);
        }
      }
      if (i == 0) {
        features.add("df=it");
      }
      pi = i;
      prevStaticFeatures = features;
    }

    String[] contexts = (String[]) features.toArray(new String[features.size() + 4]);

    contexts[features.size()] = "po= " + po;
    contexts[features.size() + 1] = "pow=" + po + toks[i];
    contexts[features.size() + 2] = "powf=" + po + 
        FeatureGeneratorUtil.tokenFeature(toks[i].toString());
    contexts[features.size() + 3] = "ppo=" + ppo;
    
    if (contextsCache != null) {
      contextsCache.put(cacheKey,contexts);
    }
    
    return contexts;
  }

  /**
    * Returns a list of the features for <code>toks[i]</code> that can
    * be safely cached.  In other words, return a list of all
    * features that do not depend on previous outcome or decision
    * features.  This method is called by <code>search</code>.
    *
    * @param toks The list of tokens being processed.
    * @param i The index of the token whose features should be returned.
    * @return a list of the features for <code>toks[i]</code> that can
    * be safely cached.
    */
  private List getStaticFeatures(Object[] toks, String[] preds, int index) {
    List feats = new ArrayList();
    
    String tokens[] = new String[toks.length];	
    
    for (int i = 0; i < toks.length; i++) {
      tokens[i] = toks[i].toString();
    }
    
    for (int i = 0; i < featureGenerators.length; i++) {
      featureGenerators[i].createFeatures(feats, tokens, preds, index);
    }
    
    return feats;
  }
}