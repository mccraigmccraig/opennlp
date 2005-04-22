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

/**
 *  Specifies the interface that Objects which determine the space of mentions for coreference should implement. 
 */
public interface MentionFinder {

  /** Specifies whether pre-nominal named-entities should be collected as mentions.
   * @param collectPrenominalNamedEntities true if pre-nominal named-entities should be collected; false otherwise. 
   */
  public void setPrenominalNamedEntityCollection(boolean collectPrenominalNamedEntities);
  /**
   * Reutrns whether this mention finder collects pre-nominal named-entities as mentions.
   * @return true if this mention finder collects pre-nominal named-entities as mentions
   */
  public boolean isPrenominalNamedEntityCollection();
  
  /**
   * Reutrns whether this mention finder collects coordinated noun phrases as mentions.
   * @return true if this mention finder collects coordinated noun phrases as mentions; false otherwise.
   */
  public boolean isCoordinatedNounPhraseCollection();
  
  /** Specifies whether coordinated noun phrases should be collected as mentions.
   * @param collectCoordinatedNounPhrases true if coordinated noun phrases should be collected; false otherwise. 
   */
  public void setCoordinatedNounPhraseCollection(boolean collectCoordinatedNounPhrases);

  /**
   * Returns an array of mentions.
   * @param parse A top level parse from which mentions are gathered.
   * @return an array of mentions which implement the <code>Extent</code> interface.
   */
  public Mention[] getMentions(Parse parse);

}
