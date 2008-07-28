///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2007 OpenNlp
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

import java.util.Iterator;

import opennlp.maxent.Event;
import opennlp.maxent.EventStream;

/**
 * This is a base class for {@link EventStream} classes.
 * It takes an {@link Iterator} of sample objects as input and
 * outputs the events creates by a subclass. 
 */
public abstract class AbstractEventStream<T> implements EventStream {

  private Iterator<T> samples;
  
  private Iterator<Event> events;
  
  /**
   * Initializes the current instance with a sample {@link Iterator}.
   * 
   * @param samples the sample {@link Iterator}.
   */
  public AbstractEventStream(Iterator<T> samples) {
    this.samples = samples;
  }
  
  /**
   * Creates events for the provided sample.
   * 
   * @param sample the sample for which training {@link Event}s 
   * are be created.
   * 
   * @return an {@link Iterator} of training events or 
   * an empty {@link Iterator}.
   */
  protected abstract Iterator<Event> createEvents(T sample);
  
  /**
   * Checks if there are more training events available.
   * 
   */
  public final boolean hasNext() {
    
    if (events.hasNext()) {
      return true;
    } else {
    
      // search next event iterator which is not empty
      while (samples.hasNext() && !events.hasNext()) {
        events = createEvents(samples.next()); 
      }
      
      return events.hasNext();
    }
  }
  
  public final Event nextEvent() {
    return events.next();
  }
}