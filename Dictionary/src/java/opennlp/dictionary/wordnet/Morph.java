package opennlp.dictionary.wordnet;

import java.io.File;
import java.io.RandomAccessFile;

/**
 *  WordNet search code morphology functions.<p>
 *
 *  This class was created by heavily modifying the WordNet 1.7 code src/lib/morph.c
 *
 * @author     Mike Atkinson (mratkinson)
 * @since      0.1.0
 * @created    20 March 2002
 * @version    "$Id: Morph.java,v 1.2 2002/03/26 19:11:22 mratkinson Exp $";
 */
public class Morph {

    private RandomAccessFile[] exceptionFiles = new RandomAccessFile[WNConsts.NUMPARTS + 1];

    private boolean done = false;
    private boolean openerr = false;
    private BinSearch binSearcher;
    private Search searcher;
    private WNrtl wnRtl;

    private String searchstr, str;
    private int svcnt, svprep;

    private static String sufx[] = {
            "s", "ses", "xes", "zes", "ches", "shes", "men",  // Noun suffixes.
            "s", "ies", "es", "es", "ed", "ed", "ing", "ing", // Verb suffixes.
            "er", "est", "er", "est"                          // Adjective suffixes.
    };

    private static String addr[] = {
            "", "s", "x", "z", "ch", "sh", "man", // Noun endings.
            "", "y", "e", "", "e", "", "e", "",   // Verb endings.
            "", "", "e", "e"                      // Adjective endings.
    };

    private static int[] offsets = {0, 0, 7, 15};
    private static int[] cnts = {0, 7, 8, 4};

    private final static int NUMPREPS = 15;

    private static String[] prepositions = {
            "to", "at", "of", "on", "off", "in", "out", "up",
            "down", "from", "with", "into", "for", "about", "between"
            };


    /**
     *  Constructor for the Morph object
     *
     * @param  binSearcher  Used to perform binary searches.
     * @param  searcher     Used to find if part of a word is defined.
     * @param  wnRtl        Holds whether the database files are open.
     * @since               0.1.0
     */
    public Morph(BinSearch binSearcher, Search searcher, WNrtl wnRtl) {
        this.binSearcher = binSearcher;
        this.searcher = searcher;
        this.wnRtl = wnRtl;
    }


    /**
     *  Open exception list files.
     *
     * @return    <tt>true</tt> if the database exception list files have been opened
     *      correctly.
     * @since     0.1.0
     */
    public boolean morphInit() {
        if (!done) {
            if (wnRtl.OpenDB) {// make sure WN database files are open.
                openerr = doInit();
                if (!openerr) {
                    done = true;
                }
            } else {
                openerr = true;
            }
        }
        return !openerr;
    }


    /**
     *  Close exception list files and reopen.
     *
     * @return    <tt>true</tt> if the database exception list files have been opened
     *      correctly.
     * @since     0.1.0
     */
    public boolean re_morphInit() {
        for (int i = 1; i <= WNConsts.NUMPARTS; i++) {
            if (exceptionFiles[i] != null) {
                try {
                    exceptionFiles[i].close();
                    exceptionFiles[i] = null;
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return (wnRtl.OpenDB) ? doInit() : false;
    }


    /**
     *  Try to find baseform (lemma) of word or collocation in POS.<p>
     *
     *  Works like strtok() - first call is with string, subsequent calls with null
     *  argument return additional baseforms for original string.<p>
     *
     *  The Parts of Speech are:
     *  <ol>
     *    <li> noun</li>
     *    <li> verb</li>
     *    <li> adj</li>
     *    <li> adv</li>
     *  </ol>
     *
     *
     * @param  origstr  Word (or collocation) to find the baseform of (or null for
     *      subsequent calls).
     * @param  pos      Part of Speech
     * @return          The baseform for the word given the part of speech.
     * @since           0.1.0
     */

    public String morphStr(String origstr, int pos) {
        int prep;

        if (pos == WNConsts.SATELLITE) {
            pos = WNConsts.ADJ;
        }

        // First time through for this string.

        if (origstr != null) {
            // Assume string hasn't had spaces substitued with '_'.
            str = WNUtil.strToLower(origstr.replace(' ', '_'));
            StringBuffer searchstr_sb = new StringBuffer();
            int cnt = WNUtil.countWords(str, '_');
            svprep = 0;

            // first try exception list.

            String tmp = excLookup(str, pos);
            if (tmp != null && !tmp.equals(str)) {
                svcnt = 1;// force next time to pass null.
                return tmp;
            }

            // Then try simply morph on original string.

            if (pos != WNConsts.VERB) {
                tmp = morphWord(str, pos);
                if (tmp != null && !tmp.equals(str)) {
                    return tmp;
                }
            }

            if (pos == WNConsts.VERB && cnt > 1 && (prep = hasPrep(str, cnt)) != 0) {
                // assume we have a verb followed by a preposition.
                svprep = prep;
                return morphPrep(str);
            } else {
                int end_idx1 = 0;
                int end_idx2 = 0;
                int st_idx = 0;
                int end_idx = 0;
                String word;
                String append;
                svcnt = cnt = WNUtil.countWords(str, '-');
                while (origstr != null && (--cnt != 0)) {
                    end_idx1 = str.indexOf('_', st_idx);
                    end_idx2 = str.indexOf('-', st_idx);
                    if (end_idx1 >= 0 && end_idx2 >= 0) {
                        if (end_idx1 < end_idx2) {
                            append = "_";
                        } else {
                            append = "-";
                        }
                    } else {
                        if (end_idx1 >= 0) {
                            append = "_";
                        } else {
                            append = "-";
                        }
                    }
                    if (end_idx < 0) {
                        return null;
                    }// shouldn't do this.
                    word = str.substring(st_idx, end_idx - st_idx);
                    tmp = morphWord(word, pos);
                    if (tmp != null) {
                        searchstr_sb.append(tmp);
                    } else {
                        searchstr_sb.append(word);
                    }
                    searchstr_sb.append(append);
                    st_idx = end_idx + 1;
                }

                word = str + st_idx;
                tmp = morphWord(word, pos);
                if (tmp != null) {
                    searchstr_sb.append(tmp);
                } else {
                    searchstr_sb.append(word);
                }
                searchstr = searchstr_sb.toString();
                if (!searchstr.equals(str) && searcher.isDefined(searchstr, pos) != 0) {
                    return searchstr;
                } else {
                    return null;
                }
            }
        } else {// subsequent call on string.
            if (svprep != 0) {// if verb has preposition, no more morphs.
                svprep = 0;
                return null;
            } else if (svcnt == 1) {
                return excLookup(null, pos);
            } else {
                svcnt = 1;
                String tmp = excLookup(str, pos);
                if (tmp != null && !tmp.equals(str)) {
                    return tmp;
                } else {
                    return null;
                }
            }
        }
    }



    /**
     *  Try to find baseform (lemma) of individual word in POS.<p>
     *
     *  The Parts of Speech are:
     *  <ol>
     *    <li> noun</li>
     *    <li> verb</li>
     *    <li> adj</li>
     *    <li> adv</li>
     *  </ol>
     *
     *
     * @param  word  Word to find the baseform of (or null for subsequent calls).
     * @param  pos   Part of Speech
     * @return       The baseform for the word given the part of speech.
     * @since        0.1.0
     */
    public String morphWord(String word, int pos) {
        if (word == null) {
            return null;
        }

        // first look for word on exception list.
        String tmp = excLookup(word, pos);
        if (tmp != null) {
            return tmp;// found it in exception list.
        }

        if (pos == WNConsts.ADV) {// only use exception list for adverbs.
            return null;
        }

        StringBuffer tmpBuf = new StringBuffer();
        String end = "";

        if (pos == WNConsts.NOUN) {
            if (word.endsWith("ful")) {
                tmpBuf.append(word.substring(0, word.length() - 3));
                end = "ful";
            } else {
                // check for noun ending with 'ss' or short words.
                if (word.endsWith("ss") || (word.length() <= 2)) {
                    return null;
                }
            }
        }

        // If not in exception list, try applying rules from tables.

        if (tmpBuf.length() == 0) {
            tmpBuf.append(word);
        }

        int offset = offsets[pos];
        int cnt = cnts[pos];

        for (int i = 0; i < cnt; i++) {
            String start = tmpBuf.toString();
            String retval = wordBase(start, (i + offset));
            if (!retval.equals(start) && searcher.isDefined(retval, pos) != 0) {
                return retval + end;
            }
        }
        return null;
    }


    /**
     *  Open the exception list files "adj.exc", "adv.exc", "noun.exc" and "verb.exc".
     *  <p>
     *
     *  To find the base directory for the database: <br>
     *  If WNSEARCHDIR system property set then use that. <br>
     *  else if WNHOME system property set then use WNHOME/dict <br>
     *  otherwise use WNConsts.DEFAULTPATH.<p>
     *
     *
     *
     * @return    <tt>true</tt> if the database exception list files have been opened
     *      correctly.
     * @since     0.1.0
     */
    private boolean doInit() {
        String env;
        String searchDir;

        // Find base directory for database.  If set, use WNSEARCHDIR.
        //   If not set, check for WNHOME/dict, otherwise use DEFAULTPATH.

        if ((env = System.getProperty("WNSEARCHDIR")) != null) {
            searchDir = env;
        } else if ((env = System.getProperty("WNHOME")) != null) {
            searchDir = env + "/dict";
        } else {
            searchDir = WNConsts.DEFAULTPATH;
        }

        for (int i = 1; i <= WNConsts.NUMPARTS; i++) {
            String fname = searchDir + "/" + WNGlobal.partNames[i] + ".exc";
            try {
                exceptionFiles[i] = new RandomAccessFile(new File(fname), "r");
            } catch (java.io.IOException e) {
                e.printStackTrace();
                String msgbuf = "WordNet library error: Can't open exception file(" + fname + ")";
                WordNet.displayMessage(msgbuf);
                openerr = true;
                return false;
            }
        }
        return true;
    }


    /**
     *  Look up the word in the exception list file for the part of speech given
     *  to get the baseform (lemma) of the word.
     *
     * @param  word  To look up.
     * @param  pos   Part of Speech.
     * @return       Baseform of the word.
     * @since        0.1.0
     */
    private String excLookup(String word, int pos) {
        String line = "";
        int beglp;
        int endlp = -1;
        String excline = "";
        boolean found = false;

        if (exceptionFiles[pos] == null) {
            return null;
        }

        // first time through load line from exception file.
        if (word != null) {
            excline = binSearcher.binSearch(word, exceptionFiles[pos]);
            if (excline != null) {
                line = excline;
                endlp = line.indexOf(' ');
            } else {
                endlp = 0;
            }
        }

        if (endlp >= 0 && (endlp < line.length() - 1) && line.charAt(endlp + 1) != ' ') {
            beglp = endlp + 1;
            while (beglp < line.length() && line.charAt(beglp) == ' ') {
                beglp++;
            }
            endlp = beglp;
            while (endlp < line.length() && line.charAt(endlp) != ' '
                     && line.charAt(endlp) != '\n') {
                endlp++;
            }
            if (endlp != beglp) {
                return line.substring(beglp, endlp);
            }
        }

        return null;
    }


    /**
     *  morph a verb (first word) followed by a preposition (second or later words).
     *
     * @param  s  Collocation to morph.
     * @return    New Collocation in baseform.
     * @since     0.1.0
     */
    private String morphPrep(String s) {
        int rest;
        int last;
        String excWord;
        String lastWord = null;
        int offset;
        int cnt;
        String word;
        String end = "";
        String retval;

        // Assume that the verb is the first word in the phrase.  Strip it
        // off, check for validity, then try various morphs with the
        // rest of the phrase tacked on, trying to find a match.

        rest = s.indexOf('_');
        last = s.lastIndexOf('_');
        if (rest != last) {// more than 2 words.
            lastWord = morphWord(s.substring(last + 1), WNConsts.NOUN);
            if (lastWord != null) {
                end = s.substring(rest, last - rest + 1);
                end = end + lastWord;
            }
        }

        word = s.substring(0, rest);

        for (int i = 0; i < word.length(); i++) {
            if (!Character.isLetterOrDigit(word.charAt(i))) {
                return null;
            }
        }

        offset = offsets[WNConsts.VERB];
        cnt = cnts[WNConsts.VERB];

        // First try to find the verb in the exception list.

        if (((excWord = excLookup(word, WNConsts.VERB)) != null) &&
                !excWord.equals(word)) {

            retval = excWord + rest;
            if (searcher.isDefined(retval, WNConsts.VERB) != 0) {
                return retval;
            } else if (lastWord != null) {
                retval = excWord + end;
                if ((searcher.isDefined(retval, WNConsts.VERB)) != 0) {
                    return retval;
                }
            }
        }

        for (int i = 0; i < cnt; i++) {
            excWord = wordBase(word, (i + offset));
            if (excWord != null && !word.equals(excWord)) {// ending is different.

                retval = excWord + rest;
                if ((searcher.isDefined(retval, WNConsts.VERB)) != 0) {
                    return retval;
                } else if (lastWord != null) {
                    retval = excWord + end;
                    if ((searcher.isDefined(retval, WNConsts.VERB)) != 0) {
                        return retval;
                    }
                }
            }
        }
        retval = word + rest;
        if (!s.equals(retval)) {
            return retval;
        }
        if (lastWord != null) {
            retval = word + end;
            if (!s.equals(retval)) {
                return retval;
            }
        }
        return null;
    }


    /**
     *  Remove the suffix from the word (if it is present).
     *
     * @param  word   To have suffix removed from.
     * @param  ender  Index into sufx array
     * @return        Word minus the suffix (or original word if word does not end
     *      with that suffix).
     * @since         0.1.0
     */
    private static String wordBase(String word, int ender) {
        if (word.endsWith(sufx[ender])) {
            word = word.substring(0, word.length() - sufx[ender].length());
        }
        return word;
    }


    /**
     *  Find a preposition in the verb string and return its corresponding word
     *  number.
     *
     * @param  s      Collocation
     * @param  wordcnt  Number of words in the collocation
     * @return        Number of the word in the collocation which is a preposition,
     *      or 0 if no preposition found.
     * @since         0.1.0
     */
    private static int hasPrep(String s, int wordcnt) {
        for (int wordnum = 2; wordnum <= wordcnt; wordnum++) {
            s = s.substring(s.lastIndexOf('_') + 1);
            for (int i = 0; i < NUMPREPS; i++) {
                if (s.startsWith(prepositions[i]) &&
                        (s.charAt(prepositions[i].length()) == '_' ||
                        s.length() == prepositions[i].length())) {
                    return wordnum;
                }
            }
        }
        return 0;
    }

}

