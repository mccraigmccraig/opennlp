package opennlp.tools.namefind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.maxent.DataStream;
import opennlp.maxent.Event;
import opennlp.maxent.EventStream;

public class NameFinderEventStream implements EventStream {

  DataStream data;
  Event[] events;
  NameContextGenerator cg;
  Map prevTags;
  int ei;

  public NameFinderEventStream(DataStream d) {
    this(d, new DefaultNameContextGenerator());
  }

  public NameFinderEventStream(DataStream d, NameContextGenerator cg) {
    this.data = d;
    this.cg = cg;
    ei = 0;
    prevTags = new HashMap();
    if (data.hasNext()) {
      String line = (String) d.nextToken();
      if (line.equals("")) {
        prevTags.clear();
      }
      else {
        addEvents(line);
      }
    }
    else {
      events = new Event[0];
    }
  }

  private void addEvents(String sentence) {
    String[] parts = sentence.split(" ");
    String outcome = NameFinderME.OTHER;
    List toks = new ArrayList();
    List outcomes = new ArrayList();
    for (int pi = 0, pl = parts.length; pi < pl; pi++) {
      if (parts[pi].equals("<START>")) {
        outcome = NameFinderME.START;
      }
      else if (parts[pi].equals("<END>")) {
        outcome = NameFinderME.OTHER;
      }
      else { //regular token
        toks.add(parts[pi]);
        outcomes.add(outcome);
        if (outcome.equals(NameFinderME.START)) {
          outcome = NameFinderME.CONTINUE;
        }
      }
    }
    events = new Event[toks.size()];
    for (int ti = 0, tl = toks.size(); ti < tl; ti++) {
      events[ti] = new Event((String) outcomes.get(ti), cg.getContext(ti, toks, outcomes, prevTags));
    }
  }

  public Event nextEvent() {
    if (ei == events.length) {
      addEvents((String) data.nextToken());
      ei = 0;
    }
    return ((Event) events[ei++]);
  }

  public boolean hasNext() {
    return (ei < events.length || data.hasNext());
  }
}
