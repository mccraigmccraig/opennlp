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

import org.jdom.*;

/**
 * A hybrid logic nominal, an atomic formula which holds true at exactly one
 * point in a model.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.3 $, $Date: 2002/01/02 10:44:22 $
 **/
public class Nominal extends HyloAtom {
    
    public Nominal (Element e) {
	super(e);
    }

    public boolean equals (Object o) {
	if (o instanceof Nominal
	    && _name == ((Nominal)o)._name) {
	    return true;
	} else {
	    return false;
	}
    }

}
