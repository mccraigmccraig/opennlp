package opennlp.dictionary.wordnet;

import java.io.File;
import java.io.RandomAccessFile;

/**
 *  Utility functions used by WordNet code.<p>
 *
 *  This class was created by heavily modifying the WordNet 1.7 code src/lib/wnutil.c.
 * @author     Mike Atkinson (mratkinson)
 * @since      0.1.0
 * @created    20 March 2002
 * @version    $Id: WNUtil.java,v 1.3 2002/03/26 19:39:36 mratkinson Exp $
 */
public class WNUtil {

    private BinSearch binSearcher;
    private Search searcher;
    private Morph morpher;
    private WNrtl wnRtl;

    private static boolean done = false;

    private final static int MAX_TRIES = 5;


    /**
     *  Constructor for the WNUtil object
     *
     * @param  binSearcher  Performs binary searches of files.
     * @param  searcher     Performs wordnet searches.
     * @param  wnRtl        Holds the database file information.
     * @since               0.1.0
     */
    public WNUtil(BinSearch binSearcher, Search searcher, WNrtl wnRtl) {
        this.binSearcher = binSearcher;
        this.searcher = searcher;
        this.wnRtl = wnRtl;
    }


    /**
     *  Sets the Morph attribute of the WNUtil object.
     *
     * This must be set before the utilities are used.
     *
     * @param  morpher  The new Morph value
     * @since           0.1.0
     */
    public void setMorph(Morph morpher) {
        this.morpher = morpher;
    }



    /**
     *  Return pointer code for pointer type characer passed.
     *
     * @param  ptrstr  Pointer type.
     * @return         Index into WNGlobal.ptrType of ptrstr or 0 if not found
     * @since          0.1.0
     */

    public int getPointerType(String ptrstr) {
        for (int i = 1; i <= WNConsts.MAXPTR; i++) {
            if (ptrstr.equals(WNGlobal.ptrType[i])) {
                return i;
            }
        }
        return 0;
    }


    /**
     *  Return part of speech code for string passed.<p>
     *
     * "n" = WNConsts.NOUN<br>
     * "v" = WNConsts.VERB<br>
     * "r" = WNConsts.ADV<br>
     * "a", "s" = WNConsts.ADJ<br>
     *
     * @param  s  One of "n", "a", "s", "v" or "r".
     * @return    part of speech.
     * @since     0.1.0
     */

    public int getpos(char s) {
        switch (s) {
            case 'n':
                return (WNConsts.NOUN);
            case 'a':
            case 's':
                return (WNConsts.ADJ);
            case 'v':
                return (WNConsts.VERB);
            case 'r':
                return (WNConsts.ADV);
            default:
                String msg = "WordNet library error: unknown part of speech " + s + "";
                WordNet.displayMessage(msg);
                throw new Error(msg);
        }
    }


    /**
     *  Return synset type code for string passed.<p>
     *
     * "n" = WNConsts.NOUN<br>
     * "v" = WNConsts.VERB<br>
     * "r" = WNConsts.ADV<br>
     * "a" = WNConsts.SATELLITE<br>
     * "s" = WNConsts.ADJ<br>
     * @param  s  One of "n", "a", "s", "v" or "r".
     * @return    Synset type.
     * @since     0.1.0
     */

    public int getsstype(char s) {
        switch (s) {
            case 'n':
                return (WNConsts.NOUN);
            case 'a':
                return (WNConsts.ADJ);
            case 'v':
                return (WNConsts.VERB);
            case 's':
                return (WNConsts.SATELLITE);
            case 'r':
                return (WNConsts.ADV);
            default:
                String msg = "WordNet library error: unknown synset type " + s + "";
                WordNet.displayMessage(msg);
                throw new Error(msg);
        }
    }


    /**
     *  Find string for 'searchstr' as it is in index file.<p>
     *
     *  Spaces may be replaced by hyphens, hypens by spaces and spaces, hyphens
     *  and periods may be removed. 
     *
     * @param  searchstr  Word (or collocation) to search for.
     * @param  dbase      Database to search in.
     * @return            Version of 'searchstr' as it is in the index file.
     * @since             0.1.0
     */

    public String getWNStr(String searchstr, int dbase) {
        String[] strings = new String[MAX_TRIES];
        int offset = 0;
        char c;
        int underscore = -1;
        int hyphen = -1;
        int period = -1;

        searchstr = searchstr.toLowerCase();

        if ((underscore = searchstr.indexOf('_')) >= 0 &&
                (hyphen = searchstr.indexOf('-')) >= 0 &&
                (period = searchstr.indexOf('.')) >= 0) {
            return strings[0] = searchstr;
        }

        for (int i = 0; i < 3; i++) {
            strings[i] = searchstr;
        }

        StringBuffer sb3 = new StringBuffer();
        StringBuffer sb4 = new StringBuffer();
        if (underscore >= 0) {
            strings[1] = strings[1].replace('_', '-');
        }
        if (hyphen >= 0) {
            strings[2] = strings[2].replace('-', '_');
        }
        for (int i = 0; i < searchstr.length(); i++) {
            c = searchstr.charAt(i);
            if (c != '_' && c != '-') {
                sb3.append(c);
            }
            if (c != '.') {
                sb4.append(c);
            }
        }
        strings[3] = sb3.toString();
        strings[4] = sb4.toString();

        for (int i = 1; i < MAX_TRIES; i++) {
            if (strings[0].equals(strings[i])) {
                strings[i] = null;
            }
        }

        for (int i = (MAX_TRIES - 1); i >= 0; i--) {
            if (strings[i] != null) {
                if (binSearcher.binSearch(strings[i], wnRtl.indexFiles[dbase]) != null) {
                    offset = i;
                }
            }
        }

        return strings[offset];
    }


    /**
     *  Return synset for sense key passed.
     *
     * @param  senseKey  Encoded sense string.
     * @return           Parsed sysnet structure.
     * @since            0.1.0
     */

    public SynSet getSynsetForSense(String senseKey) {
        int offset = getDataOffset(senseKey);
        if (offset > 0) {
            return (searcher.readSynset(getPOS(senseKey),
                    offset,
                    getWORD(senseKey)));
        } else {
            return null;
        }
    }


    /**
     *  Find offset of sense key in data file.
     *
     * @param  senseKey  Encoded sense string.
     * @return           Byte offset of corresponding synset in data file.
     * @since            0.1.0
     */

    public int getDataOffset(String senseKey) {
        if (wnRtl.senseFile == null) {
            WordNet.displayMessage("WordNet library error: Sense index file not open");
            return 0;
        }
        String line = binSearcher.binSearch(senseKey, wnRtl.senseFile);
        if (line != null) {
            int i = 0;
            while (line.charAt(i++) != ' ') {
            }
            return Integer.parseInt(line.substring(i));
        } else {
            return 0;
        }
    }


    /**
     *  Find polysemy count for sense key passed.
     *
     * @param  senseKey  Encoded sense string.
     * @return           Polysemy count for word in corresponding POS.
     * @since            0.1.0
     */
    public int getPolyCount(String senseKey) {
        Index idx = searcher.indexLookup(getWORD(senseKey), getPOS(senseKey));
        if (idx != null) {
            return idx.senseCount;
            // free_index(idx);
        }
        return 0;
    }


    /**
     *  Return word part of sense key.
     *
     * @param  senseKey  Encoded sense string.
     * @return           The key before the first '%'.
     * @since            0.1.0
     */
    public String getWORD(String senseKey) {
        int i = 0;
        char c;
        StringBuffer sb = new StringBuffer();
        while ((c = senseKey.charAt(i++)) != '%') {
            sb.append(c);
        }
        return sb.toString();
    }


    /**
     *  Return POS code for sense key passed.
     *
     * @param  senseKey  Encoded sense string.
     * @return           Part Of Speech.
     * @since            0.1.0
     */
    public int getPOS(String senseKey) {
        StringBuffer sb = new StringBuffer();
        char c;
        int i = 0;
        while ((c = senseKey.charAt(i++)) != '%') {
        }// skip over WORD.
        while ((c = senseKey.charAt(i++)) != ' ') {
            sb.append(c);
        }
        String word = sb.toString();
        int pos = Integer.parseInt(word);
        return (pos == WNConsts.SATELLITE) ? WNConsts.ADJ : pos;
    }


    /**
     *  Search for string and/or baseform of word in database and return index structure
     *  for word if found in database.
     *
     * @param  word  To get Index data for.
     * @param  pos   Part of Speech.
     * @return       Data for word.
     * @since        0.1.0
     */
    public Index getValidIndexPointer(String word, int pos) {
        Index idx = searcher.getIndex(word, pos);

        if (idx == null) {
            String morphWord = morpher.morphStr(word, pos);
            if (morphWord != null) {
                while (morphWord != null) {
                    idx = searcher.getIndex(morphWord, pos);
                    if (idx != null) {
                        break;
                    }
                    morphWord = morpher.morphStr(null, pos);
                }
            }
        }
        return idx;
    }


    /**
     *  Return sense number in database for word and lexsn passed.
     *
     * @param  word   To get sense number for.
     * @param  lexsn  Lexicon Sense
     * @return        sense number.
     * @since         0.1.0
     */

    public int getWNSense(String word, String lexsn) {
        String buf = word + "%" + lexsn;// create senseKey.
        SnsIndex senseIndex = getSenseIndex(buf);
        if (senseIndex != null) {
            return senseIndex.wnSense;
        } else {
            return (0);
        }
    }


    /**
     *  Return parsed sense index entry for sense key passed.
     *
     * @param  senseKey  Encoded sense string.
     * @return           Sense Index data.
     * @since            0.1.0
     */

    public SnsIndex getSenseIndex(String senseKey) {
        SnsIndex senseIndex = null;

        String line = binSearcher.binSearch(senseKey, wnRtl.senseFile);
        if (line != null) {
            senseIndex = new SnsIndex();
            String[] str = split(line, " ");
            senseIndex.senseKey = str[0];
            senseIndex.loc = Integer.parseInt(str[1]);
            senseIndex.wnSense = Integer.parseInt(str[2]);
            senseIndex.tagCount = Integer.parseInt(str[3]);
            // Parse out word from senseKey to make things easier for caller.
            senseIndex.word = getWORD(senseIndex.senseKey);
            if (senseIndex.word == null) {
                throw new NullPointerException("getWORD returned a new value");
            }
            senseIndex.nextSenseIndex = null;
        }
        return senseIndex;
    }


    /**
     *  Get number of times sense is tagged.
     *
     * @param  idx    Index data for word.
     * @param  sense  WordNet sense.
     * @return        number of times sense is tagged.
     * @since         0.1.0
     */

    public int getTagCount(Index idx, int sense) {
        int count = 0;

        if (wnRtl.cntListFile != null) {
            String senseKey = WNSnsToStr(idx, sense);
            String line = binSearcher.binSearch(senseKey, wnRtl.cntListFile);
            if (line != null) {
                String[] str = split(line, " ");
                count = Integer.parseInt(str[2]);
            }
        }

        return count;
    }


    /**
     *  Initialization function.
     *
     * @since    0.1.0
     */
    public void wnInit() {
        if (!done) {
            String env = System.getProperty("WNDBVERSION");
            if (env == null) {
                env = "1.7";
            }
            WNGlobal.wnRelease = env;// set release.

            doInit();
            done = true;
            wnRtl.OpenDB = true;
            morpher.morphInit();
        }
    }


    /**
     *  Re-initialization function.
     *
     * @since    0.1.0
     */
    public void re_ninit() {
        closeFps();
        String env = System.getProperty("WNDBVERSION");
        if (env == null) {
            env = "1.7";
        }
        WNGlobal.wnRelease = env;// set release.

        doInit();
        wnRtl.OpenDB = true;
        morpher.re_morphInit();
    }


    /**
     *  Close the database files.
     *
     * @since    0.1.0
     */
    public void closeFps() {
        try {
            if (wnRtl.OpenDB) {
                for (int i = 1; i < WNConsts.NUMPARTS + 1; i++) {
                    if (wnRtl.dataFiles[i] != null) {
                        wnRtl.dataFiles[i].close();
                        wnRtl.dataFiles[i] = null;
                    }
                    if (wnRtl.indexFiles[i] != null) {
                        wnRtl.indexFiles[i].close();
                        wnRtl.indexFiles[i] = null;
                    }
                }
                if (wnRtl.senseFile != null) {
                    wnRtl.senseFile.close();
                    wnRtl.senseFile = null;
                }
                if (WNConsts.WN1_6) {
                    if (wnRtl.cousinFile != null) {
                        wnRtl.cousinFile.close();
                        wnRtl.cousinFile = null;
                    }
                    if (wnRtl.cousinExcFile != null) {
                        wnRtl.cousinExcFile.close();
                        wnRtl.cousinExcFile = null;
                    }
                }
                if (wnRtl.cntListFile != null) {
                    wnRtl.cntListFile.close();
                    wnRtl.cntListFile = null;
                }
                if (wnRtl.verbSentenceFile != null) {
                    wnRtl.verbSentenceFile.close();
                    wnRtl.verbSentenceFile = null;
                }
                if (wnRtl.verbSentenceIndexFile != null) {
                    wnRtl.verbSentenceIndexFile.close();
                    wnRtl.verbSentenceIndexFile = null;
                }
                wnRtl.OpenDB = false;
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }


    /**
     *  Open the database files.
     *
     *  To find the base directory for the database: <br>
     *  If WNSEARCHDIR system property set then use that. <br>
     *  else if WNHOME system property set then use WNHOME/dict <br>
     *  otherwise use WNConsts.DEFAULTPATH.<p>
     *
     * @since    0.1.0
     */
    public void doInit() {
        try {
            String searchDir;
            String tmpBuf;

            // Find base directory for database.  If set, use WNSEARCHDIR.
            // If not set, check for WNHOME/dict, otherwise use DEFAULTPATH.

            String envhome = System.getProperty("WNHOME");
            String env = System.getProperty("WNSEARCHDIR");
            if (env != null) {
                searchDir = env;
            } else if (envhome != null) {
                searchDir = env + WNConsts.DICTDIR;
            } else {
                searchDir = WNConsts.DEFAULTPATH;
            }

            for (int i = 1; i < WNConsts.NUMPARTS + 1; i++) {
                tmpBuf = searchDir + WNConsts.DATAFILE + WNGlobal.partNames[i];
                try {
                    wnRtl.dataFiles[i] = new RandomAccessFile(new File(tmpBuf), "r");
                } catch (java.io.FileNotFoundException e) {
                    String msg = "WordNet library error: Can't open datafile(" + tmpBuf + ")";
                    wnRtl.displayMessage(msg);
                    throw e;
                }
                tmpBuf = searchDir + WNConsts.INDEXFILE + WNGlobal.partNames[i];
                try {
                    wnRtl.indexFiles[i] = new RandomAccessFile(new File(tmpBuf), "r");
                } catch (java.io.FileNotFoundException e) {
                    String msg = "WordNet library error: Can't open indexfile(" + tmpBuf + ")";
                    wnRtl.displayMessage(msg);
                    throw e;
                }
            }

            // This file isn't used by the library and doesn't have to
            // be present.  No error is reported if the open fails.

            tmpBuf = searchDir + WNConsts.SENSEIDXFILE;
            wnRtl.senseFile = new RandomAccessFile(new File(tmpBuf), "r");

            // If this file isn't present, the runtime code will skip printint out
            // the number of times each sense was tagged.

            tmpBuf = searchDir + WNConsts.CNTLISTFILE;
            wnRtl.cntListFile = new RandomAccessFile(new File(tmpBuf), "r");

            if (WNConsts.WN1_6) {
                tmpBuf = searchDir + WNConsts.COUSINFILE;
                try {
                    wnRtl.cousinFile = new RandomAccessFile(new File(tmpBuf), "r");
                } catch (java.io.IOException e) {
                    String msg = "WordNet library error: Can't open cousin tops file(" + tmpBuf + ")";
                    wnRtl.displayMessage(msg);
                    e.printStackTrace();
                }
                tmpBuf = searchDir + WNConsts.COUSINEXCFILE;
                try {
                    wnRtl.cousinExcFile = new RandomAccessFile(new File(tmpBuf), "r");
                } catch (java.io.IOException e) {
                    String msg = "WordNet library error: Can't open exception file(" + tmpBuf + ")";
                    wnRtl.displayMessage(msg);
                    e.printStackTrace();
                }
            }

            // Verb example sentences only in version 1.6 or higher.

            if (WNGlobal.wnRelease.equals("1.6") || WNGlobal.wnRelease.equals("1.7")) {
                tmpBuf = searchDir + WNConsts.VRBSENTFILE;
                try {
                    wnRtl.verbSentenceFile = new RandomAccessFile(new File(tmpBuf), "r");
                } catch (java.io.IOException e) {
                    String msg = "WordNet library error: Can't open verb example sentence file(" + tmpBuf + ")";
                    wnRtl.displayMessage(msg);
                    throw e;
                }

                tmpBuf = searchDir + WNConsts.VRBIDXFILE;
                try {
                    wnRtl.verbSentenceIndexFile = new RandomAccessFile(new File(tmpBuf), "r");
                } catch (java.io.IOException e) {
                    String msg = "WordNet library error: Can't open verb example sentence index file(" + tmpBuf + ")";
                    wnRtl.displayMessage(msg);
                    throw e;
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }


    /**
     *  Reconstruct synset from synset pointer and return as String.
     *
     * @param  synptr  Data to format.
     * @param  defn    if <tt>true</tt> append definition within '(' .. ')'.
     * @return         String representation of the SynSet.
     * @since          0.1.0
     */
    public String FmtSynset(SynSet synptr, boolean defn) {
        int i;
        StringBuffer sb = new StringBuffer();

        if (wnRtl.fileInfoFlag) {
            sb.append("<").append(WNGlobal.lexFiles[synptr.fnum]).append("> ");
        }

        sb.append("{ ");
        for (i = 0; i < (synptr.wcount - 1); i++) {
            sb.append(synptr.words[i]).append(", ");
        }

        sb.append(synptr.words[i]);

        if (defn && synptr.defn != null) {
            sb.append(" (").append(synptr.defn).append(") ");
        }

        sb.append(" }");
        return sb.toString();
    }


    /**
     *  Convert WordNet sense number passed of Index entry to sense key.
     *
     * @param  idx    Contains offset information for senses.
     * @param  sense  To get sense number for.
     * @return        Encoded sense string.
     * @since         0.1.0
     */
    public String WNSnsToStr(Index idx, int sense) {
        int j;
        int pos = getpos(idx.pos.charAt(0));
        SynSet sptr = searcher.readSynset(pos, idx.offset[sense - 1], "");
        int sstype = getsstype(sptr.pos.charAt(0));
        if (sstype == WNConsts.SATELLITE) {
            for (j = 0; j < sptr.pointers.length; j++) {
                if (sptr.pointers[j].ptrType == WNConsts.SIMPTR) {
                    SynSet adjss = searcher.readSynset(sptr.pointers[j].ppos, sptr.pointers[j].ptroff, "");
                    sptr.headWord = adjss.words[0];
                    sptr.headSense = adjss.lexid[0];
                    break;
                }
            }
        }

        for (j = 0; j < sptr.wcount; j++) {
            String lowerword = sptr.words[j];
            lowerword = strToLower(lowerword);
            if (lowerword.equals(idx.word)) {
                break;
            }
        }

        if (j == sptr.wcount) {
            return null;
        }

        String sptrFnum = (sptr.fnum < 10) ? "0" + sptr.fnum : "" + sptr.fnum;
        String sptrLexid = (sptr.lexid[j] < 10) ? "0" + sptr.lexid[j] : "" + sptr.lexid[j];

        if (sstype == WNConsts.SATELLITE) {
            return idx.word + "%" + WNConsts.SATELLITE + ":" + sptrFnum + ":" + sptrLexid
                     + ":" + sptr.headWord + ":" + sptr.headSense;
        } else {
            return idx.word + "%" + pos + ":" + sptrFnum + ":" + sptrLexid + "::";
        }
    }


    /**
     *  Default message display (does nothing).
     *
     * @param  msg  Message to display.
     * @return      -1.
     * @since       0.1.0
     */
    public int defaultDisplayMessage(String msg) {
        return -1;
    }


    /**
     *  Count the number of underscore or space (or separator) separated words in a string.
     *
     * @param  s          Count words in this String.
     * @param  separator  Additional separator.
     * @return            number of words in s.
     * @since             0.1.0
     */

    public static int countWords(String s, char separator) {
        int wordCount = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == separator || c == ' ' || c == '_') {
                wordCount++;
                while (i < s.length() && (c == separator || c == ' ' || c == '_')) {
                    c = s.charAt(i++);
                }
            }
        }
        return (++wordCount);
    }


    /**
     *  Convert string to lower case remove trailing adjective marker if found.
     *
     * @param  str  String to convert
     * @return      lower case version of str (up to first '(' ).
     * @since       0.1.0
     */

    public static String strToLower(String str) {
        int x = str.indexOf('(');
        if (x > 0) {
            str = str.substring(0, x);
        }
        str = str.toLowerCase();

        return str;
    }


    /**
     *  Pass in string for POS, return corresponding integer value.<p>
     *
     * "noun" = WNConsts.NOUN<br>
     * "verb" = WNConsts.VERB<br>
     * "adj" = WNConsts.ADJ<br>
     * "adv" = WNConsts.ADV<br>
     *
     * @param  str  Part of speech.
     * @return      Part of speech.
     * @since       0.1.0
     */

    public static int strToPos(String str) {
        if (str.equals("noun")) {
            return (WNConsts.NOUN);
        } else if (str.equals("verb")) {
            return (WNConsts.VERB);
        } else if (str.equals("adj")) {
            return (WNConsts.ADJ);
        } else if (str.equals("adv")) {
            return (WNConsts.ADV);
        } else {
            return (-1);
        }
    }


    /**
     *  Split the string into an array of strings based on the delimitters
     *  delim.
     *
     * @param  s      String to split.
     * @param  delim  Delimitters.
     * @return        s split by delimitters.
     * @since         0.1.0
     */
    public static String[] split(String s, String delim) {
        java.util.StringTokenizer st = new java.util.StringTokenizer(s, delim);
        String[] toks = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            toks[i] = st.nextToken();
            i++;
        }

        return toks;
    }
}

