package opennlp.dictionary.wordnet;

/**
 *  Structure for index file entry.
 *
 * @author     Mike Atkinson (mratkinson)
 * @since      0.1.0
 * @created    21 March 2002
 * @version    $Id: Index.java,v 1.2 2002/03/21 22:35:27 mratkinson Exp $
 */

public class Index {
    /**
     *  byte offset of entry in index file
     *
     * @since    0.1.0
     */
    public long idxoffset;
    /**
     *  word string
     *
     * @since    0.1.0
     */
    public String wd;
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
    public int sense_cnt;
    /**
     *  number senses that are tagged
     *
     * @since    0.1.0
     */
    public int tagged_cnt;
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
        sb.append(idxoffset).append(",").append(wd).append(",").append(pos).append(",");
        sb.append(sense_cnt).append(",").append(tagged_cnt).append(",[");
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


