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

package opennlp.common.structure;

import opennlp.common.discourse.*;
import opennlp.common.synsem.*;
import opennlp.common.unify.*;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The interface for knowledge bases such as discourse models and world
 * knowledge. 
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */


public interface KB {

    /**
     * Adds a property which the instantiation of the variable cannot have
     *
     * @param v The variable to restrict
     * @param p The property which this variable cannot instantiate to
     */
    public void addRestriction(Variable v, Abstraction p);

    /**
     * Clear out all variable restrictions
     * 
     */
    public void clearRestrictions();
    
    /**
     * Checks that this variable can be instantiates in a particular way.
     * A simple, single level version.  Only for testing purposes
     * @param v The variable to check
     * @param r The possible instantiation
     */
    public boolean isConsistent(Variable v, Referable r);

    /**
     * Stores another KB to be accessed if this one doesn't
     * contain the desired information.
     *
     * @param h the new hierarchy
     */
    public void setHigherKnowledge(KB hk);
    
    /**
     * Cause a new filecard to be stored in this knowledge base
     *
     * @param fc the new filecard
     */
    public void addFC(FC fc);
    
    /**
     * remove a filecard from this KB
     *
     * @param fc the filecard to remove
     */
    public void removeFC(FC fc);
    
    /**
     * Cause two filecards to be considered the same.
     *
     * @param fc1 the first filecard
     * @param fc1 the second filecard
     * @param polarity whether we are in a negative or positive context
     */
    public void merge(FC fc1, FC fc2, boolean polarity);
    
    /**
     * return the filecard denoted by a semantic form
     *
     * @param c the semantic form
     * @return the filecard the semantic form denotes
     * @exception AccommodateException thrown if can't find filecard and
     *            not allowed to create it.
     */
    public FC getFC(Denoter c) throws AccommodateException;
    
    /**
     * return the filecard denoted by a semantic form given a particular
     * accommodation strategy.
     *
     * @param c the semantic form
     * @return the filecard the semantic form denotes
     * @exception AccommodateException thrown if can't find filecard and
     *            not allowed to create it.
     */
    public FC getFC(Denoter c, AccommodatePolicy ap)
	throws AccommodateException;

    /**
     * return a Kind from the ISA denoted by a semantic form
     *
     * @param c the semantic form
     * @return the filecard the semantic form denotes
     * @exception AccommodateException thrown if can't find filecard and
     *            not allowed to create it.
     */
    public Kind getNode(Abstraction c) throws AccommodateException;
    
    /**
     * return a Kind from the ISA denoted by a semantic form given a
     * particular accommodation strategy
     *
     * @param c the semantic form
     * @return the filecard the semantic form denotes
     * @exception AccommodateException thrown if can't find filecard and
     *            not allowed to create it.
     */
    public Kind getNode(Abstraction c, AccommodatePolicy ap)
	throws AccommodateException;
    
    /**
     * Determine a semantic form which is an appropriate description
     * of something you can refer to (FC or Kind)
     *
     * @param c the referable thingy
     * @return the semantic form describing the thingy
     */
    public Category describe(Category c);
    
    /**
     * Returns the isa hiearchy for this KB
     *
     * @return the ISA
     */
    public Hierarchy getISA();
    
    /**
     * Returns the salience list for this KB
     *
     * @return the salience list
     */
    public SalienceList getSalList();
    
    /**
     * Sets the salience list for this KB
     *
     * @param the salience list
     */
    public void setSalList(SalienceList s);
    
    /**
     * Stores a proposition which should really be some representation of a
     * logical form (although this isn't enforced).
     *
     * @param c the new proposition
     */
    public void addProposition(Denoter c);
    
    /**
     * Given a semantic form, update the salience list. This will probably
     * consider the FCs and Kinds in the semantic form.
     *
     * @param c the form containing things to update in the salience list
     */
    
    public void updateSalience(Denoter c);
    /**
     * Given a Referable form, update the salience list. 
     *
     * @param ref the referable to update
     * @param utt the utterance in which it occurred
     * @param pos the position in the utterance in which it occurred.
     */
    public void updateSalience(Referable ref, int utt, int pos);
    
    /**
     * Returns propositions that have been stored in this KB.
     *
     * @return a list of propositions ordered by time added-- most recent last.
     */
    public ArrayList getPropList();
    
    /**
     * Returns an iterator over the filecards in this KB
     *
     * @return the iterator
     */
    public Iterator iterator();
}
