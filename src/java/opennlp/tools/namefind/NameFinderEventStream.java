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
package opennlp.tools.namefind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.maxent.DataStream;
import opennlp.maxent.Event;
import opennlp.maxent.EventStream;

public class NameFinderEventStream implements EventStream {

  private DataStream data;
  private Event[] events;
  private NameContextGenerator cg;
  private Map prevTags;
  private int ei;
  private List prevLineTokens;
  private List prevLineOutcomes;

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
    if (prevLineTokens != null) {
      for (int ti=0,tl=prevLineTokens.size();ti<tl;ti++) {
        //System.out.println("addEvents: "+prevLineTokens.get(ti).toString()+" -> "+prevLineOutcomes.get(ti));
        prevTags.put(prevLineTokens.get(ti).toString(),prevLineOutcomes.get(ti));
      }
    }
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
    prevLineTokens = toks;
    prevLineOutcomes = outcomes;
  }

  public Event nextEvent() {
    if (ei == events.length) {
      String line = (String) data.nextToken();
      if (line.equals("")) {
        prevTags.clear();
        prevLineTokens = null;
        prevLineOutcomes = null;
        if (data.hasNext()) {
          line = (String) data.nextToken();
        }
        else {
          return null;
        }
      }
      addEvents(line);
      ei = 0;
    }
    return ((Event) events[ei++]);
  }

  public boolean hasNext() {
    return (ei < events.length || data.hasNext());
  }
  
  public static final void main(String[] args) throws java.io.IOException {
    if (args.length == 0) {
      System.err.println("Usage: NameFinderEventStream trainfiles");
      System.exit(1);
    }
    for (int ai=0,al=args.length;ai<al;ai++) {
      EventStream es = new NameFinderEventStream(new opennlp.maxent.PlainTextByLineDataStream(new java.io.FileReader(args[ai])));
      while(es.hasNext()) {
        System.out.println(es.nextEvent());
      }
    }
  }
}
