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
package opennlp.common.xml;
    
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import java.io.*;

public class XmlVerifier {

    public static void main(String[] args) {

	if (args.length == 0) {
	    System.out.println("Usage: java opennlp.common.xml.XmlVerifier 'file'");
	    return;
	}

	String specfile = args[0];

	boolean showfile = false;
	if (args.length > 1)
	    showfile = args[1].equals("-p");
	
	try {
	    SAXBuilder builder = new SAXBuilder();
	    builder.setValidation(true);
	    Document d = builder.build(new File(specfile));
	    XMLOutputter out = new XMLOutputter("  ", true);
	    if (showfile) out.output(d, System.out);

	    System.out.println("\n" + specfile + " is valid.\n");
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (JDOMException e) {	    
	    System.out.println("\n"+e+"\n");
	} catch (NullPointerException e) {
	    e.printStackTrace();
	}
    }

}
