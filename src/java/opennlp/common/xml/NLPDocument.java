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
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

/**
 * A class which wraps an XmlDocument inside and ensures that it fits OpenNLP
 * specifications.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.2 $, $Date: 2001/11/09 12:17:56 $
 */

public abstract class NLPDocument {
    protected Document nlpdoc;
    abstract String getDTD();
    
    public NLPDocument(String text) {
	String doc = getDTD() + "<NLPDOC>\n" +
	    "<TEXT>\n" + text + "</TEXT>\n" + "</NLPDOC>\n";

	try {
            nlpdoc = new SAXBuilder(true).build(new StringReader(doc));
	} catch (Exception e) {
	    System.out.println("Invalid XML Document.");
	    System.out.println(e.toString());
	}
	
    }

    public Document getNLPDoc() { return nlpdoc; }
    
    public String toString() {
	StringWriter sw = new StringWriter();
         
	  try { 
	      new XMLOutputter("  ", true).output(nlpdoc, sw);
	  }
	  catch (Exception e) {
	      System.out.println("Unable to print document.");
	      System.out.println(e);
	  } 
	
	return sw.toString();
    }
    
    public static void main (String[] args) {
	NLPDocument doc = new PreProcessDocument("Here is a sentence");
	System.out.println(doc.toString());
    }
}
