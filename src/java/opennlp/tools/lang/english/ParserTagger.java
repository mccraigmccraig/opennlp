package opennlp.tools.lang.english;

import java.io.File;
import java.io.IOException;
import java.util.List;

import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.postag.DefaultPOSContextGenerator;
import opennlp.tools.postag.POSDictionary;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.Sequence;

public class ParserTagger extends POSTaggerME implements opennlp.tools.parser.ParserTagger {

  private static final int K = 10;
  int beamSize;
  
  public ParserTagger(String modelFile) throws IOException {
    this(modelFile,K,K);
  }

  public ParserTagger(String modelFile,int beamSize, int cacheSize) throws IOException {
    super(beamSize, new SuffixSensitiveGISModelReader(new File(modelFile)).getModel(), new DefaultPOSContextGenerator(cacheSize), null);
    this.beamSize = beamSize;
  }

  public ParserTagger(String modelFile, String tagDictionary, boolean useCase) throws IOException {
    this(modelFile,K,tagDictionary,useCase,K);
  }
  
  public ParserTagger(String modelFile, int beamSize, String tagDictionary, boolean useCase, int cacheSize) throws IOException {
    super(beamSize, new SuffixSensitiveGISModelReader(new File(modelFile)).getModel(), new DefaultPOSContextGenerator(cacheSize), new POSDictionary(tagDictionary, useCase));
    this.beamSize = beamSize;
  }

  public Sequence[] topKSequences(List sentence) {
    return beam.bestSequences(beamSize, sentence.toArray(), null);
  }

  public Sequence[] topKSequences(String[] sentence) {
    return beam.bestSequences(beamSize, sentence, null);
  }
}
