///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2002 Research Institute for Advanced Computer Science
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

import java.io.*;
import org.apache.xerces.parsers.*;
import org.xml.sax.*;

/**
 * A utility for validating an XML document with respect to an XML Schema.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.3 $, $Date: 2002/08/19 11:07:17 $
 */
public class SchemaBasedValidator {
    private SAXParser parser = new SAXParser();
    private ValidityErrorHandler errorTracker = new ValidityErrorHandler();
    private boolean isVerbose = true;
    private File schema;
    private String messageString = ""; //8.16.02 sce

    /**
     * Two-arg constructor sets the verbosity of the validator and calls the
     * one-arg constructor with the File given.
     * 
     * @param schema the schema to use as the basis for document validity
     * @param _isVerbose true if the validator should print messages, false if
     *                   it should remain quiet
     */
    public SchemaBasedValidator (File schema, boolean _isVerbose) {
	this(schema);
	isVerbose = _isVerbose;
	errorTracker.setVerbose(isVerbose);
    }


    /**
     * Constructor which takes a File containing the XML Schema with respect
     * to which this validator will validate XML documents.
     *
     * @param schema the schema to use as the basis for document validity
     */
    public SchemaBasedValidator (File _schema) {
	parser.setErrorHandler(errorTracker);
	schema = _schema;
	
	try {
	    parser.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
			       schema.getAbsolutePath());

	    XMLReader reader = (XMLReader)parser;
	    reader.setFeature("http://xml.org/sax/features/validation", true);
	    reader.setFeature("http://xml.org/sax/features/namespaces", true);
	    reader.setFeature("http://apache.org/xml/features/validation/schema", true);
	    reader.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
	}
	catch (Exception e) {}
    }

    
    /**
     * Sets whether this validator will print out messages while it is
     * validating.
     */
    public void setVerbose (boolean _isVerbose) {
	isVerbose = _isVerbose;
	errorTracker.setVerbose(isVerbose);
    }

    
    /**
     * Check the validity of an XML document against the Schema associated
     * with this validator.
     *
     * @param file The location of the XML document to validate.
     * @return true if the document is valid, false otherwise
     */
    public boolean checkValidity (String file) throws IOException {
	InputSource xmlToValidate = new InputSource(file);

	errorTracker.reset();

	try {
	    parser.reset();
	}
	catch (Exception e) {
	    System.err.println("Unable to reset validator: "
			       + e.getMessage());
	}
	
	try {
	    parser.parse(xmlToValidate);
	}
	catch (SAXException e) {}

	int numErrors = errorTracker.getNumOfErrors();
	if (numErrors == 0) {
	    //8.16.02  sce   spelling of 'repect' --> 'respect'
	    printMessage("'" + file + "' is valid with respect to schema '" 
			 + schema.getName() + "'.");
	    return true;
	}
	else {
	    if (numErrors == 1)
		printMessage("\n1 error found in '" + file + "'.");
	    else 
		printMessage("\n" + numErrors
			     + " error(s) found in '" + file + "'.");
	    return false;
	}

    }

    // helper method to print output only if this validator is verbose
    private void printMessage (String message) {
	messageString = errorTracker.getMessageString();//8.16.02 sce
	messageString = messageString + message;//8.16.02 sce
	if (isVerbose) System.out.println(message);
    }


    //8.9.02 sce
    public String getMessageString() {
	return messageString;
    }

    /**
     * Command line method to check the validity of a document with respect to
     * an XML Schema.
     *
     * <p>Usage: java opennlp.common.xml.SchemaBasedValidator 'schema-file' 'file-to-validate'
     */
    public static void main (String[] args) throws Exception {
	if (args.length != 2) {
	    System.out.println("Usage: java opennlp.common.xml.SchemaBasedValidator 'schema-file' 'file-to-validate'");
	    return;
	}
	
	SchemaBasedValidator validator =
	    new SchemaBasedValidator(new File(args[0]));

	validator.checkValidity(args[1]);
    }

}
