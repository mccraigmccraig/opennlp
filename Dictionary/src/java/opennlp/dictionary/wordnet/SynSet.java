package opennlp.dictionary.wordnet;

/** Structure for data file synset.
 */
public class SynSet {
    int hereiam;		// current file position.
    int sstype;			// type of ADJ synset.
    int fnum;			// file number that synset comes from.
    String pos;			// part of speech.
    int wcount;			// number of words in synset.
    String[] words;		// words in synset.
    int[] lexid;		// unique id in lexicographer file.
    int[] wnsns;		// sense number in wordnet.
    int whichword;		// which word in synset we're looking for.
    Pointer[] pointers;		// pointer types.
    Frame[] frames;		// frame types.
    String defn;		// synset gloss (definition).

    // these fields are used if a data structure is returned
    // instead of a text buffer.

    SynSet nextss;		// ptr to next synset containing searchword.
    SynSet nextform;	        // ptr to list of synsets for alternate
				//   spelling of wordform.
    int searchtype;		// type of search performed.
    SynSet ptrlist;		// ptr to synset list result of search.
    String headword;		// if pos is "s", this is cluster head word.
    int headsense;		// sense number of headword.
    
    public static class Pointer {
        int ptrtyp;		// pointer types.
        int ptroff;		// pointer offsets.
        int ppos;		// pointer part of speech.
        int pto;		// pointer 'to' fields.
        int pfrm;		// pointer 'from' fields.
    }
    public static class Frame {
        int frmid;		// frame numbers.
        int frmto;		// frame 'to' fields.
    }
}


