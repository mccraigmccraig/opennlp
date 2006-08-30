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

import opennlp.maxent.ContextGenerator;

public abstract class AbstractContextGenerator implements ContextGenerator {

  protected static final String EOS = "eos";

  protected boolean zeroBackOff;
  
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
      else {
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

}
