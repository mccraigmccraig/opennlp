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

package opennlp.common.unify;

/**
 * A unifiable String value.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.1 $, $Date: 2001/12/11 23:29:24 $
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
    public Object unify (Object o, Substitution s) throws UnifyFailure {
	if (equals(o)) {
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
     * @return the Object o, unmodified 
     **/
    public Object unifyCheck (Object o) throws UnifyFailure {
	if (o instanceof StringValue || o instanceof String) {
	    return o;
	} else {
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
    public Object fill (Substitution s) {
	return this;
    }


    public String toString () {
	return _val;
    }
    
}
