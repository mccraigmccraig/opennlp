///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2006 Thomas Morton
//
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////   

package opennlp.tools.parser.treeinsert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import opennlp.tools.parser.AbstractContextGenerator;
import opennlp.tools.parser.Parse;

public class CheckContextGenerator extends AbstractContextGenerator {

  private Parse[] leftNodes;
  
  public CheckContextGenerator(Set punctSet) {
    super();
    this.punctSet = punctSet;
    leftNodes = new Parse[2];
  }

  public String[] getContext(Object arg0) {
    // TODO Auto-generated method stub
    return null;
  }
  
  public String[] getContext(Parse parent, Parse[] constituents, int index, boolean trimFrontier) {
    List features = new ArrayList(100);
    //default 
    features.add("default");
    Parse[] children = Parser.collapsePunctuation(parent.getChildren(),punctSet);
    Parse pstart = children[0];
    Parse pend = children[children.length-1];
    String type = parent.getType();
    checkcons(pstart, "begin", type, features);
    checkcons(pend, "last", type, features);
    String production = "p="+production(parent,false);
    String punctProduction = "pp="+production(parent,true);
    features.add(production);
    features.add(punctProduction);
    
    
    Parse p1 = null;
    Parse p2 = null;
    Parse p_1 = null;
    Parse p_2 = null;
    Collection p1s = constituents[index].getNextPunctuationSet();
    Collection p2s = null;
    Collection p_1s = constituents[index].getPreviousPunctuationSet();
    Collection p_2s = null;
    List rf;
    if (index == 0) {
      rf = Collections.EMPTY_LIST;
    }
    else {
      rf = Parser.getRightFrontier(constituents[0],punctSet);
      if (trimFrontier) {
        int pi = rf.indexOf(parent);
        if (pi == -1) {
          throw new RuntimeException("Parent not found in right frontier:"+parent+" rf="+rf);
        }
        else {
          for (int ri=0;ri<=pi;ri++) {
            //System.err.println(pi+" removing "+((Parse)rf.get(0)).getType()+" "+rf.get(0)+" "+(rf.size()-1)+" remain");
            rf.remove(0);
          }
        }
      }
    }
    
    getFrontierNodes(rf,leftNodes);
    p_1 = leftNodes[0];
    p_2 = leftNodes[1];
    int ps = constituents.length;
    if (p_1 != null) {
      p_2s = p_1.getPreviousPunctuationSet();
    }
    if (index + 1 < ps) {
      p1 = constituents[index + 1];
      p2s = p1.getNextPunctuationSet();
    }
    if (index + 2 < ps) {
      p2 = constituents[index + 2];
    }
    surround(p_1, -1, type, p_1s, features);
    surround(p_2, -2, type, p_2s, features);
    surround(p1, 1, type, p1s, features);
    surround(p2, 2, type, p2s, features);

    return ((String[]) features.toArray(new String[features.size()]));
  }

}
