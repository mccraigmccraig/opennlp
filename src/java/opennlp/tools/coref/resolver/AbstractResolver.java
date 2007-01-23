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
import opennlp.tools.coref.mention.MentionContext;
import opennlp.tools.coref.mention.Parse;
import opennlp.tools.util.CountedSet;

/**
 * Default implementation of some methods in the {@link Resolver} interface. 
 */
public abstract class AbstractResolver implements Resolver {

  /** The number of previous entities that resolver should consider. */ 
  protected int numEntitiesBack;
  /** Debugging variable which specifies whether error output is generated if a class excludes as possibly coreferent mentions which are in-fact coreferent.*/
  protected boolean showExclusions;
  /** Debugging variable which holds statistics about mention distances durring training.*/
  protected CountedSet distances;
  /** The number of senteces back this resolver should look for a referent. */
  protected int numSentencesBack;
  

  public AbstractResolver(int neb) {
    numEntitiesBack=neb;
    showExclusions = true;
    distances = new CountedSet();
  }

  /**
   * Returns the number of previous entities that resolver should consider.
   * @return the number of previous entities that resolver should consider.
   */
  protected int getNumEntities() {
    return numEntitiesBack;
  }
  
  /**
   * Specifies the number of senteces back this resolver should look for a referent.
   * @param nsb the number of senteces back this resolver should look for a referent.
   */
  public void setNumberSentencesBack(int nsb) {
    numSentencesBack = nsb;
  }

  /**
   * The number of entites that should be considered for resolution with the specified discourse model.
   * @param dm The discourse model.
   * @return number of entites that should be considered for resolution.
   */
  protected int getNumEntities(DiscourseModel dm) {
    return Math.min(dm.getNumEntities(),numEntitiesBack);
  }

  /**
   * Returns the head parse for the specified mention.
   * @param mention The mention.
   * @return the head parse for the specified mention.
   */
  protected Parse getHead(MentionContext mention) {
    return mention.getHeadTokenParse();
  }

  /**
   * Returns the index for the head word for the specified mention.
   * @param mention The mention.
   * @return the index for the head word for the specified mention.
   */
  protected int getHeadIndex(MentionContext mention) {
    Parse[] mtokens = mention.getTokenParses();
    for (int ti=mtokens.length-1;ti>=0;ti--) {
      Parse tok = mtokens[ti];
      if (!tok.getSyntacticType().equals("POS") && !tok.getSyntacticType().equals(",") &&
          !tok.getSyntacticType().equals(".")) {
        return ti;
      }
    }
    return mtokens.length-1;
  }

  /**
   * Returns the text of the head word for the specified mention.
   * @param mention The mention.
   * @return The text of the head word for the specified mention.
   */
  protected String getHeadString(MentionContext mention) {
    return mention.getHeadTokenText().toLowerCase();
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
    return mention.getSentenceNumber() == cec.getSentenceNumber() && 
	   mention.getIndexSpan().getEnd() <= cec.getIndexSpan().getEnd();
  }

  public DiscourseEntity retain(MentionContext mention, DiscourseModel dm) {
    int ei = 0;
    if (mention.getId() == -1) {
      return null;
    }
    for (; ei < dm.getNumEntities(); ei++) {
      DiscourseEntity cde = dm.getEntity(ei);
      MentionContext cec = cde.getLastExtent(); // candidate extent context
      if (cec.getId() == mention.getId()) {
        distances.add(new Integer(ei));
        return cde;
      }
    }
    //System.err.println("AbstractResolver.retain: non-refering entity with id: "+ec.toText()+" id="+ec.id);
    return null;
  }
  
  /**
   * Returns the string of "_" delimited tokens for the specified mention.
   * @param mention The mention.
   * @return the string of "_" delimited tokens for the specified mention.
   */
  protected String featureString(MentionContext mention){
    StringBuffer fs = new StringBuffer();
    Object[] mtokens =mention.getTokens(); 
    fs.append(mtokens[0].toString());
    for (int ti=1,tl=mtokens.length;ti<tl;ti++) {
      fs.append("_").append(mtokens[ti].toString());
    }
    return fs.toString();
  }


  /**
   * Returns a string for the specified mention with punctuation, honorifics, designators, and determiners removed.
   * @param mention The mention to be striped.
   * @return a normalized string representation of the specified mention.
   */
  protected String stripNp(MentionContext mention) {
    int start=mention.getNonDescriptorStart(); //start after descriptors

    Parse[] mtokens = mention.getTokenParses();
    int end=mention.getHeadTokenIndex()+1;
    if (start == end) {
      //System.err.println("stripNp: return null 1");
      return null;
    }
    //strip determiners
    if (mtokens[start].getSyntacticType().equals("DT")) {
      start++;
    }
    if (start == end) {
      //System.err.println("stripNp: return null 2");
      return null;
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
      return null;
    }
    if (start+1 != end) { // don't do this on head words, to keep "U.S."
      //strip off honorifics in begining
      if (Linker.honorificsPattern.matcher(mtokens[start].toString()).find()) {
        start++;
      }
      if (start == end) {
        //System.err.println("stripNp: return null 4");
        return null;
      }
      //strip off and honerifics on the end
      if (Linker.designatorsPattern.matcher(mtokens[mtokens.length-1].toString()).find()) {
        end--;
      }
    }
    if (start == end) {
      //System.err.println("stripNp: return null 5");
      return null;
    }
    String strip = "";
    for (int i=start;i<end;i++) {
      strip += mtokens[i].toString() + ' ';
    }
    return strip.trim();
  }


  public void train() throws IOException {}
  
  /**
   * Returns a string representing the gender of the specifed pronoun.
   * @param pronoun An English pronoun. 
   * @return the gender of the specifed pronoun.
   */
  public static String getPronounGender(String pronoun) {
    if (Linker.malePronounPattern.matcher(pronoun).matches()) {
      return "m";
    }
    else if (Linker.femalePronounPattern.matcher(pronoun).matches()) {
      return "f";
    }
    else if (Linker.neuterPronounPattern.matcher(pronoun).matches()) {
      return "n";
    }
    else {
      return "u";
    }
  };
}
