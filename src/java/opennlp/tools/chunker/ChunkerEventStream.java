package opennlp.tools.chunker;

import opennlp.maxent.ContextGenerator;
import opennlp.maxent.DataStream;
import opennlp.maxent.Event;
import opennlp.maxent.EventStream;

import java.util.*;

/**
 * @author Tom Morton
 *
  */

public class ChunkerEventStream implements EventStream {

  ContextGenerator cg;
  DataStream data;
  Event[] events;
  int ei;

  public ChunkerEventStream(DataStream d) {
    this(d, new DefaultChunkerContextGenerator());
  }

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

  public Event nextEvent() {
    if (ei == events.length) {
      addNewEvents();
      ei = 0;
    }
    return ((Event) events[ei++]);
  }

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
