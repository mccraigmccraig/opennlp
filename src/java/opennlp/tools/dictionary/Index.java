package opennlp.tools.dictionary;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import opennlp.tools.ngram.Token;
import opennlp.tools.ngram.TokenList;

/**
 * This classes indexes {@link TokenList}s. This makes it possible
 * to check if a certain {@link Token} is contained in at least one of the 
 * {@link TokenList}s.
 */
public class Index {
  
  private Set<Token> tokens = new HashSet<Token>();
  
  /**
   * Initializes the current instance with the given
   * {@link TokenList} {@link Iterator}.
   * 
   * @param tokenLists
   */
  public Index(Iterator<TokenList> tokenLists) {
    
    while (tokenLists.hasNext()) {
    
      TokenList tokens = (TokenList) tokenLists.next();
      
      for (int i = 0; i < tokens.size(); i++) {
        this.tokens.add(tokens.getToken(i));
      }
    }
  }
  
  /**
   * Checks if at leat one {@link TokenList} contains the 
   * given {@link Token}.
   * 
   * @param token
   * 
   * @return true if the {@link Token} is contained otherwise false.
   */
  public boolean contains(Token token) {
    return tokens.contains(token);
  }
}