package opennlp.dictionary.wordnet;

/**
 *  Structure for data file synset.<p>
 *
 *  The following fields are used if a data structure is returned instead of a text
 *  buffer.
 *  <ul>
 *    <li> nextss</li>
 *    <li> nextForm</li>
 *    <li> searchType</li>
 *    <li> ptrList</li>
 *    <li> headWord</li>
 *    <li> headSense</li>
 *  </ul>
 *  <p>
 *
 *  <b>FIXME: This should probably be broken up into two classes to reflex its different
 *  uses.</b><p>
 *
 *  This class was created by heavily modifying part of the WordNet 1.7 code
 *  src/include/wntypes.h
 *
 * @author     Mike Atkinson (mratkinson)
 * @since      0.1.0
 * @created    20 March 2002
 * @version    $Id: SynSet.java,v 1.3 2002/03/26 19:36:45 mratkinson Exp $
 */
public class SynSet {
    /**
     *  // current file position.
     *
     * @since    0.1.0
     */
    public int hereiam;
    /**
     *  type of ADJ synset.
     *
     * @since    0.1.0
     */
    public int sstype;
    /**
     *  file number that synset comes from.
     *
     * @since    0.1.0
     */
    public int fnum;
    /**
     *  part of speech.
     *
     * @since    0.1.0
     */
    public String pos;
    /**
     *  number of words in synset.
     *
     * @since    0.1.0
     */
    public int wcount;
    /**
     *  words in synset.
     *
     * @since    0.1.0
     */
    public String[] words;
    /**
     *  unique id in lexicographer file.
     *
     * @since    0.1.0
     */
    public int[] lexid;
    /**
     *  sense number in wordnet.
     *
     * @since    0.1.0
     */
    public int[] wnsns;
    /**
     *  which word in synset we're looking for.
     *
     * @since    0.1.0
     */
    public int whichWord;
    /**
     *  pointer types.
     *
     * @since    0.1.0
     */
    public Pointer[] pointers;
    /**
     *  frame types.
     *
     * @since    0.1.0
     */
    public Frame[] frames;
    /**
     *  ynset gloss (definition).
     *
     * @since    0.1.0
     */
    public String defn;

    // these fields are used if a data structure is returned
    // instead of a text buffer.

    /**
     *  ptr to next synset containing searchWord.
     *
     * @since    0.1.0
     */
    public SynSet nextss;
    /**
     *  ptr to list of synsets for alternate spelling of wordform.
     *
     * @since    0.1.0
     */
    public SynSet nextForm;

    /**
     *  // type of search performed.
     *
     * @since    0.1.0
     */
    public int searchType;
    /**
     *  ptr to synset list result of search.
     *
     * @since    0.1.0
     */
    public SynSet ptrList;
    /**
     *  if pos is "s", this is cluster head word.
     *
     * @since    0.1.0
     */
    public String headWord;
    /**
     *  sense number of headWord.
     *
     * @since    0.1.0
     */
    public int headSense;


    /**
     *  This contains the pointer information for a SynSet, there might be more
     *  than one pointer for the SynSet.
     *
     * @author     Mike Atkinson (mratkinson)
     * @since      0.1.0
     * @created    20 March 2002
     */
    public static class Pointer {
        /**
         *  pointer types.
         *
         * @since    0.1.0
         */
        public int ptrType;
        /**
         *  pointer offset.
         *
         * @since    0.1.0
         */
        public int ptroff;
        /**
         *  pointer part of speech.
         *
         * @since    0.1.0
         */
        public int ppos;
        /**
         *  pointer 'to' field.
         *
         * @since    0.1.0
         */
        public int pto;
        /**
         *  pointer 'from' fields.
         *
         * @since    0.1.0
         */
        public int pfrm;
    }


    /**
     *  This contains the frame information for a SynSet, there might be more than
     *  one frame for the SynSet.
     *
     * @author     Mike Atkinson (mratkinson)
     * @since      0.1.0
     * @created    20 March 2002
     */
    public static class Frame {
        /**
         *  frame numbers.
         *
         * @since    0.1.0
         */
        public int frmid;
        /**
         *  frame 'to' fields.
         *
         * @since    0.1.0
         */
        public int frmto;
    }
}


