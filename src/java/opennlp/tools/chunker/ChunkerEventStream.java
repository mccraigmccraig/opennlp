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
package opennlp.tools.chunker;

import opennlp.maxent.ContextGenerator;
import opennlp.maxent.DataStream;
import opennlp.maxent.Event;
import opennlp.maxent.EventStream;

import java.util.*;

/**
 * Class for creating an event stream out of data files for training a chunker. 
 *
 */
public class ChunkerEventStream implements EventStream {

  private ContextGenerator cg;
  private DataStream data;
  private Event[] events;
  private int ei;

  /**
   * Creates a new event stream based on the specified data stream.
   * @param d The data stream for this event stream.
   */
  public ChunkerEventStream(DataStream d) {
    this(d, new DefaultChunkerContextGenerator());
  }

  /**
   * Creates a new event stream based on the specified data stream using the specified context generator.
   * @param d The data stream for this event stream.
   * @param cg The context generator which should be used in the creation of events for this event stream.
   */
  public ChunkerEventStream(DataStream d, ContextGenerator cg) {
    this.cg = cg;
    data = d;
    ei = 0;
    if (d.hasNext()) {
      addNewEvents();
    }
    else {
      events = new Event[0];
    }
  }

  /* inherieted javadoc */
  public Event nextEvent() {
    if (ei == events.length) {
      addNewEvents();
      ei = 0;
    }
    return ((Event) events[ei++]);
  }

  /* inherieted javadoc */
  public boolean hasNext() {
    return (ei < events.length || data.hasNext());
  }

  private void addNewEvents() {
    List toks = new ArrayList();
    List tags = new ArrayList();
    List preds = new ArrayList();
    for (String line = (String) data.nextToken(); !line.equals(""); line = (String) data.nextToken()) {
      String[] parts = line.split(" ");
      toks.add(parts[0]);
      tags.add(parts[1]);
      preds.add(parts[2]);
    }
    events = new Event[toks.size()];
    for (int ei = 0, el = events.length; ei < el; ei++) {
      Object[] params = { new Integer(ei), toks, tags, preds };
      events[ei] = new Event((String) preds.get(ei), cg.getContext(params));
    }
  }
}
