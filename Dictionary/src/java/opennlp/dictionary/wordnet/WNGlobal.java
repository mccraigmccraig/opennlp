package opennlp.dictionary.wordnet;

/**
 *  Global variables used by various WordNet applications.<p>
 *
 *  This class was created by heavily modifying the WordNet 1.7 code src/lib/wnglobal.h
 *
 * @author     Mike Atkinson (mratkinson)
 * @since      0.1.0
 * @created    20 March 2002
 * @version    $Id: WNGlobal.java,v 1.4 2002/03/26 19:37:51 mratkinson Exp $
 */
public class WNGlobal {
    /**
     *  We work with WordNet 1.7 data files, WordNet 1.6 data files may work but
     *  are not tested.
     *
     * @since    0.1.0
     */
    public static String wnRelease = "1.7";

    /**
     *  Lexicographer file names, Lexicographer numbers are equals to their index
     *  in the array.
     *  <ol start=0>
     *    <li> "adj.all"</li>
     *    <li> "adj.pert"</li>
     *    <li> "adv.all"</li>
     *    <li> "noun.Tops"</li>
     *    <li> "noun.act"</li>
     *    <li> "noun.animal"</li>
     *    <li> "noun.artifact"</li>
     *    <li> "noun.attribute"</li>
     *    <li> "noun.body"</li>
     *    <li> "noun.cognition"</li>
     *    <li> "noun.communication"</li>
     *    <li> "noun.event"</li>
     *    <li> "noun.feeling"</li>
     *    <li> "noun.food"</li>
     *    <li> "noun.group"</li>
     *    <li> "noun.location"</li>
     *    <li> "noun.motive"</li>
     *    <li> "noun.object"</li>
     *    <li> "noun.person"</li>
     *    <li> "noun.phenomenon"</li>
     *    <li> "noun.plant"</li>
     *    <li> "noun.possession"</li>
     *    <li> "noun.process"</li>
     *    <li> "noun.quantity"</li>
     *    <li> "noun.relation"</li>
     *    <li> "noun.shape"</li>
     *    <li> "noun.state"</li>
     *    <li> "noun.substance"</li>
     *    <li> "noun.time"</li>
     *    <li> "verb.body"</li>
     *    <li> "verb.change"</li>
     *    <li> "verb.cognition"</li>
     *    <li> "verb.communication"</li>
     *    <li> "verb.competition"</li>
     *    <li> "verb.consumption"</li>
     *    <li> "verb.contact"</li>
     *    <li> "verb.creation"</li>
     *    <li> "verb.emotion"</li>
     *    <li> "verb.motion"</li>
     *    <li> "verb.perception"</li>
     *    <li> "verb.possession"</li>
     *    <li> "verb.social"</li>
     *    <li> "verb.stative"</li>
     *    <li> "verb.weather"</li>
     *    <li> "adj.ppl"</li>
     *  </ol>
     *
     *
     * @since    0.1.0
     */
    public final static String[] lexFiles = {
            "adj.all", // 0
    "adj.pert", // 1
    "adv.all", // 2
    "noun.Tops", // 3
    "noun.act", // 4
    "noun.animal", // 5
    "noun.artifact", // 6
    "noun.attribute", // 7
    "noun.body", // 8
    "noun.cognition", // 9
    "noun.communication", // 10
    "noun.event", // 11
    "noun.feeling", // 12
    "noun.food", // 13
    "noun.group", // 14
    "noun.location", // 15
    "noun.motive", // 16
    "noun.object", // 17
    "noun.person", // 18
    "noun.phenomenon", // 19
    "noun.plant", // 20
    "noun.possession", // 21
    "noun.process", // 22
    "noun.quantity", // 23
    "noun.relation", // 24
    "noun.shape", // 25
    "noun.state", // 26
    "noun.substance", // 27
    "noun.time", // 28
    "verb.body", // 29
    "verb.change", // 30
    "verb.cognition", // 31
    "verb.communication", // 32
    "verb.competition", // 33
    "verb.consumption", // 34
    "verb.contact", // 35
    "verb.creation", // 36
    "verb.emotion", // 37
    "verb.motion", // 38
    "verb.perception", // 39
    "verb.possession", // 40
    "verb.social", // 41
    "verb.stative", // 42
    "verb.weather", // 43
    "adj.ppl",// 44
    };

    /**
     *  Pointer characters and searches.
     *  <ol>
     *    <li> "!", ANTPTR</li>
     *    <li> "@", HYPERPTR</li>
     *    <li> "~", HYPOPTR</li>
     *    <li> "*", ENTAILPTR</li>
     *    <li> "&", SIMPTR</li>
     *    <li> "#m", ISMEMBERPTR</li>
     *    <li> "#s", ISSTUFFPTR</li>
     *    <li> "#p", ISPARTPTR</li>
     *    <li> "%m", HASMEMBERPTR</li>
     *    <li> "%s", HASSTUFFPTR</li>
     *    <li> "%p", HASPARTPTR</li>
     *    <li> "%", MERONYM</li>
     *    <li> "#", HOLONYM</li>
     *    <li> ">", CAUSETO</li>
     *    <li> "&lt;", PPLPTR</li>
     *    <li> "^", SEEALSO</li>
     *    <li> "\\", PERTPTR</li>
     *    <li> "=", ATTRIBUTE</li>
     *    <li> "$", VERBGROUP</li>
     *    <li> "", NOMINALIZATIONS</li>
     *  </ol>
     *  <p>
     *
     *  Additional searches, but not pointers.</p>
     *  <ul>
     *    <li> "", SYNS</li>
     *    <li> "", FREQ</li>
     *    <li> "+", FRAMES</li>
     *    <li> "", COORDS</li>
     *    <li> "", RELATIVES</li>
     *    <li> "", HMERONYM</li>
     *    <li> "", HHOLONYM</li>
     *    <li> "", WNESCORT</li>
     *    <li> "", WNGREP</li>
     *    <li> "", OVERVIEW</li>
     *  </ul>
     *  <p>
     *
     *  Specific nominalization pointers.</p>
     *  <ul>
     *    <li> "+a", NOMIN_V_ATE</li>
     *    <li> "+b", NOMIN_V_IFY</li>
     *    <li> "+c", NOMIN_V_ISE_IZE</li>
     *    <li> "+d", NOMIN_V_ACY</li>
     *    <li> "+e", NOMIN_V_AGE</li>
     *    <li> "+f", NOMIN_V_AL</li>
     *    <li> "+g", NOMIN_V_ANCE_ENCE</li>
     *    <li> "+h", NOMIN_V_ANCY_ENCY</li>
     *    <li> "+i", NOMIN_V_ANT_ENT</li>
     *    <li> "+j", NOMIN_V_ARD</li>
     *    <li> "+k", NOMIN_V_ARY</li>
     *    <li> "+l", NOMIN_V_ATE</li>
     *    <li> "+m", NOMIN_V_ATION</li>
     *    <li> "+n", NOMIN_V_EE</li>
     *    <li> "+o", NOMIN_V_ER</li>
     *    <li> "+p", NOMIN_V_ERY_RY</li>
     *    <li> "+q", NOMIN_V_ING_INGS</li>
     *    <li> "+r", NOMIN_V_ION</li>
     *    <li> "+s", NOMIN_V_IST</li>
     *    <li> "+t", NOMIN_V_MENT</li>
     *    <li> "+u", NOMIN_V_OR</li>
     *    <li> "+v", NOMIN_V_URE</li>
     *    <li> "+w", NOMIN_V_MISC</li>
     *    <li> "+x", NOMIN_V_UNMARKED</li>
     *  </ul>
     *
     *
     * @since    0.1.0
     */
    public final static String[] ptrType = {
            "", // 0 not used
    "!", // 1 ANTPTR
    "@", // 2 HYPERPTR
    "~", // 3 HYPOPTR
    "*", // 4 ENTAILPTR
    "&", // 5 SIMPTR
    "#m", // 6 ISMEMBERPTR
    "#s", // 7 ISSTUFFPTR
    "#p", // 8 ISPARTPTR
    "%m", // 9 HASMEMBERPTR
    "%s", // 10 HASSTUFFPTR
    "%p", // 11 HASPARTPTR
    "%", // 12 MERONYM
    "#", // 13 HOLONYM
    ">", // 14 CAUSETO
    "<", // 15 PPLPTR
    "^", // 16 SEEALSO
    "\\", // 17 PERTPTR
    "=", // 18 ATTRIBUTE
    "$", // 19 VERBGROUP
    "", // 20 NOMINALIZATIONS
    // Additional searches, but not pointers.
    "", // SYNS
    "", // FREQ
    "+", // FRAMES
    "", // COORDS
    "", // RELATIVES
    "", // HMERONYM
    "", // HHOLONYM
    "", // WNESCORT
    "", // WNGREP
    "", // OVERVIEW
    // Specific nominalization pointers
    "+a", // NOMIN_V_ATE
    "+b", // NOMIN_V_IFY
    "+c", // NOMIN_V_ISE_IZE
    "+d", // NOMIN_V_ACY
    "+e", // NOMIN_V_AGE
    "+f", // NOMIN_V_AL
    "+g", // NOMIN_V_ANCE_ENCE
    "+h", // NOMIN_V_ANCY_ENCY
    "+i", // NOMIN_V_ANT_ENT
    "+j", // NOMIN_V_ARD
    "+k", // NOMIN_V_ARY
    "+l", // NOMIN_V_ATE
    "+m", // NOMIN_V_ATION
    "+n", // NOMIN_V_EE
    "+o", // NOMIN_V_ER
    "+p", // NOMIN_V_ERY_RY
    "+q", // NOMIN_V_ING_INGS
    "+r", // NOMIN_V_ION
    "+s", // NOMIN_V_IST
    "+t", // NOMIN_V_MENT
    "+u", // NOMIN_V_OR
    "+v", // NOMIN_V_URE
    "+w", // NOMIN_V_MISC
    "+x", // NOMIN_V_UNMARKED
    null
            };

    /**
     *  Part of Speech names.<p>
     *  <ol>
     *    <li>"noun"</li>
     *    <li>"verb"</li>
     *    <li>"adj"</li>
     *    <li>"adv"</li>
     *  </ol>
     *
     * @since    0.1.0
     */
    public final static String[] partNames = {"", "noun", "verb", "adj", "adv", null};
    /**
     *  Characters to add to end of options for the various parts of speech.
     *
     * @since    0.1.0
     */
    public final static String partChars = " nvara";// add char for satellites to end
    /**
     *  Classes of adjectives.
     *
     * @since    0.1.0
     */
    public final static String[] adjClass = {"", "(p)", "(a)", "(ip)"};


    /**
     *  Text of verb sentence frames
     *
     * @since    0.1.0
     */
    public final static String[] frameText = {
            "",
            "Something ----s",
            "Somebody ----s",
            "It is ----ing",
            "Something is ----ing PP",
            "Something ----s something Adjective/Noun",
            "Something ----s Adjective/Noun",
            "Somebody ----s Adjective",
            "Somebody ----s something",
            "Somebody ----s somebody",
            "Something ----s somebody",
            "Something ----s something",
            "Something ----s to somebody",
            "Somebody ----s on something",
            "Somebody ----s somebody something",
            "Somebody ----s something to somebody",
            "Somebody ----s something from somebody",
            "Somebody ----s somebody with something",
            "Somebody ----s somebody of something",
            "Somebody ----s something on somebody",
            "Somebody ----s somebody PP",
            "Somebody ----s something PP",
            "Somebody ----s PP",
            "Somebody's (body part) ----s",
            "Somebody ----s somebody to INFINITIVE",
            "Somebody ----s somebody INFINITIVE",
            "Somebody ----s that CLAUSE",
            "Somebody ----s to somebody",
            "Somebody ----s to INFINITIVE",
            "Somebody ----s whether INFINITIVE",
            "Somebody ----s somebody into V-ing something",
            "Somebody ----s something with something",
            "Somebody ----s INFINITIVE",
            "Somebody ----s VERB-ing",
            "It ----s that CLAUSE",
            "Something ----s INFINITIVE",
            ""
            };

    /**
     *  The line separator used by the implementing system.
     *
     * @since    0.1.0
     */
    public final static String lineSeparator = System.getProperty("line.separator");
}

