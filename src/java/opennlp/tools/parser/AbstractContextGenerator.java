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

package opennlp.tools.parser;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import opennlp.maxent.ContextGenerator;

public abstract class AbstractContextGenerator implements ContextGenerator {

  protected static final String EOS = "eos";

  protected boolean zeroBackOff;
  /** Set of punctuation to be used in generating features. */
  protected Set punctSet;
  
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

  /**
   * Generates a string representing the grammar rule production that the specified parse
   * is starting.  The rule is of the form p.type -> c.children[0..n].type.
   * @param p The parse which stats teh production.
   * @param includePunctuation Whether punctuation should be included in the production.
   * @return a string representing the grammar rule production that the specified parse
   * is starting.
   */
  protected String production(Parse p, boolean includePunctuation) {
    StringBuffer production = new StringBuffer(20);
    production.append(p.getType()).append("->");
    Parse[] children = AbstractBottomUpParser.collapsePunctuation(p.getChildren(),punctSet);
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
   
  protected void cons2(List features, Cons c0, Cons c1, Collection punct1s, boolean bigram) {
    if (punct1s != null) {
      for (Iterator pi=punct1s.iterator();pi.hasNext();) {
        Parse p = (Parse) pi.next();
        String punct = punct(p,c1.index);
        String punctbo = punctbo(p,c1.index);
        //punct(1)
        features.add(punct);
        //punctbo(1);
        features.add(punctbo);
        //cons(0)punctbo(1)
        if (c0.unigram) features.add(c0.cons+","+punctbo);
        features.add(c0.consbo+","+punctbo);
        //cons(0)punctbo(1)cons(1)
        if (bigram) features.add(c0.cons+","+punctbo+","+c1.cons);
        if (c1.unigram)  features.add(c0.consbo+","+punctbo+","+c1.cons);
        if (c0.unigram)  features.add(c0.cons+","+punctbo+","+c1.consbo);
        features.add(c0.consbo+","+punctbo+","+c1.consbo);
      }
    }
    else {
      //cons(0),cons(1)
      if (bigram) features.add(c0.cons + "," + c1.cons);
      if (c1.unigram)  features.add(c0.consbo + "," + c1.cons);
      if (c0.unigram)  features.add(c0.cons + "," + c1.consbo);
      features.add(c0.consbo + "," + c1.consbo);      
    }
  }
  
  /**
   * Creates cons features involving the 3 specified nodes and adds them to the specified feature list.
   * @param features The list of features.
   * @param c0 The first node.
   * @param c1 The second node.
   * @param c2 The third node.
   * @param punct1s The punctuation between the first and second node.
   * @param punct2s The punctuation between the second and third node.
   * @param trigram Specifies whether lexical tri-gram features between these nodes should be generated.
   * @param bigram1 Specifies whether lexical bi-gram features between the first and second node should be generated.
   * @param bigram2 Specifies whether lexical bi-gram features between the second and third node should be generated.
   */
  protected void cons3(List features, Cons c0, Cons c1, Cons c2, Collection punct1s, Collection punct2s, boolean trigram, boolean bigram1, boolean bigram2) {
//  features.add("stage=cons(0),cons(1),cons(2)");
    if (punct2s != null) {
      for (Iterator pi=punct2s.iterator();pi.hasNext();) {
        Parse p = (Parse) pi.next();
        String punct = punct(p,c2.index);
        String punctbo = punctbo(p,c2.index);
        //punct(2)
        features.add(punct);
        //punctbo(2)
        features.add(punctbo);
      }
      if (punct1s != null) {
        //cons(0),punctbo(1),cons(1),punctbo(2),cons(2)
        for (Iterator pi2=punct2s.iterator();pi2.hasNext();) {
          String punctbo2 = punctbo((Parse) pi2.next(),c2.index);
          for (Iterator pi1=punct1s.iterator();pi1.hasNext();) {
            String punctbo1 = punctbo((Parse) pi1.next(),c1.index);
            if (trigram) features.add(c0.cons   + "," + punctbo1+","+c1.cons   + "," + punctbo2+","+c2.cons);
            
            if (bigram2) features.add(c0.consbo + "," + punctbo1+","+c1.cons   + "," + punctbo2+","+c2.cons);
            if (c0.unigram && c2.unigram) features.add(c0.cons   + "," + punctbo1+","+c1.consbo + "," + punctbo2+","+c2.cons);
            if (bigram1) features.add(c0.cons   + "," + punctbo1+","+c1.cons   + "," + punctbo2+","+c2.consbo);
            
            if (c2.unigram) features.add(c0.consbo + "," + punctbo1+","+c1.consbo + "," + punctbo2+","+c2.cons);
            if (c1.unigram) features.add(c0.consbo + "," + punctbo1+","+c1.cons   + "," + punctbo2+","+c2.consbo);
            if (c0.unigram) features.add(c0.cons   + "," + punctbo1+","+c1.consbo + "," + punctbo2+","+c2.consbo);
            
            features.add(c0.consbo + "," + punctbo1+","+c1.consbo + "," + punctbo2+","+c2.consbo);
            if (zeroBackOff) {
              if (bigram1) features.add(c0.cons   + "," + punctbo1+","+c1.cons   + "," + punctbo2);
              if (c1.unigram)  features.add(c0.consbo + "," + punctbo1+","+c1.cons   + "," + punctbo2);
              if (c0.unigram)  features.add(c0.cons   + "," + punctbo1+","+c1.consbo + "," + punctbo2);
              features.add(c0.consbo + "," + punctbo1+","+c1.consbo + "," + punctbo2);
            }
          }
        }
      }
      else { //punct1s == null
        //cons(0),cons(1),punctbo(2),cons(2)
        for (Iterator pi2=punct2s.iterator();pi2.hasNext();) {
          String punctbo2 = punctbo((Parse) pi2.next(),2);
          if (trigram) features.add(c0.cons   + "," + c1.cons   + "," + punctbo2+","+c2.cons);
          
          if (bigram2) features.add(c0.consbo + "," + c1.cons   +","  + punctbo2+ "," + c2.cons);
          if (c0.unigram && c1.unigram) features.add(c0.cons   + "," + c1.consbo + "," + punctbo2+","+c2.cons);
          if (bigram1) features.add(c0.cons   + "," + c1.cons   + "," + punctbo2+","+c2.consbo);
          
          if (c2.unigram) features.add(c0.consbo + "," + c1.consbo + "," + punctbo2+","+c2.cons);
          if (c1.unigram) features.add(c0.consbo + "," + c1.cons   + "," + punctbo2+","+c2.consbo);
          if (c0.unigram) features.add(c0.cons   + "," + c1.consbo + "," + punctbo2+","+c2.consbo);
          
          features.add(c0.consbo + "," + c1.consbo + "," + punctbo2+","+c2.consbo);
          
          if (zeroBackOff) {
            if (bigram1) features.add(c0.cons   + "," + c1.cons   + "," + punctbo2);
            if (c1.unigram)  features.add(c0.consbo + "," + c1.cons   + "," + punctbo2);
            if (c0.unigram)  features.add(c0.cons   + "," + c1.consbo + "," + punctbo2);
            features.add(c0.consbo + "," + c1.consbo + "," + punctbo2);
          }
        }
      }
    }
    else {
      if (punct1s != null) {
        //cons(0),punctbo(1),cons(1),cons(2)
        for (Iterator pi1=punct1s.iterator();pi1.hasNext();) {
          String punctbo1 = punctbo((Parse) pi1.next(),1);
          if (trigram) features.add(c0.cons     + "," + punctbo1   +","+ c1.cons   +","+c2.cons);
          
          if (bigram2) features.add(c0.consbo    + "," + punctbo1   +","+ c1.cons   +","+c2.cons);
          if (c0.unigram && c2.unigram) features.add(c0.cons + "," + punctbo1   +","+ c1.consbo +","+c2.cons);
          if (bigram1) features.add(c0.cons      + "," + punctbo1   +","+ c1.cons   +","+c2.consbo);
          
          if (c2.unigram) features.add(c0.consbo  + "," + punctbo1   +","+ c1.consbo +","+c2.cons);
          if (c1.unigram) features.add(c0.cons    + "," + punctbo1     +","+ c1.cons     +","+c2.consbo);
          if (c0.unigram) features.add(c0.cons    + "," + punctbo1   +","+ c1.consbo +","+c2.consbo);   
          
          features.add(c0.consbo + "," + punctbo1   +","+ c1.consbo +","+c2.consbo);
          
          //zero backoff case covered by cons(0)cons(1)
        }
      }
      else {
        //cons(0),cons(1),cons(2)
        if (trigram) features.add(c0.cons   + "," + c1.cons   + "," + c2.cons);
        
        if (bigram2) features.add(c0.consbo + "," + c1.cons   + "," + c2.cons);
        if (c0.unigram && c2.unigram) features.add(c0.cons   + "," + c1.consbo + "," + c2.cons);
        if (bigram1) features.add(c0.cons   + "," + c1.cons   + "," + c2.consbo);
        
        if (c2.unigram) features.add(c0.consbo + "," + c1.consbo + "," + c2.cons);
        if (c1.unigram) features.add(c0.consbo + "," + c1.cons   + "," + c2.consbo);
        if (c0.unigram) features.add(c0.cons   + "," + c1.consbo + "," + c2.consbo);
        
        features.add(c0.consbo + "," + c1.consbo + "," + c2.consbo);
      }
    }
  }

  /**
   * Generates features for nodes surrounding a completed node of the specified type.
   * @param node A surrounding node.
   * @param i The index of the surrounding node with respect to the completed node.
   * @param type The type of the completed node.
   * @param punctuation The punctuation adjacent and between the specified surrounding node.
   * @param features A list to which features are added.
   */
  protected void surround(Parse node, int i, String type, Collection punctuation, List features) {
    StringBuffer feat = new StringBuffer(20);
    feat.append("s").append(i).append("=");
    if (punctuation !=null) {
      for (Iterator pi=punctuation.iterator();pi.hasNext();) {
        Parse punct = (Parse) pi.next();
        if (node != null) {
          feat.append(node.getHead().toString()).append("|").append(type).append("|").append(node.getType()).append("|").append(punct.getType());
        }
        else {
          feat.append(type).append("|").append(EOS).append("|").append(punct.getType());
        }
        features.add(feat.toString());
        
        feat.setLength(0);
        feat.append("s").append(i).append("*=");
        if (node != null) {
          feat.append(type).append("|").append(node.getType()).append("|").append(punct.getType());
        }
        else {
          feat.append(type).append("|").append(EOS).append("|").append(punct.getType());
        }
        features.add(feat.toString());
  
        feat.setLength(0);
        feat.append("s").append(i).append("*=");
        feat.append(type).append("|").append(punct.getType());
        features.add(feat.toString());
      }
    }
    else {
      if (node != null) {
        feat.append(node.getHead().toString()).append("|").append(type).append("|").append(node.getType());
      }
      else {
        feat.append(type).append("|").append(EOS);
      }
      features.add(feat.toString());
      feat.setLength(0);
      feat.append("s").append(i).append("*=");
      if (node != null) {
        feat.append(type).append("|").append(node.getType());
      }
      else {
        feat.append(type).append("|").append(EOS);
      }
      features.add(feat.toString());
    }
  }

  /**
   * Produces features to determine whether the specified child node is part of
   * a complete constituent of the specified type and adds those features to the
   * specfied list. 
   * @param child The parse node to consider.
   * @param i A string indicating the position of the child node. 
   * @param type The type of constituent being built.
   * @param features List to add features to.
   */
  protected void checkcons(Parse child, String i, String type, List features) {
    StringBuffer feat = new StringBuffer(20);
    feat.append("c").append(i).append("=").append(child.getType()).append("|").append(child.getHead().toString()).append("|").append(type);
    features.add(feat.toString());
    feat.setLength(0);
    feat.append("c").append(i).append("*=").append(child.getType()).append("|").append(type);
    features.add(feat.toString());
  }

  protected void checkcons(Parse p1, Parse p2, String type, List features) {
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
   * Populates specified nodes array with left-most right frontier
   * node with a unique head. If the right frontier doesn't contain
   * enough nodes, then nulls are placed in the array elements. 
   * @param rf The current right frontier.
   * @param nodes The array to be populated.
   */
  protected void getFrontierNodes(List rf, Parse[] nodes) {
    int leftIndex = 0;
    int prevHeadIndex = -1;
    
    for (int fi=0;fi<rf.size();fi++) {
      Parse fn = (Parse) rf.get(fi);
      int headIndex = fn.getHeadIndex();
      if (headIndex != prevHeadIndex) {
        nodes[leftIndex] = fn;
        leftIndex++;
        prevHeadIndex = headIndex;
        if (leftIndex == nodes.length) {
          break;
        }
      }
    }
    for (int ni=leftIndex;ni<nodes.length;ni++){
      nodes[ni] = null;
    }
  }

}
