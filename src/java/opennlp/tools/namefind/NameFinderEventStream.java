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

  /** 
   * Creates a new name finder event stream using the specified data stream and context generator.
   * @param dataStream The data stream of events.
   * @param contextGenerator The context generator used to generate features for the event stream.
   */
  public NameFinderEventStream(DataStream dataStream, NameContextGenerator contextGenerator) {
    this.dataStream = dataStream;
    this.contextGenerator = contextGenerator;
    this.contextGenerator.addFeatureGenerator(new WindowFeatureGenerator(additionalContextFeatureGenerator, 8, 8,true));
  }

  public NameFinderEventStream(DataStream dataStream) {
    this(dataStream, new NameContextGenerator());
  }
  
  /**
   * Generates the name tag outcomes (start, continue, other) for each token in a sentence
   * with the specified length using the specified name spans.
   * @param names Token spans for each of the names.
   * @param length The length of the sentence.
   * @return An array of start, continue, other outcomes based on the specified names and sentence length.
   */
  public static String[] generateOutcomes(Span[] names, int length) {
    String[] outcomes = new String[length];
    for (int i = 0; i < outcomes.length; i++) {
      outcomes[i] = NameFinderME.OTHER;
    }
    for (int nameIndex = 0; nameIndex < names.length; nameIndex++) {
      Span name = names[nameIndex];
      outcomes[name.getStart()] = NameFinderME.START;
      // now iterate from begin + 1 till end
      for (int i = name.getStart() + 1; i < name.getEnd(); i++) {
        outcomes[i] = NameFinderME.CONTINUE;
      }
    }
    return outcomes;
  }
    
  private void createNewEvents() {
    if (dataStream.hasNext()) {
      NameSample sample = (NameSample) dataStream.nextToken();
      while (sample.isClearAdaptiveDataSet()) {
        contextGenerator.clearAdaptiveData();
        if (dataStream.hasNext()) {
          sample = (NameSample) dataStream.nextToken();
        }
        else {
          return;
        }
      }
      //System.err.println(sample);
      String outcomes[] = generateOutcomes(sample.names(),sample.sentence().length);
      additionalContextFeatureGenerator.setCurrentContext(sample.additionalContext());
      String[] tokens = new String[sample.sentence().length]; 
      List events = new ArrayList(outcomes.length);
      for (int i = 0; i < sample.sentence().length; i++) {
        tokens[i] = sample.sentence()[i].getToken();
      }
      for (int i = 0; i < outcomes.length; i++) {
        events.add(new Event((String) outcomes[i], contextGenerator.getContext(i, sample.sentence(), outcomes,null)));
      }
      this.events = events.iterator();
      contextGenerator.updateAdaptiveData(tokens, outcomes);
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
    
    
  /**
   * Generated previous decision features for each token based on contents of the specifed map.
   * @param tokens The token for which the context is generated.
   * @param prevMap A mapping of tokens to their previous decisions.
   * @return An additional context arrary with features for each token.
   */
  public static String[][] additionalContext(String[] tokens, Map prevMap) {
    String[][] ac = new String[tokens.length][1];
    for (int ti=0;ti<tokens.length;ti++) {
      String pt = (String) prevMap.get(tokens[ti]);
      ac[ti][0]="pd="+pt;
    }
    return ac;

  }

  public static final void main(String[] args) throws java.io.IOException {
    if (args.length != 0) {
      System.err.println("Usage: NameFinderEventStream < training files");
      System.exit(1);
    }
    EventStream es = new NameFinderEventStream(new NameSampleDataStream(new opennlp.maxent.PlainTextByLineDataStream(new java.io.InputStreamReader(System.in))));
    while (es.hasNext()) {
      System.out.println(es.nextEvent());
    }
  }
}