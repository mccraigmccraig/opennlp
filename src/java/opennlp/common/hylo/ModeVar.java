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

public class ModeVar extends HyloVar implements Mode {
    
    private static int UNIQUE_STAMP = 0;
    
    public ModeVar () {
	super("MV"+UNIQUE_STAMP++);
    }
    
    public ModeVar (String name) {
	super(name);
    }

    protected ModeVar (String name, int index) {
	super(name, index);
    }
    

    public LF copy () {
	return new ModeVar(_name, _index);
    }

    
    public boolean equals (Object o) {
	if (o instanceof ModeVar
	    && _index == ((ModeVar)o)._index
	    && _name.equals(((ModeVar)o)._name)) {
	    return true;
	} else {
	    return false;
	}
    }
    
    public Unifiable unify (Unifiable u, Substitution sub) throws UnifyFailure {
	if (u instanceof ModeLabel) {
	    return sub.makeSubstitution(this, u);
	} else if (u instanceof ModeVar) {
	    ModeVar u_nv = (ModeVar)u;
	    if (!equals(u_nv)) {
		ModeVar $nv =
		    new ModeVar(_name+u_nv._name, _index+u_nv._index);
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
