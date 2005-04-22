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
package opennlp.tools.coref;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import opennlp.tools.coref.mention.MentionContext;

/**
 * Represents the elements which are part of a discourse. 
 */
public class DiscourseModel {

  private List entities;
  
  int nextEntityId = 1;

  /** 
   * Creates a new discourse model.
   *
   */
  public DiscourseModel() {
    entities=new ArrayList();
  }

  /**
   * Indicates that the specified entity has been mentioned.
   * @param e The entity which has been mentioned.
   */
  public void mentionEntity(DiscourseEntity e) {
    if (entities.remove(e)) {
      entities.add(0,e);
    }
    else {
      System.err.println("DiscourseModel.mentionEntity: failed to remove "+e);
    }
  }
  
  /**
   * Returns the number of entities in this discourse model.
   * @return the number of entities in this discourse model.
   */
  public int getNumEntities() {
    return(entities.size());
  }
  
  /**
   * Returns the entity at the specified index.
   * @param i The index of the entity to be returned.
   * @return the entity at the specified index.
   */
  public DiscourseEntity getEntity(int i) {
    return((DiscourseEntity) entities.get(i));
  }

  /**
   * Adds the specified entity to this discourse model.
   * @param e the entity to be added to the model. 
   */
  public void addEntity(DiscourseEntity e) {
    e.setId(nextEntityId);
    nextEntityId++;
    entities.add(0,e);
  }

  /**
   * Merges the specified entities into a single entity with the specified confidence.
   * @param e1 The first entity. 
   * @param e2 The second entity.
   * @param confidence The confidence.
   */
  public void mergeEntities(DiscourseEntity e1,DiscourseEntity e2,float confidence) {
    for (Iterator ei=e2.getMentions();ei.hasNext();) {
      e1.addMention((MentionContext) ei.next());
    }
    //System.err.println("DiscourseModel.mergeEntities: removing "+e2);
    entities.remove(e2);
  }

  /**
   * Returns the entities in the discourse model.
   * @return the entities in the discourse model.
   */
  public DiscourseEntity[] getEntities() {
    DiscourseEntity[] des = new DiscourseEntity[entities.size()];
    entities.toArray(des);
    return(des);
  }

  /**
   * Removes all elements from this discourse model.
   */
  public void clear() {
    entities.clear();
  }
}
