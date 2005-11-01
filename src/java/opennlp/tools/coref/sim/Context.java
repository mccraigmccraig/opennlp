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
import java.util.List;
import java.util.Set;

import opennlp.tools.coref.mention.Dictionary;
import opennlp.tools.coref.mention.DictionaryFactory;
import opennlp.tools.coref.mention.HeadFinder;
import opennlp.tools.coref.mention.Mention;
import opennlp.tools.coref.mention.Parse;
import opennlp.tools.util.Span;

/**
 * Specifies the context of a mention for computing gender, number, and semantic compatibility.
 */
public class Context extends Mention {

  protected String headTokenText;
  protected String headTokenTag;
  protected Set synsets;
  protected Object[] tokens;
  
  /** The token index in of the head word of this mention. */ 
  protected int headTokenIndex;
  
  public Context(Span span, Span headSpan, int entityId, Parse parse, String extentType, String nameType, HeadFinder headFinder) {
    super(span,headSpan,entityId,parse,extentType,nameType);
    init(headFinder);
  }

  public Context(Object[] tokens, String headToken, String headTag, String neType) {
    super(null,null,1,null,null,neType);
    this.tokens =tokens;
    this.headTokenIndex = tokens.length-1;
    this.headTokenText = headToken;
    this.headTokenTag = headTag;
    this.synsets = getSynsetSet(this);
  }
      
  public Context(Mention mention, HeadFinder headFinder) {
    super(mention);
    init(headFinder);
  }
  
  private void init(HeadFinder headFinder) {
    Parse head = headFinder.getLastHead(parse);
    List tokenList = head.getTokens();
    headTokenIndex = headFinder.getHeadIndex(head);
    Parse headToken = headFinder.getHeadToken(head);
    tokens = (Parse[]) tokenList.toArray(new Parse[tokenList.size()]);
    this.headTokenTag = headToken.getSyntacticType();
    this.headTokenText = headToken.toString();
    if (headTokenTag.startsWith("NN") && !headTokenTag.startsWith("NNP")) {
      this.synsets = getSynsetSet(this);
    }
    else {
      this.synsets=Collections.EMPTY_SET;
    }
  }
  
  
  public static Context[] constructContexts(Mention[] mentions,HeadFinder headFinder) {
    Context[] contexts = new Context[mentions.length];
    for (int mi=0;mi<mentions.length;mi++) {
      contexts[mi] = new Context(mentions[mi],headFinder);
    }
    return contexts;
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
  
  public String getHeadTokenText() {
    return headTokenText;
  }
  
  public String getHeadTokenTag() {
    return headTokenTag;
  }
  
  public Set getSynsets() {
    return synsets;
  }
    
  public static Context parseContext(String word) {
      String[] parts = word.split("/");
      if (parts.length == 2) {
        String[] tokens = parts[0].split(" ");
        return new Context(tokens,tokens[tokens.length-1], parts[1], null);
      }
      else if (parts.length == 3) {
        String[] tokens = parts[0].split(" ");
        return new Context(tokens,tokens[tokens.length-1], parts[1], parts[2]);
      }
      return null;
    }

  private static Set getSynsetSet(Context c) {
    Set synsetSet = new HashSet();
    String[] lemmas = getLemmas(c);
    Dictionary dict = DictionaryFactory.getDictionary();
    //System.err.println(lemmas.length+" lemmas for "+c.headToken);
    for (int li = 0; li < lemmas.length; li++) {
      synsetSet.add(dict.getSenseKey(lemmas[li],"NN",0));
      String[] synsets = dict.getParentSenseKeys(lemmas[li],"NN",0);
      for (int si=0,sn=synsets.length;si<sn;si++) {
        synsetSet.add(synsets[si]);
      }
    }
    return (synsetSet);
  }

  private static String[] getLemmas(Context c) {
    String word = c.headTokenText.toLowerCase();
    return DictionaryFactory.getDictionary().getLemmas(word,"NN");
  }

  /** Returns the token index into the mention for the head word. 
   * @return the token index into the mention for the head word. 
   */
  public int getHeadTokenIndex() {
    return headTokenIndex;
  }
}
