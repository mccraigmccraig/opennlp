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
import java.util.*;

public abstract class WFF {
    public static HashMap varhash = new HashMap();

    public static WFF getWFF(Element e) {
	String type = e.getName();
	if (type.equals("unary-op")) {
	    return new UnaryOp(e);
	}
	else if (type.equals("binary-op")) {
	    return new BinaryOp(e);
	    
	}
	else if (type.equals("nary-op")) {
	    return new NaryOp(e);	    
	}
	else if (type.equals("wff-var")) {
	    String varname = e.getAttributeValue("val");
	    WFF v;
	    if (varhash.containsKey(varname)) {
		v = (WFF)varhash.get(varname);
	    }
	    else {
		v = new LogicVar();
		varhash.put(varname, v);
	    }
	    return v;
	}
	else if (type.equals("nominal")) {
	    return new Nominal(e);
	}
	else if (type.equals("propsym")) {
	    return new PropSym(e);
	}
	else if (type.equals("sat-op")) {
	    return new SatOp(e);
	}
	else if (type.equals("logical-form")) {
	    return getWFF((Element)e.getChildren().get(0));
	}
	System.out.println("Invalid WFF type: " + type);
	return null;
    }

}
