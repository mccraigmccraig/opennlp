package opennlp.dictionary.wordnet;

/**
 *  Structure for sense index file entry.
 *
 *  This class was created by heavily modifying part of the WordNet 1.7 code
 *  src/include/wntypes.h
 *
 * @author     Mike Atkinson (mratkinson)
 * @since      0.1.0
 * @created    20 March 2002
 * @version    $Id: SnsIndex.java,v 1.3 2002/03/26 19:13:02 mratkinson Exp $
 */
public class SnsIndex {
    /**
     *  sense key.
     *
     * @since    0.1.0
     */
    public String senseKey;
    /**
     *  word string.
     *
     * @since    0.1.0
     */
    public String word;
    /**
     *  synset offset.
     *
     * @since    0.1.0
     */
    public int loc;
    /**
     *  WordNet sense number.
     *
     * @since    0.1.0
     */
    public int wnSense;
    /**
     *  number of semantic tags to sense.
     *
     * @since    0.1.0
     */
    public int tagCount;
    /**
     *  ptr to next sense index entry.
     *
     * @since    0.1.0
     */
    public SnsIndex nextSenseIndex;
}

