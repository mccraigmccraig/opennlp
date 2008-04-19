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

package opennlp.tools.postag;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import opennlp.maxent.Event;
import opennlp.maxent.EventCollector;
import opennlp.tools.util.Pair;

/**
 * An event generator for the maxent POS Tagger.
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.11 $, $Date: 2008/04/19 19:56:58 $
 */

public class POSEventCollector implements EventCollector {
  
  private BufferedReader br;
  private POSContextGenerator cg;
  
  /**
   * Initializes the current instance.
   * 
   * @param data
   * @param gen
   */
  public POSEventCollector(Reader data, POSContextGenerator gen) {
    br = new BufferedReader(data);
    cg = gen;
  }
  
  private static Pair<String, String> split(String s) {
    int split = s.lastIndexOf("_");
    if (split == -1) {
      System.out.println("There is a problem in your training data: "
          + s
          + " does not conform to the format WORD_TAG.");
      return new Pair<String, String>(s, "UNKNOWN");
    }
    
    return new Pair<String, String>(s.substring(0, split), s.substring(split+1));
  }
  
  public static Pair<List<String>, List<String>> convertAnnotatedString(String s) {
    ArrayList<String> tokens = new ArrayList<String>();
    ArrayList<String> outcomes = new ArrayList<String>();
    StringTokenizer st = new StringTokenizer(s);
    while(st.hasMoreTokens()) {
      Pair<String, String> p = split(st.nextToken());
      tokens.add(p.a);
      outcomes.add(p.b);
    }
    return new Pair<List<String>, List<String>>(tokens, outcomes);
  }
  
  public Event[] getEvents() {
    return getEvents(false);
  }
    
  /** 
   * Builds up the list of features using the Reader as input.  For now, this
   * should only be used to create training data.
   */
  public Event[] getEvents(boolean evalMode) {
    List<Event> elist = new ArrayList<Event>();
    try {
      String s = br.readLine();
      
      while (s != null) {
        Pair<List<String>, List<String>> p = convertAnnotatedString(s);
        List<String> tokens = p.a;
        List<String> outcomes = p.b;
        List<String> tags = new ArrayList<String>();
        
        for (int i=0; i<tokens.size(); i++) {
          String[] context = cg.getContext(i,tokens.toArray(),(String[]) tags.toArray(new String[tags.size()]),null);
          Event e = new Event((String)outcomes.get(i), context);
          tags.add(outcomes.get(i));
          elist.add(e);
        }
        s = br.readLine();
      }
    } 
    catch (Exception e) { 
      e.printStackTrace(); 
    }
    
    Event[] events = new Event[elist.size()];
    for(int i=0; i<events.length; i++)
      events[i] = (Event)elist.get(i);
    
    return events;
  }
}