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
package opennlp.tools.coref.resolver;

import java.io.IOException;

import opennlp.tools.coref.DiscourseEntity;
import opennlp.tools.coref.DiscourseModel;
import opennlp.tools.coref.Linker;
import opennlp.tools.coref.MentionContext;
import opennlp.tools.coref.mention.Parse;
import opennlp.tools.util.CountedSet;

public abstract class AbstractResolver implements Resolver {

  protected int NUM_ENTITIES_BACK;
  protected boolean showExclusions;
  protected CountedSet distances;
  

  public AbstractResolver(int neb) {
    NUM_ENTITIES_BACK=neb;
    showExclusions = true;
    distances = new CountedSet();
  }

  protected int getNumEntities() {
    return(NUM_ENTITIES_BACK);
  }

  protected int getNumEntities(DiscourseModel dm) {
    return(Math.min(dm.getNumEntities(),NUM_ENTITIES_BACK));
  }

  protected Parse getHead(MentionContext mention) {
    return mention.getHeadToken();
  }

  protected int getHeadIndex(MentionContext mention) {
    Parse[] mtokens = mention.getTokens();
    for (int ti=mtokens.length-1;ti>=0;ti--) {
      Parse tok = mtokens[ti];
      if (!tok.getSyntacticType().equals("POS") && !tok.getSyntacticType().equals(",") &&
          !tok.getSyntacticType().equals(".")) {
        return(ti);
      }
    }
    return(mtokens.length-1);
  }

  protected String getHeadString(MentionContext mention) {
    return(mention.getHeadTokenText().toLowerCase());
  }

  /**
   * Determines if the specified entity is too far from the specified mention to be resolved to it.  
   * Once an entity has been determined to be out of range subsequent entities are not considered.
   * To skip intermediate entities @see excluded. 
   * @param mention The mention which is being considered.
   * @param entity The entity to which the mention is to be resolved.
   * @return true is the entity is in range of the mention, false otherwise.
   */
  protected boolean outOfRange(MentionContext mention, DiscourseEntity entity) {
    return false;
  }
  
  
  /** 
   * Excludes entities which you are not compatible with the entity under consideration.  The default 
   * implementation excludes entties whose last extent contatins the extent under consideration.
   * This prevents posessive pronouns from refering to the noun phrases they modify and other 
   * undesireable things.
   * @param mention The mention which is being considered as referential.
   * @param entity The entity to which the mention is to be resolved.
   * @return true if the entity should be excluded, false otherwise.
   */
  protected boolean excluded(MentionContext mention, DiscourseEntity entity) {
    MentionContext cec = entity.getLastExtent();
    return(mention.getSentenceNumber() == cec.getSentenceNumber() && 
	   mention.getSpan().getEnd() <= cec.getSpan().getEnd());
  }

  public DiscourseEntity retain(MentionContext mention, DiscourseModel dm) {
    int ei = 0;
    if (mention.getId() == -1) {
      return(null);
    }
    for (; ei < dm.getNumEntities(); ei++) {
      DiscourseEntity cde = dm.getEntity(ei);
      MentionContext cec = cde.getLastExtent(); // candidate extent context
      if (cec.getId() == mention.getId()) {
        distances.add(new Integer(ei));
        return (cde);
      }
    }
    //System.err.println("AbstractResolver.retain: non-refering entity with id: "+ec.toText()+" id="+ec.id);
    return(null);
  }
  
  protected String featureString(MentionContext mention){
    StringBuffer fs = new StringBuffer();
    Parse[] mtokens =mention.getTokens(); 
    fs.append(mtokens[0].toString());
    for (int ti=1,tl=mtokens.length;ti<tl;ti++) {
      fs.append("_").append(mtokens[ti].toString());
    }
    return fs.toString();
  }


  protected String stripNp(MentionContext mention) {
    int start=mention.getNonDescriptorStart(); //start after descriptors

    Parse[] mtokens = mention.getTokens();
    int end=mention.getHeadTokenIndex()+1;
    if (start == end) {
      //System.err.println("stripNp: return null 1");
      return(null);
    }
    //strip determiners
    if (mtokens[start].getSyntacticType().equals("DT")) {
      start++;
    }
    if (start == end) {
      //System.err.println("stripNp: return null 2");
      return(null);
    }
    //get to first NNP
    String type;
    for (int i=start;i<end;i++) {
      type = mtokens[start].getSyntacticType();
      if (type.startsWith("NNP")) {
        break;
      }
      start++;
    }
    if (start == end) {
      //System.err.println("stripNp: return null 3");
      return(null);
    }
    if (start+1 != end) { // don't do this on head words, to keep "U.S."
      //strip off honorifics in begining
      if (Linker.honorificsPattern.matcher(mtokens[start].toString()).find()) {
        start++;
      }
      if (start == end) {
        //System.err.println("stripNp: return null 4");
        return(null);
      }
      //strip off and honerifics on the end
      if (Linker.designatorsPattern.matcher(mtokens[mtokens.length-1].toString()).find()) {
        end--;
      }
    }
    if (start == end) {
      //System.err.println("stripNp: return null 5");
      return(null);
    }
    String strip=new String();
    for (int i=start;i<end;i++) {
      strip+=mtokens[i].toString()+" ";
    }
    return(strip.trim());
  }


  public void train() throws IOException {}
  
  public static String getPronounGender(String pronoun) {
    if (Linker.malePronounPattern.matcher(pronoun).matches()) {
      return ("m");
    }
    else if (Linker.femalePronounPattern.matcher(pronoun).matches()) {
      return ("f");
    }
    else if (Linker.neuterPronounPattern.matcher(pronoun).matches()) {
      return ("n");
    }
    else {
      return ("u");
    }
  };
}
