///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2002 Jason Baldridge
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

package opennlp.common.unify;

/**
 * A unifiable String value.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.3 $, $Date: 2002/02/08 12:17:50 $
 **/
public class StringValue implements Unifiable {
    private String _val;

    public StringValue (String s) {
	_val = s;
    }


    /**
     * Determines if a Variable occurs within this Unifiable
     *
     * @param v the Variable to check for
     * @return whether or not the Variable occurs
     */
    public boolean occurs (Variable v) {
	return false;
    }


    /**
     * Tests for equality with the given Object.
     *
     * @param o object to test for equality
     * @return true if this Unifiable is equal to <code>o</code>,
     * false if not.
     **/
    public boolean equals (Object o) {
	if (o instanceof String) {
	    return _val.equals(o);
	} else if (o instanceof StringValue) {
	    return _val.equals(((StringValue)o)._val);
	} else {
	    return false;
	}
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
	if (equals(u)) {
	    return this;
	} else {
	    throw new UnifyFailure();
	}
    }


    /**
     * Check if this StringValue can unify with another Object.
     *
     * @param o object to check for unifiability
     * @exception UnifyFailure if this Unifiable cannot be unified with 
     *            the Object
     **/
    public void unifyCheck (Unifiable u) throws UnifyFailure {
	if (!(u instanceof StringValue)) {
	    throw new UnifyFailure();
	}
    }

    /**
     * Replaces any variables in this Unifiable with the values found
     * for them in the Substitution argument.
     *
     * @param s Substitution containing the variable resolutions
     * @return a copy of this Unifiable with all variables from the
     *         Substitution replaced by their values.  
     */
    public Unifiable fill (Substitution s) {
	return this;
    }


    public String toString () {
	return _val;
    }
    
}
