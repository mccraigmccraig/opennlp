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
import java.util.*;

/**
 * A hybrid logic satifaction operator, which tests whether a formula is true
 * a particular point named by a nominal.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.7 $, $Date: 2002/01/07 18:12:03 $
 **/
public class SatOp extends HyloFormula {
    protected Nominal _nominal;
    protected LF _arg;

    public SatOp (Element e) {
	List l = e.getChildren();
	_nominal = new Nominal((Element)l.get(0));
	_arg = HyloHelper.getLF((Element)l.get(1));
    }

    public SatOp (Nominal nom, LF arg) {
	_nominal = nom;
	_arg = arg;
    }

    public LF copy () {
	return new SatOp((Nominal)_nominal.copy(), _arg.copy());
    }

    public void deepMap (ModFcn mf) {
	_nominal.deepMap(mf);
	_arg.deepMap(mf);
	mf.modify(this);
    }

    public boolean occurs (Variable var) {
	return (_arg.occurs(var));
    }

    public boolean equals (Object o){
	if (o instanceof SatOp
	    && _nominal.equals(((SatOp)o)._nominal)
	    && _arg.equals(((SatOp)o)._arg)) {
	    return true;
	} else {
	    return false;
	}
    }
    
    public void unifyCheck (Unifiable u) throws UnifyFailure {
	if (u instanceof SatOp) {
	    _nominal.unifyCheck(((SatOp)u)._nominal);
	    _arg.unifyCheck(((SatOp)u)._arg);
	} else {
	    throw new UnifyFailure();
	}
    }

    public Unifiable fill (Substitution sub) {
	if (_arg instanceof Variable) {
	    LF $arg = (LF)sub.getValue((Variable)_arg);
	    if ($arg != null) {
		return new SatOp(_nominal, $arg);
	    }
	}
	return new SatOp(_nominal, _arg);
    }

    public String toString () {	
	return "@_" + _nominal + "(" + _arg + ")";
    }

}
