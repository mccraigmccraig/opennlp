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
import java.util.Map;

import opennlp.tools.util.BeamSearchContextGenerator;
import opennlp.tools.util.Cache;
import opennlp.tools.util.Sequence;


/** 
 * Class for determining contextual features for a tag/chunk style 
 * named-entity recognizer.
 * 
 * @version $Revision: 1.9 $, $Date: 2007/06/26 13:03:54 $
 */
public class NameContextGenerator implements BeamSearchContextGenerator {
  
  private Cache contextsCache;
  private Object wordsKey;
  private int pi = -1;
  private List prevStaticFeatures;
  
  private FeatureGenerator mFeatureGenerators[];
  
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
   * Creates a name context generator with the specified cach size.
   */
  public NameContextGenerator(int cacheSize, 
      FeatureGenerator featureGenerators[]) {
    
    if (featureGenerators != null) {
      mFeatureGenerators = featureGenerators;
    }
    else {
      mFeatureGenerators =  new FeatureGenerator[] 
        {
          new WindowFeatureGenerator(new TokenFeatureGenerator(), 2, 2),
          new WindowFeatureGenerator(new TokenClassFeatureGenerator(), 2, 2)
        };
    }
    
    if (cacheSize > 0) {
      contextsCache = new Cache(cacheSize);
    }
  }
  
  void addFeatureGenerator(FeatureGenerator generator) {
      FeatureGenerator generators[] = mFeatureGenerators;
      
      mFeatureGenerators = new FeatureGenerator[mFeatureGenerators.length + 1];
      
      System.arraycopy(generators, 0, mFeatureGenerators, 0, generators.length);
      
      mFeatureGenerators[mFeatureGenerators.length - 1] = generator;
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
   * @param preds The previous decisions made in the taging of this sequence.  Only indices less than i will be examined.
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
    String cacheKey = i+po+ppo;
    if (contextsCache != null) {
      if (wordsKey == toks){
        String[] cachedContexts = (String[]) contextsCache.get(cacheKey);    
        if (cachedContexts != null) {
          return cachedContexts;
        }
      }
      else {
        contextsCache.clear();
        wordsKey = toks;
      }
    }
    List features;
    if (wordsKey == toks && i == pi) {
      features =prevStaticFeatures; 
    }
    else {
      features = getStaticFeatures(toks,i);
      if (additionalContext != null) {
        for (int aci=0;aci<additionalContext[i].length;aci++) {
          features.add(additionalContext[i][aci]);
        }
      }
      if (i == 0) {
        features.add("df=it");
      }
      pi=i;
      prevStaticFeatures=features;
    }
    
    int fn = features.size();
    String[] contexts = new String[fn+4];
    for (int fi=0;fi<fn;fi++) {
      contexts[fi]=(String) features.get(fi);
    }
    contexts[fn]="po="+po;
    contexts[fn+1]="pow="+po+toks[i];
    contexts[fn+2]="powf="+po+FeatureGeneratorUtil.tokenFeature(toks[i].toString());
    contexts[fn+3]="ppo="+ppo;
    
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
  protected List getStaticFeatures(Object[] toks, int index) {
    List feats = new ArrayList();
    
    String tokens[] = new String[toks.length];	
    
    for (int i = 0; i < toks.length; i++) {
      tokens[i] = toks[i].toString();
    }
    
    for (int i = 0; i < mFeatureGenerators.length; i++) {
      mFeatureGenerators[i].createFeatures(feats, tokens, index);
    }
    
    return feats;
  }
}