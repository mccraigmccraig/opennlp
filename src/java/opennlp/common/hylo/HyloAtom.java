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
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////

package opennlp.common.hylo;

import opennlp.common.unify.*;
import org.jdom.*;

/**
 * A logical atomic formula.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.2 $, $Date: 2002/01/03 11:10:49 $
 **/
public abstract class HyloAtom extends HyloFormula {
    
    protected final String _name;

    protected HyloAtom (String n) {
	_name = n;
    }
    
    protected HyloAtom (Element e) {
	_name = e.getAttributeValue("n");
    }

    public boolean occurs (Variable var) {
	return false;
    }

    public String toString () {	
	return _name;
    }

}
