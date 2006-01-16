///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2006 Thomas Morton
// 
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////

package opennlp.tools.parser.treeinsert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import opennlp.tools.parser.Parse;

public class AttachContextGenerator extends AbstractContextGenerator {

  
  public AttachContextGenerator() {
    super();
  }

  public String[] getContext(Object o) {
    Object[] parts = (Object[]) o;
    return getContext((Parse[]) parts[0],(Parse) parts[1],((Integer)parts[2]).intValue(),(Set)parts[3]);
  }
  
  public String[] getContext(Parse[] constituents, Parse fn, int index, Set jumpedNodes) {
    List features = new ArrayList(100);
    int nodeDistance = jumpedNodes.size();
    Parse p0 = constituents[index];
    Parse p1 = null;
    if (index+1 < constituents.length) {
      p1 = constituents[index+1];
    }
    
    Collection punct1s = null;
    Collection punct_1s = null;
    punct_1s=p0.getPreviousPunctuationSet();
    punct1s=p0.getNextPunctuationSet();

    
    String consp0 = cons(p0,0);
    String consbop0 = cons(p0,0);
    String consf = cons(fn,-1);
    String consbof = consbo(fn,-1);
    String consp1 = cons(p1,1);
    String consbop1 = cons(p1,1);
    
    //default 
    features.add("default");

    //unigrams
    features.add(consf);
    features.add(consbof);
    features.add(consp0);
    features.add(consbop0);
    features.add(consp1);
    features.add(consbop1);
    
    //productions
    String prod = production(fn,false);
    String punctProd = production(fn,true);
    features.add("pd="+prod+","+p0.getType());
    features.add("ps="+fn.getType()+"->"+fn.getType()+","+p0.getType());
    if (punct_1s != null) {
      StringBuffer punctBuf = new StringBuffer(5);
      for (Iterator pi=punct_1s.iterator();pi.hasNext();) {
        Parse punct = (Parse) pi.next();
        punctBuf.append(punct.getType()).append(",");
      }
      features.add("ppd="+punctProd+","+punctBuf.toString()+p0.getType());
      features.add("pps="+fn.getType()+"->"+fn.getType()+","+punctBuf.toString()+p0.getType());
    }
    
    //bi-grams
    //cons(fn),cons(0)
    if (punct_1s != null) {
      for (Iterator pi=punct_1s.iterator();pi.hasNext();) {
        Parse p = (Parse) pi.next();
        String punct = punct(p,-1);
        String punctbo = punctbo(p,-1);
        //punct(-1)
        features.add(punct);
        //punctbo(-1);
        features.add(punctbo);
        
        features.add(consf+","+punctbo+","+consp0);
        features.add(consf+","+punctbo+","+consbop0);
        features.add(consbof+","+punctbo+","+consp0);
        features.add(consbof+","+punctbo+","+consbop0);
        features.add(consbof+","+punctbo+","+consbop0+","+nodeDistance);
      }
    }
    else {
      features.add(consf+","+consp0);
      features.add(consf+","+consbop0);
      features.add(consbof+","+consp0);
      features.add(consbof+","+consbop0);
      features.add(consbof+","+consbop0+","+nodeDistance);
    }
    //cons(p0),cons(p1)
    if (punct1s != null) {
      for (Iterator pi=punct1s.iterator();pi.hasNext();) {
        Parse p = (Parse) pi.next();
        String punct = punct(p,1);
        String punctbo = punctbo(p,1);
        //punct(1)
        features.add(punct);
        //punctbo(1);
        features.add(punctbo);
        
        features.add(consp0+","+punctbo+","+consp1);
        features.add(consp0+","+punctbo+","+consbop1);
        features.add(consbop0+","+punctbo+","+consp1);
        features.add(consbop0+","+punctbo+","+consbop1);
      }
    }
    else {
      features.add(consp0+","+consp1);
      features.add(consp0+","+consbop1);
      features.add(consbop0+","+consp1);
      features.add(consbop0+","+consbop1);
    }
    //tri-grams
    if (punct_1s != null && punct1s != null) {
      for (Iterator pi_1=punct_1s.iterator();pi_1.hasNext();) {
        String punctbo_1 = punctbo((Parse) pi_1.next(),-1);
        for (Iterator pi1=punct1s.iterator();pi1.hasNext();) {
          String punctbo1 = punctbo((Parse) pi1.next(),1);
          features.add(consf  +","+punctbo_1+","+consp0  +","+punctbo1+","+consp1);
          features.add(consbof+","+punctbo_1+","+consp0  +","+punctbo1+","+consp1);
          features.add(consf  +","+punctbo_1+","+consbop0+","+punctbo1+","+consp1);
          features.add(consf  +","+punctbo_1+","+consp0  +","+punctbo1+","+consbop1);
          features.add(consbof+","+punctbo_1+","+consbop0+","+punctbo1+","+consp1);
          features.add(consf  +","+punctbo_1+","+consbop0+","+punctbo1+","+consbop1);
          features.add(consbof+","+punctbo_1+","+consbop0+","+punctbo1+","+consbop1);
        }
      }
    }
    else if (punct_1s != null) {
      for (Iterator pi_1=punct_1s.iterator();pi_1.hasNext();) {
        String punctbo_1 = punctbo((Parse) pi_1.next(),-1);
        features.add(consf  +","+punctbo_1+","+consp0  +","+consp1);
        features.add(consbof+","+punctbo_1+","+consp0  +","+consp1);
        features.add(consf  +","+punctbo_1+","+consbop0+","+consp1);
        features.add(consf  +","+punctbo_1+","+consp0  +","+consbop1);
        features.add(consbof+","+punctbo_1+","+consbop0+","+consp1);
        features.add(consf  +","+punctbo_1+","+consbop0+","+consbop1);
        features.add(consbof+","+punctbo_1+","+consbop0+","+consbop1);
      }
    }
    else if (punct1s != null) {
      for (Iterator pi1=punct1s.iterator();pi1.hasNext();) {
        String punctbo1 = punctbo((Parse) pi1.next(),1);
        features.add(consf  +","+consp0  +","+punctbo1+","+consp1);
        features.add(consbof+","+consp0  +","+punctbo1+","+consp1);
        features.add(consf  +","+consbop0+","+punctbo1+","+consp1);
        features.add(consf  +","+consp0  +","+punctbo1+","+consbop1);
        features.add(consbof+","+consbop0+","+punctbo1+","+consp1);
        features.add(consf  +","+consbop0+","+punctbo1+","+consbop1);
        features.add(consbof+","+consbop0+","+punctbo1+","+consbop1);
      }
    }
    else {
      features.add(consf  +","+consp0  +","+consp1);
      features.add(consbof+","+consp0  +","+consp1);
      features.add(consf  +","+consbop0+","+consp1);
      features.add(consf  +","+consp0  +","+consbop1);
      features.add(consbof+","+consbop0+","+consp1);
      features.add(consf  +","+consbop0+","+consbop1);
      features.add(consbof+","+consbop0+","+consbop1);
    }
    for (Iterator ji=jumpedNodes.iterator();ji.hasNext();) {
      Parse jn = (Parse) ji.next();
      features.add("jn="+jn.getType());
    }
    features.add("hd="+(p0.getHeadIndex()-fn.getHeadIndex()));
    features.add("nd="+nodeDistance);
    return ((String[]) features.toArray(new String[features.size()]));
  }
}
