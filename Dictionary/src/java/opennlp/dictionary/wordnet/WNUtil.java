package opennlp.dictionary.wordnet;
/**

  wnutil.c - utility functions used by WordNet code

*/

import java.io.File;
import java.io.RandomAccessFile;


public class WNUtil {
    
    private BinSearch binSearcher;
    private Search searcher;
    private Morph morpher;
    private WNrtl wnRtl;
    
    public WNUtil(BinSearch binSearcher, Search searcher, WNrtl wnRtl) {
       this.binSearcher= binSearcher;
       this.searcher= searcher;
       this.wnRtl = wnRtl;
    }
       
    public void setMorph(Morph morpher) {
       this.morpher = morpher;
    }

    private static boolean done = false;

   /** Initialization function.
    */
    public void wninit() {
        if (!done) {
            String env = System.getProperty("WNDBVERSION");
            if (env==null) {
               env = "1.7";
            }
            WNGlobal.wnrelease = env;	// set release.

            do_init();	
            done = true;	
            wnRtl.OpenDB = true;
            morpher.morphinit();
        }
    }

   /** Re-initialization function.
    */
    public void re_wninit() {
    
        closefps();
        String env = System.getProperty("WNDBVERSION");
        if (env==null) {
            env = "1.7";
        }
        WNGlobal.wnrelease = env;	// set release.

        do_init();
        wnRtl.OpenDB = true;
        morpher.re_morphinit();
    }

    public void closefps() {
        try {
            if (wnRtl.OpenDB) {
                for (int i = 1; i < WNConsts.NUMPARTS + 1; i++) {
                    if (wnRtl.datafps[i] != null) {
                        wnRtl.datafps[i].close(); wnRtl.datafps[i] = null;
                    }
                    if (wnRtl.indexfps[i] != null) {
                        wnRtl.indexfps[i].close(); wnRtl.indexfps[i] = null;
                    }
                }
                if (wnRtl.sensefp != null) {
                    wnRtl.sensefp.close(); wnRtl.sensefp = null;
                }
                if (WNConsts.WN1_6) {
                    if (wnRtl.cousinfp != null) {
                        wnRtl.cousinfp.close(); wnRtl.cousinfp = null;
                    }
                    if (wnRtl.cousinexcfp != null) {
                        wnRtl.cousinexcfp.close(); wnRtl.cousinexcfp = null;
                    }
                }
                if (wnRtl.cntlistfp != null) {
                    wnRtl.cntlistfp.close(); wnRtl.cntlistfp = null;
                }
                if (wnRtl.vsentfilefp != null) {
                    wnRtl.vsentfilefp.close(); wnRtl.vsentfilefp = null;
                }
                if (wnRtl.vidxfilefp != null) {
                    wnRtl.vidxfilefp.close(); wnRtl.vidxfilefp = null;
                }
                wnRtl.OpenDB = false;
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void do_init() {
        try {
            String searchdir;
            String tmpbuf;

            // Find base directory for database.  If set, use WNSEARCHDIR.
            // If not set, check for WNHOME/dict, otherwise use DEFAULTPATH.
        
            String envhome = System.getProperty("WNHOME");
            String env = System.getProperty("WNSEARCHDIR");
            if (env != null) {
                searchdir = env;
            } else if (envhome != null) {
                searchdir = env+WNConsts.DICTDIR;
            } else {
                searchdir = WNConsts.DEFAULTPATH;
            }
        
            for (int i = 1; i < WNConsts.NUMPARTS + 1; i++) {
                tmpbuf = searchdir + WNConsts.DATAFILE + WNGlobal.partnames[i];
                try {
                    wnRtl.datafps[i] = new RandomAccessFile(new File(tmpbuf), "r");
                } catch (java.io.FileNotFoundException e) {
                    String msg = "WordNet library error: Can't open datafile("+tmpbuf+")";
                    wnRtl.display_message(msg);
                    throw e;
                }
                tmpbuf = searchdir+ WNConsts.INDEXFILE+ WNGlobal.partnames[i];
                try {
                   wnRtl.indexfps[i] = new RandomAccessFile(new File(tmpbuf), "r");
                } catch (java.io.FileNotFoundException e) {
                    String msg = "WordNet library error: Can't open indexfile("+tmpbuf+")";
                    wnRtl.display_message(msg);
                    throw e;
                }
            }
        
            // This file isn't used by the library and doesn't have to
            // be present.  No error is reported if the open fails.
        
            tmpbuf = searchdir+WNConsts.SENSEIDXFILE;
            wnRtl.sensefp = new RandomAccessFile(new File(tmpbuf), "r");
        
            // If this file isn't present, the runtime code will skip printint out
            // the number of times each sense was tagged.
        
            tmpbuf = searchdir+WNConsts.CNTLISTFILE;
            wnRtl.cntlistfp = new RandomAccessFile(new File(tmpbuf), "r");
        
            if (WNConsts.WN1_6) {
                tmpbuf = searchdir+WNConsts.COUSINFILE;
                try {
                    wnRtl.cousinfp = new RandomAccessFile(new File(tmpbuf), "r");
                } catch (java.io.IOException e) {
                    String msg = "WordNet library error: Can't open cousin tops file("+tmpbuf+")";
                    wnRtl.display_message(msg);
                    e.printStackTrace();
                }
                tmpbuf = searchdir+WNConsts.COUSINEXCFILE;
                try {
                   wnRtl.cousinexcfp = new RandomAccessFile(new File(tmpbuf), "r");
                } catch (java.io.IOException e) {
                    String msg = "WordNet library error: Can't open exception file("+tmpbuf+")";
                    wnRtl.display_message(msg);
                    e.printStackTrace();
                }
            }
        
            // Verb example sentences only in version 1.6 or higher.
        
            if (WNGlobal.wnrelease.equals("1.6") || WNGlobal.wnrelease.equals("1.7")) {
                tmpbuf = searchdir+WNConsts.VRBSENTFILE;
                try {
                    wnRtl.vsentfilefp = new RandomAccessFile(new File(tmpbuf), "r");
                } catch (java.io.IOException e) {
                    String msg = "WordNet library error: Can't open verb example sentence file("+tmpbuf+")";
                    wnRtl.display_message(msg);
                    throw e;
                }
        
                tmpbuf = searchdir+WNConsts.VRBIDXFILE;
                try {
                    wnRtl.vidxfilefp = new RandomAccessFile(new File(tmpbuf), "r");
                } catch (java.io.IOException e) {
                    String msg = "WordNet library error: Can't open verb example sentence index file("+tmpbuf+")";
                    wnRtl.display_message(msg);
                    throw e;
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    
    }

   /** Count the number of underscore or space separated words in a string.
    */

    public static int cntwords(String s, char separator) {
        int wdcnt = 0;
    
        for (int i=0; i<s.length(); i++) {
            char c = s.charAt(i);
            if (c == separator || c == ' ' || c == '_') {
                wdcnt++;
                while (i<s.length() && (c==separator || c==' ' || c=='_')) {
                    c = s.charAt(i++);
                }
            }
        }
        return (++wdcnt);
    }

    /** Convert string to lower case remove trailing adjective marker if found.
     */
    
    public static String strtolower(String str){
        String s = str.toLowerCase();
        int x = s.indexOf('(');
        if (x>0) {
            s = s.substring(0, x);
        }
    
        return s;
    }



    /** Return pointer code for pointer type characer passed. */
    
    public int getptrtype(String ptrstr) {
        for (int i = 1; i <= WNConsts.MAXPTR; i++) {
            if (ptrstr.equals(WNGlobal.ptrtyp[i])) {
                return i;
            }
        }
        return 0;
    }

    /** Return part of speech code for string passed. */
    
    public int getpos(char s) {
        switch (s) {
            case 'n':
                return(WNConsts.NOUN);
            case 'a':
            case 's':
                return(WNConsts.ADJ);
            case 'v':
                return(WNConsts.VERB);
            case 'r':
                return(WNConsts.ADV);
            default:
                String msg = "WordNet library error: unknown part of speech "+s+"";
                WordNet.display_message(msg);
                throw new Error(msg);
        }
    }

    /** Return synset type code for string passed. */
    
    public int getsstype(char s) {
        switch (s) {
            case 'n':
                return(WNConsts.NOUN);
            case 'a':
                return(WNConsts.ADJ);
            case 'v':
                return(WNConsts.VERB);
            case 's':
                return(WNConsts.SATELLITE);
            case 'r':
                return(WNConsts.ADV);
            default:
                String msg = "WordNet library error: unknown synset type "+s+"";
                WordNet.display_message(msg);
                throw new Error(msg);
        }
    }

    /** Pass in string for POS, return corresponding integer value. */
    
    public static int strToPos(String str) {
        if (str.equals("noun")) {
            return(WNConsts.NOUN);
        } else if (str.equals("verb")) {
            return(WNConsts.VERB);
        } else if (str.equals("adj")) {
            return(WNConsts.ADJ);
        } else if (str.equals("adv")) {
            return(WNConsts.ADV);
        } else {
            return(-1);
        }
    }

    private final static int MAX_TRIES = 5;
    
    /** Find string for 'searchstr' as it is in index file. */
    
    public String GetWNStr(String searchstr, int dbase) {
        String[] strings = new String[MAX_TRIES];
        int offset = 0;
        char c;
        int underscore = -1;
        int hyphen = -1;
        int period = -1;

        searchstr = searchstr.toLowerCase();
        
        if ((underscore = searchstr.indexOf('_'))>=0 &&
            (hyphen = searchstr.indexOf('-'))>=0 &&
            (period = searchstr.indexOf('.'))>=0) {
            return strings[0]=searchstr;
        }
    
        for (int i = 0; i < 3; i++) {
            strings[i]=searchstr;
        }
            
        StringBuffer sb3 = new StringBuffer();
        StringBuffer sb4 = new StringBuffer();
        if (underscore>=0) { strings[1]=strings[1].replace('_', '-'); }
        if (hyphen>=0) { strings[2]=strings[2].replace('-', '_'); }
        for (int i=0; i<searchstr.length(); i++){
            c = searchstr.charAt(i);
            if (c != '_' && c != '-')  {sb3.append(c); }
            if (c != '.')   {sb4.append(c); }
        }
        strings[3] = sb3.toString();
        strings[4] = sb4.toString();
            
        for (int i = 1; i < MAX_TRIES; i++) {
           if (strings[0].equals(strings[i])) {
               strings[i] = null;
           }
        }
            
        for (int i = (MAX_TRIES - 1); i >= 0; i--) {
            if (strings[i] != null)
            if (binSearcher.bin_search(strings[i], wnRtl.indexfps[dbase]) != null) {
                offset = i;
            }
        }
            
        return strings[offset];
    }

    /** Return synset for sense key passed. */
    
    public SynSet GetSynsetForSense(String sensekey) {
        int offset = GetDataOffset(sensekey);
    
        // Pass in sense key and return parsed sysnet structure.
    
        if (offset>0) {
            return(searcher.read_synset(GetPOS(sensekey),
                                   offset,
                                   GetWORD(sensekey)));
        } else {
            return null;
        }
    }

    /** Find offset of sense key in data file. */
    
    public int GetDataOffset(String sensekey) {
        // Pass in encoded sense string, return byte offset of corresponding
        // synset in data file.
    
        if (wnRtl.sensefp == null) {
            WordNet.display_message("WordNet library error: Sense index file not open");
            return 0;
        }
        String line = binSearcher.bin_search(sensekey, wnRtl.sensefp);
        if (line!=null) {
            int i=0;
            while (line.charAt(i++) != ' ') { }
            return Integer.parseInt(line.substring(i));
        } else {
            return 0;
        }
    }

    /** Find polysemy count for sense key passed. */
    public int GetPolyCount(String sensekey) {
        // Pass in encoded sense string and return polysemy count
        //   for word in corresponding POS.
    
        Index idx = searcher.index_lookup(GetWORD(sensekey), GetPOS(sensekey));
        if (idx != null) {
            return idx.sense_cnt;
            // free_index(idx);
        }
        return 0;
    }

    /** Return word part of sense key */
    public String GetWORD(String sensekey) {
        int i = 0;
        char c;
    
        // Pass in encoded sense string and return WORD.
        StringBuffer sb = new StringBuffer();
        while ( (c = sensekey.charAt(i++)) != '%') {
            sb.append(c);
        }
        return sb.toString();
    }

    /** Return POS code for sense key passed. */
    public int GetPOS(String sensekey) {
        // Pass in encoded sense string and return POS.
        StringBuffer sb = new StringBuffer();
        char c;
        int i=0;
        while ( (c = sensekey.charAt(i++)) != '%') { } // skip over WORD.
        while ( (c = sensekey.charAt(i++)) != ' ') {
            sb.append(c);
        }
        String word = sb.toString();
        int pos = Integer.parseInt(word);
        return (pos == WNConsts.SATELLITE) ? WNConsts.ADJ : pos;
    }

    /** Reconstruct synset from synset pointer and return ptr to buffer. */
    public String FmtSynset(SynSet synptr, boolean defn) {
        int i;
        StringBuffer sb = new StringBuffer();
    
        if (wnRtl.fileinfoflag) {
            sb.append("<").append(WNGlobal.lexfiles[synptr.fnum]).append("> ");
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

    /** Convert WordNet sense number passed of IndexPtr entry to sense key. */
    public String WNSnsToStr(Index idx, int sense) {
        int j;
        int pos = getpos(idx.pos.charAt(0));
        SynSet sptr = searcher.read_synset(pos, idx.offset[sense - 1], "");
        int sstype = getsstype(sptr.pos.charAt(0));
        if (sstype == WNConsts.SATELLITE) {
            for (j = 0; j < sptr.pointers.length; j++) {
                if (sptr.pointers[j].ptrtyp == WNConsts.SIMPTR) {
                    SynSet adjss = searcher.read_synset(sptr.pointers[j].ppos,sptr.pointers[j].ptroff,"");
                    sptr.headword = adjss.words[0];
                    sptr.headsense = adjss.lexid[0];
                    break;
                }
            }
        }
    
        for (j = 0; j < sptr.wcount; j++) {
            String lowerword = sptr.words[j];
            lowerword=strtolower(lowerword);
            if (lowerword.equals(idx.wd)) {
                break;
            }
        }
    
        if (j == sptr.wcount) {
            return null;
        }
    
        String sptrFnum = (sptr.fnum<10) ? "0"+sptr.fnum : ""+sptr.fnum;
        String sptrLexid = (sptr.lexid[j]<10) ? "0"+sptr.lexid[j] : ""+sptr.lexid[j];
        
        if (sstype == WNConsts.SATELLITE) {
            return idx.wd+"%"+WNConsts.SATELLITE+":"+sptrFnum+":"+sptrLexid
                       +":"+sptr.headword+":"+sptr.headsense;
        } else {
            return idx.wd+"%"+pos+":"+sptrFnum+":"+sptrLexid+"::";
        }
    }

    /** Search for string and/or baseform of word in database and return
     *  index structure for word if found in database.
     */
    public Index GetValidIndexPointer(String word, int pos) {
        Index idx = searcher.getindex(word, pos);
    
        if (idx == null) {
            String morphword = morpher.morphstr(word, pos);
            if (morphword != null) {
                while (morphword != null) {
                    idx = searcher.getindex(morphword, pos);
                    if (idx != null) { break; }
                    morphword = morpher.morphstr(null, pos);
                }
            }
        }
        return idx;
    }

    /** Return sense number in database for word and lexsn passed. */
    
    public int GetWNSense(String word, String lexsn) {
        String buf = word+"%"+lexsn;           // create sensekey.
        SnsIndex snsidx = GetSenseIndex(buf);
        if (snsidx != null) {
            return snsidx.wnsense;
        } else {
            return(0);
        }
    }

    /** Return parsed sense index entry for sense key passed. */
    
    public SnsIndex GetSenseIndex(String sensekey) {
        SnsIndex snsidx = null;
    
        String line = binSearcher.bin_search(sensekey, wnRtl.sensefp);
        if (line != null) {
            snsidx = new SnsIndex();
            String[] str = split(line," ");
            snsidx.sensekey = str[0];
            snsidx.loc = Integer.parseInt(str[1]);
            snsidx.wnsense = Integer.parseInt(str[2]);
            snsidx.tag_cnt = Integer.parseInt(str[3]);
            // Parse out word from sensekey to make things easier for caller.
            snsidx.word = GetWORD(snsidx.sensekey);
            if (snsidx.word==null) {
                throw new NullPointerException("GetWORD returned a new value");
            }
            snsidx.nextsi = null;
        }
        return snsidx;
    }

    /** Return number of times sense is tagged */
    
    public int GetTagcnt(Index idx, int sense)  {
        int snum, cnt = 0;

        if (wnRtl.cntlistfp != null) {
            String sensekey = WNSnsToStr(idx, sense);
            String line = binSearcher.bin_search(sensekey, wnRtl.cntlistfp);
            if (line != null) {
                String[] str = split(line," ");
                cnt = Integer.parseInt(str[2]);
            }
        }
    
        return cnt;
    }


    public int default_display_message(String msg) {
        return -1;
    }

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

