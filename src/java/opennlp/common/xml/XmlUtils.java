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

package opennlp.common.xml;

import org.jdom.*;
import java.util.*;

/**
 * Utilities for manipulating XML based objects.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.3 $, $Date: 2001/11/16 12:12:15 $
 */

public class XmlUtils {

    /**
     * Returns all of the children with the given tag name.  This method stops
     * recursing under elements that match the <code>name</code> argument.
     */
    public static List getChildrenNested (Element e, String name) {
	List elements = new ArrayList();
	List children = e.getChildren();
	for (Iterator i=children.iterator(); i.hasNext(); ) {
	    Element c = (Element)i.next();
	    if (c.getName().equals("name")) {
		elements.add(c);
	    }
	    else {
		elements.addAll(getChildrenNested(c, name));
	    }
	}
	return elements;
    }

    /**
     * Returns the text content of all children with the given tag name.
     */
    public static String getChildTextNested (Element e, String name) {
	StringBuffer sb = new StringBuffer();
	List children = e.getChildren();
	if (e.getName().equals(name)) {
	    sb.append(e.getText());
	    if (children.size() > 0)
		sb.append(' ');
	}
	for (Iterator i=children.iterator(); i.hasNext(); )
	    sb.append(getChildTextNested((Element)i.next(), name)).append(' ');
	return sb.toString();
    }

    /**
     * Returns the text content of all children with the given tag name.
     */
    public static String getAllTextNested (Element e) {
	StringBuffer sb = new StringBuffer();
	sb.append(e.getText());
	List children = e.getChildren();
	if (children.size() > 0)
	    sb.append(' ');
	for (Iterator i=children.iterator(); i.hasNext(); )
	    sb.append(getAllTextNested((Element)i.next())).append(' ');
	return sb.toString();
    }


}
    
