///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2000 Jason Baldridge
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

package opennlp.common.hylo;

import opennlp.common.synsem.*;
import opennlp.common.unify.*;

public class NominalVar extends HyloVar implements Nominal {
    
    private static int UNIQUE_STAMP = 0;
    
    public NominalVar () {
	super("HLV"+UNIQUE_STAMP++);
    }
    
    public NominalVar (String name) {
	super(name);
    }

    protected NominalVar (String name, int index) {
	super(name, index);
    }
    

    public LF copy () {
	return new NominalVar(_name, _index);
    }

    
    public boolean equals (Object o) {
	if (o instanceof NominalVar
	    && _index == ((NominalVar)o)._index
	    && _name.equals(((NominalVar)o)._name)) {
	    return true;
	} else {
	    return false;
	}
    }
    
    public Unifiable unify (Unifiable u, Substitution sub) throws UnifyFailure {
	if (u instanceof NominalAtom) {
	    return sub.makeSubstitution(this, u);
	} else if (u instanceof NominalVar) {
	    NominalVar u_nv = (NominalVar)u;
	    if (!equals(u_nv)) {
		NominalVar $nv =
		    new NominalVar(_name+u_nv._name, _index+u_nv._index);
		sub.makeSubstitution(this, $nv);
		sub.makeSubstitution(u_nv, $nv);
		return $nv;
	    } else {
		return this.copy();
	    }
	} else {
	    throw new UnifyFailure();
	}
    }

    public Unifiable fill (Substitution sub) throws UnifyFailure {
	Unifiable val = sub.getValue(this);
	if (val != null) {
	    return val;
	} else {
	    return this;
	}
    }

    
    public String toString () {	
	return _name+_index;
    }

}
