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

package opennlp.common.xml;

import java.io.*;

/**
 * A class for going from plain text to a roughly marked-up NLPDocument.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.3 $, $Date: 2001/11/09 12:17:56 $
 */

public class RawToXml {

    public static PreProcessDocument process(File f) {
	try {
	    return process(new FileReader(f));
	} catch (IOException E) {
	    E.printStackTrace();
	    return null;
	}
    }
    public static PreProcessDocument process(InputStream i) {
	return process(new InputStreamReader(i));
    }

    public static PreProcessDocument process(Reader r) {
	StringBuffer text = new StringBuffer(32);
	String read;
	StringBuffer chunk = new StringBuffer(16);

	try {
	    BufferedReader br = new BufferedReader(r);
	    read = br.readLine();

	    while (read != null) {
				
		while (read.equals("")) read = br.readLine();
		while (read != null && !read.equals("")) {
		    chunk.append(read);
		    read = br.readLine();
		}
		
		// Don't think this is needed - jmb, joaoc
		//if (chunk.length() > 0)
		//   text.append("<PAR><SENT><TOK><LEX><![CDATA[").append(chunk.toString()).append("]]></LEX></TOK></SENT></PAR>");

	    }
	    
	    return new PreProcessDocument(chunk.toString());
	    
	} catch (Exception E) {
	    System.out.println(E);
	    return null;
	}
	
    }

    public static PreProcessDocument process(String s) {
	return new PreProcessDocument(s);
    }

}
