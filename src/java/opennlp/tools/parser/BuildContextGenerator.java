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

import opennlp.maxent.ContextGenerator;

public class BuildContextGenerator implements ContextGenerator {

  public BuildContextGenerator() {
    super();
  }

  public String[] getContext(Object o) {
    Object[] params = (Object[]) o;
    return getContext((List) params[0], ((Integer) params[1]).intValue());
  }

  private String cons(Parse p, int i) {
    StringBuffer feat = new StringBuffer(20);
    feat.append("cons(").append(i).append(")=");
    if (p != null) {
      if (p.getLabel() != null) {
        feat.append(p.getLabel()).append("|");
      }
      feat.append(p.getType()).append("|").append(p.getHead().toString());
    }
    else {
      feat.append("eos|eos|eos");
    }
    return (feat.toString());
  }

  private String consbo(Parse p, int i) { //cons back-off
    StringBuffer feat = new StringBuffer(20);
    feat.append("cons(").append(i).append("*)=");
    if (p != null) {
      if (p.getLabel() != null) {
        feat.append(p.getLabel()).append("|");
      }
      feat.append(p.getType());
    }
    else {
      feat.append("eos|eos");
    }
    return (feat.toString());
  }

  public String[] getContext(List parts, int index) {
    List features = new ArrayList(100);
    int ps = parts.size();

    //default 
    features.add("default");
    // cons(-2), cons(-1), cons(0), cons(1), cons(2)
    // cons(-2)
    Parse p_2 = null;
    Parse p_1 = null;
    Parse p0 = null;
    Parse p1 = null;
    Parse p2 = null;

    if (index - 2 >= 0) {
      p_2 = (Parse) parts.get(index - 2);
    }
    if (index - 1 >= 0) {
      p_1 = (Parse) parts.get(index - 1);
    }
    p0 = (Parse) parts.get(index);
    if (index + 1 < ps) {
      p1 = (Parse) parts.get(index + 1);
    }
    if (index + 2 < ps) {
      p2 = (Parse) parts.get(index + 2);
    }

    // cons(-2), cons(-1), cons(0), cons(1), cons(2)
    String consp_2 = cons(p_2, -2);
    String consp_1 = cons(p_1, -1);
    String consp0 = cons(p0, 0);
    String consp1 = cons(p1, 1);
    String consp2 = cons(p2, 2);

    String consbop_2 = consbo(p_2, -2);
    String consbop_1 = consbo(p_1, -1);
    String consbop0 = consbo(p0, 0);
    String consbop1 = consbo(p1, 1);
    String consbop2 = consbo(p2, 2);

    // cons(-2), cons(-1), cons(0), cons(1), cons(2)
    features.add(consp_2);
    features.add(consbop_2);
    features.add(consp_1);
    features.add(consbop_1);
    features.add(consp0);
    features.add(consbop0);
    features.add(consp1);
    features.add(consbop1);
    features.add(consp2);
    features.add(consbop2);

    // cons(-1,0), cons(0,1)
    features.add(consp_1 + "," + consp0);
    features.add(consbop_1 + "," + consp0);
    features.add(consp_1 + "," + consbop0);
    features.add(consbop_1 + "," + consbop0);

    features.add(consp0 + "," + consp1);
    features.add(consbop0 + "," + consp1);
    features.add(consp0 + "," + consbop1);
    features.add(consbop0 + "," + consbop1);

    // cons3(-2,-1,0), cons3(-1,0,1), cons3(0,1,2)
    features.add(consp_2 + "," + consp_1 + "," + consp0);
    features.add(consbop_2 + "," + consp_1 + "," + consp0);
    features.add(consp_2 + "," + consbop_1 + "," + consp0);
    features.add(consbop_2 + "," + consbop_1 + "," + consp0);
    features.add(consbop_2 + "," + consbop_1 + "," + consbop0);

    features.add(consp_1 + "," + consp0 + "," + consp1);
    features.add(consbop_1 + "," + consp0 + "," + consp1);
    features.add(consp_1 + "," + consp0 + "," + consbop1);
    features.add(consbop_1 + "," + consp0 + "," + consbop1);
    features.add(consbop_1 + "," + consbop0 + "," + consbop1);

    features.add(consp0 + "," + consp1 + "," + consp2);
    features.add(consp0 + "," + consbop1 + "," + consp2);
    features.add(consp0 + "," + consp1 + "," + consbop2);
    features.add(consp0 + "," + consbop1 + "," + consbop2);
    features.add(consbop0 + "," + consbop1 + "," + consbop2);

    // punct
    String p0Word = p0.toString();
    if (p0Word.equals("-RRB-")) {
      for (int pi = index - 1; pi >= 0; pi--) {
        Parse p = (Parse) parts.get(pi);
        if (p.toString().equals("-LRB-")) {
          features.add("bracketsmatch");
          break;
        }
        if (p.getLabel().startsWith(ParserME.START)) {
          break;
        }
      }
    }
    if (p0Word.equals("-RCB-")) {
      for (int pi = index - 1; pi >= 0; pi--) {
        Parse p = (Parse) parts.get(pi);
        if (p.toString().equals("-LCB-")) {
          features.add("bracketsmatch");
          break;
        }
        if (p.getLabel().startsWith(ParserME.START)) {
          break;
        }
      }
    }
    if (p0Word.equals("''")) {
      for (int pi = index - 1; pi >= 0; pi--) {
        Parse p = (Parse) parts.get(pi);
        if (p.toString().equals("``")) {
          features.add("quotesmatch");
          break;
        }
        if (p.getLabel().startsWith(ParserME.START)) {
          break;
        }
      }
    }
    if (p0Word.equals("'")) {
      for (int pi = index - 1; pi >= 0; pi--) {
        Parse p = (Parse) parts.get(pi);
        if (p.toString().equals("`")) {
          features.add("quotesmatch");
          break;
        }
        if (p.getLabel().startsWith(ParserME.START)) {
          break;
        }
      }
    }
    if (p0Word.equals(",")) {
      for (int pi = index - 1; pi >= 0; pi--) {
        Parse p = (Parse) parts.get(pi);
        if (p.toString().equals(",")) {
          features.add("iscomma");
          break;
        }
        if (p.getLabel().startsWith(ParserME.START)) {
          break;
        }
      }
    }
    if (p0Word.equals(".") && index == ps - 1) {
      for (int pi = index - 1; pi >= 0; pi--) {
        Parse p = (Parse) parts.get(pi);
        if (p.getLabel().startsWith(ParserME.START)) {
          if (pi == 0) {
            features.add("endofsentence");
          }
          break;
        }
      }
    }
    return ((String[]) features.toArray(new String[features.size()]));
  }

}
