///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2003 Thomas Morton
//
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////

package opennlp.tools.util;

import java.util.HashMap;
import java.util.Iterator;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Set which counts the number of times a values are added to it.  
 */
public class CountedSet extends HashMap {

  public CountedSet() {
    super();
  }

  public CountedSet(int s) {
    super(s);
  }

  public void add(Object o) {
    Integer count = (Integer) this.get(o);  
    if ( count == null ) { 
      this.put(o, new Integer(1));  
    } 
    else { 
      this.put(o, new Integer(count.intValue()+1)); 
    }
  }

  public void subtract(Object o) {
    Integer count = (Integer) this.get(o);  
    if ( count != null ) { 
      int c = count.intValue()-1;
      if (c > 0) {
	this.remove(o);

      }
      else {
	this.put(o, new Integer(c)); 
      }
    }
  }

  public void setCount(Object o,int c) {
    this.put(o,new Integer(c));
  }

  public int getCount(Object o) {
    Integer count = (Integer) this.get(o);   
    if ( count == null ) {
      return(0);
    }
    else {
      return(count.intValue());
    }
  }

  public void write(String fileName,int countCutoff) {
    write(fileName,countCutoff," ");
  }

  public void write(String fileName,int countCutoff,String delim) {
    write(fileName,countCutoff,delim,null); 
  }


  public void write(String fileName,int countCutoff,String delim,String encoding) {
    PrintWriter out = null;
    try{  
      if (encoding != null) {
	out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName),encoding));  
      }
      else {
	out = new PrintWriter(new FileWriter(fileName));  
      }
  
      for (Iterator e = this.keySet().iterator();  e.hasNext();) {  
	Object key = e.next();  
	int count = this.getCount(key);
	if ( count >= countCutoff ) {
	  out.println(count + delim + key);  
	}
      }  
      out.close();  
    }  
    catch (IOException e) {  
      System.err.println(e);  
    }
  }

}
