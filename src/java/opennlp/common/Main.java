///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2002 Artifactus Limited
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
 * @version     $Revision: 1.3 $, $Date: 2002/02/08 12:17:50 $
 */
public class Main {

    /**
     * Prints a message to the would-be executor telling them that the
     * executable exists as a standalone jar more than an exectuable
     * as such.  
     */
    public static void main (String[] args) throws Exception {
	System.out.println(
       "\n********************************************************************\n"
     + "The \"executable\" jar of OpenNLP Common does not currently execute\n"
     + "anything except this message.  It exists only so that there is a jar\n"
     + "of the package which contains all of the other jar dependencies\n"
     + "needed by the API so that users can download it and be able to use\n"
     + "it to build OpenNLP applications without hunting down the other jars.\n"
     + "********************************************************************\n"
        );
	
    }

}
