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

import java.util.Collection;
import java.util.Iterator;

import opennlp.maxent.ContextGenerator;
import opennlp.tools.parser.Parse;

public abstract class AbstractContextGenerator implements ContextGenerator {

  protected static final String EOS = "eos";

  /**
   * Creates punctuation feature for the specified punctuation at the specfied index based on the punctuation mark.
   * @param punct The punctuation which is in context.
   * @param i The index of the punctuation with relative to the parse.
   * @return Punctuation feature for the specified parse and the specified punctuation at the specfied index.
   */
  protected String punct(Parse punct, int i) {
    StringBuffer feat = new StringBuffer(5);
    feat.append(i).append("=");
    feat.append(punct.toString());
    return (feat.toString());
  }

  /**
   * Creates punctuation feature for the specified punctuation at the specfied index based on the punctuation's tag.
   * @param punct The punctuation which is in context.
   * @param i The index of the punctuation with relative to the parse.
   * @return Punctuation feature for the specified parse and the specified punctuation at the specfied index.
   */
  protected String punctbo(Parse punct, int i) {
    StringBuffer feat = new StringBuffer(5);
    feat.append(i).append("=");
    feat.append(punct.getType());
    return (feat.toString());
  }

  protected String cons(Parse p, int i) {
    StringBuffer feat = new StringBuffer(20);
    feat.append(i).append("=");
    if (p != null) {
      feat.append(p.getType()).append("|").append(p.getHead().toString());
    }
    else {
      feat.append(EOS);
    }
    return (feat.toString());
  }

  protected String consbo(Parse p, int i) { //cons back-off
    StringBuffer feat = new StringBuffer(20);
    feat.append(i).append("*=");
    if (p != null) {
      feat.append(p.getType());
    }
    else {
      feat.append(EOS);
    }
    return (feat.toString());
  }

  protected String production(Parse p, boolean includePunctuation) {
    StringBuffer production = new StringBuffer(20);
    production.append(p.getType()).append("->");
    Parse[] children = p.getChildren();
    for (int ci = 0; ci < children.length; ci++) {
      production.append(children[ci].getType());
      if (ci+1 != children.length) {
        production.append(",");
        Collection nextPunct = children[ci].getNextPunctuationSet();
        if (includePunctuation && nextPunct != null) {
          //TODO: make sure multiple punctuation comes out the same 
          for (Iterator pit=nextPunct.iterator();pit.hasNext();) {
            Parse punct = (Parse) pit.next();
            production.append(punct.getType()).append(",");
          }
        }
      }
    }
    return production.toString();
  }
   
}
