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
import com.sun.xml.tree.*;
import org.xml.sax.*;

/**
 * A class which wraps an XmlDocument inside and ensures that it fits OpenNLP
 * specifications.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */

public abstract class NLPDocument {
    protected XmlDocument nlpdoc;

    abstract String getDTD();
    
    public NLPDocument(String text) {
	String doc = getDTD() + "<NLPDOC>\n" +
	    "<TEXT>\n" + text + "</TEXT>\n" + "</NLPDOC>\n";

	InputSource is = new InputSource(new StringReader(doc));

	try {
	    nlpdoc = XmlDocument.createXmlDocument(is, true);
	} catch (Exception e) {
	    System.out.println("Invalid XML Document.");
	}
	
    }

    public XmlDocument getNLPDoc() { return nlpdoc; }
    
    public String toString() {
	StringWriter sw = new StringWriter();

	try { nlpdoc.write(sw); }
	catch (Exception e) {
	    System.out.println("Unable to print document.");
	    System.out.println(e);
	} 
	
	return sw.toString();
    }
    
}
