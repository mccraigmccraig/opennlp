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

public class HyloVar extends HyloFormula implements Variable, Indexed {
    
    private final String _name;
    private int _index;
    private int _hashCode;
    
    private static int UNIQUE_STAMP = 0;
    
    public HyloVar () {
	this("HLV"+UNIQUE_STAMP++);
    }
    
    public HyloVar (String name) {
	this(name, 0);
    }

    protected HyloVar (String name, int index) {
	_name = name;
	_index = index;
	_hashCode = _name.hashCode() + _index;
    }
    
    public String name () {
	return _name;
    }

    public LF copy () {
	return new HyloVar(_name, _index);
    }


    public int getIndex () {
	return _index;
    }

    public void setIndex (int index) {
	_hashCode += index - _index;
	_index = index;
    }

    public boolean occurs (Variable var) {
	return equals(var);
    }

    public int hashCode () {
	return _hashCode;
    }
    
    public boolean equals (Object o) {
	if (o instanceof HyloVar
	    && _index == ((HyloVar)o)._index
	    && _name.equals(((HyloVar)o)._name)) {
	    return true;
	} else {
	    return false;
	}
    }
    
    public Unifiable unify (Unifiable u, Substitution sub) throws UnifyFailure {
	if (u instanceof LF) {
	    if (u.occurs(this)) {
		throw new UnifyFailure();
	    }
	    return sub.makeSubstitution(this, u);
	}
	else {
	    throw new UnifyFailure();
	}
    }

    public Unifiable fill (Substitution sub) {
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
