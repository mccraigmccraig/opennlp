package opennlp.dictionary.wordnet;

/**
 *  Structure for data file synset.<p>
 *
 *  The following fields are used if a data structure is returned instead of a text
 *  buffer.
 *  <ul>
 *    <li> nextss</li>
 *    <li> nextform</li>
 *    <li> searchtype</li>
 *    <li> ptrlist</li>
 *    <li> headword</li>
 *    <li> headsense</li>
 *  </ul>
 *  <p>
 *
 *  FIXME: This should probably be broken up into two classes to reflex its different
 *  uses.
 *
 * @author     Mike Atkinson (mratkinson)
 * @since      0.1.0
 * @created    20 March 2002
 * @version    $Id: SynSet.java,v 1.2 2002/03/21 23:09:52 mratkinson Exp $
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
    public int whichword;
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
     *  ptr to next synset containing searchword.
     *
     * @since    0.1.0
     */
    public SynSet nextss;
    /**
     *  ptr to list of synsets for alternate spelling of wordform.
     *
     * @since    0.1.0
     */
    public SynSet nextform;

    /**
     *  // type of search performed.
     *
     * @since    0.1.0
     */
    public int searchtype;
    /**
     *  ptr to synset list result of search.
     *
     * @since    0.1.0
     */
    public SynSet ptrlist;
    /**
     *  if pos is "s", this is cluster head word.
     *
     * @since    0.1.0
     */
    public String headword;
    /**
     *  sense number of headword.
     *
     * @since    0.1.0
     */
    public int headsense;


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
        public int ptrtyp;
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


