package opennlp.dictionary.wordnet;

public class SnsIndex {
    String sensekey;		// sense key.
    String word;	        // word string.
    int loc;			// synset offset.
    int wnsense;		// WordNet sense number.
    int tag_cnt;		// number of semantic tags to sense.
    SnsIndex nextsi;		// ptr to next sense index entry.
}

