/**
 *
 *  wnrtl.c - global variables used by WordNet Run Time Library
 *
 */
package opennlp.dictionary.wordnet;

import java.io.RandomAccessFile;

public class WNrtl {

    public static String Id = "$Id: WNrtl.java,v 1.1 2002/03/20 20:23:54 mratkinson Exp $";
    
    // Search code variables and flags.
    
    public  SearchResults wnresults = new SearchResults();	// structure containing results of search.
    
    public  boolean fnflag = false;		// if set, print lex filename after sense.
    public  boolean dflag = true;			// if set, print definitional glosses.
    public  boolean saflag = true;		// if set, print SEE ALSO pointers.
    public  boolean fileinfoflag = false;		// if set, print lex file info on synsets.
    public  boolean frflag = false;		// if set, print verb frames.
    public  boolean abortsearch = false;		// if set, stop search algorithm.
    public  boolean offsetflag = false;		// if set, print byte offset of each synset.
    public  boolean wnsnsflag = false;		// if set, print WN sense # for each word.
    
    // File pointers for database files.
    
    public  boolean OpenDB = false;		// if true, database file are open.
    public  RandomAccessFile[] datafps = { null, null, null, null, null };
    
    public  RandomAccessFile[] indexfps = { null, null, null, null, null };
    public  RandomAccessFile sensefp = null;
    public  RandomAccessFile cntlistfp = null;
    public  RandomAccessFile cousinfp = null;
    public  RandomAccessFile cousinexcfp = null;
    public  RandomAccessFile vsentfilefp = null;
    public  RandomAccessFile vidxfilefp = null;
    
    // Method for interface to check for events while search is running.
    
    //void (*interface_doevents_func)(void) = null;
                            // callback function for interruptable searches
                            // in single-threaded interfaces.
    
    // General error message handler - can be defined by interface.
    // Default function provided in library returns -1.
    public static void display_message(String msg) {
        WordNet.display_message(msg);
    }
}

