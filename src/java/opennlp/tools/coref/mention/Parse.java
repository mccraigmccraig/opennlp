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

import java.util.List;

import opennlp.tools.util.Span;

/** Interface for syntactic and named-entity information to be used in coreference annotation. */
public interface Parse extends Comparable {
  
  /**
   * Returns the index of the sentence which contains this parse.
   * @return The index of the sentence which contains this parse.
   */
  public int getSentenceNumber();

  /** Returns a list of the all noun phrases
   * contained by this parse.  The noun phrases in this list should
   * also implement the {@link Parse} interface.  
   * @return a list of all the noun phrases contained by this parse.
   */
  public List getNounPhrases();
  
  /** Returns a list of all the named entities
   * contained by this parse.  The named entities in this list should
   * also implement the {@link Parse} interface.  
   * @return a list of all the named entities contained by this parse. */
  public List getNamedEntities();

  /** Returns a list of the children to this object.  The
   * children should also implement the {@link Parse} interface.
   * @return a list of the children to this object.
   * */
  public List getChildren();
  
  /**
   * Returns a list of the children to this object which are constituents or tokens.  The
   * children should also implement the {@link Parse} interface.  This allows
   * implementations which contain addition nodes for things such as semantic categories to
   * hide those nodes from the componets which only care about syntactic nodes. 
   * @return a list of the children to this object which are constituents or tokens.
   */
  public List getSyntacticChildren();

  /** Returns a list of the tokens contained by this object.  The tokens in this list should also
   * implement the {@link Parse} inteface.  
   */
  public List getTokens();

  /** Returns the syntactic type of this node. Typically this is the part-of-speech or 
   * constituent labeling.
   * @return the syntactic type. 
   */
  public String getSyntacticType();

  /** Returns the named-entity type of this node.
   * @return the named-entity type. */
  public String getEntityType();
  
  /** Determines whether this has an ancestor of type NAC.
   * @return true is this has an ancestor of type NAC, false otherwise. 
   * */
  public boolean isParentNAC();
  
  /** Returns the parent parse of this parse node.
   * @return the parent parse of this parse node.
   */
  public Parse getParent();

  /**
   * Specifies whether this parse is a named-entity.
   * @return True if this parse is a named-entity; false otherwise.
   */
  public boolean isNamedEntity();
  
  /**
   * Specifies whether this parse is a noun phrase.
   * @return True if this parse is a noun phrase; false otherwise.
   */
  public boolean isNounPhrase();
  
  /**
   * Specifies whether this parse is a sentence.
   * @return True if this parse is a sentence; false otherwise.
   */
  public boolean isSentence();

  /**
   * Specifies whether this parse is a coordinated noun phrase.
   * @return True if this parse is a coordinated noun phrase; false otherwise.
   */
  public boolean isCoordinatedNounPhrase();

  /** Specifies whether this parse is a token.
   * @return True if this parse is a token; false otherwise.
   */
  public boolean isToken();

  public String toString();

  /** Returns an entity id associated with this parse and coreferent parses.  This is only used for training on
   * already annotated coreference annotation.
   * @return an entity id associated with this parse and coreferent parses.
   */
  public int getEntityId();

  /**
   * Returns the character offsets of this parse node.
   * @return The span representing the character offsets of this parse node.
   */
  public Span getSpan();
  
  /**
   * Returns the first token which is not a child of this parse.  If the first token of a sentence is
   * a child of this parse then null is returned.
   * @return the first token which is not a child of this parse or null if no such token exists.
   */
  public Parse getPreviousToken();
  
  /**
   * Returns the next token which is not a child of this parse.  If the last token of a sentence is
   * a child of this parse then null is returned.
   * @return the next token which is not a child of this parse or null if no such token exists.
   */
  public Parse getNextToken();
}
