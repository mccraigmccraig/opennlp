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
 * @version     $Revision: 1.10 $, $Date: 2002/01/20 17:45:25 $
 **/
public class SatOp extends HyloFormula {
    protected Nominal _nominal;
    protected LF _arg;

    public SatOp (Element e) {
	String nom = e.getAttributeValue("nom");
	if (nom != null) {
	    _nominal = new NominalAtom(nom);
	} else {
	    nom = e.getAttributeValue("nomvar");
	    if (nom != null) {
		_nominal = new NominalVar(nom);
	    } else {
		_nominal = new NominalVar();
	    }
	}    
	_arg = HyloHelper.getLF((Element)e.getChildren().get(0));
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
	return (_nominal.occurs(var) || _arg.occurs(var));
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
    
    public Unifiable unify (Unifiable u, Substitution sub)
	throws UnifyFailure {

	if (u instanceof HyloFormula) {
	    if (u instanceof SatOp) {
		try {
		    Nominal $nom;
		    LF $nomUnif =
			(LF)Unifier.unify(_nominal, ((SatOp)u)._nominal, sub);
		    if ($nomUnif instanceof Nominal) {
			$nom = (Nominal)$nomUnif;
		    } else {
			$nom = (Nominal)_nominal.copy();
		    }
		    LF $arg = (LF)Unifier.unify(_arg,((SatOp)u)._arg, sub);
		    return new SatOp($nom, $arg);
		} catch (UnifyFailure uf) {}
	    }
	    return super.unify(u,sub);
	} else {
	    throw new UnifyFailure();
	}
    }

    public Unifiable fill (Substitution sub) throws UnifyFailure {
	return new SatOp((Nominal)_nominal.fill(sub), (LF)_arg.fill(sub));
    }

    public String toString () {	
	return "@" + _nominal + "(" + _arg + ")";
    }

}
