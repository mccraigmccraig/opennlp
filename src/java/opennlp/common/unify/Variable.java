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
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////

package opennlp.common.unify;

/**
 * A variable that can stand for any Object.
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.3 $, $Date: 2002/01/02 10:44:22 $
 **/
public interface Variable extends Unifiable {

    /**
     * Returns the name of this variable.
     *
     * @return the variable's name
     **/        
    public String name ();

    /**
     * Creates a copy of this variable using the given int to make the new
     * Variable unique.
     *
     * @param index An int to use in creating the unique copy.
     *
     * @return a copy of this variable which is unique by virtue of the given
     * index.
     **/
    public Variable uniqueCopy (int index);
    
}
