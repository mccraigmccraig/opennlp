package opennlp.common.preprocess;

import java.util.*;

import opennlp.common.xml.*;

/**
 * The interface for components which may be stuck together with other
 * Pipelinks to create a generic Pipeline.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */

public interface Pipelink {

    /**
     * Process an NLPDocument.  The document is mutated by implementations of
     * this method.
     *
     * @param doc The document to be processed.
     */
    public void process(NLPDocument doc);

    /**
     * Returns the previous interfaces rquired before this can run.  For
     * example, a Tokenizer might require that a SentenceDetector run first.
     * Therefore the Tokenizer PipeLink would specify the SentenceDetector
     * interface.
     *
     * @return the interfaces of the Pipelinks reuiquired before this one
     */
    public Set requires();
  
}
