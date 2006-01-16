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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import opennlp.tools.parser.Parse;

public class BuildContextGenerator extends AbstractContextGenerator {

  
  public BuildContextGenerator() {
  }

  public String[] getContext(Object o) {
    Object[] parts = (Object[]) o;
    return getContext((Parse[]) parts[0],((Integer)parts[1]).intValue());
  }
    
  public String[] getContext(Parse[] constituents, int index) {
    Parse p0 = null;
    Parse p1 = null;
    Parse p2 = null;
    int ps = constituents.length;
    
    p0 = constituents[index];
    if (index + 1 < ps) {
      p1 = constituents[index + 1];
    }
    if (index +2 < ps) {
      p2 = constituents[index + 2];
    }
    
    Collection punct1s = null;
    Collection punct_1s = null;
    Collection punct2s = null;
    
    punct_1s=p0.getPreviousPunctuationSet();
    punct1s=p0.getNextPunctuationSet();
    if (p1 != null) {
      punct2s=p1.getNextPunctuationSet();
    }
    
    String consp0 = cons(p0, 0);
    String consp1 = cons(p1, 1);
    String consp2 = cons(p2, 2);
    String consbop0 = consbo(p0, 0);
    String consbop1 = consbo(p1, 1);
    String consbop2 = consbo(p2, 2);
    
    List features = new ArrayList();
    List rf;
    if (index == 0) {
      rf = Collections.EMPTY_LIST;
    }
    else {
      //this isn't a root node so, punctSet won't be used and can be passed as empty.
      rf = Parser.getRightFrontier(constituents[0],Collections.EMPTY_SET);
    }
    features.add("default");
    
    //unigrams
    features.add(consp0);
    features.add(consbop0);
    features.add(consp1);
    features.add(consbop1);
    features.add(consp2);
    features.add(consbop2);

    for (int fi=0;fi<rf.size();fi++) {
      Parse fn = (Parse) rf.get(fi);
      String consf = cons(fn,-1);
      String consbof = consbo(fn,-1);
      
      //cons(fn)
      features.add(consf);
      features.add(consbof);
      features.add(fi+","+consf);
      features.add(fi+","+consbof);
      //productions
      String pf = production(fn,false);
      features.add("p="+pf);
      features.add("p"+fi+"="+pf);
      //cons(fn),cons(p0)
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
        }
      }
      else {
        features.add(consf+","+consp0);
        features.add(consf+","+consbop0);
        features.add(consbof+","+consp0);
        features.add(consbof+","+consbop0);
      }
    }
    if (rf.isEmpty()) {
      features.add(EOS+","+consp0);
      features.add(EOS+","+consbop0);
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
    //cons(p0),cons(p1),cons(p2)
    if (punct2s != null) {
      for (Iterator pi=punct2s.iterator();pi.hasNext();) {
        Parse p = (Parse) pi.next();
        String punct = punct(p,2);
        String punctbo = punctbo(p,2);
        //punct(2)
        features.add(punct);
        //punctbo(2)
        features.add(punctbo);
      }
      if (punct1s != null) {
        //cons(0),punctbo(1),cons(1),punctbo(2),cons(2)
        for (Iterator pi2=punct2s.iterator();pi2.hasNext();) {
          String punctbo2 = punctbo((Parse) pi2.next(),2);
          for (Iterator pi1=punct1s.iterator();pi1.hasNext();) {
            String punctbo1 = punctbo((Parse) pi1.next(),1);
            features.add(consp0   + "," + punctbo1+","+consp1   + "," + punctbo2+","+consp2);
            
            features.add(consbop0 + "," + punctbo1+","+consp1   + "," + punctbo2+","+consp2);
            features.add(consp0   + "," + punctbo1+","+consbop1 + "," + punctbo2+","+consp2);
            features.add(consp0   + "," + punctbo1+","+consp1   + "," + punctbo2+","+consbop2);
            
            features.add(consbop0 + "," + punctbo1+","+consbop1 + "," + punctbo2+","+consp2);
            features.add(consbop0 + "," + punctbo1+","+consp1   + "," + punctbo2+","+consbop2);
            features.add(consp0   + "," + punctbo1+","+consbop1 + "," + punctbo2+","+consbop2);
            
            features.add(consbop0 + "," + punctbo1+","+consbop1 + "," + punctbo2+","+consbop2);
          }
        }
      }
      else {
        //cons(0),cons(1),punctbo(2),cons(2)
        for (Iterator pi2=punct2s.iterator();pi2.hasNext();) {
          String punctbo2 = punctbo((Parse) pi2.next(),2);
          features.add(consp0   + "," + consp1   + "," + punctbo2+","+consp2);
          
          features.add(consbop0 + "," + consp1   +","  + punctbo2+ "," + consp2);
          features.add(consp0   + "," + consbop1 + "," + punctbo2+","+consp2);
          features.add(consp0   + "," + consp1   + "," + punctbo2+","+consbop2);
          
          features.add(consbop0 + "," + consbop1 + "," + punctbo2+","+consp2);
          features.add(consbop0 + "," + consp1   + "," + punctbo2+","+consbop2);
          features.add(consp0   + "," + consbop1 + "," + punctbo2+","+consbop2);
          
          features.add(consbop0 + "," + consbop1 + "," + punctbo2+","+consbop2);
        }
      }
    }
    else {
      if (punct1s != null) {
        //cons(0),punctbo(1),cons(1),cons(2)
        for (Iterator pi1=punct1s.iterator();pi1.hasNext();) {
          String punctbo1 = punctbo((Parse) pi1.next(),1);
          features.add(consp0     + "," + punctbo1   +","+ consp1   +","+consp2);
          
          features.add(consbop0    + "," + punctbo1   +","+ consp1   +","+consp2);
          features.add(consp0 + "," + punctbo1   +","+ consbop1 +","+consp2);
          features.add(consp0      + "," + punctbo1   +","+ consp1   +","+consbop2);
          
          features.add(consbop0     + "," + punctbo1   +","+ consbop1 +","+consp2);
          features.add(consbop0     + "," + punctbo1   +","+ consp1 +","+consbop2);
          features.add(consp0       + "," + punctbo1   +","+ consbop1 +","+consbop2);   
          
          features.add(consbop0 + "," + punctbo1   +","+ consbop1 +","+consbop2);
        }
      }
      else {
        features.add(consp0   + "," + consp1   + "," + consp2);
        
        features.add(consbop0 + "," + consp1   + "," + consp2);
        features.add(consp0   + "," + consbop1 + "," + consp2);
        features.add(consp0   + "," + consp1   + "," + consbop2);
        
        features.add(consbop0 + "," + consbop1 + "," + consp2);
        features.add(consbop0 + "," + consp1   + "," + consbop2);
        features.add(consp0   + "," + consbop1 + "," + consbop2);
        
        features.add(consbop0 + "," + consbop1 + "," + consbop2);
      }
    }
    return (String[]) features.toArray(new String[features.size()]);
  }

}
