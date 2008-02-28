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
import java.util.regex.Pattern;

import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;

/**
 * Partitions tokens into sub-tokens based on character classes and generates 
 * class features for each of the sub-tokens and combinations of those sub-tokens. 
 */
public class TokenPatternFeatureGenerator extends FeatureGeneratorAdapter {
    
    private Pattern noLetters = Pattern.compile("[^a-zA-Z]");
    private Tokenizer tokenizer;

    /**
     * Initializes a new instance. 
     * For tokinization the {@link SimpleTokenizer} is used.
     */
    public TokenPatternFeatureGenerator() {
        this(new SimpleTokenizer());
    }
    
    /**
     * Initializes a new instance.
     * 
     * @param supportTokenizer
     */
    public TokenPatternFeatureGenerator(Tokenizer supportTokenizer) {
        tokenizer = supportTokenizer;
    }

    public void createFeatures(List feats, String[] toks, int index, String[] preds) {
      
      String[] tokenized = tokenizer.tokenize(toks[index]);
      
      if (tokenized.length == 1) {
        feats.add("st=" + toks[index].toLowerCase());
        return;
      }
      
      feats.add("stn=" + tokenized.length);
      
      StringBuffer pattern = new StringBuffer();
      
      for (int i = 0; i < tokenized.length; i++) {

        if (i < tokenized.length - 1) {
          feats.add("pt2=" + FeatureGeneratorUtil.tokenFeature(tokenized[i]) +
              FeatureGeneratorUtil.tokenFeature(tokenized[i + 1]));
        }
        
        if (i < tokenized.length - 2) {
          feats.add("pt3=" + FeatureGeneratorUtil.tokenFeature(tokenized[i]) +
              FeatureGeneratorUtil.tokenFeature(tokenized[i + 1]) + 
              FeatureGeneratorUtil.tokenFeature(tokenized[i + 2]));
        }
        
        pattern.append(FeatureGeneratorUtil.tokenFeature(tokenized[i]));
        
        if (!noLetters.matcher(tokenized[i]).find()) {
          feats.add("st=" + tokenized[i].toLowerCase());
        }
      }
      
      feats.add("pta=" + pattern.toString());
    }
}