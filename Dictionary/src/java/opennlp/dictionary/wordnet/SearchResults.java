package opennlp.dictionary.wordnet;

/**
 *  This contains the results of a search.
 *
 * @author     Mike Atkinson (mratkinson)
 * @since      0.1.0
 * @created    21 March 2002
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
     *  number of word forms searchword has.
     *
     * @since    0.1.0
     */
    public int numforms;
    /**
     *  number of senses printed by search.
     *
     * @since    0.1.0
     */
    public int printcnt;
    /**
     *  buffer containing formatted results.
     *
     * @since    0.1.0
     */
    public String searchbuf;
    /**
     *  data structure containing search results.
     *
     * @since    0.1.0
     */
    public SynSet searchds;


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
        numforms = size - 1;
        printcnt = 0;
        searchbuf = search;
        searchds = new SynSet();
    }

}

