package opennlp.dictionary.wordnet;

/**
 *  This contains the results of a search.
 *
 *  This class was created by heavily modifying part of the WordNet 1.7 code
 *  src/include/wntypes.h
 *
 * @author     Mike Atkinson (mratkinson)
 * @since      0.1.0
 * @created    20 March 2002
 * @version    $Id: SearchResults.java,v 1.3 2002/03/26 19:12:29 mratkinson Exp $
 */
public class SearchResults {
    /**
     *  number of senses word form has.
     *
     * @since    0.1.0
     */
    public int[] senseCount;
    /**
     *  number of senses printed for word form.
     *
     * @since    0.1.0
     */
    public int[] outSenseCount;
    /**
     *  number of word forms searchWord has.
     *
     * @since    0.1.0
     */
    public int numForms;
    /**
     *  number of senses printed by search.
     *
     * @since    0.1.0
     */
    public int printCount;
    /**
     *  buffer containing formatted results.
     *
     * @since    0.1.0
     */
    public String searchBuf;
    /**
     *  data structure containing search results.
     *
     * @since    0.1.0
     */
    public SynSet searchDs;


    /**
     *  Constructor for the SearchResults object.<p>
     *
     *  Equivalent to SearchResults("", 1000);
     *
     * @since    0.1.0
     */
    public SearchResults() {
        this("", 1000);
    }


    /**
     *  Constructor for the SearchResults object
     *
     * @param  search  Results description
     * @param  size    Maximum number of senses
     * @since          0.1.0
     */
    public SearchResults(String search, int size) {
        senseCount = new int[size];
        outSenseCount = new int[size];
        numForms = size - 1;
        printCount = 0;
        searchBuf = search;
        searchDs = null;
    }

}

