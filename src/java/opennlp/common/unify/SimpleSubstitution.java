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

import java.util.*;

/**
 * Simple implementation of Substitution interface.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.8 $, $Date: 2002/06/04 17:36:26 $
 */
public class SimpleSubstitution extends HashMap implements Substitution {

    public Object makeSubstitution (Variable var, Object u) 
	throws UnifyFailure{

	if (u instanceof Unifiable) {
	    u = ((Unifiable)u).fill(this);
	}
	put(var, u);
	return u;
    }

    public Object getValue (Variable var) {
	return get(var);
    }

    public Iterator varIterator () {
	return keySet().iterator();
    }
    
}
