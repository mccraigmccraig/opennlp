package opennlp.common.preprocess;

import opennlp.common.xml.*;

/**
 * The interface for sentence detectors, which find the sentence boundaries in
 * a text.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */

public interface SentenceDetector extends Pipelink {

    /**
     * Sentence detect a string.
     *
     * @param s The string to be sentence detected.
     * @return  The String[] with the individual sentences as the array
     *          elements.
     */
    public String[] sentDetect(String s);

    /**
     * Sentence detect a string.
     *
     * @param s The string to be sentence detected.
     * @return  An int[] with the starting offset positions of each
     *          detected sentences. 
     */
    public int[] sentPosDetect(String s);
    
}
