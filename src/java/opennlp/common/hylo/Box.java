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
import org.jdom.*;

public final class Box extends ModalOp {

    public Box (Element e) {
	super(e);
    }

    private Box (Mode mode, LF arg) {
	super(mode, arg);
    }

    public LF copy () {
	return new Box ((Mode)_mode.copy(), _arg.copy());
    }
    
    public boolean equals (Object o) {
	if (o instanceof Box) {
	    return super.equals((Box)o);
	} else {
	    return false;
	}
    }

    public void unifyCheck (Unifiable u) throws UnifyFailure {
	if (u instanceof Box) {
	    super.unifyCheck((Box)u);
	} else {
	    throw new UnifyFailure();
	}
    }

    public Unifiable fill (Substitution sub) throws UnifyFailure {
	return new Box((Mode)_mode.fill(sub), (LF)_arg.fill(sub));
    }
    
    public String toString () {
	return new StringBuffer().append('[').append(_mode.toString()).append(']').append(_arg.toString()).toString();
    }

}
