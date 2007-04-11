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

import opennlp.tools.parser.AbstractContextGenerator;
import opennlp.tools.parser.Cons;
import opennlp.tools.parser.Parse;

public class AttachContextGenerator extends AbstractContextGenerator {

  
  public AttachContextGenerator(Set punctSet) {
    super();
    this.punctSet = punctSet;
  }

  public String[] getContext(Object o) {
    Object[] parts = (Object[]) o;
    return getContext((Parse[]) parts[0],((Integer)parts[1]).intValue(),(List) parts[2],((Integer)parts[3]).intValue());
  }
  
  private boolean containsPunct(Collection puncts, String punct){
    if (puncts != null){
      for (Iterator pi=puncts.iterator();pi.hasNext();) {
        Parse p = (Parse) pi.next();
        if (p.getType().equals(punct)) {
          return true;
        }
      }
    }
    return false;    
  }
  
  /**
   * 
   * @param constituents The constituents as they have been constructed so far.
   * @param index The constituent index of the node being attached.
   * @param rightFrontier The nodes which have been not attach to so far.
   * @return A set of contextual features about this attachment.
   */
  public String[] getContext(Parse[] constituents, int index, List rightFrontier, int rfi) {
    List features = new ArrayList(100);
    int nodeDistance = rfi;
    Parse fn = (Parse) rightFrontier.get(rfi);
    Parse fp = null;
    if (rfi+1 < rightFrontier.size()) {
      fp = (Parse) rightFrontier.get(rfi+1);
    }
    Parse p_1 = null;
    if (rightFrontier.size() > 0) {
      p_1 = (Parse) rightFrontier.get(0);
    }
    Parse p0 = constituents[index];
    Parse p1 = null;
    if (index+1 < constituents.length) {
      p1 = constituents[index+1];
    }
    
    Collection punct1s = null;
    Collection punct_1s = null;
    Collection punct_1fs = null;
    punct_1fs = fn.getPreviousPunctuationSet();
    punct_1s=p0.getPreviousPunctuationSet();
    punct1s=p0.getNextPunctuationSet();

    String consfp = cons(fp,-3);
    String consf = cons(fn,-2);
    String consp_1 = cons(p_1,-1);
    String consp0 = cons(p0,0);
    String consp1 = cons(p1,1);
    
    String consbofp = consbo(fp,-3);
    String consbof = consbo(fn,-2);
    String consbop_1 = consbo(p_1,-1);
    String consbop0 = consbo(p0,0);
    String consbop1 = consbo(p1,1);
    
    Cons cfp = new Cons(consfp,consbofp,-3,true);
    Cons cf = new Cons(consf,consbof,-2,true);
    Cons c_1 = new Cons(consp_1,consbop_1,-1,true);
    Cons c0 = new Cons(consp0,consbop0,0,true);
    Cons c1 = new Cons(consp1,consbop1,1,true);

    //default 
    features.add("default");

    //unigrams
    features.add(consfp);
    features.add(consbofp);
    features.add(consf);
    features.add(consbof);
    features.add(consp_1);
    features.add(consbop_1);
    features.add(consp0);
    features.add(consbop0);
    features.add(consp1);
    features.add(consbop1);
    
    //productions
    String prod = production(fn,false);
    //String punctProd = production(fn,true,punctSet);
    features.add("pn="+prod);
    features.add("pd="+prod+","+p0.getType());
    features.add("ps="+fn.getType()+"->"+fn.getType()+","+p0.getType());
    if (punct_1s != null) {
      StringBuffer punctBuf = new StringBuffer(5);
      for (Iterator pi=punct_1s.iterator();pi.hasNext();) {
        Parse punct = (Parse) pi.next();
        punctBuf.append(punct.getType()).append(",");
      }
      //features.add("ppd="+punctProd+","+punctBuf.toString()+p0.getType());
      //features.add("pps="+fn.getType()+"->"+fn.getType()+","+punctBuf.toString()+p0.getType());
    }
    
    //bi-grams
    //cons(fn),cons(0)
    cons2(features,cfp,c0,punct_1s,true);
    cons2(features,cf,c0,punct_1s,true);
    cons2(features,c_1,c0,punct_1s,true);
    cons2(features,c0,c1,punct1s,true);
    cons3(features,cf,c_1,c0,null,punct_1s,true,true,true);
    cons3(features,cf,c0,c1,punct_1s,punct1s,true,true,true);
    cons3(features,cfp,cf,c0,null,punct_1s,true,true,true);
    /*
    for (int ri=0;ri<rfi;ri++) {
      Parse jn = (Parse) rightFrontier.get(ri);
      features.add("jn="+jn.getType());
    }
    */
    int headDistance = (p0.getHeadIndex()-fn.getHeadIndex());
    features.add("hd="+headDistance);
    features.add("nd="+nodeDistance);
    
    features.add("nd="+p0.getType()+"."+nodeDistance);
    features.add("hd="+p0.getType()+"."+headDistance);
    //features.add("fs="+rightFrontier.size());
    //paired punct features
    if (containsPunct(punct_1s,"''")) {
      if (containsPunct(punct_1fs,"``")) {
        features.add("quotematch");//? not generating feature correctly
        
      }
      else {
        //features.add("noquotematch");
      }
    }
    return ((String[]) features.toArray(new String[features.size()]));
  }
}
