package opennlp.dictionary.wordnet;

/**
 *  Structure for sense index file entry.
 *
 * @author     Mike Atkinson (mratkinson)
 * @since      0.1.0
 * @created    20 March 2002
 * @version    $Id: SnsIndex.java,v 1.2 2002/03/21 23:08:56 mratkinson Exp $
 */
public class SnsIndex {
    /**
     *  sense key.
     *
     * @since    0.1.0
     */
    public String sensekey;
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
    public int wnsense;
    /**
     *  number of semantic tags to sense.
     *
     * @since    0.1.0
     */
    public int tag_cnt;
    /**
     *  ptr to next sense index entry.
     *
     * @since    0.1.0
     */
    public SnsIndex nextsi;
}

