package opennlp.dictionary.wordnet;

/**
 *  Structure for index file entry.
 *
 *  This class was created by heavily modifying part of the WordNet 1.7 code
 *  src/include/wntypes.h
 *
 * @author     Mike Atkinson (mratkinson)
 * @since      0.1.0
 * @created    20 March 2002
 * @version    $Id: Index.java,v 1.3 2002/03/26 19:10:33 mratkinson Exp $
 */

public class Index {
    /**
     *  byte offset of entry in index file
     *
     * @since    0.1.0
     */
    public long indexOffset;
    /**
     *  word string
     *
     * @since    0.1.0
     */
    public String word;
    /**
     *  part of speech
     *
     * @since    0.1.0
     */
    public String pos;
    /**
     *  sense (collins) count
     *
     * @since    0.1.0
     */
    public int senseCount;
    /**
     *  number senses that are tagged
     *
     * @since    0.1.0
     */
    public int taggedCount;
    /**
     *  offsets of synsets containing word
     *
     * @since    0.1.0
     */
    public int[] offset;
    /**
     *  pointers used
     *
     * @since    0.1.0
     */
    public int[] ptruse;


    /**
     *  Create a string representation of the index data for debugging purposes.
     *
     * @return    A representation of the index data.
     * @since     0.1.0
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(indexOffset).append(",").append(word).append(",").append(pos).append(",");
        sb.append(senseCount).append(",").append(taggedCount).append(",[");
        for (int i = 0; i < offset.length - 1; i++) {
            sb.append(offset[i]).append(",");
        }
        if (offset.length > 0) {
            sb.append(offset[offset.length - 1]).append("],[");
        }
        for (int i = 0; i < ptruse.length - 1; i++) {
            sb.append(ptruse[i]).append(",");
        }
        if (ptruse.length > 0) {
            sb.append(ptruse[ptruse.length - 1]).append("]");
        }
        return sb.toString();
    }

}


