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

public interface Parse extends Comparable {

  /** Returns a <code>List</code> of the top-most noun phrases
   * contained by this object.  The noun phrases in this list should
   * also implement the <code>Parse</code> interface.  */
  public List getNounPhrases();
  
  /** Returns a <code>List</code> of the top-most named entities
   * contained by this object.  The named entities in this list should
   * also implement the <code>Parse</code> interface.  */
  public List getNamedEntities();

  /** Returns a <code>List</code> of the children to this object.  The
   * children should also implement the <code>Parse</code> interface.  */
  public List getChildren();
  
  public List getSyntacticChildren();

  /** Returns a <code>List</code> of the top-most tokens
   * contained by this object The tokens in this list should also
   * implement the <code>Parse</code> inteface.  
   */
  public List getTokens();

  /** Returns the syntactic type of this node.
   * @return the syntactic type.  Typically part-of-sppech or 
   * constituent labeling. */
  public String getSyntacticType();

  /** Sets the syntactic type of this node. */
  public void setSyntacticType(String t);

  /** Returns the named-entity type of this node.
   * @return the named-entity type. */
  public String getEntityType();

  /** Sets the entity type of this node. */
  public void setEntityType(String t);

  /** Returns a <code>List</code> of the top-most sub-noun phrases
     * contained by this object.  The sub-noun phrases in this list should
     * also implement the <code>Parse</code> interface.  */
  public List getSubNounPhrases();
  
  /** Determines whether this has a parent of type NAC.
   * @return true is this has a parent of type NAC, false otherwise. 
   * */
  public boolean isParentNAC();
  
  public Parse getParent();

  public void setValidated(boolean v);

  public boolean isValidated();

  public boolean isNamedEntity();
  
  public boolean isNounPhrase();
  
  public boolean isSentence();

  public boolean isCoordinatedNounPhrase();

  public boolean isToken();

  public List getSentences();

  public List getEntities();

  public String getProperty(String p);

  public void setProperty(String k, String v);

  public String toString();

  public int getEntityId();

  public void setEntityId(int id);

  public void removeEntityId();

  /**
   * Returns the character offsets of this parse node.
   * @return The span representing the character offsets of this parse node.
   */
  public Span getSpan();

  public void show();
}
