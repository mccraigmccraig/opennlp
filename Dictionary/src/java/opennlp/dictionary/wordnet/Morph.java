/**
 * 
 * morph.c - WordNet search code morphology functions
 * 
 */

package opennlp.dictionary.wordnet;

import java.io.File;
import java.io.RandomAccessFile;

public class Morph {
   private static String Id = "$Id: Morph.java,v 1.1 2002/03/20 20:24:17 mratkinson Exp $";

   private static String sufx[] ={ 
        "s", "ses", "xes", "zes", "ches", "shes", "men",   // Noun suffixes.
        "s", "ies", "es", "es", "ed", "ed", "ing", "ing",  // Verb suffixes.
        "er", "est", "er", "est"                           // Adjective suffixes.
    };
    
    private static String addr[] ={ 
        
        "", "s", "x", "z", "ch", "sh", "man",  // Noun endings.
        "", "y", "e", "", "e", "", "e", "",    // Verb endings.
        "", "", "e", "e"                        // Adjective endings.
    };

    private static int[] offsets = { 0, 0, 7, 15 };
    private static int[] cnts = { 0, 7, 8, 4 };

    private static final int NUMPREPS	= 15;

    private static class Prepositions {
        public String str;
        public Prepositions(String prep) {
            str = prep;
        }
    }

    private static Prepositions[] prepositions = {
        new Prepositions("to"),
        new Prepositions("at"),
        new Prepositions("of"),
        new Prepositions("on"),
        new Prepositions("off"),
        new Prepositions("in"),
        new Prepositions("out"),
        new Prepositions("up"),
        new Prepositions("down"),
        new Prepositions("from"),
        new Prepositions("with"),
        new Prepositions("into"),
        new Prepositions("for"),
        new Prepositions("about"),
        new Prepositions("between")
    };
    
    private RandomAccessFile[] exc_fps = new RandomAccessFile[WNConsts.NUMPARTS + 1];
    


    private boolean done = false;
    private int openerr = 0;
    private BinSearch binSearcher;
    private Search searcher;
    private WNrtl wnRtl;
    
    public Morph(BinSearch binSearcher, Search searcher, WNrtl wnRtl) {
       this.binSearcher= binSearcher;
       this.searcher= searcher;
       this.wnRtl= wnRtl;
    }
    
    
   /** Open exception list files. */
    public int morphinit() {
        if (!done) {
            if (wnRtl.OpenDB) {		// make sure WN database files are open.
                openerr = do_init();
                if (openerr==0) { 
                    done = true;
                }
            } else {
                openerr = -1;
            }
        }
        return openerr;
    }

    
    /** Close exception list files and reopen. */
    public int re_morphinit() {
        for (int i = 1; i <= WNConsts.NUMPARTS; i++) {
            if (exc_fps[i] != null) {
                try {
                   exc_fps[i].close();
                   exc_fps[i] = null;
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }
    
        return (wnRtl.OpenDB) ? do_init() : -1;
    }

    private int do_init() {
        int openerr = 0;
        String env;
        String searchdir;
    
        // Find base directory for database.  If set, use WNSEARCHDIR.
        //   If not set, check for WNHOME/dict, otherwise use DEFAULTPATH.
    
        if ((env = System.getProperty("WNSEARCHDIR")) != null) {
                searchdir = env;
        } else if ((env = System.getProperty("WNHOME")) != null) {
                searchdir = env+"/dict";
        } else {
                searchdir= WNConsts.DEFAULTPATH;
        }
    
        for (int i = 1; i <= WNConsts.NUMPARTS; i++) {
            String fname = searchdir+"/" +WNGlobal.partnames[i]+".exc";
            try {
                exc_fps[i] = new RandomAccessFile(new File(fname), "r");
            } catch (java.io.IOException e) {
                e.printStackTrace();
                String msgbuf="WordNet library error: Can't open exception file("+fname+")";
                WordNet.display_message(msgbuf);
                openerr = -1;
            }
        }
        return openerr;
    }

    private String searchstr, str;
    private int svcnt, svprep;

   /** Try to find baseform (lemma) of word or collocation in POS.
    *  Works like strtok() - first call is with string, subsequent calls
    *  with null argument return additional baseforms for original string.
    */

public String morphstr(String origstr, int pos) {
    int prep;
    
    if (pos == WNConsts.SATELLITE) {
	pos = WNConsts.ADJ;
    }

    // First time through for this string.

    if (origstr != null) {
	// Assume string hasn't had spaces substitued with '_'.
	str = WNUtil.strtolower(origstr.replace(' ', '_'));
	StringBuffer searchstr_sb = new StringBuffer();
	int cnt = WNUtil.cntwords(str, '_');
	svprep = 0;

	// first try exception list.

        String tmp = exc_lookup(str, pos);
	if (tmp!=null && !tmp.equals(str)) {
	    svcnt = 1;		// force next time to pass null.
	    return tmp;
	}

	// Then try simply morph on original string.

	if (pos != WNConsts.VERB) {
            tmp = morphword(str, pos);
            if (tmp!=null && !tmp.equals(str)) {
	       return tmp;
            }
        }

	if (pos == WNConsts.VERB && cnt > 1 && (prep = hasprep(str, cnt))!=0 ) {
	    // assume we have a verb followed by a preposition.
	    svprep = prep;
	    return morphprep(str);
	} else {
            int end_idx1=0;
            int end_idx2=0;
            int st_idx = 0;
            int end_idx=0;
            String word;
            String append;
	    svcnt = cnt = WNUtil.cntwords(str, '-');
	    while (origstr!=null && (--cnt!=0) ) {
		end_idx1 = str.indexOf('_', st_idx);
		end_idx2 = str.indexOf('-', st_idx);
		if (end_idx1>=0 && end_idx2>=0) {
		    if (end_idx1 < end_idx2) {
			append = "_";
		    } else {
			append = "-";
		    }
		} else {
		    if (end_idx1>=0) {
			append = "_";
		    } else {
			append = "-";
		    }
		}	
		if (end_idx < 0) { return null; }		// shouldn't do this.
		word = str.substring(st_idx, end_idx - st_idx);
                tmp = morphword(word, pos);
		if (tmp!=null) {
		    searchstr_sb.append(tmp);
		} else {
		    searchstr_sb.append(word);
                }
		searchstr_sb.append(append);
		st_idx = end_idx + 1;
	    }
            
            word = str + st_idx;
	    tmp = morphword(word, pos);
	    if(tmp!=null)  {
		searchstr_sb.append(tmp);
	    } else {
		searchstr_sb.append(word);
            }
            searchstr= searchstr_sb.toString();
	    if (!searchstr.equals(str) && searcher.is_defined(searchstr,pos)!=0) {
		return searchstr;
	    } else {
		return null;
            }
	}
    } else {		          // subsequent call on string.
	if (svprep!=0) {		// if verb has preposition, no more morphs.
	    svprep = 0;
	    return null;
	} else if (svcnt == 1) {
	    return exc_lookup(null, pos);
	} else {
	    svcnt = 1;
            String tmp = exc_lookup(str, pos);
	    if (tmp!=null && !tmp.equals(str)) {
		return tmp;
	    } else {
		return null;
            }
	}
    }
}


    
    /** Try to find baseform (lemma) of individual word in POS */
    public String morphword(String word, int pos) {        
        if (word == null) {
            return null;
        }
    
        // first look for word on exception list.
        String tmp = exc_lookup(word, pos);
        if (tmp != null) {
            return tmp ;		// found it in exception list.
        }
    
        if (pos == WNConsts.ADV) {	// only use exception list for adverbs.
            return null;
        }
        
        StringBuffer tmpbuf = new StringBuffer();
        String end = "";
        
        if (pos == WNConsts.NOUN) {
            if (word.endsWith("ful")) {
                tmpbuf.append(word.substring(0, word.length()-3));
                end = "ful";
            } else {
                // check for noun ending with 'ss' or short words.
                if (word.endsWith("ss") || (word.length() <= 2)) {
                    return null;
                }
            }
        }
    
        // If not in exception list, try applying rules from tables.
    
        if (tmpbuf.length() == 0) {
           tmpbuf.append(word);
        }
    
        int offset = offsets[pos];
        int cnt = cnts[pos];
    
        for (int i = 0; i < cnt; i++){
            String start = tmpbuf.toString();
            String retval = wordbase(start, (i + offset));
            if (!retval.equals(start) && searcher.is_defined(retval, pos)!=0) {
                return retval + end;
            }
        }
        return null;
    }


    private static String wordbase(String word, int ender) {
        if (word.endsWith(sufx[ender])) {
            word = word.substring(0, word.length()-sufx[ender].length());
        }
        return word;
    }

    private static int hasprep(String s, int wdcnt) {
        // Find a preposition in the verb string and return its
        // corresponding word number.
        for (int wdnum = 2; wdnum <= wdcnt; wdnum++) {
            int pos = s.lastIndexOf('_');
            pos++;
            for (int i = 0; i < NUMPREPS; i++) {
                if (s.startsWith(prepositions[i].str) &&
                   (   s.charAt(prepositions[i].str.length()) == '_' ||
                       s.charAt(prepositions[i].str.length()) == '\0') ) {
                    return wdnum;
                   }
            }
        }
        return 0;
    }
 
    private String exc_lookup(String word, int pos) {
        String line = "";
        int  beglp;
        int  endlp=-1;
        String excline = "";
        boolean found = false;
    
        if (exc_fps[pos] == null) {
            return null;
        }
    
        // first time through load line from exception file.
        if (word != null){
            excline = binSearcher.bin_search(word, exc_fps[pos]);
            if (excline != null) {
                line = excline;
                endlp = line.indexOf(' ');
            } else {
                endlp = 0;
            }
        }

        if (endlp>=0 && (endlp<line.length()-1) && line.charAt(endlp+1) != ' '){
            beglp = endlp + 1;
            while (beglp<line.length() && line.charAt(beglp) == ' ') {
                beglp++;
            }
            endlp = beglp;
            while (endlp<line.length() && line.charAt(endlp) != ' '
                                       && line.charAt(endlp) != '\n')  {
                endlp++;
            }
            if (endlp != beglp){
                return line.substring(beglp,endlp);
            }
        }

        return null;
    }

    private String morphprep(String s) {
        int rest;
        int last;
        String exc_word;
        String lastwd = null;
        int offset, cnt;
        String word;
        String end="";
        String retval;
    
        // Assume that the verb is the first word in the phrase.  Strip it
        // off, check for validity, then try various morphs with the
        // rest of the phrase tacked on, trying to find a match.
    
        rest = s.indexOf('_');
        last = s.lastIndexOf('_');
        if (rest != last) {		// more than 2 words.
            lastwd = morphword(s.substring(last + 1), WNConsts.NOUN);
            if (lastwd!=null) {
                end = s.substring(rest, last - rest + 1);
                end = end + lastwd;
            }
        }
        
        word = s.substring(0, rest);
    
        for (int i = 0; i < word.length(); i++) {
            if (!Character.isLetterOrDigit(word.charAt(i))) { return null; }
        }
    
        offset = offsets[WNConsts.VERB];
        cnt = cnts[WNConsts.VERB];
    
        // First try to find the verb in the exception list.
    
        if ( ((exc_word = exc_lookup(word, WNConsts.VERB))!=null) &&
            !exc_word.equals(word)) {
    
            retval = exc_word+rest;
            if (searcher.is_defined(retval, WNConsts.VERB)!=0) {
                return retval;
            } else if (lastwd!=null) {
                retval = exc_word+end;
                if ( (searcher.is_defined(retval, WNConsts.VERB))!=0) {
                    return retval;
                }
            }
        }
        
        for (int i = 0; i < cnt; i++) {
            exc_word = wordbase(word, (i + offset));
            if (exc_word!=null && !word.equals(exc_word)) { // ending is different.
    
                retval = exc_word + rest;
                if ((searcher.is_defined(retval, WNConsts.VERB))!=0) {
                    return retval;
                } else if (lastwd!=null) {
                    retval = exc_word + end;
                    if ((searcher.is_defined(retval, WNConsts.VERB))!=0) {
                        return retval;
                    }
                }
            }
        }
        retval = word + rest;
        if (!s.equals(retval)) {
            return retval;
        }
        if (lastwd!=null) {
            retval = word + end;
            if (!s.equals(retval)) {
                return retval;
            }
        }
        return null;
    }

}

