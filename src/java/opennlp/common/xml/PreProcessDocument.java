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


// import java.io.*;
// import org.xml.sax.*;
// import com.sun.xml.tree.*;

/**
 * A document which contains the various kinds of markup for the preprocessing
 * steps. 
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.2 $, $Date: 2001/11/09 12:17:56 $
 */

public class PreProcessDocument extends NLPDocument {
    public static final String dtd =
	"<?xml version='1.0' encoding='UTF-8'?>" +
	"<!DOCTYPE NLPDOC [\n" +
	"<!ELEMENT NLPDOC (TEXT)>\n" +
	"<!ELEMENT TEXT (PAR+)>\n" +
	"<!ELEMENT PAR (SENT+)>\n" +
	"<!ELEMENT SENT ((TOK|NAME)+)>\n" +
	"<!ELEMENT NAME (TOK+)>\n" +
	"  <!ATTLIST NAME type CDATA #IMPLIED>\n" +
	"<!ELEMENT TOK (LEX,(MORPH)*,POS?)>\n" +
	"<!ELEMENT LEX (#PCDATA)>\n" +
	"<!ELEMENT MORPH (STEM,POS?,(MACRO)*)>\n" +
	"<!ELEMENT STEM (#PCDATA)>\n" +
	"<!ELEMENT POS (#PCDATA)>\n" +
	"<!ELEMENT MACRO (#PCDATA)>\n" +
	"]>\n";
	
    
    public PreProcessDocument(String text) {
	  super("<PAR><SENT><TOK><LEX><![CDATA["
		+ text
		+"]]></LEX></TOK></SENT></PAR>");
    }

    public String getDTD() { return dtd; }

}
