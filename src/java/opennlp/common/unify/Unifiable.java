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

package opennlp.common.unify;

/**
 * An interface for classes that may be unified.
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 **/

public interface Unifiable {
   
    /**
     * Determines if a Variable occurs within this Unifiable
     *
     * @param v the Variable to check for
     * @return whether or not the Variable occurs
     */
    public boolean occurs(Variable v);

    
    /**
     * Determines if this Unifiable is exactly equal to another Object.
     *
     * @param o object to check for equality
     * @return whether or not this is equal to <code> o </code>
     */
    public boolean equals(Object o);

}
