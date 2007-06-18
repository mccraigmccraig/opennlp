package opennlp.tools.namefind;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import opennlp.maxent.DataStream;
import opennlp.maxent.Event;
import opennlp.maxent.EventStream;
import opennlp.tools.namefind.NameContextGenerator;
import opennlp.tools.namefind.NameFinderEventStream;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.Span;

/**
 * Class for creating an event stream out of data files for training an name
 * finder.
 */
public class NameFinderEventStream implements EventStream {

  private DataStream dataStream;

  private Iterator events = Collections.EMPTY_LIST.iterator();

  private NameContextGenerator contextGenerator;

  private Map prevTags = new HashMap();

  private AdditionalContextFeatureGenerator additionalContextFeatureGenerator = new AdditionalContextFeatureGenerator();

  public NameFinderEventStream(DataStream dataStream,
      NameContextGenerator contextGenerator) {
    this.dataStream = dataStream;
    this.contextGenerator = contextGenerator;

    this.contextGenerator.addFeatureGenerator(new WindowFeatureGenerator(
        additionalContextFeatureGenerator, 8, 8));
  }

  public NameFinderEventStream(DataStream dataStream) {
    this(dataStream, new NameContextGenerator());
  }
    
    private void createNewEvents() {
    if (dataStream.hasNext()) {
      NameSample sample = (NameSample) dataStream.nextToken();

      String outcomes[] = new String[sample.sentence().length];

      // set each slot of outcomes array to other
      for (int i = 0; i < outcomes.length; i++) {
        outcomes[i] = NameFinderME.OTHER;
      }

      // set start and cont outcomes
      for (int nameIndex = 0; nameIndex < sample.names().length; nameIndex++) {
        Span name = sample.names()[nameIndex];

        outcomes[name.getStart()] = NameFinderME.START;

        // now iterate from begin + 1 till end
        for (int i = name.getStart() + 1; i < name.getEnd(); i++) {
          outcomes[i] = NameFinderME.CONTINUE;
        }
      }

      additionalContextFeatureGenerator.setCurrentContext(sample
          .additionalContext());

      // TODO: move before or after context generation ???
      for (int i = 0; i < sample.sentence().length; i++) {
        prevTags.put(sample.sentence()[i].getToken(), outcomes[i]);
      }

      List events = new ArrayList(outcomes.length);

      for (int i = 0; i < outcomes.length; i++) {
        events.add(new Event((String) outcomes[i], contextGenerator.getContext(
            i, sample.sentence(), outcomes, prevTags)));
      }

      this.events = events.iterator();
    }
  }
    
    public boolean hasNext() {

    // check if iterator has next event
    if (events.hasNext()) {
      return true;
    } else {
      createNewEvents();

      return events.hasNext();
    }
  }

    public Event nextEvent() {
    // call to hasNext() is necessary for reloading elements
    // if the events iterator was already consumed
    if (!events.hasNext()) {
      throw new NoSuchElementException();
    }

    return (Event) events.next();
  }

    // TODO: fix and test it
  public static final void main(String[] args) throws java.io.IOException {
    if (args.length == 0) {
      System.err.println("Usage: NameFinderEventStream trainfiles");
      System.exit(1);
    }
    for (int ai = 0; ai < args.length; ai++) {
      EventStream es = new NameFinderEventStream(new NameSampleDataStream(
          new opennlp.maxent.PlainTextByLineDataStream(new java.io.FileReader(
              args[ai]))));
      while (es.hasNext()) {
        System.out.println(es.nextEvent());
      }
    }
  }
}