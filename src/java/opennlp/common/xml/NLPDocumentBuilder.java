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

public class NLPDocumentBuilder {
   /**
    * Creates an NLPDocument from a String.
    **/
    public NLPDocument build (String text) {
	NLPDocument doc = new NLPDocument(text);
	return doc;
    }

   /**
    * Creates an NLPDocument from a File.
    **/
    public NLPDocument build (File input) {
	try {
	    return build(new FileReader(input));
	} catch (Exception e) {
            System.out.println(e);
	    return new NLPDocument();
	}
    }

   /**
    * Creates an NLPDocument from a Reader.
    **/
    public NLPDocument build (Reader input) {
	StringBuffer text = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(input);
            String read = br.readLine();
            while (read != null) {                                
		text.append(read).append('\n');
		read = br.readLine();
	    }
        } catch (Exception e) {
            System.out.println(e);
	}
	return build(text.toString());
    }
}
