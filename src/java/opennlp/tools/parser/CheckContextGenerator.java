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

/**
 * Class for generating predictive context for deciding when a constituent is complete.
 * @author Tom Morton
 *
 */
public class CheckContextGenerator implements ContextGenerator {

  private static final String EOS = "eos";
  
  /**
   * Creates a new context generator for generating predictive context for deciding when a constituent is complete.
   */
  public CheckContextGenerator() {
    super();
  }

  public String[] getContext(Object o) {
    Object[] params = (Object[]) o;
    return getContext((List) params[0], (String) params[1], ((Integer) params[2]).intValue(), ((Integer) params[3]).intValue());
  }

  private void surround(Parse p, int i, String type, List features) {
    StringBuffer feat = new StringBuffer(20);
    feat.append("s").append(i).append("=");
    if (p != null) {
      feat.append(p.getHead().toString()).append("|").append(type).append("|").append(p.getHead().getType());
    }
    else {
      feat.append(EOS).append("|").append(type).append("|").append(EOS);
    }
    features.add(feat.toString());
    feat.setLength(0);
    feat.append("s").append(i).append("*=");
    if (p != null) {
      feat.append(type).append("|").append(p.getHead().getType());
    }
    else {
      feat.append(type).append("|").append(EOS);
    }
    features.add(feat.toString());
  }

  private void checkcons(Parse p, String i, String type, List features) {
    StringBuffer feat = new StringBuffer(20);
    feat.append("c").append(i).append("=").append(p.getType()).append("|").append(p.getHead().toString()).append("|").append(type);
    features.add(feat.toString());
    feat.setLength(0);
    feat.append("c").append(i).append("*=").append(p.getType()).append("|").append(type);
    features.add(feat.toString());
  }

  private void checkcons(Parse p1, Parse p2, String type, List features) {
    StringBuffer feat = new StringBuffer(20);
    feat.append("cil=").append(type).append(",").append(p1.getType()).append("|").append(p1.getHead().toString()).append(",").append(p2.getType()).append("|").append(p2.getHead().toString());
    features.add(feat.toString());
    feat.setLength(0);
    feat.append("ci*l=").append(type).append(",").append(p1.getType()).append(",").append(p2.getType()).append("|").append(p2.getHead().toString());
    features.add(feat.toString());
    feat.setLength(0);
    feat.append("cil*=").append(type).append(",").append(p1.getType()).append("|").append(p1.getHead().toString()).append(",").append(p2.getType());
    features.add(feat.toString());
    feat.setLength(0);
    feat.append("ci*l*=").append(type).append(",").append(p1.getType()).append(",").append(p2.getType());
    features.add(feat.toString());
  }

  /**
   * Returns predictive context for deciding whether the specified constituents between the specified start and end index 
   * can be combined to form a new constituent of the specified type.  
   * @param constituents The constituents which have yet to be combined into new constituents.
   * @param type The type of the new constituent proposed.
   * @param start The first constituent of the proposed constituent.
   * @param end The last constituent of the proposed constituent.
   * @return The predictive context for deciding whether a new constituent should be created.
   */
  public String[] getContext(List constituents, String type, int start, int end) {
    int ps = constituents.size();
    List features = new ArrayList(100);

    //default 
    features.add("default");

    Parse pstart = (Parse) constituents.get(start);
    Parse pend = (Parse) constituents.get(end);
    checkcons(pstart, "begin", type, features);
    checkcons(pend, "last", type, features);
    StringBuffer production = new StringBuffer(20);
    production.append(type).append("->");
    for (int pi = start; pi < end; pi++) {
      Parse p = (Parse) constituents.get(pi);
      checkcons(p, pend, type, features);
      production.append(p.getType()).append(",");
    }
    production.append(pend.getType());
    features.add(production.toString());
    Parse p_2 = null;
    Parse p_1 = null;
    Parse p1 = null;
    Parse p2 = null;
    if (start - 2 >= 0) {
      p_2 = (Parse) constituents.get(start - 2);
    }
    if (start - 1 >= 0) {
      p_1 = (Parse) constituents.get(start - 1);
    }
    if (end + 1 < ps) {
      p1 = (Parse) constituents.get(end + 1);
    }
    if (end + 2 < ps) {
      p2 = (Parse) constituents.get(end + 2);
    }
    surround(p_1, -1, type, features);
    surround(p_2, -2, type, features);
    surround(p1, 1, type, features);
    surround(p2, 2, type, features);
    return ((String[]) features.toArray(new String[features.size()]));
  }
}
