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

import java.util.*;

/**
 * Implementation of Substitution interface which ensures that all
 * the categories it contains are updated as new substitutions are
 * made.
 *
 * @author      Jason Baldridge
 * @version $Revision: 1.2 $, $Date: 2001/12/19 11:32:17 $ 
*/
public class SelfCondensingSub extends HashMap implements Substitution {

    /**
     * Request the Substitution to identify a variable with an
     * object. Automagically condenses the Substitution so that all
     * other values in this Substitution get the new value for the
     * variable if they contain it.
     *
     * @param var the variable whose value has been determined
     * @param o the Object identified with the variable
     * @return the Object identified with the variable, which has
     * potentially undergone further unifications as a result of
     * making the substitution
     * @exception throws UnifyFailure if the Object cannot be unified
     * with a previous value substituted for the Variable.
     */
    public Object makeSubstitution(Variable var, Object o) throws UnifyFailure {

	Object val1 = getValue(var);

	if (o instanceof Variable) {
	    Variable var2 = (Variable)o;
	    Object val2 = getValue(var2);
	    if (val1 != null) {
		if (val2 != null)
		    o = Unifier.unify(var, val2, this);
		else
		    o = makeSubstitution(var2, val1);
	    }
	    else {
		if (val2 != null)
		    makeSubstitution(var, val2);
		else
		    put(var, var2);
	    }
	}
	else if (val1 != null) {
	    o = Unifier.unify(val1, o, this);
	}
	put(var, o);
	for (Iterator i=keySet().iterator(); i.hasNext();) {
	    Variable v = (Variable)i.next();
	    Object val = getValue(v);
	    if (val instanceof Unifiable)
		put(v, ((Unifiable)val).fill(this));
	}
	if (o instanceof Unifiable)
	    o = ((Unifiable)o).fill(this);
	return o;
    }

    /**
     * Try to get the value of a variable from this Substitution.
     * Returns null if the variable is unknown to the Substitution.
     *
     * @param var the variable whose value after unification is desired
     * @return the Object which this variable has been unified with 
     */
    public Object getValue(Variable var) {
	return get(var);
    }

    public Iterator varIterator () {
	return keySet().iterator();
    }
    
}
