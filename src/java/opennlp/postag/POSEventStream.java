///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2003 Tom Morton
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

package opennlp.postag;

import opennlp.maxent.EventStream;
import opennlp.maxent.ContextGenerator;
import opennlp.maxent.DataStream;
import opennlp.maxent.Event;
import opennlp.maxent.EventCollector;

import java.io.StringReader;

/**
 * An implementation of EventStream whcih assumes the data stream gives a
 * sentence at a time with tokens as word_tag pairs.
 */

public class POSEventStream implements EventStream {

  ContextGenerator cg;
  DataStream data;
  Event[] events;
  int ei;
  

  public POSEventStream(DataStream d) {
    this(d,new POSContextGenerator());
    int ei=0;
    if (d.hasNext()) {
      addNewEvents((String) d.nextToken());
    }
    else {
      events = new Event[0];
    }
  }

  public POSEventStream(DataStream d, ContextGenerator cg) {
    this.cg=cg;
    data = d;
  }

  public boolean hasNext() {
    return(ei < events.length || data.hasNext());
  }

  public Event nextEvent () {
    if (ei == events.length) {
      addNewEvents((String) data.nextToken());
      ei=0;
    }
    return((Event) events[ei++]);
  }
  
  private void addNewEvents(String sentence) {
    //String sentence = "the_DT stories_NNS about_IN well-heeled_JJ communities_NNS and_CC developers_NNS";
    EventCollector ec = new POSEventCollector(new StringReader(sentence),cg);
    events = ec.getEvents();
  }

}
