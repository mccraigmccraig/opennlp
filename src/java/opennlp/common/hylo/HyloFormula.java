///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2001 Jason Baldridge
// 
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////

package opennlp.common.hylo;

import opennlp.common.synsem.*;
import opennlp.common.unify.*;

/**
 * A parent class to implement reasonable default behavior for classes
 * representing data structures for hybrid logic.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.8 $, $Date: 2002/01/20 17:45:25 $
 **/
public abstract class HyloFormula implements LF {

    public abstract LF copy ();
    
    public void reduce () {}
    
    public void deepMap (ModFcn mf) {
	mf.modify(this);
    }

    
    /**
     * Unify this Unfiable with another Object.
     *
     * @param o object to unify with
     * @param s Substitution containing the variable resolutions
     * @exception UnifyFailure if this Unifiable cannot be unified with 
     *            the Object
     * @return an object which represents the unification of 
     *         this Unifiable with the Object
     */
    public Unifiable unify (Unifiable u, Substitution s) throws UnifyFailure {
	if (u instanceof Variable || u instanceof Op) {
	    return u.unify(this, s);
	} else if (u instanceof HyloFormula) {
	    return new Op("conj", copy(), ((LF)u).copy());
	} else {
	    throw new UnifyFailure();
	}
    }

    
    /**
     * Check if this Unifiable can unify with another Object.  This
     * should be implemented as a quick check to allow users of the
     * Unifiable to scan a group of Unifications to rapidly see if the
     * entire group is at least possible before descending into each
     * one with a full unification procedure.  Thus, if a call to this
     * method does not result in a UnifyFailure exception being
     * thrown, it doesn't mean that the Object can definitely be
     * unified with this Unifiable -- what is important is that when a
     * call to this method throws a UnifyFailure exception, it permits
     * one to avoid calling the unify() method on other Unifiables in
     * a group because the quick check failed on this one.
     *
     * @param o object to check for unifiability
     * @exception UnifyFailure if this Unifiable cannot be unified with 
     *            the Object
     * @return the Object o, unmodified 
     **/
    public void unifyCheck (Unifiable u) throws UnifyFailure {}


    /**
     * Replaces any variables in this Unifiable with the values found
     * for them in the Substitution argument.
     *
     * @param s Substitution containing the variable resolutions
     * @return a copy of this Unifiable with all variables from the
     *         Substitution replaced by their values.  
     */
    public Unifiable fill (Substitution s) throws UnifyFailure {
	return this;
    }
       
    

}
