///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2000 Jason Baldridge and Gann Bierner
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

package opennlp.common.discourse;

import opennlp.common.synsem.*;
import opennlp.common.unify.*;

import java.util.*;

/**
 * A class for storing and maintaining a list of referrable things.  That
 * items on the list can be referred to is not required but probably will be
 * soon.
 *
 * @author      Gann Bierner
 * @version $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */

public interface SalienceList extends List {

    /**
     * Finds the most salient item in a given set
     *
     * @param s the set
     * @return the most salient item in the set
     */
    public Denoter mostSalient(Set s);
    
    /**
     * Find the most salient item in a given set with
     * the right features
     *
     * @param s the set
     * @param f the required features
     * @return the most salient item in the set
     */
    public Denoter mostSalient(Set gs, Feature f);
    
    /**
     * Find the most salient item that could be described
     * by a give semantic form
     *
     * @param c semantics that needs to match the item
     * @return the most salient item in the set
     */
    public Denoter mostSalient(Denoter c);
    
    /**
     * Make the entity described by a semantic form more salient
     *
     * @param ent semantic form represting entity to promote
     */
    public void promote(Denoter ent);

    /**
     * return a shallow copy of this list
     *
     * @return a shallow copy of this list
     */
    public Object clone();

}
