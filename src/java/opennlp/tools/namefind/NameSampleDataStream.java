package opennlp.tools.namefind;

import opennlp.maxent.DataStream;

// gets tagged string as input and outputs a name sample
public class NameSampleDataStream implements DataStream {

    private final DataStream in;
    private final String type;

    public NameSampleDataStream(DataStream in, String type) {
	this.in = in;
	this.type = type;
    }
    
    public boolean hasNext() {
	return in.hasNext();
    }

    public Object nextToken() {
	return new NameSample((String) in.nextToken(), type);
    }
}
