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
import opennlp.tools.util.ReverseListIterator;

/**
 * Represents an item in which can be put into the discourse model.  Object which are
 * to be placed in the discourse model should extend this class.
 * @see opennlp.tools.coref.DiscourseModel    
 */
public abstract class DiscourseElement {

  private List extents;
  private int id=-1;
  private MentionContext lastExtent;
  
  public DiscourseElement(MentionContext e) {
    extents=new ArrayList(1);
    lastExtent = e;
    extents.add(e);
  }

  public Iterator getReverseExtents() {
    return(new ReverseListIterator(extents));
  }

  public Iterator getExtents() {
    return(extents.listIterator());
  }

  public int getNumExtents() {
    return(extents.size());
  }

  public void addExtent(MentionContext e) {
    extents.add(e);
    if (e.getType() == null || !e.getType().equals("isa")) {
      lastExtent=e;
    }
  }

  public MentionContext getLastExtent() {
    return(lastExtent);
  }

  public void setId(int i) {
    id=i;
  }

  public int getId() {
    return(id);
  }

  public String toString() {
    Iterator ei = extents.iterator();
    MentionContext ex = (MentionContext) ei.next();
    StringBuffer de = new StringBuffer();
    de.append("[ ").append(ex.toText());//.append("<").append(ex.getHeadText()).append(">");
    while (ei.hasNext()) {
      ex = (MentionContext) ei.next();
      de.append(", ").append(ex.toText());//.append("<").append(ex.getHeadText()).append(">");
    }
    de.append(" ]");
    return(de.toString());
  }


}


