package opennlp.common.preprocess;

import java.util.*;

import opennlp.common.xml.*;

/**
 * The interface for part of speech taggers.
 *
 * @author      Jason Baldridge
 * @version $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $ */

public interface POSTagger extends Pipelink {
    public List tag(List sentence);
    public String[] tag(String[] sentence);
    public String tag(String sentence);

}
