package opennlp.common.preprocess;

import java.util.*;

import opennlp.common.xml.*;

/**
 * The interface for morphological analyzers, which return morphological
 * information for word forms.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */

public interface MorphAnalyzer extends Pipelink {

    /**
     * Returns the morphological information for a word.
     *
     * @param word  The string representation of the word to be analyzed.
     * @return A String with the morph info, such as root, tense, person,
     *         etc.  Eventually, this should be a class instead of a String.
     */
    public String analyze(String word);
    
}
