package opennlp.dictionary.wordnet;

/**
 *  Global constants used by WordNet Java implementation.<p>
 *
 *  This class was created by heavily modifying the WordNet 1.7 code src/include/wnconsts.c
 *
 * @author     Mike Atkinson (mratkinson)
 * @since      0.1.0
 * @created    20 March 2002
 * @version    "$Id: WNConsts.java,v 1.2 2002/03/26 19:13:43 mratkinson Exp $";
 */
public class WNConsts {
    // Unix
    public final static String DICTDIR          = "/dict";
    public final static String DEFAULTPATH	= "/usr/local/wordnet1.7/dict";
    public final static String DEFAULTBIN       = "/usr/local/wordnet1.7/bin";
    public final static String DATAFILE	        = "data.";
    public final static String INDEXFILE	= "index.";
    public final static String SENSEIDXFILE	= "index.sense";
    public final static String COUSINFILE	= "cousin.tops";
    public final static String COUSINEXCFILE	= "cousin.exc";
    public final static String VRBSENTFILE      = "sents.vrb";
    public final static String VRBIDXFILE	= "sentidx.vrb";
    public final static String CNTLISTFILE      = "cntlist.rev";
/*
    // PC
    public final static String DICTDIR          = "\\dict"
    public final static String DEFAULTPATH	= "c:\\wn17\\dict"
    public final static String DEFAULTBIN       = "c:\\wn17\\bin"
    public final static String DATAFILE	        = ".dat"
    public final static String INDEXFILE	= ".idx"
    public final static String SENSEIDXFILE	= "sense.idx"
    public final static String COUSINFILE	= "cousin.tps"
    public final static String COUSINEXCFILE	= "cousin.exc"
    public final static String VRBSENTFILE  	= "sents.vrb"
    public final static String VRBIDXFILE	= "sentidx.vrb"
    public final static String CNTLISTFILE      = "cntlist.rev"

    // MAC
    public final static String DICTDIR          = ":Database"
    public final static String DEFAULTPATH      = ":Database"
    public final static String DEFAULTBIN       = ":"
    public final static String DATAFILE	        = "%s:data.%s"
    public final static String INDEXFILE        = "%s:index.%s"
    public final static String SENSEIDXFILE	= "%s:index.sense"
    public final static String COUSINFILE	= "%s:cousin.tops"
    public final static String COUSINEXCFILE	= "%s:cousin.exc"
    public final static String VRBSENTFILE      = "%s:sents.vrb"
    public final static String VRBIDXFILE 	= "%s:sentidx.vrb"
    public final static String CNTLISTFILE      = "%s:cntlist.rev"
*/

// Various buffer sizes

// Search output buffer
    public final static int SEARCHBUF   = 200*1024;

    public final static int LINEBUF	= 15*1024; // 15K buffer to read index & data files.
    public final static int SMLINEBUF	= 3*1024; // small buffer for output lines.
    public final static int WORDBUF	= 256;	// buffer for one word or collocation.

    public final static int ALLSENSES	= 0;	// pass to findTheInfo() if want all senses.
    public final static int MAXID	= 15;	// maximum id number in lexicographer file.
    public final static int MAXDEPTH	= 20;	// maximum tree depth - used to find cycles.
    public final static int MAXSENSE	= 75;	// maximum number of senses in database.
    public final static int MAX_FORMS	= 5;	// max # of different 'forms' word can have.
    public final static int MAXFNUM	= 44;	// maximum number of lexicographer files.

// Pointer type and search type counts.

// Pointers

    public final static int ANTPTR          =  1;	// !
    public final static int HYPERPTR        =  2;	// @
    public final static int HYPOPTR         =  3;	// ~
    public final static int ENTAILPTR       =  4;	// *
    public final static int SIMPTR          =  5;	// &

    public final static int ISMEMBERPTR     =  6;	// #m
    public final static int ISSTUFFPTR      =  7;	// #s
    public final static int ISPARTPTR       =  8;	// #p

    public final static int HASMEMBERPTR    =  9;	// %m
    public final static int HASSTUFFPTR     = 10;	// %s
    public final static int HASPARTPTR      = 11;	// %p

    public final static int MERONYM         = 12;	// % (not valid in lexicographer file)
    public final static int HOLONYM         = 13;	// # (not valid in lexicographer file)
    public final static int CAUSETO         = 14;	// >
    public final static int PPLPTR	    = 15;	// <
    public final static int SEEALSOPTR      = 16;	// ^
    public final static int PERTPTR	    = 17;	// \
    public final static int ATTRIBUTE	    = 18;	// =
    public final static int VERBGROUP	    = 19;	// $
    public final static int NOMINALIZATIONS = 20;	// +

    public final static int LASTTYPE	    = NOMINALIZATIONS;

// Misc searches.

    public final static int SYNS            = (LASTTYPE + 1);
    public final static int FREQ            = (LASTTYPE + 2);
    public final static int FRAMES          = (LASTTYPE + 3);
    public final static int COORDS          = (LASTTYPE + 4);
    public final static int RELATIVES	    = (LASTTYPE + 5);
    public final static int HMERONYM        = (LASTTYPE + 6);
    public final static int HHOLONYM	    = (LASTTYPE + 7);
    public final static int WNESCORT	    = (LASTTYPE + 8);
    public final static int WNGREP	    = (LASTTYPE + 9);
    public final static int OVERVIEW	    = (LASTTYPE + 10);

    public final static int MAXSEARCH       = OVERVIEW;

// Specific nominalization pointers.

    public final static int NOMIN_START     = (OVERVIEW + 1);

    public final static int NOMIN_V_ATE     = (NOMIN_START);          // +a
    public final static int NOMIN_V_IFY     = (NOMIN_START + 1);      // +b
    public final static int NOMIN_V_ISE_IZE = (NOMIN_START + 2);      // +c
    public final static int NOMIN_ACY       = (NOMIN_START + 3);      // +d
    public final static int NOMIN_AGE       = (NOMIN_START + 4);      // +e
    public final static int NOMIN_AL        = (NOMIN_START + 5);      // +f
    public final static int NOMIN_ANCE_ENCE = (NOMIN_START + 6);      // +g
    public final static int NOMIN_ANCY_ENCY = (NOMIN_START + 7);      // +h
    public final static int NOMIN_ANT_ENT   = (NOMIN_START + 8);      // +i
    public final static int NOMIN_ARD       = (NOMIN_START + 9);      // +j
    public final static int NOMIN_ARY       = (NOMIN_START + 10);     // +k
    public final static int NOMIN_ATE       = (NOMIN_START + 11);     // +l
    public final static int NOMIN_ATION     = (NOMIN_START + 12);     // +m
    public final static int NOMIN_EE        = (NOMIN_START + 13);     // +n
    public final static int NOMIN_ER        = (NOMIN_START + 14);     // +o
    public final static int NOMIN_ERY_RY    = (NOMIN_START + 15);     // +p
    public final static int NOMIN_ING_INGS  = (NOMIN_START + 16);     // +q
    public final static int NOMIN_ION       = (NOMIN_START + 17);     // +r
    public final static int NOMIN_IST       = (NOMIN_START + 18);     // +s
    public final static int NOMIN_MENT      = (NOMIN_START + 19);     // +t
    public final static int NOMIN_OR        = (NOMIN_START + 20);     // +u
    public final static int NOMIN_URE       = (NOMIN_START + 21);     // +v
    public final static int NOMIN_MISC      = (NOMIN_START + 22);     // +w
    public final static int NOMIN_UNMARKED  = (NOMIN_START + 23);     // +x

    public final static int NOMIN_END       = NOMIN_UNMARKED;

    public final static int MAXPTR          = NOMIN_END;

// WordNet part of speech stuff.

    public final static int NUMPARTS	        = 4;	// number of parts of speech.
    public final static int NUMFRAMES	        = 35;	// number of verb frames.

// Generic names for part of speech.

    public final static int NOUN		= 1;
    public final static int VERB		= 2;
    public final static int ADJ		        = 3;
    public final static int ADV		        = 4;
    public final static int SATELLITE	        = 5;	// not really a part of speech.
    public final static int ADJSAT		= SATELLITE;

    public final static int ALL_POS		=0;	// passed to inWN() to check all POS.


// Adjective markers.

    public final static int PADJ		= 1;	// (p)
    public final static int NPADJ		= 2;	// (a)
    public final static int IPADJ		= 3;	// (ip)

    public final static int UNKNOWN_MARKER	= 0;
    public final static int ATTRIBUTIVE	        = NPADJ;
    public final static int PREDICATIVE	        = PADJ;
    public final static int IMMED_POSTNOMINAL	= IPADJ;

    
    
    public final static int bit(int n) { return 1<<n; }
    
    public final static boolean WN1_6 = false;

}

