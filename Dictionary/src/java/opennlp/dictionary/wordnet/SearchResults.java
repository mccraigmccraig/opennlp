package opennlp.dictionary.wordnet;

public class SearchResults {
    public int[] senseCount;	        // number of senses word form has.
    public int[] outSenseCount;         // number of senses printed for word form.
    public int numforms;		// number of word forms searchword has.
    public int printcnt;		// number of senses printed by search.
    public String searchbuf;		// buffer containing formatted results.
    public SynSet searchds;		// data structure containing search results.
    public SearchResults() {
        this("", 1000);
    }
    public SearchResults(String search, int size) {
        senseCount = new int[size];
        outSenseCount = new int[size];
        numforms = size-1;
        printcnt = 0;
        searchbuf = search;
        searchds = new SynSet();
    }
    
}

