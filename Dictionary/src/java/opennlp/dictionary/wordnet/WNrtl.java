package opennlp.dictionary.wordnet;

import java.io.RandomAccessFile;

/**
 *  Global variables used by WordNet Run Time Library.<p>
 *
 *  This class was created by heavily modifying the WordNet 1.7 code src/lib/wnrtl.c
 *
 * @author     Mike Atkinson (mratkinson)
 * @since      0.1.0
 * @created    20 March 2002
 * @version    "$Id: WNrtl.java,v 1.2 2002/03/26 19:15:21 mratkinson Exp $";
 */
public class WNrtl {

    // Search code variables and flags.

    /**
     *  structure containing results of search.
     *
     * @since    0.1.0
     */
    public SearchResults wnResults = new SearchResults();

    /**
     *  If set, print lex filename after sense.
     *
     * @since    0.1.0
     */
    public boolean fnFlag = false;
    /**
     *  If set, print definitional glosses.
     *
     * @since    0.1.0
     */
    public boolean dFlag = true;
    /**
     *  If set, print SEE ALSO pointers.
     *
     * @since    0.1.0
     */
    public boolean saFlag = true;
    /**
     *  If set, print lex file info on synsets.
     *
     * @since    0.1.0
     */
    public boolean fileInfoFlag = false;
    /**
     *  If set, print verb frames.
     *
     * @since    0.1.0
     */
    public boolean frFlag = false;
    /**
     *  If set, stop search algorithm.
     *
     * @since    0.1.0
     */
    public boolean abortSearch = false;
    /**
     *  If set, print byte offset of each synset.
     *
     * @since    0.1.0
     */
    public boolean offsetFlag = false;
    /**
     *  If set, print WN sense # for each word.
     *
     * @since    0.1.0
     */
    public boolean wnsnsFlag = false;

    /**
     *  If true, database file are open.
     *
     * @since    0.1.0
     */
    public boolean OpenDB = false;
    /**
     *  File pointers for data database files.
     *
     * @since    0.1.0
     */
    public RandomAccessFile[] dataFiles = {null, null, null, null, null};

    /**
     *  File pointers for index database files.
     *
     * @since    0.1.0
     */
    public RandomAccessFile[] indexFiles = {null, null, null, null, null};
    /**
     *  File pointer for sense database file.
     *
     * @since    0.1.0
     */
    public RandomAccessFile senseFile = null;
    /**
     *  File pointer for count database file.
     *
     * @since    0.1.0
     */
    public RandomAccessFile cntListFile = null;
    /**
     *  File pointer for cousin database file (not present in WordNet 1.7).
     *
     * @since    0.1.0
     */
    public RandomAccessFile cousinFile = null;
    /**
     *  File pointer for cousin exceptions database file (not present in WordNet
     *  1.7).
     *
     * @since    0.1.0
     */
    public RandomAccessFile cousinExcFile = null;
    /**
     *  File pointer for verb sentence database file.
     *
     * @since    0.1.0
     */
    public RandomAccessFile verbSentenceFile = null;
    /**
     *  File pointer for verb sentence index database file.
     *
     * @since    0.1.0
     */
    public RandomAccessFile verbSentenceIndexFile = null;


    // Method for interface to check for events while search is running.

    //void (*interfaceDoEvents_func)(void) = null;
    // callback function for interruptable searches
    // in single-threaded interfaces.

    // General error message handler - can be defined by interface.
    // Default function provided in library returns -1.
    /**
     *  Write the msg string to the main WordNet display.
     *
     * @param  msg  The message to display.
     * @since       0.1.0
     */
    public static void displayMessage(String msg) {
        WordNet.displayMessage(msg);
    }
}

