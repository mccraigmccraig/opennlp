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

import org.jdom.*;

public abstract class Op extends HyloFormula {
    protected String op;
    protected String modality;

    public Op (String o, String m) {
	op = o;
	modality = m;
    }

    public Op (Element e) {
	op = e.getAttributeValue("op");
	modality = e.getAttributeValue("modality");
	if (modality == null) modality = "";
    }

    public String getOp () {
	return op;
    }

    public String toString () {	
	if (op.equals("box"))          
	    return "["+modality+"]";
	else if (op.equals("diamond")) 
	    return "<"+modality+">";
	else {
	    if (modality.equals("")) return printOp(op);
	    else return printOp(op) + "_" + modality; 
	}
	    
    }
    
    public static String printOp(String o) {
	if (o.equals("conj"))       return "^";
	else if (o.equals("disj"))  return "v";
	else if (o.equals("neg"))   return "~";
	else                        return o;
    }

}
