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

package opennlp.common.structure;

/**
 * Raised whenever the string representation of a <code> Category </code>
 * is unable to be parsed.  There is nothing saying that this exception
 * could not also be thrown if a more complex description of a category
 * fails to "become" a category.  For instance, an XML representation.
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */
public class CatParseException extends Exception {
    /** the category that has failed to be parsed */
    protected String cat;

    /**
     * Class constructor specifying the string that could not be parsed
     * as a category
     *
     * @param s represents the desired category
     */
    public CatParseException(String s) {cat = s;}

    public String toString() {
	return ("Unable to Parse: " + cat);
    }
}
