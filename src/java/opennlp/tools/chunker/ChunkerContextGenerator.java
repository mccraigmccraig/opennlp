package opennlp.tools.chunker;

import java.util.List;

import opennlp.tools.util.BeamSearchContextGenerator;

public interface ChunkerContextGenerator extends BeamSearchContextGenerator {

  /**
   * Returns the contexts for chunking of the specified index.
   * @param i The index of the token in the specified toks array for which the context should be constructed. 
   * @param toks The tokens of the sentence.  The <code>toString</code> methods of these objects should return the token text.
   * @param preds The previous decisions made in the taging of this sequence.  Only indices less than i will be examined.
   * @param tags The POS tags for the the specified tokens.
   * @return An array of predictive contexts on which a model basis its decisions.
   */
  public abstract String[] getContext(int i, List toks, List preds, List tags);
}