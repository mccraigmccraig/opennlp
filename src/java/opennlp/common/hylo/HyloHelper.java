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
import org.jdom.*;
import java.util.*;

public class HyloHelper {

    public static LF getLF (Element e) {
	String type = e.getName();
	if (type.equals("op")) {
	    return new Op(e);
	} else if (type.equals("var")) {
	    return new HyloVar(e.getAttributeValue("n"));
	} else if (type.equals("nomvar")) {
	    return new NominalVar(e.getAttributeValue("n"));
	} else if (type.equals("nom")) {
	    return new NominalAtom(e);
	} else if (type.equals("prop")) {
	    return new Proposition(e);
	} else if (type.equals("satop")) {
	    return new SatOp(e);
	} else if (type.equals("b")) {
	    return new Box(e);
	} else if (type.equals("d")) {
	    return new Diamond(e);
	} else if (type.equals("mode")) {
	    return new ModeLabel(e);
	} else if (type.equals("modevar")) {
	    return new ModeVar(e.getAttributeValue("n"));
	} else if (type.equals("lf")) {
	    return getLF((Element)e.getChildren().get(0));
	}
	System.out.println("Invalid hybrid logic LF type: " + type);
	return null;
    }

}
