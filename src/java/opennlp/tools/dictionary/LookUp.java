package opennlp.tools.dictionary;

import opennlp.tools.ngram.TokenList;

/**
 * Retrives a {@link TokenList} which do not exactly match with an
 * existing {@link TokenList} inside the {@link LookUp}.
 */
public interface LookUp {
  
  /**
   * Retrives the best matching {@link TokenList} for the provdied sample.
   * @param sample 
   * 
   * @return best matching {@link TokenList} or null if none is matching
   */
  TokenList getBest(TokenList sample);  
}
