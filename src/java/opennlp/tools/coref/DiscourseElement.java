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
  
  /**
   * Creates a new discourse element which contains the specified mention.
   * @param mention The mention which begins this discourse element.
   */
  public DiscourseElement(MentionContext mention) {
    extents=new ArrayList(1);
    lastExtent = mention;
    extents.add(mention);
  }

  /**
   * Returns an iterator over the mentions which iteratates through them based on which were most recently mentioned.  
   * @return an iterator over the mentions which iteratates through them based on which were most recently mentioned.
   */
  public Iterator getRecentMentions() {
    return(new ReverseListIterator(extents));
  }
  
  /**
   * Returns an iterator over the mentions which iteratates through them based on their occurance in the document.
   * @return an iterator over the mentions which iteratates through them based on their occurance in the document.
   */
  public Iterator getMentions() {
    return(extents.listIterator());
  }

  /** Returns the number of mentions in this element. 
   * 
   * @return number of mentions
   */
  public int getNumMentions() {
    return(extents.size());
  }

  /**
   * Adds the specified mention to this discourse element.
   * @param mention The mention to be added.
   */
  public void addMention(MentionContext mention) {
    extents.add(mention);
    lastExtent=mention;
  }

  /**
   * Returns the last mention for this element.  For appositives this will be the
   * first part of the appositive.
   * @return the last mention for this element.
   */
  public MentionContext getLastExtent() {
    return(lastExtent);
  }

  /**
   * Associates an id with this element.
   * @param id The id.
   */
  public void setId(int id) {
    this.id=id;
  }

  /**
   * Returns the id associated with this element.
   * @return the id associated with this element.
   */
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


