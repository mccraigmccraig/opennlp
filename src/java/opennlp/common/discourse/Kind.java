///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2000 Jason Baldridge and Gann Bierner
// 
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////

package opennlp.common.discourse;

import opennlp.common.synsem.*;

import java.util.Set;
import java.util.List;

/**
 * Nodes in the ISA hierarchies. Each node contains two sets, one
 * which contains things that match the description (called members)
 * and the other (called complement) which contains things which are
 * known to be explicitly in the complement of the members set 
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */


public interface Kind extends Referable {

    /**
     * Adds a filecard as being a member of this Kind
     *
     * @param fc the new filecard
     */
    public void add(FC fc);
    
    /**
     * Adds a filecard as being a member or not a member of this Kind
     *
     * @param bool whether or not the fc is a member
     * @param fc the new filecard
     */
    public void add(boolean bool, FC fc);
    
    /**
     * Removes a filecard from the set or complement set of this Kind
     *
     * @param bool remove the fc from the set or the complement set
     * @param fc the new filecard
     */
    public void remove(boolean bool, FC fc);

    /**
     * Gets the direct subkinds of this Kind.
     *
     * @return a list of subkinds
     */
    public List getChildren();

    /**
     * Gets the parents of this Kind
     *
     * @return a list of subkinds
     */
    public List getParents();

    /**
     * sets the direct subkinds of this Kind.
     *
     * @param l a list of subkinds
     */
    public void setChildren(List l);
    
    /**
     * sets the parents of this Kind.
     *
     * @param l a list of subkinds
     */
    public void setParents(List l);
    
    /**
     * Gets the semantic expression that can be used to refer to this Kind
     *
     * @return the semantic expression
     */
    public Denoter getRefexp();

    /**
     * return either the entry set or the complement set of this Kind
     *
     * @param bool true if entry set, false if complement set
     */
    public Set get(boolean bool);

    /**
     * Returns a kind containing all decendants in its entry set
     *
     * @return a new kind whose subtree has been flattened
     */
    public Kind flatten();
    
    /**
     * Returns all entities in the subtree of this kind
     *
     * @return a set of entities
     */
    public Set getSubTreeEntities();
    
    /**
     * Returns all entities in the subtree of parent of this kind
     *
     * @return a set of entities
     */
    public Set getParentSubTreeEntities();
    
    /**
     * Returns all ancestors (recursive parents) of this kind
     *
     * @return a set of entities
     */
    public Set getAncestors();
    
    /**
     * Performs a full copy of this Kind.
     *
     * @return a new kind
     */
    public Kind realCopy();
    
    /**
     * Creates a string representing this kind
     *
     * @return a description
     */
    public String name();
}
