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
//GNU General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////
package opennlp.tools.coref.mention;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import opennlp.tools.lang.english.NameFinder;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.chunking.Parser;
import opennlp.tools.util.Span;

/**
 * This class is a wrapper for {@link opennlp.tools.parser.Parse} mapping it to the API specified in {@link opennlp.tools.coref.mention.Parse}.
 *  This allows coreference to be done on the output of the parser.
 */
public class DefaultParse extends AbstractParse {

  private Parse parse;
  private int sentenceNumber;
  private static Set entitySet = new HashSet(Arrays.asList(NameFinder.NAME_TYPES));
  
  /**
   * Initializes the current instance.
   * 
   * @param parse
   * @param sentenceNumber
   */
  public DefaultParse(Parse parse, int sentenceNumber) {
    this.parse = parse;
    this.sentenceNumber = sentenceNumber;
  }
  
  public int getSentenceNumber() {
    return sentenceNumber;
  }
  
  public List getNamedEntities() {
    List names = new ArrayList();
    List kids = new LinkedList(Arrays.asList(parse.getChildren()));
    while (kids.size() > 0) {
      Parse p = (Parse) kids.remove(0);
      if (entitySet.contains(p.getType())) {
        names.add(p);
      }
      else {
        kids.addAll(Arrays.asList(p.getChildren()));
      }
    }
    return createParses((Parse[]) names.toArray(new Parse[names.size()]));
  }

  public List getChildren() {
    return createParses(parse.getChildren());
  }

  public List getSyntacticChildren() {
    List kids = new ArrayList(Arrays.asList(parse.getChildren()));
    for (int ci = 0; ci < kids.size(); ci++) {
      Parse kid = (Parse) kids.get(ci);
      if (entitySet.contains(kid.getType())) {
        kids.remove(ci);
        kids.addAll(ci, Arrays.asList(kid.getChildren()));
        ci--;
      }
    }
    return createParses((Parse[]) kids.toArray(new Parse[kids.size()]));
  }

  public List getTokens() {
    List tokens = new ArrayList();
    List kids = new LinkedList(Arrays.asList(parse.getChildren()));
    while (kids.size() > 0) {
      Parse p = (Parse) kids.remove(0);
      if (p.isPosTag()) {
        tokens.add(p);
      }
      else {
        kids.addAll(0,Arrays.asList(p.getChildren()));
      }
    }
    return createParses((Parse[]) tokens.toArray(new Parse[tokens.size()]));
  }

  public String getSyntacticType() {
    if (entitySet.contains(parse.getType())) {
      return null;
    }
    else {
      return parse.getType();
    }
  }
  
  private List createParses(Parse[] parses) {
    List newParses = new ArrayList(parses.length);
    for (int pi=0,pn=parses.length;pi<pn;pi++) {
      newParses.add(new DefaultParse(parses[pi],sentenceNumber));
    }
    return newParses;
  }

  public String getEntityType() {
    if (entitySet.contains(parse.getType())) {
      return parse.getType();
    }
    else {
      return null;
    }
  }

  public boolean isParentNAC() {
    Parse parent = parse.getParent();
    while(parent != null) {
      if (parent.getType().equals("NAC")) {
        return true;
      }
      parent = parent.getParent();
    }
    return false;
  }

  public opennlp.tools.coref.mention.Parse getParent() {
    Parse parent = parse.getParent();
    if (parent == null) {
      return null;
    }
    else {
      return new DefaultParse(parent,sentenceNumber);
    }
  }

  public boolean isNamedEntity() {
    if (entitySet.contains(parse.getType())) {
      return true;
    }
    else {
      return false;
    }
  }

  public boolean isNounPhrase() {
    return parse.getType().equals("NP");
  }

  public boolean isSentence() {
    return parse.getType().equals(Parser.TOP_NODE);
  }

  public boolean isToken() {
    return parse.isPosTag();
  }

  public int getEntityId() {
    return -1;
  }

  public Span getSpan() {
    return parse.getSpan();
  }

  public int compareTo(Object o) {
    if ( o == this) {
      return 0;
    }
    DefaultParse p = (DefaultParse) o;
    if (sentenceNumber < p.sentenceNumber) {
      return -1;
    }
    else if (sentenceNumber > p.sentenceNumber) {
      return 1;
    }
    else {
      return parse.getSpan().compareTo(p.getSpan());
    }
  }
  
  public String toString() {
    return parse.toString();
  }

  
  public opennlp.tools.coref.mention.Parse getPreviousToken() {
    Parse parent = parse.getParent();
    Parse node = parse;
    int index=-1;
    //find parent with previous children
    while(parent != null && index < 0) {
      index = parent.indexOf(node)-1;
      if (index < 0) {
        node = parent;
        parent = parent.getParent();
      }
    }
    //find right-most child which is a token
    if (index < 0) {
      return null;
    }
    else {
      Parse p = parent.getChildren()[index];
      while (!p.isPosTag()) {
        Parse[] kids = p.getChildren();
        p = kids[kids.length-1];
      }
      return new DefaultParse(p,sentenceNumber);
    }
  }
  
  public opennlp.tools.coref.mention.Parse getNextToken() {
    Parse parent = parse.getParent();
    Parse node = parse;
    int index=-1;
    //find parent with subsequent children
    while(parent != null) {
      index = parent.indexOf(node)+1;
      if (index == parent.getChildCount()) {
        node = parent;
        parent = parent.getParent();
      }
      else {
        break;
      }
    }
    //find left-most child which is a token
    if (parent == null) {
      return null;
    }
    else {
      Parse p = parent.getChildren()[index];
      while (!p.isPosTag()) {
        p = p.getChildren()[0];
      }
      return new DefaultParse(p,sentenceNumber);
    } 
  }
  
  public boolean equals(Object o) {
    
    boolean result;
    
    if (o == this) {
      result = true;
    }
    else if (o instanceof DefaultParse) {
      result = parse == ((DefaultParse) o).parse;
    }
    else {
      result = false;
    }
    
    return result;
  }

  public int hashCode() {
    return parse.hashCode();
  }
  
  /**
   * Retrives the {@link Parse}.
   * 
   * @return the {@link Parse}
   */
  public Parse getParse() {
    return parse;
  }
}
