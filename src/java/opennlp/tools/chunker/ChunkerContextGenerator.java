
package opennlp.tools.chunker;

import java.util.List;

import opennlp.tools.util.BeamSearchContextGenerator;


public interface ChunkerContextGenerator extends BeamSearchContextGenerator {
  public abstract String[] getContext(int i, List toks, List preds, List tags);
}