package opennlp.tools.dictionary;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import opennlp.tools.util.StringList;

/**
 * This classes indexes {@link StringList}s. This makes it possible
 * to check if a certain {@link Token} is contained in at least one of the 
 * {@link StringList}s.
 */
public class Index {
  
  private Set<String> tokens = new HashSet<String>();
  
  /**
   * Initializes the current instance with the given
   * {@link StringList} {@link Iterator}.
   * 
   * @param tokenLists
   */
  public Index(Iterator<StringList> tokenLists) {
    
    while (tokenLists.hasNext()) {
    
      StringList tokens = (StringList) tokenLists.next();
      
      for (int i = 0; i < tokens.size(); i++) {
        this.tokens.add(tokens.getToken(i));
      }
    }
  }
  
  /**
   * Checks if at leat one {@link StringList} contains the 
   * given {@link Token}.
   * 
   * @param token
   * 
   * @return true if the {@link Token} is contained otherwise false.
   */
  public boolean contains(String token) {
    return tokens.contains(token);
  }
}