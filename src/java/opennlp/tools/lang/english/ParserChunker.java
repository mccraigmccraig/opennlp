package opennlp.tools.lang.english;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.parser.ChunkContextGenerator;
import opennlp.tools.parser.chunking.Parser;
import opennlp.tools.util.Sequence;

/**
 * Class which perform chunking for non-recursive constituents.  This follows the scheme used by
 * the CONLL shared chunking task. http://www.cnts.ua.ac.be/conll2000/chunking/
 * @author Tom Morton
 *
 */
public class ParserChunker extends ChunkerME implements opennlp.tools.parser.ParserChunker {
  private static final int K = 10;
  private int beamSize;
  private Map continueStartMap;
  
  public ParserChunker(String modelFile) throws IOException {
    this(modelFile,K,K);
  }
  
  public ParserChunker(MaxentModel model) throws IOException {
    this(model,K,K);
  }
  
  public ParserChunker(String modelFile, int beamSize, int cacheSize) throws IOException {
    super(new SuffixSensitiveGISModelReader(new File(modelFile)).getModel(), new ChunkContextGenerator(cacheSize), beamSize);
    this.beamSize = beamSize;
    init();
  }
  
  public ParserChunker(MaxentModel model, int beamSize, int cacheSize) throws IOException {
    super(model, new ChunkContextGenerator(cacheSize), beamSize);
    this.beamSize = beamSize;
    init();
  }
  
  protected void init() {
    continueStartMap = new HashMap(model.getNumOutcomes());
    for (int oi=0,on=model.getNumOutcomes();oi<on;oi++) {
      String outcome = model.getOutcome(oi);
      if (outcome.startsWith(Parser.CONT)){
        continueStartMap.put(outcome,Parser.START+outcome.substring(Parser.CONT.length()));
      }
    }
  }

  public Sequence[] topKSequences(List sentence, List tags) {
    return beam.bestSequences(beamSize, sentence.toArray(), new Object[] { tags });
  }

  public Sequence[] topKSequences(String[] sentence, String[] tags, double minSequenceScore) {
    return beam.bestSequences(beamSize, sentence, new Object[] { tags },minSequenceScore);
  }

  protected boolean validOutcome(String outcome, String[] tagList) {
    if (continueStartMap.containsKey(outcome)) {
      int lti = tagList.length - 1;
      if (lti == -1) {
        return (false);
      }
      else {
        String lastTag = tagList[lti];
        if (lastTag.equals(outcome)) {
           return true;
        }
        if (lastTag.equals(continueStartMap.get(outcome))) {
          return true;
        }
        if (lastTag.equals(Parser.OTHER)) {
          return (false);
        }
        return false;
      }
    }
    return (true);
  }
}