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
 * A modality label.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.1 $, $Date: 2002/02/05 11:50:29 $
 **/
public final class ModeLabel extends HyloAtom implements Mode {

    protected int _index = 0;
    
    public ModeLabel (String n) {
	super(n);
    }

    public ModeLabel (String n, int index) {
	this(n);
	setIndex(index);
    }

    public ModeLabel (Element e) {
	super(e);
    }

    public int getIndex () {
	return _index;
    }

    public void setIndex (int index) {
	_index = index;
    }

    public LF copy () {
	return new ModeLabel(_name);
    }

    public Unifiable unify (Unifiable u, Substitution sub)
	throws UnifyFailure {

	if (u instanceof HyloFormula) {
	    if (u instanceof ModeLabel && equals(u)) {
		return copy();
	    }
	    return super.unify(u,sub);
	} else {
	    throw new UnifyFailure();
	}
    }
    
    public boolean equals (Object o) {
	if (o instanceof ModeLabel
	    && _name.equals(((ModeLabel)o)._name) 
	    && _index == ((ModeLabel)o)._index) {
	    return true;
	} else {
	    return false;
	}
    }

}
