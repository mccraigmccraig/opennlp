///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2002 NASA Research Institute for Advanced Computer Science
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

import org.xml.sax.helpers.*;
import org.xml.sax.*;

/**
 * A ErrorHandler to provide messages about errors in parsing XML documents.
 * It furthermore keeps track of how many times it was called upon to report an
 * error. 
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.2 $, $Date: 2002/02/08 12:17:50 $
 */
public class ValidityErrorHandler implements ErrorHandler {
    private int numOfErrorsReported = 0;

    private boolean isVerbose = true;

    public void setVerbose (boolean _isVerbose) {
	isVerbose = _isVerbose;
    }
    
    public void reset () {
	numOfErrorsReported = 0;
    }

    public int getNumOfErrors () {
	return numOfErrorsReported;
    }
    
    /**
     * Receive notification of a recoverable error.
     */
    public void error (SAXParseException ex) {
	numOfErrorsReported++;
        if (isVerbose) System.err.println(getMessage("Error", ex));
    }

    /**
     * Receive notification of a non-recoverable error.
     */
    public void fatalError (SAXParseException ex) throws SAXException {
	numOfErrorsReported++;
        if (isVerbose) System.err.println(getMessage("Fatal Error", ex));
    }

    /**
     * Receive notification of a warning.
     */
    public void warning (SAXParseException ex) {
	numOfErrorsReported++;
        if (isVerbose) System.err.println(getMessage("Warning", ex));
    }


    /**
     * A helper method for preparing the message to print when something goes
     * wrong.
     */
    private String getMessage (String errorType, SAXParseException ex) {
	StringBuffer sb = new StringBuffer();
	sb.append(errorType);
	sb.append(" in document '");

	String name = ex.getSystemId();
        if (name == null) {
	    sb.append("UNKNOWN");
	}
	else {
            int index = name.lastIndexOf('/');
            if (index != -1) name = name.substring(index + 1);
            sb.append(name);
        }

	sb.append("' at line ");
	sb.append(ex.getLineNumber());
	sb.append(", column ");
	sb.append(ex.getColumnNumber());
	sb.append(":\n\t");
	sb.append(ex.getMessage());

	return sb.toString();
    }

}
