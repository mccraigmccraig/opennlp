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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opennlp.tools.coref.sim.GenderEnum;
import opennlp.tools.coref.sim.NumberEnum;
import opennlp.tools.util.Span;

/** Data strucure representation of a mention.  This includes numerous contextual
 * information used in performing coreference resolution.
 */
public class MentionContext {
  /** Parse elements for each token in the head basal noun phrase of this entity. */
  private Parse[] tokens;
  /** The index of first token which is not part of a descriptor.  This is 0 if no descriptor is present. */
  private int nonDescriptorStart;
  /** The Parse for this mention. */
  private Parse parse;
  /** Sentence-token-based span whose end is the last token of the mention. */
  private Span span;
  /** Sentence-token-based span whose end is the last token of the menion head. */
  private Span headSpan;
  /** Position of the NP in the sentence. */
  private int nounLocation;
  /** Position of the NP in the document. */
  private  int nounNumber; 
  /** Number of Noun Phrase in the sentence containing this mention. */
  private int maxNounLocation;
  /** Index of the sentece in the document which contains this mention. */
  private int sentenceNumber;
  /** The token preceeding this mention's maximal noun phrase.*/
  private Parse prevToken;
  /** The token following this mention's maximal noun phrase.*/
  private Parse nextToken;
  /** The token following this mention's basal noun phrase.*/
  private Parse basalNextToken;
  /** The type of coreference performed on this mention. */
  private String type = null;
  /** Specifies the entity id number.  This is used to indicate coreference relationships during training. */
  private int id = -1;
  /** The named-entity type associated with this mention. */
  private String neType;
  /** The token index in of the head word of this mention. */ 
  private int headTokenIndex;
  /** The pos-tag of the mention's head word. */
  private String headTokenTag;
  /** The parse of the mention's head word. */
  private Parse headToken;
  /** The text of the mention's head word. */
  private String headTokenText;
  /** The parse of the first word in the mention. */
  private Parse firstToken;
  /** The text of the first word in the mention. */
  private String firstTokenText;
  /** The pos-tag of the first word in the mention. */
  private String firstTokenTag;
  /** The categorical keys associated with this mention. 
   * In the current implementation WordNet provides the source of these keys. */
  private Set synsets;
  /** The gender assigned to this mention. */
  private GenderEnum gender;
  /** The probability associated with the gender assignment. */
  private double genderProb;
  /** The number assigned to this mention. */
  private NumberEnum number;
  /** The robability associated with the number assignment. */
  private double numberProb;

  /** 
   * Constructs context information for the specified mention.
   * @param mentionParse Mention parse structure for which context is to be constructed.
   *  @param mentionIndex mention position in sentence.
   *  @param mentionsInSentence Number of mentions in the sentence.
   *  @param mentionsInDocument Number of mentions in the document.
   *  @param sentenceIndex Sentence number for this mention.
   *  @param nameType The named-entity type for this mention.
   *  @param headFinder Object which provides head information.
   **/
  public MentionContext(Parse mentionParse, int mentionIndex, int mentionsInSentence, int mentionsInDocument, int sentenceIndex, String nameType, HeadFinder headFinder) {
    nounLocation = mentionIndex;
    maxNounLocation = mentionsInDocument;
    sentenceNumber = sentenceIndex;
    parse = mentionParse;
    span = mentionParse.getSpan();
    prevToken = mentionParse.getPreviousToken();
    nextToken = mentionParse.getNextToken();
    Parse head = headFinder.getLastHead(mentionParse);
    List headTokens = head.getTokens();
    tokens = (Parse[]) headTokens.toArray(new Parse[headTokens.size()]);
    basalNextToken = head.getNextToken();
    //System.err.println("MentionContext.init: "+ent+" "+ent.getEntityId()+" head="+head);
    headSpan = head.getSpan();
    nonDescriptorStart = 0;
    initHeads(head, headFinder);
    this.neType= nameType;
    if (getHeadTokenTag().startsWith("NN") && !getHeadTokenTag().startsWith("NNP")) {
      //if (headTokenTag.startsWith("NNP") && neType != null) {
      this.synsets = getSynsetSet(this);
    }
    else {
      this.synsets=Collections.EMPTY_SET;
    }
    gender = GenderEnum.UNKNOWN;
    this.genderProb = 0d;
    number = NumberEnum.UNKNOWN;
    this.numberProb = 0d;
  }

  private void initHeads(Parse head, HeadFinder headFinder) {
    this.headTokenIndex=headFinder.getHeadIndex(head);
    this.headToken = tokens[getHeadTokenIndex()];
    this.headTokenText = headToken.toString();
    this.headTokenTag=headToken.getSyntacticType();
    this.firstToken = tokens[0];
    this.firstTokenTag = firstToken.getSyntacticType();
    this.firstTokenText=firstToken.toString();
  }

  public Parse getHeadToken() {
    return headToken;
  }
  
  public Parse[] getTokens() {
    return tokens;
  }

  public String getHeadText() {
    StringBuffer headText = new StringBuffer();
    for (int hsi = 0; hsi < tokens.length; hsi++) {
      headText.append(" ").append(tokens[hsi].toString());
    }
    return (headText.toString().substring(1));
  }
  
  public Parse getParse() {
    return parse;
  }
  
  public int getNonDescriptorStart() {
    return this.nonDescriptorStart;
  }
  
  public Span getSpan() {
    return span;
  }
  
  public Span getHeadSpan() {
    return headSpan;
  }
  
  public int getNounPhraseSentenceIndex() {
    return nounLocation;
  }
  
  public int getNounPhraseDocumentIndex() {
    return nounNumber;
  }
  
  public int getMaxNounPhraseSentenceIndex() {
    return maxNounLocation;
  }
  
  public Parse getNextTokenBasal() {
    return basalNextToken;
  }
  
  public Parse getPreviousToken() {
    return prevToken;
  }
  
  public Parse getNextToken() {
    return nextToken;
  }
  
  public int getSentenceNumber() {
    return sentenceNumber;
  }

  public String getType() {
    return type;
  }


  /**
   * Returns the entity id number.  This value is only used during training.
   * @return the entity id number.
   */
  public int getId() {
    return id;
  }
  
  /**
   * Assigns this mention the specified entity id.
   * @param id An index representing which entity group this mention belongs to.
   * This is used when training models.
   */
  public void setId(int id) {
    this.id = id;
  }
  
  /**
   * Returns the named-entity type of this annotation.
   * @return the named-entity type of this annotation.
   */
  public String getNeType() {
    return neType;
  }

  /** Returns the token index into the mention for the head word. 
   * @return the token index into the mention for the head word. 
   */
  public int getHeadTokenIndex() {
    return headTokenIndex;
  }
  
  /** Returns the pos-tag of this mention's head token.
   * @return the pos-tag of this mention's head token.
   */
  public String getHeadTokenTag() {
    return headTokenTag;
  }

  /** Returns the text of this mention's head token.
   * @return the text of this mention's head token.
   */
  public String getHeadTokenText() {
    return headTokenText;
  }
  
  /** Returns the parse for the first token in this mention.
   * @return The parse for the first token in this mention.
   */
  public Parse getFirstToken() {
    return firstToken;
  }

  /** Returns the text for the first token of the mention.
   * @return The text for the first token of the mention.
   */
  public String getFirstTokenText() {
    return firstTokenText;
  }
  
  /**
   * @return Returns the firstTokenTag.
   */
  public String getFirstTokenTag() {
    return firstTokenTag;
  }

  /** Returns the set of categorical keys associated with this mention.
   * @return The set of categorical keys assocoated with this mention.
   */
  public Set getSynsets() {
    return synsets;
  }

  /**
   * Returns the text of this mention. 
   * @return A space-delimited string of the tokens of this mention.
   */
  public String toText() {
    return (parse.toString());
  }
  
  private static String[] getLemmas(MentionContext xec) {
    //TODO: Try multi-word lemmas first.
    String word = xec.getHeadTokenText();
    return DictionaryFactory.getDictionary().getLemmas(word,"NN");
  }
  
  private static Set getSynsetSet(MentionContext xec) {
    //System.err.println("getting synsets for mention:"+xec.toText());
    Set synsetSet = new HashSet();
    String[] lemmas = getLemmas(xec);
    for (int li = 0; li < lemmas.length; li++) {
      String[] synsets = DictionaryFactory.getDictionary().getParentSenseKeys(lemmas[li],"NN",0);
      for (int si=0,sn=synsets.length;si<sn;si++) {
        synsetSet.add(synsets[si]);
      }
    }
    return (synsetSet);
  }

  /**
   * Assigns the specified gender with the specified probability to this mention.
   * @param gender The gender to be given to this mention.
   * @param probability The probability assosicated with the gender assignment.
   */
  public void setGender(GenderEnum gender, double probability) {
    this.gender = gender;
    this.genderProb = probability;
  }

  /** 
   * Returns the gender of this mention.
   * @return The gender of this mention.
   */
  public GenderEnum getGender() {
    return gender;
  }

  /**
   * Returns the probability associated with the gender assignment.a
   * @return The probability associated with the gender assignment.
   */
  public double getGenderProb() {
    return genderProb;
  }

  /**
   * Assigns the specified number with the specified probability to this mention.
   * @param number The number to be given to this mention.
   * @param probability The probability assosicated with the number assignment.
   */
  public void setNumber(NumberEnum number, double probability) {
    this.number = number;
    this.numberProb = probability;
  }
  
  /** 
   * Returns the number of this mention.
   * @return The number of this mention.
   */
  public NumberEnum getNumber() {
    return number;
  }

  /**
   * Returns the probability associated with the number assignment.
   * @return The probability associated with the number assignment.
   */
  public double getNumberProb() {
    return numberProb;
  }
}
