package opennlp.dictionary.wordnet;

/**
 *  This is a simple class to create a cache for recently found data from WordNet
 *  files.
 *
 * @author     Mike Atkinson (mratkinson)
 * @since      0.2.0
 * @created    21 March 2002
 * @verson     $Id: WordNetCache.java,v 1.1 2002/03/21 23:27:22 mratkinson Exp $
 */
public class WordNetCache extends java.util.WeakHashMap {
    private Object[] holdOnTo;
    private int ptr = 0;


    /**
     *  Constructor for the WordNetCache object
     *
     * @param  size  The number of objects to keep a tight hold on.
     * @since        0.2.0
     */
    public WordNetCache(int size) {
        super();
        if (size < 16) {
            size = 16;
        } else if (size > 1000000) {
            size = 1000000;
        }
        holdOnTo = new Object[size];
    }


    /**
     *  Get data from the cache.
     *
     * @param  key  For the data.
     * @return      The data (if found) or null.
     * @since       0.2.0
     */
    public Object get(Object key) {
        return super.get(key);
    }


    /**
     *  Put some data in the cache.
     *
     * @param  key   For the data.
     * @param  data  What to put against the key.
     * @return       Previous contents at that key (or null if no previous contents).
     * @since        0.2.0
     */
    public Object put(Object key, Object data) {
        holdOnTo[ptr] = key;
        ptr++;
        if (ptr >= holdOnTo.length) {
            ptr = 0;
        }
        return super.put(key, data);
    }

}

