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
import java.util.Iterator;
import java.util.List;

import opennlp.maxent.ContextGenerator;

/**
 * Class to generator predictive contexts for deciding how constituents should be combined together.
 * @author Tom Morton
 */
public class BuildContextGenerator implements ContextGenerator {

  private static final String EOS = "eos";
  
  /**
   * Creates a new context generator for making decisions about combining constitients togehter.
   *
   */
  public BuildContextGenerator() {
    super();
  }

  public String[] getContext(Object o) {
    Object[] params = (Object[]) o;
    return getContext((Parse[]) params[0], ((Integer) params[1]).intValue());
  }

  /**
   * Creates punctuation feature for the specified parse and the specified punctuation at the specfied index.
   * @param p The parse whichi is being considered.
   * @param punct The punctuation which is in context.
   * @param i The index of the punctuation with relative to the parse.
   * @param backoff Whether lexical information should be included.
   * @return Punctuation feature for the specified parse and the specified punctuation at the specfied index.
   */
  private String punct(Parse p, Parse punct, int i, boolean backoff) {
    StringBuffer feat = new StringBuffer(20);
    if (backoff) {
      feat.append(i).append("*=");
    }
    else {
      feat.append(i).append("=");
    }
    feat.append(punct.getType());
    if (p != null) {
      feat.append("|");
      //if (i < 0) {
      //  feat.append(p.getLabel()).append("|");
      //}
      feat.append(p.getType());
      if (!backoff) {
        feat.append("|").append(p.getHead().toString());
      }
    }
    return (feat.toString());
  }


  private String cons(Parse p, int i) {
    StringBuffer feat = new StringBuffer(20);
    feat.append(i).append("=");
    if (p != null) {
      if (i < 0) {
        feat.append(p.getLabel()).append("|");
      }
      feat.append(p.getType()).append("|").append(p.getHead().toString());
    }
    else {
      feat.append(EOS).append("|").append(EOS).append("|").append(EOS);
    }
    return (feat.toString());
  }

  private String consbo(Parse p, int i) { //cons back-off
    StringBuffer feat = new StringBuffer(20);
    feat.append(i).append("*=");
    if (p != null) {
      if (i < 0) {
        feat.append(p.getLabel()).append("|");
      }
      feat.append(p.getType());
    }
    else {
      feat.append(EOS).append("|").append(EOS);
    }
    return (feat.toString());
  }
  
  /**
   * Returns the predictive context used to determine how constituent at the specified index 
   * should be combined with other contisuents. 
   * @param constituents The constituents which have yet to be combined into new constituents.
   * @param index The index of the constituent whcihi is being considered.
   * @return the context for building constituents at the specified index.
   */
  public String[] getContext(Parse[] constituents, int index) {
    List features = new ArrayList(100);
    int ps = constituents.length;

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
      p_2 = constituents[index - 2];
    }
    if (index - 1 >= 0) {
      p_1 = constituents[index - 1];
    }
    p0 = constituents[index];
    if (index + 1 < ps) {
      p1 = constituents[index + 1];
    }
    if (index + 2 < ps) {
      p2 = constituents[index + 2];
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
    //punct(0,1)
    if (constituents[index].getNextPunctuationSet() != null) {
      //System.err.println("BuildContextGenerator.getContext: hasNextPunct: "+constituents[index]+" "+constituents[index].getNextPunctuationSet());
      for (Iterator pi=constituents[index].getNextPunctuationSet().iterator();pi.hasNext();) {
        Parse punct = (Parse) pi.next(); 
        features.add("p"+punct(null,punct,1,false));
        features.add("p"+punct(constituents[index],punct,1,false));
        features.add("p"+punct(constituents[index],punct,1,true));
      }
    }
    //punct(-1,0)
    if (constituents[index].getPreviousPunctuationSet() != null) {
      //System.err.println("BuildContextGenerator.getContext: hasPrevPunct: "+constituents[index].getPreviousPunctuationSet()+" "+constituents[index]);
      for (Iterator pi=constituents[index].getPreviousPunctuationSet().iterator();pi.hasNext();) {
        Parse punct = (Parse) pi.next(); 
        features.add("p"+punct(null,punct,-1,false));
        features.add("p"+punct(constituents[index],punct,-1,false));
        features.add("p"+punct(constituents[index],punct,-1,true));
      }      
    }
    
    //punct(-1),cons(0,1)
    //cons(-1),punct(-1),cons(0);
    //cons(-1,0),punct(1)
    
    
    String p0Word = p0.toString();
    if (p0Word.equals("-RRB-")) {
      for (int pi = index - 1; pi >= 0; pi--) {
        Parse p = constituents[pi];
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
        Parse p = constituents[pi];
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
        Parse p = constituents[pi];
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
        Parse p = constituents[pi];
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
        Parse p = constituents[pi];
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
        Parse p = constituents[pi];
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
