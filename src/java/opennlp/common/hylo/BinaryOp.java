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

public class BinaryOp extends Op {
    private LF arg1, arg2;

    public BinaryOp (String o, String m, LF a1, LF a2) {
	super(o,m);
	arg1 = a1;
	arg2 = a2;
    }

    public BinaryOp (Element e) {
	super(e);
	List argElements = e.getChildren();
	arg1 = HyloHelper.getLF((Element)argElements.get(0));
	arg2 = HyloHelper.getLF((Element)argElements.get(1));
    }


    public String toString () {
	return "(" + arg1 + super.toString() + arg2 + ")";
    }

}
