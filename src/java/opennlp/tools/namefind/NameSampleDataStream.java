package opennlp.tools.namefind;

import java.util.ArrayList;
import java.util.List;

import opennlp.maxent.DataStream;
import opennlp.tools.util.Span;

// gets tagged string as input and outputs a name sample
public class NameSampleDataStream implements NameSampleStream {

  public static final String START_TAG = "<START>";

  public static final String END_TAG = "<END>";
  
  private final DataStream in;

  public NameSampleDataStream(DataStream in) {
    this.in = in;
  }

  /* (non-Javadoc)
   * @see opennlp.tools.namefind.NameSampleStream#hasNext()
   */
  public boolean hasNext() {
    return in.hasNext();
  }

  /* (non-Javadoc)
   * @see opennlp.tools.namefind.NameSampleStream#nextNameSample()
   */
  public NameSample next() {
    String token = (String) in.nextToken();
    // clear adaptive data for every empty line
    return createNameSample(token);
  }
  
  private NameSample createNameSample(String taggedTokens) {
    String[] parts = taggedTokens.split(" ");

    List<String> tokenList = new ArrayList<String>(parts.length);
    List<Span> nameList = new ArrayList<Span>();

    int startIndex = -1;
    int wordIndex = 0;
    for (int pi = 0; pi < parts.length; pi++) {
      if (parts[pi].equals(START_TAG)) {
        startIndex = wordIndex;
      } 
      else if (parts[pi].equals(END_TAG)) {
        // create name
        nameList.add(new Span(startIndex, wordIndex));
      } 
      else {
        tokenList.add(parts[pi]);
        wordIndex++;
      }
    }
    String[] sentence = (String[]) tokenList.toArray(new String[tokenList.size()]);
    Span[] names = (Span[]) nameList.toArray(new Span[nameList.size()]);
    return new NameSample(sentence,names,sentence.length==0);
  }
}