///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2001 Artifactus Limited
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
package opennlp.common;

/**
 * A main class for the executable jar.  It isn't much for now, but we make a
 * call to the opennlp.common.xml.SchemaBasedValidator class.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.1 $, $Date: 2001/11/09 15:23:48 $
 */
public class Main {

    /**
     * Calls the main method of the <code>SchemaBasedValidator</code> class.
     */
    public static void main (String[] args) throws Exception {
	opennlp.common.xml.SchemaBasedValidator.main(args);
    }

}
