package opennlp.tools.namefind;

import opennlp.maxent.DataStream;

// gets tagged string as input and outputs a name sample
public class NameSampleDataStream implements NameSampleStream {

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
    return new NameSample(token, token.length() == 0);
  }
}