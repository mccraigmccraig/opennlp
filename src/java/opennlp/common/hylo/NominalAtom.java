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

package opennlp.common.hylo;

import opennlp.common.synsem.*;
import opennlp.common.unify.*;
import org.jdom.*;

/**
 * A hybrid logic nominal, an atomic formula which holds true at exactly one
 * point in a model.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.2 $, $Date: 2002/01/20 15:03:47 $
 **/
public class NominalAtom extends HyloAtom implements Nominal {

    protected int _index = 0;
    
    public NominalAtom (String n) {
	super(n);
    }

    public NominalAtom (Element e) {
	super(e);
    }

    public int getIndex () {
	return _index;
    }

    public void setIndex (int index) {
	_index = index;
	_name += index;
    }

    public LF copy () {
	return new NominalAtom(_name);
    }

    public Unifiable unify (Unifiable u, Substitution sub)
	throws UnifyFailure {

	if (u instanceof HyloFormula) {
	    if (u instanceof NominalAtom && equals(u)) {
		return copy();
	    }
	    return super.unify(u,sub);
	} else {
	    throw new UnifyFailure();
	}
    }
    
    public boolean equals (Object o) {
	if (o instanceof NominalAtom
	    && _name.equals(((NominalAtom)o)._name) 
	    && _index == ((NominalAtom)o)._index) {
	    return true;
	} else {
	    return false;
	}
    }

}
