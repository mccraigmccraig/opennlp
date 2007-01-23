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

import java.util.List;

/**
 * 
 */
public class TokenContextFeatureGenerator implements FeatureGenerator {
  
  public void createFeatures(List feats, String[] toks, int i) {
    
    feats.add("def");

    //current word
    String w = toks[i].toLowerCase();
    feats.add("w=" + w);
    String wf = FeatureGeneratorUtil.tokenFeature(toks[i]);
    feats.add("wf=" + wf);
    feats.add("w&wf=" + w + "," + wf);
    

    // previous previous word
    if (i - 2 >= 0) {
      String ppw = toks[i - 2].toLowerCase();
      feats.add("ppw=" + ppw);
      String ppwf = FeatureGeneratorUtil.tokenFeature(toks[i - 2].toString());
      feats.add("ppwf=" + ppwf);
      feats.add("ppw&f=" + ppw + "," + ppwf);
    }
    else {
      feats.add("ppw=BOS");
    }
    // previous word
    if (i == 0) {
      feats.add("pw=BOS");
      feats.add("pw=BOS,w=" + w);
      feats.add("pwf=BOS,wf" + wf);
    }
    else {
      String pw = toks[i - 1].toLowerCase();
      feats.add("pw=" + pw);
      String pwf = FeatureGeneratorUtil.tokenFeature(toks[i - 1].toString());
      feats.add("pwf=" + pwf);
      feats.add("pw&f=" + pw + "," + pwf);
      feats.add("pw=" + pw + ",w=" + w);
      feats.add("pwf=" + pwf + ",wf=" + wf);
    }
    //next word
    if (i + 1 >= toks.length) {
      feats.add("nw=EOS");
      feats.add("w=" + w + ",nw=EOS");
      feats.add("wf=" + wf + ",nw=EOS");
    }
    else {
      String nw = toks[i + 1].toLowerCase();
      feats.add("nw=" + nw);
      String nwf = FeatureGeneratorUtil.tokenFeature(toks[i + 1].toString());
      feats.add("nwf=" + nwf);
      feats.add("nw&f=" + nw + "," + nwf);
      feats.add("w=" + w + ",nw=" + nw);
      feats.add("wf=" + wf + ",nwf=" + nwf);
    }
    if (i + 2 >= toks.length) {
      feats.add("nnw=EOS");
    }
    else {
      String nnw = toks[i + 2].toLowerCase();
      feats.add("nnw=" + nnw);
      String nnwf = FeatureGeneratorUtil.tokenFeature(toks[i + 2].toString());
      feats.add("nnwf=" + nnwf);
      feats.add("nnw&f=" + nnw + "," + nnwf);
    }

  }
}