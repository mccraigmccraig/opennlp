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
import java.util.Collection;

/**
 * An FC is the collection of information which defines an entity, proposition,
 * or a group of entities or propositions.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */

public interface FC extends Referable {
    /**
     * Finds a semantic expression that refers to this FC
     *
     * @return the referring expression
     */
    public Denoter getRefexp();


    /**
     * The number of entities this FC represents
     *
     * @return The number of entities this FC represents
     */
    public int size();

    /**
     * compute FCs in this group (if it is a group)
     *
     * @return a collection containing the FCs in this group
     */
    public Collection group();
    
    /**
     * Makes two FC's refer to the same thing.
     *
     * @param b the FC to be the same as this FC
     */
    public void makeCoref(FC b);
    
    /**
     * Compute the set of FCs that are the same as this FC
     *
     * @return the coreference set
     */
    public Set getCorefs();
    
    /**
     * Set the coreference set
     *
     * @param s the new coreference set
     */
    public void setCorefs(Set s);
    
    /**
     * Determines if this was the last FC created?
     *
     * @return whether or not this was the last FC created
     */
    public boolean mostRecent();
    
    /**
     * Adds Kind n to the list of nodes this FC is a member of.
     *
     * @param n the new property for this FC
     */
    public void addKind(Kind n);

    /**
     * Return the list of Kinds which this FC is a member of.
     *
     * @return the properties this FC has
     */
    public Collection getKinds();

    /**
     * Sets the local context in which this Denoter was created.  That is,
     * if this Denoter is an argument of a logical form, what is the logical
     * form?
     *
     * @param c the local context
     */
    public void setCreatedContext(Denoter c);

    /**
     * Returns the context in which this fc was created
     *
     * @return the local context of the creation of this FC
     */
    public Denoter getCreatedContext();
    
    /**
     * A unique indentification for this FC
     *
     * @return the unique id
     */
    public int id();


    /**
     * Tests if the FCs in this group are the same as in another
     *
     * @param fcSet the other group
     * @return whether the groups are the same
     */
    public boolean setEquals(Set fcSet);

    /**
     * Finds which groups this FC belongs to
     *
     * @return the set of groups (which are FCs)
     */
    public Set memberOf();

    /**
     * Adds to the list of groups which this FC is a member of.
     *
     * @param fc the new group
     */
    public void memberOf(FC fc);

    /**
     * equality check agains an id
     *
     * @param i the id
     * @return if the idea is the same as the id for this FC
     */
    public boolean equals(int i);

    /**
     * equality check agains an object.
     *
     * @param fc the object against which to check equality
     * @return whether the object is an fc with the same id
     */
    public boolean equals(Object fc);

    /**
     * Tests if a denoter is compatible with this FC in the sense that all
     * its semantic attributes are non-conflicting.
     *
     * @param c the denoter to check agains
     * @return whether they are compatible
     */
    public boolean matches(Denoter c);
    
    public void informDominance(Denoter d);

}
