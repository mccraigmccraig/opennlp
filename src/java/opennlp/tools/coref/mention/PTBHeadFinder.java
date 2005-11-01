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
package opennlp.tools.coref.mention;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Finds head information from Penn Treebank style parses. 
 */
public final class PTBHeadFinder implements HeadFinder {

  private static PTBHeadFinder instance;
  private static Set skipSet = new HashSet();
  static {
    skipSet.add("POS");
    skipSet.add(",");
    skipSet.add(":");
    skipSet.add(".");
    skipSet.add("''");
    skipSet.add("-RRB-");
    skipSet.add("-RCB-");
  }

  private PTBHeadFinder() {}

  /**
   * Returns an instance of this head finder.
   * @return an instance of this head finder.
   */
  public static HeadFinder getInstance() {
    if (instance == null) {
      instance = new PTBHeadFinder();
    }
    return (instance);
  }

  public Parse getHead(Parse p) {
    if (p == null) {
      return (null);
    }
    if (p.isNounPhrase()) {
      List parts = p.getSyntacticChildren();
      //shallow parse POS
      if (parts.size() > 2) {
        Parse child0 = (Parse) parts.get(0);
        Parse child1 = (Parse) parts.get(1);
        Parse child2 = (Parse) parts.get(2);
        if (child1.isToken() && child1.getSyntacticType().equals("POS") && child0.isNounPhrase() && child2.isNounPhrase()) {
          return (child2);
        }
      }
      //full parse POS
      if (parts.size() > 1) {
        Parse child0 = (Parse) parts.get(0);
        if (child0.isNounPhrase()) {
          List ctoks = child0.getTokens();
          if (ctoks.size() == 0) {
            System.err.println("PTBHeadFinder: NP "+child0+" with no tokens");
          }
          Parse tok = (Parse) ctoks.get(ctoks.size() - 1);
          if (tok.getSyntacticType().equals("POS")) {
            return (null);
          }
        }
      }
      //coordinated nps are their own entities
      if (parts.size() > 1) {
        for (int pi = 1; pi < parts.size() - 1; pi++) {
          Parse child = (Parse) parts.get(pi);
          if (child.isToken() && child.getSyntacticType().equals("CC")) {
            return (null);
          }
        }
      }
      //all other NPs
      for (int pi = 0; pi < parts.size(); pi++) {
        Parse child = (Parse) parts.get(pi);
        //System.err.println("PTBHeadFinder.getHead: "+p.getSyntacticType()+" "+p+" child "+pi+"="+child.getSyntacticType()+" "+child);
        if (child.isNounPhrase()) {
          return (child);
        }
      }
      return (null);
    }
    else {
      return (null);
    }
  }

  public int getHeadIndex(Parse p) {
    List sChildren = p.getSyntacticChildren();
    boolean countTokens = false;
    int tokenCount = 0;
    //check for NP -> NN S type structures and return last token before S as head.
    for (int sci=0,scn = sChildren.size();sci<scn;sci++) {
      Parse sc = (Parse) sChildren.get(sci);
      //System.err.println("PTBHeadFinder.getHeadIndex "+p+" "+p.getSyntacticType()+" sChild "+sci+" type = "+sc.getSyntacticType());
      if (sc.getSyntacticType().startsWith("S")) {
        if (sci != 0) {
          countTokens = true;
        }
        else {
          //System.err.println("PTBHeadFinder.getHeadIndex(): NP -> S production assuming right-most head");
        }
      }
      if (countTokens) {
        tokenCount+=sc.getTokens().size();
      }
    }
    List toks = p.getTokens();
    if (toks.size() == 0) {
      System.err.println("PTBHeadFinder.getHeadIndex(): empty tok list for parse "+p);
    }
    for (int ti = toks.size() - tokenCount -1; ti >= 0; ti--) {
      Parse tok = (Parse) toks.get(ti);
      if (!skipSet.contains(tok.getSyntacticType())) {
        return (ti);
      }
    }
    //System.err.println("PTBHeadFinder.getHeadIndex: "+p+" hi="+toks.size()+"-"+tokenCount+" -1 = "+(toks.size()-tokenCount -1));
    return (toks.size() - tokenCount -1);
  }

  /** Returns the bottom-most head of a <code>Parse</code>.  If no
      head is available which is a child of <code>p</code> then
      <code>p</code> is returned. */
  public Parse getLastHead(Parse p) {
    Parse head;
    //System.err.print("EntityFinder.getLastHead: "+p);

    while (null != (head = getHead(p))) {
      //System.err.print(" -> "+head);
      //if (p.getEntityId() != -1 && head.getEntityId() != p.getEntityId()) {	System.err.println(p+" ("+p.getEntityId()+") -> "+head+" ("+head.getEntityId()+")");      }
      p = head;
    }
    //System.err.println(" -> null");
    return (p);
  }

  public Parse getHeadToken(Parse p) {
    List toks = p.getTokens();
    return ((Parse) toks.get(getHeadIndex(p)));
  }

}
