package opennlp.dictionary.wordnet;

/**
 *  This class was created by heavily modifying the WordNet 1.7 code src/lib/wnhelp.c 
 *
 * @author     Mike Atkinson (mratkinson)
 * @since      0.1.0
 * @created    20 March 2002
 * @modified   $Id: WNHelp.java,v 1.2 2002/03/22 00:14:27 mratkinson Exp $
 */

public class WNHelp {

    /**
     *  Help for FREQ
     *
     * @since    0.1.0
     */
    public static String freq_help =
            "Display familiarity and polysemy information for the search string. " +
            WNGlobal.lineSeparator +
            "This number represents the number of senses in WordNet. " +
            WNGlobal.lineSeparator
            ;

    /**
     *  Help for WNGREP
     *
     * @since    0.1.0
     */
    public static String grep_help =
    "Print all strings in the database containing the search string " +
            WNGlobal.lineSeparator +
            "as an individual word, or as the first or last string in a word or " +
            WNGlobal.lineSeparator +
            "collocation. " +
            WNGlobal.lineSeparator
            ;

    /**
     *  Help for COORDS
     *
     * @since    0.1.0
     */
    public static String coord_help =
            "Display the coordinates (sisters) of the search string.  This search " +
            WNGlobal.lineSeparator +
            "prints the immediate hypernym for each synset that contains the " +
            WNGlobal.lineSeparator +
            "search string and the hypernym's immediate `hyponyms'. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Hypernym is the generic term used to designate a whole class of " +
            WNGlobal.lineSeparator +
            "specific instances.  Y is a hypernym of X if X is a (kind of) Y. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Hyponym is the generic term used to designate a member of a class. " +
            WNGlobal.lineSeparator +
            "X is a hyponym of Y if X is a (kind of) Y.  " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Coordinate words are words that have the same hypernym." +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Hypernym synsets are preceded by \"->\", and hyponym synsets are " +
            WNGlobal.lineSeparator +
            "preceded by \"=>\". " +
            WNGlobal.lineSeparator
            ;

    /**
     *  Help for HYPERPTR
     *
     * @since    0.1.0
     */
    public static String hyper_help =
            "Display synonyms and immediate hypernyms of synsets containing " +
            WNGlobal.lineSeparator +
            "the search string.  Synsets are ordered by frequency of occurrence.  " +
            WNGlobal.lineSeparator +
            "" +
            WNGlobal.lineSeparator +
            "Hypernym is the generic term used to designate a whole class of " +
            WNGlobal.lineSeparator +
            "specific instances.  Y is a hypernym of X if X is a (kind of) Y. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Hypernym synsets are preceded by \"=>\". " +
            WNGlobal.lineSeparator
            ;

    /**
     *  Help for RELATIVES
     *
     * @since    0.1.0
     */
    public static String relatives_help =
            "Display synonyms and immediate hypernyms of synsets containing " +
            WNGlobal.lineSeparator +
            "the search string.  Synsets are grouped by similarity of meaning. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Hypernym is the generic term used to designate a whole class of " +
            WNGlobal.lineSeparator +
            "specific instances.  Y is a hypernym of X if X is a (kind of) Y. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Hypernym synsets are preceded by \"=>\". " +
            WNGlobal.lineSeparator
            ;

    /**
     *  Help for ANTPTR
     *
     * @since    0.1.0
     */
    public static String ant_help =
            "Display synsets containing direct anotnyms of the search string.  " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Direct antonyms are a pair of words between which there is an " +
            WNGlobal.lineSeparator +
            "associative bond built up by co-occurrences. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Antonym synsets are preceded by \"=>\". " +
            WNGlobal.lineSeparator
            ;

    /**
     *  Help for -HYPERPTR
     *
     * @since    0.1.0
     */
    public static String hypertree_help =
            "Recursively display hypernym (superordinate) tree for the search " +
            WNGlobal.lineSeparator +
            "string. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Hypernym is the generic term used to designate a whole class of " +
            WNGlobal.lineSeparator +
            "specific instances.  Y is a hypernym of X if X is a (kind of) Y. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "" +
            "Hypernym synsets are preceded by \"=>\", and are indented from " +
            WNGlobal.lineSeparator +
            "the left according to their level in the hierarchy. " +
            WNGlobal.lineSeparator
            ;

    /**
     *  Help for HYPONYM
     *
     * @since    0.1.0
     */
    public static String hypo_help =
            "Display immediate hyponyms (subordinates) for the search string. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Hyponym is the generic term used to designate a member of a class. " +
            WNGlobal.lineSeparator +
            "X is a hyponym of Y if X is a (kind of) Y.  " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Hyponym synsets are preceded by \"=>\". " +
            WNGlobal.lineSeparator
            ;

    /**
     *  Help for -HYPONYM
     *
     * @since    0.1.0
     */
    public static String hypotree_help =
            "Display hyponym (subordinate) tree for the search string.  This is " +
            WNGlobal.lineSeparator +
            "a recursive search that finds the hyponyms of each hyponym.  " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Hyponym is the generic term used to designate a member of a class. " +
            WNGlobal.lineSeparator +
            "X is a hyponym of Y if X is a (kind of) Y.  " +
            WNGlobal.lineSeparator +
            "Hyponym synsets are preceded by \"=>\", and are indented from the left " +
            WNGlobal.lineSeparator +
            "according to their level in the hierarchy. " +
            WNGlobal.lineSeparator
            ;

    /**
     *  Help for HOLONYM
     *
     * @since    0.1.0
     */
    public static String holo_help =
            "Display all holonyms of the search string. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "A holonym is the name of the whole of which the 'meronym' names a part. " +
            WNGlobal.lineSeparator +
            "Y is a holonym of X if X is a part of Y. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "A meronym is the name of a constituent part, the substance of, or a " +
            WNGlobal.lineSeparator +
            "member of something.  X is a meronym of Y if X is a part of Y. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Holonym synsets are preceded with either the string \"MEMBER OF\", " +
            WNGlobal.lineSeparator +
            "\"PART OF\" or \"SUBSTANCE OF\" depending on the specific type of holonym. " +
            WNGlobal.lineSeparator
            ;

    /**
     *  Help for -HOLONYM
     *
     * @since    0.1.0
     */
    public static String holotree_help =
            "Display holonyms for search string tree.  This is a recursive search " +
            WNGlobal.lineSeparator +
            "that prints all the holonyms of the search string and all of the " +
            WNGlobal.lineSeparator +
            "holonym's holonyms. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "A holonym is the name of the whole of which the meronym names a part. " +
            WNGlobal.lineSeparator +
            "Y is a holonym of X if X is a part of Y. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "A meronym is the name of a constituent part, the substance of, or a " +
            WNGlobal.lineSeparator +
            "member of something.  X is a meronym of Y if X is a part of Y. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Holonym synsets are preceded with either the string \"MEMBER OF\", " +
            WNGlobal.lineSeparator +
            "\"PART OF\" or \"SUBSTANCE OF\" depending on the specific " +
            WNGlobal.lineSeparator +
            "type of holonym.  Synsets are indented from the left according to " +
            WNGlobal.lineSeparator +
            "their level in the hierarchy. " +
            WNGlobal.lineSeparator
            ;

    /**
     *  Help for MERONYM
     *
     * @since    0.1.0
     */
    public static String mero_help =
            "Display all meronyms of the search string.  " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "A meronym is the name of a constituent part, the substance of, or a " +
            WNGlobal.lineSeparator +
            "member of something.  X is a meronym of Y if X is a part of Y. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "A holonym is the name of the whole of which the meronym names a part. " +
            WNGlobal.lineSeparator +
            "Y is a holonym of X if X is a part of Y. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Meronym synsets are preceded with either the string \"HAS MEMBER\", " +
            WNGlobal.lineSeparator +
            "\"HAS PART\" or \"HAS SUBSTANCE\" depending on the specific type of holonym. " +
            WNGlobal.lineSeparator
            ;

    /**
     *  Help for -HMERONYM
     *
     * @since    0.1.0
     */
    public static String merotree_help =
            "Display meronyms for search string tree.  This is a recursive search " +
            WNGlobal.lineSeparator +
            "the prints all the meronyms of the search string and all of its " +
            WNGlobal.lineSeparator +
            "hypernyms.  " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "A meronym is the name of a constituent part, the substance of, or a " +
            WNGlobal.lineSeparator +
            "member of something.  X is a meronym of Y if X is a part of Y. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "A holonym is the name of the whole of which the meronym names a part. " +
            WNGlobal.lineSeparator +
            "Y is a holonym of X if X is a part of Y. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Hypernym is the generic term used to designate a whole class of " +
            WNGlobal.lineSeparator +
            "specific instances.  Y is a hypernym of X if X is a (kind of) Y. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Meronym synsets are preceded with either the string \"HAS MEMBER\", " +
            WNGlobal.lineSeparator +
            "\"HAS PART\" or \"HAS SUBSTANCE\" depending on the specific type of " +
            WNGlobal.lineSeparator +
            "holonym.  Synsets are indented from the left according to their level " +
            WNGlobal.lineSeparator +
            "in the hierarchy. " +
            WNGlobal.lineSeparator
            ;

    /**
     *  Help for NOMINALIZATIONS
     *
     * @since    0.1.0
     */
    public static String nomin_help =
            "Display nominalizations - nouns and verbs that are related morphologically. " +
            WNGlobal.lineSeparator
            ;

    /**
     *  Help for ATTRIBUTE
     *
     * @since    0.1.0
     */
    public static String nattrib_help =
            "Display adjectives for which search string is an attribute. " +
            WNGlobal.lineSeparator
            ;

    /**
     *  Help for ATTRIBUTE
     *
     * @since    0.1.0
     */
    public static String aattrib_help =
            "Display nouns that are attributes of search string. " +
            WNGlobal.lineSeparator
            ;

    /**
     *  Help for -HYPOPTR
     *
     * @since    0.1.0
     */
    public static String tropo_help =
            "Display hyponym tree for the search string.  This is " +
            WNGlobal.lineSeparator +
            "a recursive search that finds the hyponyms of each hyponym.  " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "For verbs, hyponyms are refered to as troponyms.  Troponyms indicate particular ways " +
            WNGlobal.lineSeparator +
            "to perform a function.  X is a hyponym of Y if to X is a particular way to Y. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Troponym synsets are preceded by \"=>\", and are indented from the left " +
            WNGlobal.lineSeparator +
            "according to their level in the hierarchy. " +
            WNGlobal.lineSeparator
            ;

    /**
     *  Help for ENTAILPTR
     *
     * @since    0.1.0
     */
    public static String entail_help =
    "Recursively display entailment relations of the search string.  " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "The action represented by the verb X entails Y if X cannot be done " +
            WNGlobal.lineSeparator +
            "unless Y is, or has been, done. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Entailment synsets are preceded by \"=>\", and are indented from the left " +
            WNGlobal.lineSeparator +
            "according to their level in the hierarchy. " +
            WNGlobal.lineSeparator
            ;

    /**
     *  Help for CAUSETO
     *
     * @since    0.1.0
     */
    public static String causeto_help =
    "Recursively display CAUSE TO relations of the search string. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "The action represented by the verb X causes the action represented by " +
            WNGlobal.lineSeparator +
            "the verb Y. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "CAUSE TO synsets are preceded by \"=>\", and are indented from the left " +
            WNGlobal.lineSeparator +
            "according to their level in the hierarch.  " +
            WNGlobal.lineSeparator
            ;

    /**
     *  Help for FRAMES
     *
     * @since    0.1.0
     */
    public static String frames_help =
            "Display applicable verb sentence frames for the search string. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "A frame is a sentence template illustrating the usage of a verb. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Verb sentence frames are preceded with the string \"*>\" if a sentence " +
            WNGlobal.lineSeparator +
            "frame is acceptable for all of the words in the synset, and with \"=>\" " +
            WNGlobal.lineSeparator +
            "if a sentence frame is acceptable for the search string only.  " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Some verb senses have example sentences.  These are preceeded with \"EX:\". " +
            WNGlobal.lineSeparator
            ;

    /**
     *  Help for Adjective SIMPTR
     *
     * @since    0.1.0
     */
    public static String adj_simptr_help =
            "Display synonyms and synsets related to synsets containing " +
            WNGlobal.lineSeparator +
            "the search string.  If the search string is in a head synset " +
            WNGlobal.lineSeparator +
            "the 'cluster's' satellite synsets are displayed.  If the search " +
            WNGlobal.lineSeparator +
            "string is in a satellite synset, its head synset is displayed. " +
            WNGlobal.lineSeparator +
            "If the search string is a pertainym the word or synset that it " +
            WNGlobal.lineSeparator +
            "pertains to is displayed. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "A cluster is a group of adjective synsets that are organized around " +
            WNGlobal.lineSeparator +
            "antonymous pairs or triplets.  An adjective cluster contains two or more " +
            WNGlobal.lineSeparator +
            "head synsets that contan antonyms.  Each head synset has one or more " +
            WNGlobal.lineSeparator +
            "satellite synsets. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "A head synset contains at least one word that has a direct antonym " +
            WNGlobal.lineSeparator +
            "in another head synset of the same cluster. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "A satellite synset represents a concept that is similar in meaning to " +
            WNGlobal.lineSeparator +
            "the concept represented by its head synset. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Direct antonyms are a pair of words between which there is an " +
            WNGlobal.lineSeparator +
            "associative bond built up by co-occurrences. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Direct antonyms are printed in parentheses following the adjective. " +
            WNGlobal.lineSeparator +
            "The position of an adjective in relation to the noun may be restricted " +
            WNGlobal.lineSeparator +
            "to the prenominal, postnominal or predicative position.  Where present " +
            WNGlobal.lineSeparator +
            "these restrictions are noted in parentheses. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "A pertainym is a relational adjective, usually defined by such phrases " +
            WNGlobal.lineSeparator +
            "as \"of or pertaining to\" and that does not have an antonym.  It pertains " +
            WNGlobal.lineSeparator +
            "to a noun or another pertainym. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Senses contained in head synsets are displayed above the satellites, " +
            WNGlobal.lineSeparator +
            "which are indented and preceded by \"=>\".  Senses contained in " +
            WNGlobal.lineSeparator +
            "satellite synsets are displayed with the head synset below.  The head " +
            WNGlobal.lineSeparator +
            "synset is preceded by \"=>\". " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Pertainym senses display the word or synsets that the search string " +
            WNGlobal.lineSeparator +
            "pertains to. " +
            WNGlobal.lineSeparator;

    /**
     *  Help for ANTPTR
     *
     * @since    0.1.0
     */
    public static String antptr_help =
    //
            "Display synsets containing antonyms of the search string. If the " +
            WNGlobal.lineSeparator +
            "search string is in a head synset the direct antonym is displayed " +
            WNGlobal.lineSeparator +
            "along with the head synset's satellite synsets.  If the search " +
            WNGlobal.lineSeparator +
            "string is in a satellite synset, its indirect antonym is displayed " +
            WNGlobal.lineSeparator +
            "via the head synset " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "A head synset contains at least one word that has a direct antonym " +
            WNGlobal.lineSeparator +
            "in another head synset of the same cluster. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "A satellite synset represents a concept that is similar in meaning to " +
            WNGlobal.lineSeparator +
            "the concept represented by its head synset. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Direct antonyms are a pair of words between which there is an " +
            WNGlobal.lineSeparator +
            "associative bond built up by co-occurrences. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Direct antonyms are printed in parentheses following the adjective. " +
            WNGlobal.lineSeparator +
            "The position of an adjective in relation to the noun may be restricted " +
            WNGlobal.lineSeparator +
            "to the prenominal, postnominal or predicative position.  Where present " +
            WNGlobal.lineSeparator +
            "these restrictions are noted in parentheses. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Senses contained in head synsets are displayed, followed by the " +
            WNGlobal.lineSeparator +
            "head synset containing the search string's direct antonym and its " +
            WNGlobal.lineSeparator +
            "similar synsets, which are indented and preceded by \"=>\".  Senses " +
            WNGlobal.lineSeparator +
            "contained in satellite synsets are displayed followed by the indirect " +
            WNGlobal.lineSeparator +
            "antonym via the satellite's head synset. " +
            WNGlobal.lineSeparator;

    /**
     *  Help for Adverb SIMPTR
     *
     * @since    0.1.0
     */
    public static String adv_simptr_help =
            "Display synonyms and synsets related to synsets containing " +
            WNGlobal.lineSeparator +
            "the search string.  If the search string is a pertainym the word " +
            WNGlobal.lineSeparator +
            "or synset that it pertains to is displayed. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "A pertainym is a relational adverb that is derived from an adjective. " +
            WNGlobal.lineSeparator +
            WNGlobal.lineSeparator +
            "Pertainym senses display the word that the search string is derived from " +
            WNGlobal.lineSeparator +
            "and the adjective synset that contains the word.  If the adjective synset " +
            WNGlobal.lineSeparator +
            "is a satellite synset, its head synset is also displayed. " +
            WNGlobal.lineSeparator;

    /**
     *  Help for nouns, array of the various posible help strings.
     *
     * @since    0.1.0
     */
    public static String[] nounhelps = {
            hyper_help,
            relatives_help,
            ant_help,
            coord_help,
            hypertree_help,
            hypo_help,
            hypotree_help,
            holo_help,
            holotree_help,
            mero_help,
            merotree_help,
            nomin_help,
            nattrib_help,
            freq_help,
            grep_help
            };

    /**
     *  Help for verbs, array of the various posible help strings.
     *
     * @since    0.1.0
     */
    public static String[] verbhelps = {
            hyper_help,
            relatives_help,
            ant_help,
            coord_help,
            hypertree_help,
            tropo_help,
            entail_help,
            causeto_help,
            nomin_help,
            frames_help,
            freq_help,
            grep_help
            };

    /**
     *  Help for adjectives, array of the various posible help strings.
     *
     * @since    0.1.0
     */
    public static String[] adjhelps = {
            adj_simptr_help,
            antptr_help,
            aattrib_help,
            freq_help,
            grep_help
            };

    /**
     *  Help for adverbs, array of the various posible help strings.
     *
     * @since    0.1.0
     */
    public static String[] advhelps = {
            adv_simptr_help,
            ant_help,
            freq_help,
            grep_help
            };

    /**
     *  All the Help Strings
     *
     * @since    0.1.0
     */
    public static String[][] helptext = {
            null, nounhelps, verbhelps, adjhelps, advhelps
            };

}

