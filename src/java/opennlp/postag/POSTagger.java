package opennlp.postag;

import java.util.List;

/**
 * The interface for part of speech taggers.
 *
 * @author      Jason Baldridge
 * @version $Revision: 1.1 $, $Date: 2003/03/07 04:00:43 $ */

public interface POSTagger {
    public List tag(List sentence);
    public String[] tag(String[] sentence);
    public String tag(String sentence);

}
