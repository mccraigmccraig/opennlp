///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2000 Jason Baldridge and Gann Bierner
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

package opennlp.tools.tokenize;

import java.util.ArrayList;
import java.util.List;

import opennlp.maxent.ContextGenerator;
import opennlp.tools.util.ObjectIntPair;


/**
 * Generate events for maxent decisions for tokenization.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.4 $, $Date: 2006/08/15 21:15:36 $
 */

public class TokContextGenerator implements ContextGenerator {

  public static final String SPLIT ="T";
  public static final String NO_SPLIT ="F";
  
  /**
   * Builds up the list of features based on the information in the Object,
   * which is a pair containing a String and and Integer which
   * indicates the index of the position we are investigating.
   */
  public String[] getContext(Object o) {
    String sb = (String)((ObjectIntPair)o).a;
    int id = ((ObjectIntPair)o).b;
    return getContext(sb,id);
  }

  /**
   * Returns an array of features for the specified sentence string at the specified index.
   * @param sentence The string for a sentence.
   * @param index The index to consider spliting as a token.
   * @return an array of features for the specified sentence string at the specified index.
   */
  public String[] getContext(String sentence, int index) {	
    List preds = new ArrayList();
    preds.add("p="+sentence.substring(0,index));
    preds.add("s="+sentence.substring(index));
    if (index>0) {
      addCharPreds("p1", sentence.charAt(index-1), preds);
      if (index>1) {
        addCharPreds("p2", sentence.charAt(index-2), preds);
        preds.add("p21="+sentence.charAt(index-2)+sentence.charAt(index-1));
      }
      else {
        preds.add("p2=bok");
      }
      preds.add("p1f1="+sentence.charAt(index-1)+sentence.charAt(index));
    }
    else {
      preds.add("p1=bok");
    }
    addCharPreds("f1",sentence.charAt(index), preds);
    if (index+1 < sentence.length()) {
      addCharPreds("f2", sentence.charAt(index+1), preds);
      preds.add("f12="+sentence.charAt(index)+sentence.charAt(index+1));
    }
    else {
      preds.add("f2=bok");
    }
    if (sentence.charAt(0) == '&' && sentence.charAt(sentence.length()-1) == ';') {
      preds.add("cc");//character code
    }
    
    String[] context = new String[preds.size()];
    preds.toArray(context);
    return context;
  }
    

  /**
   * Helper function for getContext.
   */
  private void addCharPreds(String key, char c, List preds) {
    preds.add(key + "=" + c);
    if (Character.isLetter(c)) {
      preds.add(key+"_alpha");
      if (Character.isUpperCase(c)) {
        preds.add(key+"_caps");
      }
    } 
    else if (Character.isDigit(c)) {
      preds.add(key+"_num");
    } 
    else if (Character.isWhitespace(c)) {
      preds.add(key+"_ws");
    } 
    else {
      if (c=='.' || c=='?' || c=='!') {
        preds.add(key+"_eos");
      } 
      else if (c=='`' || c=='"' || c=='\'') {
        preds.add(key+"_quote");
      } 
      else if (c=='[' || c=='{' || c=='(') {
        preds.add(key+"_lp");
      } 
      else if (c==']' || c=='}' || c==')') {
        preds.add(key+"_rp");
      }
    }
  }
}

