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

import opennlp.common.util.*;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import java.io.*;


/**
 * A class which wraps an XmlDocument inside and ensures that it fits OpenNLP
 * specifications.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.4 $, $Date: 2001/11/27 17:19:38 $
 */
public class NLPDocument extends Document {

   /**
    * Constructor method.
    * 
    * Creates a NLPDocument object from a given text.
    * It breaks the text into paragraphs and "quasiWords" 
    * (chunks of text separated by white spaces).
    */
    public NLPDocument(String text) {
	super();
	
	Element root = new Element("nlpDocument");
	Element textEl = new Element("text");
	String[] paragraphs, quasiWords;
	    
	paragraphs = PerlHelp.getParagraphs(text);
	for (int i=0; i<paragraphs.length; i++) {
	    Element paraEl = new Element("p");
	    Element sentEl = new Element("s");
	    quasiWords = PerlHelp.splitByWhitespace(paragraphs[i]);
	    for (int j=0; j<quasiWords.length; j++) {
		Element tokenEl = new Element("t");
		Element wordEl = new Element("w");
		wordEl.setText(quasiWords[j]);
		tokenEl.addContent(wordEl);
		sentEl.addContent(tokenEl);
	    }
	    paraEl.addContent(sentEl);
	    textEl.addContent(paraEl);
	}
	root.addContent(textEl);
	setRootElement(root);
    }

    
    /**
     * Create a SENT element for an NLP Document.
     * 
     * @param sent the string of text associated with this sentence.
     */
    public static Element createSENT(String sent) {
        int l = sent.length();
        Element sentEl = new Element("s");
        if (l>0) {
            char delim = sent.charAt(l-1);
            if(delim == '.' || delim == '?' || delim == '!') {
                sentEl.addContent(createTOK(sent.substring(0,l-1)));
                sentEl.addContent(createTOK(""+delim));
            }
	    else {
                sentEl.addContent(createTOK(sent));
	    }
        }
	return sentEl;
    }


    /**
     * Create a TOK element for an NLP Document.
     * 
     * @param tok the string of text associated with this token.
     */
    public static Element createTOK(String tok) {
        Element tokEl = new Element("t");
        Element lexEl = new Element("w");
	lexEl.setText(tok);
        tokEl.addContent(lexEl);
        return tokEl;
    }

    public String toString() {
        StringWriter sw = new StringWriter();
	
          try {
              new XMLOutputter("  ", true).output(this, sw);
          }
          catch (Exception e) {
              System.out.println("Unable to print document.");
              System.out.println(e);
          }
	  
	  return sw.toString();
    }

    public static void main (String[] args) {
	NLPDocument doc = new NLPDocument("Here is a sentence");
	System.out.println(doc.toString());
    }
}
