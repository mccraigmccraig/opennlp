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

import com.sun.xml.tree.*;
import org.w3c.dom.*;

import java.util.*;

/**
 * Modified from The Bean Factory, LLC for use in OpenNLP.  Original
 * author Nazmul Idris.
 *
 * <B>XmlUtils</B> is a utility method to get information about a 
 * XML document.  It also retrieves data in an XML document. 
 *
 * @author       : Jason Baldridge
 * @version      : $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */

public class XmlUtils{

    /**
     Return an Element given an XML document, tag name, and index
     
     @param     doc     XML docuemnt
     @param     tagName a tag name
     @param     index   a index
     @return    an Element
     */
    public static Element getElement(Document doc, String tagName, int index){
        NodeList rows = doc.getDocumentElement().getElementsByTagName(tagName);
        return (Element)rows.item(index);
    }
    
    public static Element getElement(Element e, String tagName, int index){
        NodeList rows = e.getElementsByTagName(tagName);
        return (Element)rows.item(index);
    }

    public static int getSize(Document doc, String tagName){
        NodeList rows = doc.getDocumentElement().getElementsByTagName(tagName);
        return rows.getLength();
    }


    public static int getSize(Element e, String tagName){
        NodeList rows = e.getElementsByTagName(tagName);
        return rows.getLength();
    }


    public static String getValue(Element e, String tagName){
	try{
	    //get node lists of a tag name from a Element
	    NodeList elements = e.getElementsByTagName(tagName);
	    
	    if (elements.getLength()==0) return "";
	    
	    Node node = elements.item(0);
	    
	    return (produceString(node.getChildNodes()));
	    
	}
	catch(Exception ex){
	    System.out.println(ex);
	    ex.printStackTrace();
	}
        
	return null;	
    }

    
    public static String getTotalValue(Element e, String tagName){
	try{
	    //get node lists of a tag name from a Element
	    NodeList elements = e.getElementsByTagName(tagName);
	    
	    if (elements.getLength()==0) return "";
	    
	    String str = "";
	    for(int i=0; i<elements.getLength(); i++)
		str+=produceString(elements.item(i).getChildNodes())+" ";
	    
	    return str.trim();
	    
	}
	catch(Exception ex){
	    System.out.println(ex);
	    ex.printStackTrace();
	}	
	return null;	
    }
    
    
    public static ArrayList getArrayList(Element e, String tagName){
	try{
	    ArrayList al = new ArrayList();
	    //get node lists of a tag name from a Element
	    NodeList elements = e.getElementsByTagName(tagName);
	    
	    if (elements.getLength()==0) return new ArrayList();
	    
	    for(int i=0; i<elements.getLength(); i++) {
		Node node = elements.item(i);
		String res = produceString(node.getChildNodes());
		al.add(res);
	    }
	    
	    return al;
	    
	}
	catch(Exception ex){
	    System.out.println(ex);
	    ex.printStackTrace();
	}
        
	return null;
	
    }
   

    public static String produceString(NodeList nodes) {
       String s = "";
        for(int i=0; i<nodes.getLength(); i++){
            String toAdd = ((Node)nodes.item(i)).getNodeValue().trim();
            if(toAdd.equals("") || toAdd.equals("\r")) {
                continue;
            }
            else s+= toAdd;
        }
	return s;
    }


    
    /**
     * For testing purpose, print out Node list
     * 
     * @param     rows    a Nodelist
     */
    public static void printNodeTypes(NodeList rows){
        System.out.println("\tenumerating NodeList (of Elements):");
        System.out.println("\tClass\tNT\tNV");
        //iterate a given Node list
        for(int ri = 0; ri < rows.getLength(); ri++){
            Node n = (Node)rows.item(ri);
            if(n instanceof Element) {
                System.out.print("\tElement");
            }
            else System.out.print("\tNode");
    
            //print out Node type and Node value
            System.out.println(
                "\t"+
                n.getNodeType() + "\t" +
                n.getNodeValue()
                );
        }
        System.out.println();
    }

    /**
     * Make a regular string into suitable PCDATA.
     * 
     * @param     s  The string which may contain special XML characters.
     */    
   public static String filt2XML(String s) {
	StringBuffer input = new StringBuffer(s);
	StringBuffer output = new StringBuffer(input.length());
	for(int i=0; i<input.length(); i++) {
	    char c = input.charAt(i);
	    switch(c) {
	    case '<': output.append("&lt;"); break;
	    case '>': output.append("&gt;"); break;
	    case '&': output.append("&amp;"); break;
	    case '\'': output.append("&apos;"); break;
	    case '"': output.append("&quot;"); break;
	    default: output.append(c);
	    }
	}
	return output.toString();
    }

    
    /**
     * Create ArrayList of out of the elements of a NodeList along with one to
     * keep mapping to original lexical material associated with those
     * elements. 
     * 
     * @param     nl  The nodelist to collect information from.
     * @return  A Pair containing two ArrayLists, the first containing token
     * nodes, and the second containing the lexical material associated with
     * those token nodes.
     */
    public static Pair getElementTokenLists(NodeList tokNList) {

      	ArrayList tokStrings = new ArrayList();
	ArrayList tokElements = new ArrayList();

	for (int i=0; i<tokNList.getLength(); i++) {
	    Element e = (Element)tokNList.item(i);
	    tokStrings.add(XmlUtils.getValue(e, "LEX"));
	    tokElements.add(e);
	}
	return new Pair(tokElements, tokStrings);
    }

    
    /**
     * Create a TOK element for an NLP Document.
     * 
     * @param xd the XmlDocument which this TOK will belong to.
     * @param tok the string of text associated with this token.
     */
    public static Element createTOK(XmlDocument xd, String tok) {
	Element newTok = xd.createElement("TOK");
	Element newLex = xd.createElement("LEX");
	newLex.appendChild(xd.createTextNode(tok));
	newTok.appendChild(newLex);
	return newTok;
    }

    
    /**
     * Create a SENT element for an NLP Document.
     * 
     * @param xd the XmlDocument which this SENT will belong to.
     * @param sent the string of text associated with this sentence.
     */
    public static Element createSENT(XmlDocument xd, String sent) {
	int l = sent.length();
	Element newSENT = xd.createElement("SENT");
	if (l>0) {
	    Element newDELIM = xd.createElement("TOK");
	    String delim = sent.substring(l-1);
	    if(delim.equals(".") || delim.equals("?") || delim.equals("!")){
		newSENT.appendChild(XmlUtils.createTOK(xd,
						       sent.substring(0,l-1)));
		newDELIM.appendChild(XmlUtils.createTOK(xd, delim));
		newSENT.appendChild(newDELIM);
	    } else
		newSENT.appendChild(XmlUtils.createTOK(xd, sent));

	    return newSENT;
	} else {
	    return null;
	}
    }
    

}

