package opennlp.tools.namefind;

import opennlp.maxent.DataStream;

// gets tagged string as input and outputs a name sample
public class NameSampleDataStream implements DataStream {

  private final DataStream in;

  public NameSampleDataStream(DataStream in) {
    this.in = in;
  }

  public boolean hasNext() {
    return in.hasNext();
  }

  public Object nextToken() {
    return new NameSample((String) in.nextToken());
  }
}