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

public class Modal extends HyloFormula {
    // If true, this Modal is of the diamond variety, if false, it is a box.
    private final boolean _isDiamond;

    // the relation
    private final String _relation;
    private LF _arg;


    public Modal (Element e) {
	String type = e.getAttributeValue("type");
	if (type != null && type.equals("b")) {
	    _isDiamond = false;
	} else {
	    _isDiamond = true;
	}
	
	_relation = e.getAttributeValue("rel");
	_arg = HyloHelper.getLF((Element)e.getChildren().get(0));
    }

    protected Modal (boolean isDiamond, String rel, LF arg) {
	_isDiamond = isDiamond;
	_relation = rel;
	_arg = arg;
    }

    public LF copy () {
	return new Modal (_isDiamond, _relation, _arg.copy());
    }
    
    public void modify (ModFcn mf) {
	_arg.deepMap(mf);
	mf.modify(this);
    }

    public boolean occurs (Variable var) {
	return _arg.occurs(var);
    }

    public boolean equals (Object o) {
	if (o instanceof Modal
	    && _isDiamond == ((Modal)o)._isDiamond
	    && _relation.equals(((Modal)o)._relation)
	    && _arg.equals(((Modal)o)._arg)) {
	    return true;
	} else {
	    return false;
	}
    }

    public Object unifyCheck (Object o) throws UnifyFailure {
	if (o instanceof Modal
	    && _isDiamond == ((Modal)o)._isDiamond
	    && _relation.equals(((Modal)o)._relation)) {

	    _arg.unifyCheck(((Modal)o)._arg);
	    return this;

	} else {
	    throw new UnifyFailure();
	}
    }

    public Object fill (Substitution sub) {
	return new Modal(_isDiamond, _relation, (LF)_arg.fill(sub));
    }
    
    public String toString () {
	StringBuffer sb = new StringBuffer();
	if (_isDiamond) {
	    sb.append('<');
	} else {
	    sb.append('[');
	}
	sb.append(_relation);
	if (_isDiamond) {
	    sb.append('>');
	} else {
	    sb.append(']');
	}
	sb.append(_arg.toString());
	return sb.toString();
    }

}
