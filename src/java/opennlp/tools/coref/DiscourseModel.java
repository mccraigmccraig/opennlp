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


public class DiscourseModel {

  private List entities;
  
  int nextEntityId = 1;

  public DiscourseModel() {
    entities=new ArrayList();
  }

  public void mentionEntity(DiscourseEntity e) {
    if (entities.remove(e)) {
      entities.add(0,e);
    }
    else {
      System.err.println("DiscourseModel.mentionEntity: failed to remove "+e);
    }
  }
  
  public int getNumEntities() {
    return(entities.size());
  }
  
  public DiscourseEntity getEntity(int i) {
    return((DiscourseEntity) entities.get(i));
  }

  public void addEntity(DiscourseEntity e) {
    e.setId(nextEntityId);
    nextEntityId++;
    entities.add(0,e);
  }

  public void mergeEntities(DiscourseEntity e1,DiscourseEntity e2,float p) {
    for (Iterator ei=e2.getExtents();ei.hasNext();) {
      e1.addExtent((MentionContext) ei.next());
    }
    //System.err.println("DiscourseModel.mergeEntities: removing "+e2);
    entities.remove(e2);
  }

  public DiscourseEntity[] getEntities() {
    DiscourseEntity[] des = new DiscourseEntity[entities.size()];
    entities.toArray(des);
    return(des);
  }

  public DiscourseEntity getEntityAt(int i) {
	return((DiscourseEntity) entities.get(i));
  }

  public void clear() {
    entities.clear();
  }
}
