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
package opennlp.tools.parser;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.chunker.ChunkerContextGenerator;
import opennlp.tools.util.Sequence;

/**
 * @author Tom Morton
 *
 */
public class ChunkContextGenerator implements ChunkerContextGenerator {

  private static final String EOS = "eos";

  public String[] getContext(Object o) {
    Object[] data = (Object[]) o;
    return (getContext(((Integer) data[0]).intValue(), (List) data[1], (List) data[3], ((Sequence) data[2]).getOutcomes()));
  }
  
  public String[] getContext(int i, List toks, Sequence s, Object[] ac) {
    return getContext(i,toks,s.getOutcomes(),(List) ac[0]); 
  }

  public String[] getContext(int i, List toks, List preds, List tags) {
    return (getContext(i, toks.toArray(), (String[]) tags.toArray(new String[tags.size()]), (String[]) preds.toArray(new String[preds.size()])));
  }

  public String[] getContext(int i, Object[] words, String[] tags, String[] preds) {
    List features = new ArrayList(19);
    int x0 = i;
    int x_2 = x0 - 2;
    int x_1 = x0 - 1;
    int x2 = x0 + 2;
    int x1 = x0 + 1;
    String ct_2;
    String ctbo_2;
    String ct_1;
    String ctbo_1;
    String ct0;
    String ctbo0;
    String ct1;
    String ctbo1;
    String ct2;
    String ctbo2;

    // chunkandpostag(-2)
    if (x_2 >= 0) {
      String t_2 = tags[x_2];
      ct_2 = chunkandpostag(-2, words[x_2].toString(), tags[x_2], t_2);
      ctbo_2 = chunkandpostagbo(-2, tags[x_2], t_2);
    }
    else {
      ct_2 = chunkandpostag(-2, EOS, EOS, EOS);
      ctbo_2 = chunkandpostagbo(-2, EOS, EOS);
    }

    // chunkandpostag(-1)
    if (x_1 >= 0) {
      String t_1 = tags[x_1];
      ct_1 = chunkandpostag(-1, words[x_1].toString(), tags[x_1], t_1);
      ctbo_1 = chunkandpostagbo(-1, tags[x_1], t_1);
    }
    else {
      ct_1 = chunkandpostag(-1, EOS, EOS, EOS);
      ctbo_1 = chunkandpostagbo(-1, EOS, EOS);
    }

    // chunkandpostag(0)
    ct0 = chunkandpostag(0, words[x0].toString(), tags[x0], null);
    ctbo0 = chunkandpostagbo(0, tags[x0], null);

    // chunkandpostag(1)
    if (x1 < tags.length) {
      ct1 = chunkandpostag(1, words[x1].toString(), tags[x1], null);
      ctbo1 = chunkandpostagbo(1, tags[x1], null);
    }
    else {
      ct1 = chunkandpostag(1, EOS, EOS, EOS);
      ctbo1 = chunkandpostagbo(1, EOS, EOS);
    }

    // chunkandpostag(2)
    if (x2 < tags.length) {
      ct2 = chunkandpostag(2, words[x2].toString(), tags[x2], null);
      ctbo2 = chunkandpostagbo(2, tags[x2], null);
    }
    else {
      ct2 = chunkandpostag(2, EOS, EOS, EOS);
      ctbo2 = chunkandpostagbo(2, EOS, EOS);
    }

    features.add("default");
    features.add(ct_2);
    features.add(ctbo_2);
    features.add(ct_1);
    features.add(ctbo_1);
    features.add(ct0);
    features.add(ctbo0);
    features.add(ct1);
    features.add(ctbo1);
    features.add(ct2);
    features.add(ctbo2);

    //chunkandpostag(-1,0)
    features.add(ct_1 + "," + ct0);
    features.add(ctbo_1 + "," + ct0);
    features.add(ct_1 + "," + ctbo0);
    features.add(ctbo_1 + "," + ctbo0);

    //chunkandpostag(0,1)
    features.add(ct0 + "," + ct1);
    features.add(ctbo0 + "," + ct1);
    features.add(ct0 + "," + ctbo1);
    features.add(ctbo0 + "," + ctbo1);

    return ((String[]) features.toArray(new String[features.size()]));
  }

  private String chunkandpostag(int i, String tok, String tag, String chunk) {
    StringBuffer feat = new StringBuffer(20);
    feat.append(i).append("=").append(tok).append("|").append(tag);
    if (i < 0) {
      feat.append("|").append(chunk);
    }
    return (feat.toString());
  }

  private String chunkandpostagbo(int i, String tag, String chunk) {
    StringBuffer feat = new StringBuffer(20);
    feat.append(i).append("*=").append(tag);
    if (i < 0) {
      feat.append("|").append(chunk);
    }
    return (feat.toString());
  }
}