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
package opennlp.tools.coref.sim;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import opennlp.tools.coref.Dictionary;
import opennlp.tools.coref.MentionContext;

/**
 * @author Tom Morton
 *
 */
public class Context {

  private String headToken;
  private String headTag;
  private Set synsets;
  private String neType;
  private Object[] tokens;
  
  private static Dictionary dictionary;

  public Context(Object[] tokens, String headToken, String headTag, String neType, Set synsets) {
    this.tokens = tokens;
    this.headToken = headToken;
    this.headTag = headTag;
    this.synsets = synsets;
    this.neType = neType;  
  }

  public Context(Object[] tokens, String headToken, String headTag, String neType) {
    this(tokens, headToken, headTag, neType, null);
    this.synsets = getSynsetSet(this);
  }
    
  public static void setDictionary(Dictionary dict) {
    Context.dictionary = dict;
  }
  

  public static Context getContext(MentionContext ec) {
    return new Context(ec.getTokens(),ec.getHeadTokenText(), ec.getHeadTokenTag(), ec.getNeType(), ec.getSynsets());
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer();
    for (int ti=0,tl=tokens.length;ti<tl;ti++){
      sb.append(tokens[ti]).append(" ");
    }
    return sb.toString();
  }
  
  public Object[] getTokens() {
    return tokens;
  }
  
  public String getHeadToken() {
    return headToken;
  }
  
  public String getHeadTag() {
    return headTag;
  }
  
  public Set getSynsets() {
    return synsets;
  }
  
  public String getNameType() {
    return neType;
  }
  
  public static Context parseContext(String word) {
      String[] parts = word.split("/");
      Context c1 = null;
      if (parts.length == 2) {
        String[] tokens = parts[0].split(" ");
        return new Context(tokens,tokens[tokens.length-1], parts[1], null);
      }
      else if (parts.length == 3) {
        String[] tokens = parts[0].split(" ");
        return new Context(tokens,tokens[tokens.length-1], parts[1], parts[2], Collections.EMPTY_SET);
      }
      return null;
    }

  private static Set getSynsetSet(Context c) {
    Set synsetSet = new HashSet();
    String[] lemmas = getLemmas(c);
    //System.err.println(lemmas.length+" lemmas for "+c.headToken);
    for (int li = 0; li < lemmas.length; li++) {
      String[] synsets = dictionary.getParentSenseKeys(lemmas[li],"NN",0);
      for (int si=0,sn=synsets.length;si<sn;si++) {
        synsetSet.add(synsets[si]);
      }
    }
    return (synsetSet);
  }

  private static String[] getLemmas(Context c) {
    String word = c.headToken.toLowerCase();
    return dictionary.getLemmas(word,"NN");
  }
}
