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
import org.jdom.*;
import java.util.*;

public class Op extends HyloFormula {
    protected final String _name;
    protected List _args;
    
    public Op (Element e) {
	_name = e.getAttributeValue("n");
	List argElements = e.getChildren();
	int argSize = argElements.size();
	_args = new ArrayList(argSize);
	for (int i=0; i<argSize; i++) {
	    _args.add(HyloHelper.getLF((Element)argElements.get(i)));
	}
    }

    public Op (String name, List args) {
	_name = name;
	_args = args;
    }

    public Op (String name, LF first, LF second) {
	_name = name;
	_args = new ArrayList();
	_args.add(first);
	_args.add(second);
    }
    
    public String getName () {
	return _name;
    }

    public List getArguments () {
	return _args;
    }
    
    public void addArgument (LF formula) {
	_args.add(formula);
    }

    public LF copy () {
	List $args = new ArrayList();
	for (Iterator argsIt = _args.iterator(); argsIt.hasNext();) {
	    $args.add(((LF)argsIt.next()).copy());
	}
	return new Op(_name, $args);
    }

    public void deepMap (ModFcn mf) {
	for (Iterator argsIt = _args.iterator(); argsIt.hasNext(); ) {
	    ((LF)argsIt.next()).deepMap(mf);
	}
	mf.modify(this);
    }

    public boolean occurs (Variable var) {
	for (Iterator argsIt = _args.iterator(); argsIt.hasNext(); ) {
	    if (((LF)argsIt.next()).occurs(var)) {
		return true;
	    }
	}
	return false;
    }

    public boolean equals (Object o) {
	if (o instanceof Op && _name == ((Op)o)._name) {
	    List oArgs = ((Op)o)._args;
	    if (_args.size() == oArgs.size()) {
		for (Iterator argsIt = _args.iterator(); argsIt.hasNext();) {
		    boolean found = false;
		    Object arg = argsIt.next();
		    for (Iterator oArgsIt = oArgs.iterator();
			 !found && oArgsIt.hasNext(); ) {
			if (arg.equals(oArgsIt.next())) {
			    found = true;
			}
		    }
		    if (!found) {
			return false;
		    }
		}
		return true;
	    } else {
		return false;
	    }
	} else {
	    return false;
	}
    }
    
    public void unifyCheck (Unifiable u) throws UnifyFailure {
	if (!(u instanceof Op) || !(_name.equals(((Op)u)._name))) {
	    throw new UnifyFailure();
	}
    }

    public Unifiable unify (Unifiable u, Substitution s) throws UnifyFailure {
	if (u instanceof HyloFormula) {
	    Op $op = (Op)copy();
	    if (u instanceof Op
		&& (_name.equals("conj") || _name.equals("disj"))
		&& _name.equals(((Op)u)._name)) {

		for (Iterator addArgs=((Op)u).getArguments().iterator();
		     addArgs.hasNext();) {
		    $op.addArgument(((LF)addArgs.next()).copy());
		}
	    } else {
		$op.addArgument(((LF)u).copy());
	    }
	    return $op;
	} else {
	    throw new UnifyFailure();
	}
    }
    
    public Unifiable fill (Substitution sub) throws UnifyFailure {
	List $args = new ArrayList();
	for (Iterator argsIt = _args.iterator(); argsIt.hasNext();) {
	    $args.add(((LF)argsIt.next()).fill(sub));
	}
	return new Op(_name, $args);
    }
    
    public String toString () {
	StringBuffer sb = new StringBuffer();
	String opString = printOp(_name);
	if (_args.size() == 1) {
	    sb.append(opString);
	    sb.append(_args.get(0).toString());
	} else {
	    sb.append('(');
	    Iterator argsIt = _args.iterator();
	    sb.append(argsIt.next().toString());
	    for (; argsIt.hasNext(); ) {
		sb.append(' ').append(opString).append(' ');
		sb.append(argsIt.next().toString());
	    }
	    sb.append(')');
	}
	return sb.toString();
    }
    
    public static String printOp (String o) {
	if (o.equals("conj"))       return "^";
	else if (o.equals("disj"))  return "v";
	else if (o.equals("neg"))   return "~";
	else                        return o;
    }

}
