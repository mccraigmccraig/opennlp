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

package opennlp.common;

import opennlp.common.preprocess.*;
import opennlp.common.xml.*;
import java.util.*;
import java.io.*;
import java.net.*;


/**
 * A pipeline of components that process NLPDocuments.
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.3 $, $Date: 2002/02/08 12:17:50 $
 **/
public class Pipeline {

    Pipelink[] links;

    public Pipeline () {
	links = new Pipelink[0];
    }
    
    public Pipeline (Pipelink[] l) throws PipelineException {
	links = l;
	verify();
    }

    public Pipeline (String[] s) throws PipelineException {
	links = new Pipelink[s.length];
	
	try {
	    for (int i=0; i<s.length; i++) {
		links[i] = (Pipelink)Class.forName(s[i]).newInstance();
	    }
	} catch (Exception e) {
	    System.out.println("Error in creating Pipeline:");
	    System.out.println(e);
	}
				       
	verify();
    }

    public void verify () throws PipelineException {
	Set modules = new HashSet();
	for (int i=0; i<links.length; i++) {
	    Pipelink pl = links[i];
	    if(!modules.containsAll(pl.requires())) {
		exit("Pipeline does not meet requirements of " + pl);
	    }
	    modules.addAll(interfacesOf(pl.getClass()));
	}
    }
    
    public static void verify (List l) throws PipelineException {
	Set modules = new HashSet();
	for (Iterator i = l.iterator(); i.hasNext();) {
	    String pl = (String)i.next();
	    Set set=null;
	    Class c=null;
	    try {
		c = Class.forName(pl);
		set = ((Pipelink)c.newInstance()).requires();
	    } catch (ClassNotFoundException E) {
		exit("Not a valid class: " + pl);
	    } catch (InstantiationException E) {
		exit("Cannot instantiate: " + pl);
	    } catch (IllegalAccessException E) {
		exit("Cannot Access: " + pl);
	    }
	    if(!modules.containsAll(set)) {
		exit("Pipeline does not meet requirements of " + pl);
	    }
	    modules.addAll(interfacesOf(c));
	}
    }

    public NLPDocument run (Object input) throws PipelineException {
	NLPDocumentBuilder builder = new NLPDocumentBuilder();
	NLPDocument doc=null;
	if (input instanceof String) {
	    doc = builder.build((String)input);
	} else if (input instanceof File) {
	    doc = builder.build((File)input);
	} else if (input instanceof Reader) {
	    doc = builder.build((Reader)input);
	} else {
	    exit("Invalid input type to Pipeline");
	}

	for(int i=0; i<links.length; i++) {
	    links[i].process(doc);
	}
	
	return doc;
    }

    private static Collection interfacesOf (Class c) {
	HashSet set = new HashSet();
	interfacesOf(c, set);
	return set;
    }
    
    private static void interfacesOf(Class o, Set set) {
	Class[] interfaces = o.getInterfaces();
	for (int i=0; i<interfaces.length; i++) {
	    set.add(interfaces[i]);
	    interfacesOf(interfaces[i], set);
	}
	Class superClass = o.getSuperclass();
	if (superClass!=null) {
	    interfacesOf(superClass, set);
	}
    }

    private static void exit (String message) throws PipelineException {
	throw new PipelineException(message);
    }
    
}

