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
import org.jdom.*;
import java.util.*;

public class NaryOp extends Op {
    private LF[] args;

    public NaryOp (String o, String m, LF[] a) {
	super(o,m);
	args = a;
    }

    public NaryOp (Element e) {
	super(e);
	List argElements = e.getChildren();
	args = new LF[argElements.size()];
	for (int i=0; i<args.length; i++)
	    args[i] = HyloHelper.getLF((Element)argElements.get(i));
    }

    public Op insertLF (LF formula) {
	NaryOp cp = this.copy();
	cp.args = new LF[args.length+1];
	for (int i=0; i<args.length; i++)
	    cp.args[i] = args[i];
	cp.args[cp.args.length-1] = formula;
	return cp;
    }

    public NaryOp copy () {
	return new NaryOp (op, modality, args);
    }

    public String toString () {
	String arglist = args[0].toString();
	for (int i=1; i<args.length; i++)
	    arglist += ", " + args[i].toString();
	return super.toString() + "(" + arglist + ")";
    }

}
