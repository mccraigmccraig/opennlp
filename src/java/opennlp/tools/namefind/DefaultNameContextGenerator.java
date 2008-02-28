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

import opennlp.tools.util.Sequence;

/** 
 * Class for determining contextual features for a tag/chunk style 
 * named-entity recognizer.
 * 
 * @version $Revision$, $Date$
 */
public class DefaultNameContextGenerator implements NameContextGenerator {
  
  private AdaptiveFeatureGenerator featureGenerators[];
  
  private static Object[] prevTokens;
  private static String[] prevStrings;
  
  /**
   * Creates a name context generator.
   */
  public DefaultNameContextGenerator() {
    this(null);
  }
  
  /**
   * Creates a name context generator with the specified cache size.
   */
  public DefaultNameContextGenerator(AdaptiveFeatureGenerator featureGenerators[]) {
    
    if (featureGenerators != null) {
      this.featureGenerators = featureGenerators;
    }
    else {
      // use defaults
      
      this.featureGenerators =  new AdaptiveFeatureGenerator[1];
      
      this.featureGenerators[0] = new CachedFeatureGenerator(new AdaptiveFeatureGenerator[]{
          new WindowFeatureGenerator(new TokenFeatureGenerator(), 2, 2), 
          new WindowFeatureGenerator(new TokenClassFeatureGenerator(true), 2, 2), 
          new PreviousMapFeatureGenerator()});
    }    
  }
  
  public void addFeatureGenerator(AdaptiveFeatureGenerator generator) {
      AdaptiveFeatureGenerator generators[] = featureGenerators;
      
      featureGenerators = new AdaptiveFeatureGenerator[featureGenerators.length + 1];
      
      System.arraycopy(generators, 0, featureGenerators, 0, generators.length);
      
      featureGenerators[featureGenerators.length - 1] = generator;
  }
  
  public void updateAdaptiveData(String[] tokens, String[] outcomes) {
    
    if (tokens != null && outcomes != null && tokens.length != outcomes.length) {
        throw new IllegalArgumentException(
            "The tokens and outcome arrays MUST have the same size!");
      }
    
    for (int i = 0; i < featureGenerators.length; i++) {
      featureGenerators[i].updateAdaptiveData(tokens, outcomes);
    }    
  }
  
  public void clearAdaptiveData() {
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
   * @param index The index of the token in the specified toks array for which the context should be constructed. 
   * @param toks The tokens of the sentence.  The <code>toString</code> methods of these objects should return the token text.
   * @param preds The previous decisions made in the tagging of this sequence.  Only indices less than i will be examined.
   * @param additionalContext Addition features which may be based on a context outside of the sentence. 
   * @return the context for finding names at the specified index.
   */
  public String[] getContext(int index, Object[] toks, String[] preds, String[][] additionalContext) {
    String po=NameFinderME.OTHER;
    String ppo=NameFinderME.OTHER;

    if (index > 1){
      ppo = preds[index-2];
    }

    if (index > 0) {
      po = preds[index-1];
    }

    List features = new ArrayList();
    features.add("def");
    String[] tokens;
    if (prevTokens == toks) {
      tokens = prevStrings;
    }
    else {
      tokens = new String[toks.length];  
      for (int i = 0; i < toks.length; i++) {
        tokens[i] = toks[i].toString();
      }
      prevTokens = toks;
      prevStrings = tokens;
    }
    for (int i = 0; i < featureGenerators.length; i++) {
      featureGenerators[i].createFeatures(features, tokens, index, preds);
    }    
    if (additionalContext != null && additionalContext.length != 0) {
      for (int aci = 0; aci < additionalContext[index].length; aci++) {
        features.add(additionalContext[index][aci]);
      }
    }
    if (index == 0) {
      features.add("fwis"); //first word in sentence
    }
    String[] contexts = (String[]) features.toArray(new String[features.size() + 4]);

    contexts[features.size()] = "po=" + po;
    contexts[features.size() + 1] = "pow=" + po + "," + toks[index];
    contexts[features.size() + 2] = "powf=" + po + "," + FeatureGeneratorUtil.tokenFeature(toks[index].toString());
    contexts[features.size() + 3] = "ppo=" + ppo;
    return contexts;
  } 
}