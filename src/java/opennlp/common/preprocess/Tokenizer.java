package opennlp.common.preprocess;

import opennlp.common.xml.*;

/**
 * The interface for tokenizers, which turn messy text into nicely segmented
 * text tokens.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */

public interface Tokenizer extends Pipelink {
    
    /**
     * Tokenize a string.
     *
     * @param s The string to be tokenized.
     * @return  The String[] with the individual tokens as the array
     *          elements.
     */
    public String[] tokenize(String s);
    
}
