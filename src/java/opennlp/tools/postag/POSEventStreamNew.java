///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2008 OpenNlp
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

package opennlp.tools.postag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import opennlp.maxent.Event;
import opennlp.tools.util.AbstractEventStream;

/**
 * This class reads the {@link POSSample}s from the given {@link Iterator}
 * and converts the {@link POSSample}s into {@link Event}s which
 * can be used by the maxent library for training.
 */
public class POSEventStreamNew extends AbstractEventStream<POSSample> {

  /**
   * The {@link POSContextGenerator} used
   * to create the training {@link Event}s.
   */
  private POSContextGenerator cg;
  
  /**
   * Initializes the current instance with the given samples and the
   * given {@link POSContextGenerator}.
   * 
   * @param samples
   * @param cg
   */
  public POSEventStreamNew(Iterator<POSSample> samples, POSContextGenerator cg) {
    super(samples);
  }
  
  /**
   * Initializes the current instance with given samples
   * and a {@link DefaultPOSContextGenerator}.
   * @param samples
   */
  public POSEventStreamNew(Iterator<POSSample> samples) {
    this(samples, new DefaultPOSContextGenerator(null));
  }
  
  @Override
  protected Iterator<Event> createEvents(POSSample sample) {
    
    String sentence[] = sample.getSentence();
    String tags[] = sample.getTags();
    
    List<Event> events = new ArrayList<Event>(sentence.length);
    
    for (int i=0; i < sentence.length; i++) {
      
      // it is safe to pass the tags as previous tags because
      // the context generator does not look for non predicted tags
      String[] context = cg.getContext(i, sentence, tags, null);
      
      events.add(new Event(tags[i], context));
    }
    
    return events.iterator();
  }
}