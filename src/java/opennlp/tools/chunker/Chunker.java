package opennlp.tools.chunker;

import java.util.List;

/**
 * The interface for chunkers which provide chunk tags for a sequence of tokens.
 * @author      Thomas Morton
 */
public interface Chunker {
  
  /** Generates chunk tags for the given sequence returning the resulat in a list.
   * @param toks a list of the tokens or words of the sequence.
   * @param tags a list of the pos tags of the sequence.
   * @return a list of chunk tags for each token in the sequence.
   */
  public List chunk(List toks, List tags);
  
  /** Generates chunk tags for the given sequence returning the result in an array.
   * @param toks an array of the tokens or words of the sequence.
   * @param tags an array of the pos tags of the sequence.
   * @return an array of chunk tags for each token in the sequence.
   */
  public String[] chunk(Object[] toks, String tags[]);
}
