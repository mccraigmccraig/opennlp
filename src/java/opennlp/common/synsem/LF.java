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

package opennlp.common.synsem;

import opennlp.common.unify.*;
import java.util.*;

/**
 * An interface for objects which represent Logical Forms.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.6 $, $Date: 2002/11/21 18:02:15 $
 */

public interface LF extends Unifiable, Mutable {

    /**
     * Simplifies this LF though some evaluation formula.
     */
    public void reduce ();

    public LF copy ();

    /**
     * Returns a hash string using the given map from vars to int strings.
     */
    public String hashString(Map varMap);
}
