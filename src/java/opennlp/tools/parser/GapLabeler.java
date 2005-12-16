package opennlp.tools.parser;

import java.util.Stack;

/**
 * Interface for labeling nodes which contain traces so that these traces can be predicted 
 * by the parser.  
 */
public interface GapLabeler {
  /**
   * Labels the constituents found in the stack with gap labels if appropiate.
   * @param stack The stack of un-completed constituents.
   */
  public void labelGaps(Stack stack);
}
