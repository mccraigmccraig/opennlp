///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2002 Jason Baldridge and Gann Bierner
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
package opennlp.common.util;

/**
 * To get around the lack of 1st class functions, this class defines
 * a function that determines whether a string is acceptable or not.
 * Thus, it is a "filter".
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.1 $, $Date: 2002/04/25 14:41:31 $
 */
public interface FilterFcn {

    /**
     * Determines if a string is "acceptable" by some unknown metric.
     *
     * @param s the string
     * @return its acceptability
     */
    public boolean filter (String s);


    /**
     * Determines if two strings are "acceptable" by some unknown metric.
     *
     * @param s1 the first string for the filter to consider
     * @param s2 the second string for the filter to consider
     * @return the acceptability of the two strings with respect to each other
     */
    public boolean filter (String s1, String s2);

}
