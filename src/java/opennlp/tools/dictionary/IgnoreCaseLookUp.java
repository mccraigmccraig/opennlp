package opennlp.tools.dictionary;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import opennlp.tools.ngram.TokenList;

/**
 * Retrives a {@link TokenList} which matches to an existing {@link TokenList}
 * by ignoring the case.
 */
public class IgnoreCaseLookUp implements LookUp {

  private Set mTokenLists = new HashSet();
  
  /**
   * Initializes the current instance.
   * 
   * @param tokenLists
   */
  public IgnoreCaseLookUp(Iterator tokenLists) {
    
    while(tokenLists.hasNext()) {
      mTokenLists.add((TokenList) tokenLists.next());
    }
  }
  
  /**
   * Retrives a found {@link TokenList} or null.
   * 
   * The exact match has priority over non-exact matches.
   * 
   * If there are more than one possible non-exact hit it is not 
   * specified which one will be retrived.
   */
  public TokenList getBest(TokenList sample) {
   
    TokenList bestMatch = null;
    
    // if hit return exact match
    if (mTokenLists.contains(sample)) {
      bestMatch = sample;
    }
    else {
      // search for the first ignore case hit
      String sampleString = sample.toString().toLowerCase();
      
      for (Iterator it = mTokenLists.iterator(); it.hasNext();) {
        
        TokenList candiate = (TokenList) it.next(); 
        String cadidateString = candiate.toString().toLowerCase();
        
        if (sampleString.equals(cadidateString)) {
          bestMatch = candiate; 
          break;
        }
      }
    }
    
    return bestMatch;
  }
}