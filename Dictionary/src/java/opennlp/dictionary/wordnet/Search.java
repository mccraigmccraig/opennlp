/**
  
  search.c - WordNet library of search code
  
*/
package opennlp.dictionary.wordnet;

import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class Search {
   private static String Id = "$Id: Search.java,v 1.3 2002/03/26 19:09:11 mratkinson Exp $";

   // For adjectives, indicates synset type.
   
   public final static int DONT_KNOW	= 0;
   public final static int DIRECT_ANT	= 1;	// direct antonyms (cluster head).
   public final static int INDIRECT_ANT= 2;	// indrect antonyms (similar).
   public final static int PERTAINYM	= 3;	// no antonyms or similars (pertainyms).
   
   // Flags for printSynset().
   
   public final static int ALLWORDS	   = 0;	// print all words.
   public final static int SKIP_ANTS	= 0;	// skip printing antonyms in printSynset().
   public final static int PRINT_ANTS	= 1;	// print antonyms in printSynset().
   public final static int SKIP_MARKER	= 0;	// skip printing adjective marker.
   public final static int PRINT_MARKER= 1;	// print adjective marker.
   
   // Trace types used by printSpaces() to determine print sytle.
   
   public final static int TRACEP		= 1;	// tracePtrs.
   public final static int TRACEC		= 2;	// traceCoords().
   public final static int TRACEI		= 3;	// traceInherit().
   
   public final static int DEFON       = 1;
   public final static int DEFOFF      = 0;
   
   
   // Static variables.
   
   public  boolean prflag;
   public  int sense, prlexid;
   public  boolean overflag = false;     // set when output buffer overflows.
   public  StringBuffer  searchBuffer = new StringBuffer(1024*64);
   public  int lastholomero;	       // keep track of last holo/meronym printed.
   public final static int TMPBUFSIZE =1024*10;
   public  String tmpBuf;	               // general purpose printing buffer.
   public  String wordbuf;	               // general purpose word buffer.
   public  String  msgbuf;	       // buffer for constructing error messages.
   public  int adjMarker;
   private BinSearch binSearcher;
   private WNUtil wnUtil;
   private WNrtl wnRtl;
   

   public Search(BinSearch binSearcher) {
       this.binSearcher=binSearcher;
   }
   public void setWNUtil(WNUtil wnUtil, WNrtl wnRtl) {
       this.wnUtil = wnUtil;
       this.wnRtl = wnRtl;
   }
   
   /** Find word in index file and return parsed entry in data structure.
    *  Input word must be exact match of string in database.
    */
   
   public Index indexLookup(String word, int dbase) {
      Index idx = null;
      RandomAccessFile fp;
      String line;
      
      if ((fp = wnRtl.indexFiles[dbase]) == null) {
         msgbuf="WordNet library error: "+WNGlobal.partNames[dbase]+" indexfile not open";
         WordNet.displayMessage(msgbuf);
         return null;
      }
      
      if ((line = binSearcher.binSearch(word, fp)) != null) {
         idx = parseIndex( binSearcher.getLastBinSearchOffset(), dbase, line);
      } 
      return idx;
   }

   /** This function parses an entry from an index file into an Index data
    * structure. It takes the byte offset and file number, and optionally the
    * line. If the line is null, parseIndex will get the line from the file.
    * If the line is non-null, parseIndex won't look at the file, but it still
    * needs the dbase and offset parameters to be set, so it can store them in
    * the Index struct.
    */

   public Index parseIndex(long offset, int dbase, String  line) {
       String[] splitLine = wnUtil.split(line," \n");
       //for (int i=0; i<splitLine.length; i++) {
       //    System.out.println("splitLine["+i+"]="+splitLine[i]);
       //}
       Index idx = null;
   
       if (line==null) {
         line = binSearcher.read_index( offset, wnRtl.indexFiles[dbase] );
       }
       
       idx = new Index();
   
       // set offset of entry in index file
       idx.indexOffset = offset;
       
       idx.word="";
       idx.pos="";
       idx.taggedCount = 0;
       idx.senseCount=0;
       idx.offset=null;
       idx.ptruse=null;
       
       // get the word
       
       int n=0;
       idx.word = splitLine[n++];
       
       // get the part of speech.
       idx.pos = splitLine[n++];
   
       // get the collins count.
       idx.senseCount = Integer.parseInt(splitLine[n++]);
       
       // get the number of pointers types.
       idx.ptruse = new int[Integer.parseInt(splitLine[n++])];
       
       if (idx.ptruse.length>0) {
          // get the pointers types.
          for (int j=0; j < idx.ptruse.length; j++) {
             idx.ptruse[j] = wnUtil.getPointerType(splitLine[n++]);
          }
       }
       
       // get the number of offsets.
       int off_cnt = Integer.parseInt(splitLine[n++]);
       
       if ("1.6".equals(WNGlobal.wnRelease) || "1.7".equals(WNGlobal.wnRelease)) {
          // get the number of senses that are tagged.
          idx.taggedCount = Integer.parseInt(splitLine[n++]);
       } else {
          idx.taggedCount = -1;
       }
       
       // make space for the offsets.
       idx.offset = new int[off_cnt];
       
       // get the offsets.
       for (int j=0; j<off_cnt; j++) {
          idx.offset[j] = Integer.parseInt(splitLine[n++]);
       }
       return idx;
   }

   /** 'smart' search of index file.  Find word in index file, trying different
    * techniques - replace hyphens with underscores, replace underscores with
    * hyphens, strip hyphens and underscores, strip periods.
    */

   public Index getIndex(String searchstr, int dbase) {
      Index idx;
      String[] strings = new String[WNConsts.MAX_FORMS]; // vector of search strings.
      Index[] offsets = new Index[WNConsts.MAX_FORMS];
      
      // This works like strrok(): if passed with a non-null string,
      // prepare vector of search strings and offsets.  If string
      // is null, look at current list of offsets and return next
      // one, or null if no more alternatives for this word.
      
      if (searchstr != null) {
      
          int offset = 0;
          wnUtil.strToLower(searchstr);
          for (int i = 0; i < WNConsts.MAX_FORMS; i++) {
             strings[i] = searchstr;
             offsets[i] = null;
          }
          
          strings[1].replace('_', '-');
          strings[2].replace('-', '_');
          
          // Remove all spaces and hyphens from last search string, then
          // all periods.
          StringBuffer sb3 = new StringBuffer();
          StringBuffer sb4 = new StringBuffer();

          for (int i = 0; i<searchstr.length(); i++) {
             char c = searchstr.charAt(i);
             if (c != '_' && c != '-') {
                sb3.append(c);
             }
             if (c != '.') {
                sb4.append(c);
             }
          }
          strings[3] = sb3.toString();
          strings[4] = sb4.toString();
          
          // Get offset of first entry.  Then eliminate duplicates
          // and get offsets of unique strings.
          
          offsets[0] = indexLookup(strings[0], dbase);
          
          for (int i = 1; i < WNConsts.MAX_FORMS; i++) {
             if (!(strings[0].equals(strings[i]))) {
                offsets[i] = indexLookup(strings[i], dbase);
             }
          }
      
          for (int i = offset; i < WNConsts.MAX_FORMS; i++) {
             if (offsets[i] != null) {
                offset = i + 1;
                return offsets[i];
             }
          }
      }
      
      return null;
   }
   

   /** Read synset from data file at byte offset passed and return parsed
    *  entry in data structure.
    */

   public SynSet readSynset(int dbase, long boffset, String word) {
       try {
            SynsetKey key = new SynsetKey(dbase, boffset, word);
            SynSet synset = (SynSet)synsetCache.get(key);
            if (synset != null) {
                return synset;
            }
            RandomAccessFile fp = wnRtl.dataFiles[dbase];
            
            if (fp == null) {
                msgbuf = "WordNet library error: "+WNGlobal.partNames[dbase]+" datafile not open";
                WordNet.displayMessage(msgbuf);
                return null;
            }
            
            fp.seek(boffset);	// position file to byte offset requested.
            
            synset = parseSynset(fp, dbase, word); // parse synset and return.
            synsetCache.put(key, synset);
            return synset;
       } catch (Exception e) {
           e.printStackTrace();
       }
       return new SynSet();
   }

   private Map synsetCache = new WordNetCache(10000);
   private final static class SynsetKey {
       private int dbase;
       private long boffset;
       private String word;
       public SynsetKey(int dbase, long boffset, String word) {
           this.dbase=dbase;
           this.boffset=boffset;
           this.word=word;
       }
       public int hashCode() {
           return dbase + (int)boffset + word.hashCode();
       }
       public boolean equals(Object o) {
           if (o instanceof SynsetKey) {
               SynsetKey rhs = (SynsetKey)o;
               return dbase==rhs.dbase && boffset==rhs.boffset && word.equals(word);
           }
           return false;
       }
   }
   /** Read synset at current byte offset in file and return parsed entry
    *  in data structure.
    */

   public SynSet parseSynset(RandomAccessFile fp, int dbase, String word) {
       try {
            String tbuf;
            String ptrtok;
            byte[] tmpptr = new byte[WNConsts.LINEBUF];
            int foundpert = 0;
            String wordnum;
        
            long loc = fp.getFilePointer();
            int size = Search.fgets(fp, tmpptr);
            if (size<=0) {
                return null;
            }
            //String line = new String(tmpptr, 0 , size);
            String line = new String(tmpptr, 0, 0 , size);
            
            SynSet synptr =  new SynSet();
            
            synptr.hereiam = 0;
            synptr.sstype = DONT_KNOW;
            synptr.fnum = 0;
            synptr.pos = "";
            synptr.wcount = 0;
            synptr.words = null;
            synptr.whichWord = 0;
            synptr.pointers=null;
            synptr.frames = null;
            synptr.defn = "";
            synptr.nextss = null;
            synptr.nextForm = null;
            synptr.searchType = -1;
            synptr.ptrList = null;
            synptr.headWord = null;
            synptr.headSense = 0;
            
            String[] splitLine = wnUtil.split(line," \n");
            int n=0;

            synptr.hereiam = Integer.parseInt(splitLine[n++]);
        
            // sanity check - make sure starting file offset matches first field
            if (synptr.hereiam != loc) {
                   msgbuf = "WordNet library error: no synset at location "+loc;
                   WordNet.displayMessage(msgbuf);
                   return null;
            }
            
            // looking at FNUM
            synptr.fnum = Integer.parseInt(splitLine[n++]);
            
            // looking at POS
            synptr.pos = splitLine[n++];
            if (wnUtil.getsstype(synptr.pos.charAt(0)) == WNConsts.SATELLITE) {
                    synptr.sstype = INDIRECT_ANT;
            }
            
            // looking at numwords
            synptr.wcount = Integer.parseInt(splitLine[n++], 16);
            
            synptr.words = new String[synptr.wcount];
            synptr.wnsns = new int[synptr.wcount];
            synptr.lexid = new int[synptr.wcount];
            
            for (int i = 0; i < synptr.wcount; i++) {
              synptr.words[i] = splitLine[n++];
              
              /// is this the word we're looking for? 
              if (word!=null && word.equals(wnUtil.strToLower(splitLine[n-1]))) {
                 synptr.whichWord = i+1;
              }
              synptr.lexid[i] = Integer.parseInt(splitLine[n++], 16);
           }
              
           // get the pointer count
           int ptrcount = Integer.parseInt(splitLine[n++]);
           
           // alloc storage for the pointers
           synptr.pointers = new SynSet.Pointer[ptrcount];
           if (ptrcount>0) {
              for (int i = 0; i < ptrcount; i++) {
               // get the pointer type
               synptr.pointers[i] = new SynSet.Pointer();
               synptr.pointers[i].ptrType = wnUtil.getPointerType(splitLine[n++]);
               // For adjectives, set the synset type if it has a direct
               // antonym
               if (dbase == WNConsts.ADJ && synptr.sstype == DONT_KNOW) {
                    if (synptr.pointers[i].ptrType == WNConsts.ANTPTR) {
                        synptr.sstype = DIRECT_ANT;
                    } else if (synptr.pointers[i].ptrType == WNConsts.PERTPTR) {
                        foundpert = 1;
                    }
               }
              
               // get the pointer offset
               synptr.pointers[i].ptroff = Integer.parseInt(splitLine[n++]);
              
               // get the pointer part of speech
               synptr.pointers[i].ppos = wnUtil.getpos(splitLine[n++].charAt(0));
              
               // get the lexp to/from restrictions
               wordnum= splitLine[n].substring(0,2);
               synptr.pointers[i].pfrm = Integer.parseInt(wordnum, 16);
              
               wordnum= splitLine[n++].substring(2,4);
               synptr.pointers[i].pto = Integer.parseInt(wordnum, 16);
              }
           }
              
           // If synset type is still not set, see if it's a pertainym
              
           if (dbase == WNConsts.ADJ && synptr.sstype == DONT_KNOW && foundpert == 1) {
              synptr.sstype = PERTAINYM;
           }
              
           // retireve optional information from verb synset
           if (dbase == WNConsts.VERB) {
             int fcount = Integer.parseInt(splitLine[n++]);
          
             // allocate frame storage
             synptr.frames = new SynSet.Frame[fcount];  
          
             for (int i=0;i<fcount;i++) {
                // skip the frame pointer (+)
                n++;
                synptr.frames[i] = new SynSet.Frame();
                synptr.frames[i].frmid = Integer.parseInt(splitLine[n++]);
                synptr.frames[i].frmto = Integer.parseInt(splitLine[n++]);
             }
          }
          
          // get the optional definition
          
          //ptrtok = splitLine[n++];
          if (n<splitLine.length) {
             n++;
             StringBuffer sb = new StringBuffer("(");
             for (int i=n; i<splitLine.length; i++) {
                 sb.append(splitLine[i]);
                 if (i<splitLine.length-1) {
                   sb.append(" ");
                 }
             }
             sb.append(")");
             synptr.defn = sb.toString();
          }
    
          // Can't do earlier - calls indexlookup which messes up strtok calls
          for (int i = 0; i < synptr.wcount; i++) {
             synptr.wnsns[i] = getSearchSense(synptr, i + 1);
          }
    
          return synptr;
       } catch (Exception e) {
           e.printStackTrace();
       }
       return new SynSet();
   }


/** Recursive search algorithm to trace a pointer tree
 */

public void tracePtrs(SynSet synptr, int ptrType, int dbase, int depth) {
    int extraindent = 0;
    SynSet cursyn;
    String prefix="";
    String tbuf;

    interfaceDoEvents();
    if (wnRtl.abortSearch) {
	return;
    }

    if (ptrType < 0) {
	ptrType = -ptrType;
	extraindent = 2;
    }
    
    for (int i = 0; i < synptr.pointers.length; i++) {
	if ((synptr.pointers[i].ptrType == ptrType) &&
	   ((synptr.pointers[i].pfrm == 0) ||
	    (synptr.pointers[i].pfrm == synptr.whichWord))) {

	    if (!prflag) {	// print sense number and synset
		printSenses(synptr, sense + 1);
		prflag = true;
	    }
	    printSpaces(TRACEP, depth + extraindent);

	    switch(ptrType) {
                case WNConsts.PERTPTR:
                    if (dbase == WNConsts.ADV) {
                        prefix = "Derived from "+WNGlobal.partNames[synptr.pointers[i].ppos]+" ";
                    } else {
                        prefix = "Pertains to "+WNGlobal.partNames[synptr.pointers[i].ppos]+" ";
                    }
                    break;
                case WNConsts.ANTPTR:
                    if (dbase != WNConsts.ADJ) {
                        prefix = "Antonym of ";
                    }
                    break;
                case WNConsts.PPLPTR:
                    prefix = "Participle of verb ";
                    break;
                case WNConsts.HASMEMBERPTR:
                    prefix = "   HAS MEMBER: ";
                    break;
                case WNConsts.HASSTUFFPTR:
                    prefix = "   HAS SUBSTANCE: ";
                    break;
                case WNConsts.HASPARTPTR:
                    prefix = "   HAS PART: ";
                    break;
                case WNConsts.ISMEMBERPTR:
                    prefix = "   MEMBER OF: ";
                    break;
                case WNConsts.ISSTUFFPTR:
                    prefix = "   SUBSTANCE OF: ";
                    break;
                case WNConsts.ISPARTPTR:
                    prefix = "   PART OF: ";
                    break;
                default:
                    prefix = "=> ";
                    break;
	    }

	    // Read synset pointed to
	    cursyn=readSynset(synptr.pointers[i].ppos, synptr.pointers[i].ptroff, "");

	    // For Pertainyms and Participles pointing to a specific
	    //   sense, indicate the sense then retrieve the synset
	    //   pointed to and other info as determined by type.
	    //   Otherwise, just print the synset pointed to.

	    if ((ptrType == WNConsts.PERTPTR || ptrType == WNConsts.PPLPTR) &&
		synptr.pointers[i].pto != 0) {
		tbuf = " (Sense "+cursyn.wnsns[synptr.pointers[i].pto - 1]+")"+WNGlobal.lineSeparator;
		printSynset(prefix, cursyn, tbuf, DEFOFF, synptr.pointers[i].pto,
			    SKIP_ANTS, PRINT_MARKER);
		if (ptrType == WNConsts.PPLPTR) { // adjective pointing to verb
		    printSynset("      =>", cursyn, WNGlobal.lineSeparator,
				DEFON, ALLWORDS, PRINT_ANTS, PRINT_MARKER);
		    tracePtrs(cursyn, WNConsts.HYPERPTR, wnUtil.getpos(cursyn.pos.charAt(0)), 0);
		} else if (dbase == WNConsts.ADV) { // adverb pointing to adjective
		    printSynset("      =>", cursyn, WNGlobal.lineSeparator, DEFON, ALLWORDS, 
				((wnUtil.getsstype(cursyn.pos.charAt(0)) == WNConsts.SATELLITE)
				 ? SKIP_ANTS : PRINT_ANTS), PRINT_MARKER);
//#ifdef FOOP
 		    tracePtrs(cursyn, WNConsts.HYPERPTR, wnUtil.getpos(cursyn.pos.charAt(0)), 0);
//#endif
		} else {	// adjective pointing to noun
		    printSynset("      =>", cursyn, WNGlobal.lineSeparator,
				DEFON, ALLWORDS, PRINT_ANTS, PRINT_MARKER);
		    tracePtrs(cursyn, WNConsts.HYPERPTR, wnUtil.getpos(cursyn.pos.charAt(0)), 0);
		}
	    } else if (ptrType == WNConsts.ANTPTR && dbase != WNConsts.ADJ && synptr.pointers[i].pto != 0) {
		tbuf = " (Sense "+cursyn.wnsns[synptr.pointers[i].pto - 1]+")"+WNGlobal.lineSeparator;
		printSynset(prefix, cursyn, tbuf, DEFOFF, synptr.pointers[i].pto,
			    SKIP_ANTS, PRINT_MARKER);
		printSynset("      =>", cursyn, WNGlobal.lineSeparator, DEFON, ALLWORDS,
			    PRINT_ANTS, PRINT_MARKER);
	    } else {
		printSynset(prefix, cursyn, WNGlobal.lineSeparator, DEFON, ALLWORDS,
			    PRINT_ANTS, PRINT_MARKER);
            }

	    // For HOLONYMS and MERONYMS, keep track of last one
	    //   printed in buffer so results can be truncated later.

	    if (ptrType >= WNConsts.ISMEMBERPTR && ptrType <= WNConsts.HASPARTPTR) {
		lastholomero = searchBuffer.length();
            }

	    if (depth>0) {
		depth = depthCheck(depth, cursyn);
		tracePtrs(cursyn, ptrType, wnUtil.getpos(cursyn.pos.charAt(0)), (depth+1));

	    }
       }
    }
}

public void traceCoords(SynSet synptr, int ptrType, int dbase, int depth) {
    SynSet cursyn;

    interfaceDoEvents();
    if (wnRtl.abortSearch) {
	return;
    }

    for (int i = 0; i < synptr.pointers.length; i++) {
	if ((synptr.pointers[i].ptrType == WNConsts.HYPERPTR) &&
	   ((synptr.pointers[i].pfrm == 0) ||
	    (synptr.pointers[i].pfrm == synptr.whichWord))) {
	    
	    if (!prflag) {
		printSenses(synptr, sense + 1);
		prflag = true;
	    }
	    printSpaces(TRACEC, depth);

	    cursyn = readSynset(synptr.pointers[i].ppos, synptr.pointers[i].ptroff, "");

	    printSynset(". ", cursyn, WNGlobal.lineSeparator, DEFON, ALLWORDS,
			SKIP_ANTS, PRINT_MARKER);

	    tracePtrs(cursyn, ptrType, wnUtil.getpos(cursyn.pos.charAt(0)), depth);
	    
	    if (depth>0) {
		depth = depthCheck(depth, cursyn);
		traceCoords(cursyn, ptrType, wnUtil.getpos(cursyn.pos.charAt(0)), (depth+1));
	    }
	}
    }
}

public void traceNomins(SynSet synptr, int dbase) {
    int j;
    int idx=0;
    SynSet cursyn;
    int[] prlist = new int[32];

    interfaceDoEvents();
    if (wnRtl.abortSearch) {
	return;
    }

    for (int i = 0; i < synptr.pointers.length; i++) {
	if ((synptr.pointers[i].ptrType >= WNConsts.NOMIN_START) &&
	    (synptr.pointers[i].ptrType <= WNConsts.NOMIN_END)) {

	    if (!prflag) {
		printSenses(synptr, sense + 1);
		prflag = true;
	    }
	    cursyn = readSynset(synptr.pointers[i].ppos, synptr.pointers[i].ptroff, "");

	    for (j = 0; j < idx; j++) {
		if (synptr.pointers[i].ptroff == prlist[j]) {
		    break;
		}
	    }

	    if (j == idx) {
		prlist[idx++] = synptr.pointers[i].ptroff;
		printSpaces(TRACEP, 0);
		printSynset("<. ", cursyn, WNGlobal.lineSeparator, DEFON, ALLWORDS,
			    SKIP_ANTS, PRINT_MARKER);
	    }

	}
    }
}

/** Trace through the hypernym tree and print all MEMBER, STUFF
 *  and PART info.
 */

public void traceInherit(SynSet synptr, int ptrbase, int dbase, int depth) {
    SynSet cursyn;

    interfaceDoEvents();
    if (wnRtl.abortSearch) {
	return;
    }
    
    for (int i=0; i<synptr.pointers.length; i++) {
	if ((synptr.pointers[i].ptrType == WNConsts.HYPERPTR) &&
	   ((synptr.pointers[i].pfrm == 0) ||
	    (synptr.pointers[i].pfrm == synptr.whichWord))) {
	    
	    if (!prflag) {
		printSenses(synptr, sense + 1);
		prflag = true;
	    }
	    printSpaces(TRACEI, depth);
	    
	    cursyn = readSynset(synptr.pointers[i].ppos, synptr.pointers[i].ptroff, "");

	    printSynset("=> ", cursyn, WNGlobal.lineSeparator, DEFON, ALLWORDS, SKIP_ANTS, PRINT_MARKER);
	    
	    tracePtrs(cursyn, ptrbase, WNConsts.NOUN, depth);
	    tracePtrs(cursyn, ptrbase + 1, WNConsts.NOUN, depth);
	    tracePtrs(cursyn, ptrbase + 2, WNConsts.NOUN, depth);
	    
	    if (depth>0) {
		depth = depthCheck(depth, cursyn);
		traceInherit(cursyn, ptrbase, wnUtil.getpos(cursyn.pos.charAt(0)), (depth+1));
	    }
	}
    }

    // Truncate search buffer after last holo/meronym printed */
    searchBuffer.setLength(lastholomero);
}

public void partsAll(SynSet synptr, int ptrType) {
    //int hasptr = 0;
    
    int ptrbase = (ptrType == WNConsts.HMERONYM) ? WNConsts.HASMEMBERPTR : WNConsts.ISMEMBERPTR;
    
    // First, print out the MEMBER, STUFF, PART info for this synset

    for (int i = 0; i < 3; i++) {
	if (hasPtr(synptr, ptrbase + i)!=0) {
	    tracePtrs(synptr, ptrbase + i, WNConsts.NOUN, 1);
	}
	interfaceDoEvents();
	if (wnRtl.abortSearch) {
	    return;
        }
    }

    // Print out MEMBER, STUFF, PART info for hypernyms on
    // HMERONYM search only.

    if (ptrType == WNConsts.HMERONYM) {
	lastholomero = searchBuffer.length();
	traceInherit(synptr, ptrbase, WNConsts.NOUN, 1);
    }
}

public void traceAdjant(SynSet synptr) {
    SynSet newsynptr;
    int anttype = DIRECT_ANT;
    SynSet simptr, antptr;
    final String similar = "        => ";

    // This search is only applicable for ADJ synsets which have
    // either direct or indirect antonyms (not valid for pertainyms).
    
    if (synptr.sstype == DIRECT_ANT || synptr.sstype == INDIRECT_ANT) {
	printSenses(synptr, sense + 1);
	printBuffer(WNGlobal.lineSeparator);
	
	// if indirect, get cluster head.
	
	if (synptr.sstype == INDIRECT_ANT) {
	    anttype = INDIRECT_ANT;
            int i=0;
	    while (synptr.pointers[i].ptrType != WNConsts.SIMPTR) i++;
	    newsynptr = readSynset(WNConsts.ADJ, synptr.pointers[i].ptroff, "");
	} else {
	    newsynptr = synptr;
        }
	
	// Find antonyms - if direct, make sure that the antonym
	// ptr we're looking at is from this word.
	
	for (int i = 0; i < newsynptr.pointers.length; i++) {

	    if (newsynptr.pointers[i].ptrType == WNConsts.ANTPTR &&
		((anttype == DIRECT_ANT &&
		  newsynptr.pointers[i].pfrm == newsynptr.whichWord) ||
		 (anttype == INDIRECT_ANT))) {
		
		// Read the antonym's synset and print it.  if a
		// direct antonym, print it's satellites.
		
		antptr = readSynset(WNConsts.ADJ, newsynptr.pointers[i].ptroff, "");
    
		if (anttype == DIRECT_ANT) {
		    printSynset("", antptr, WNGlobal.lineSeparator, DEFON, ALLWORDS,
				PRINT_ANTS, PRINT_MARKER);
		    for (int j = 0; j < antptr.pointers.length; j++) {
			if (antptr.pointers[j].ptrType == WNConsts.SIMPTR) {
			    simptr = readSynset(WNConsts.ADJ, antptr.pointers[j].ptroff, "");
			    printSynset(similar, simptr, WNGlobal.lineSeparator, DEFON,
					ALLWORDS, SKIP_ANTS, PRINT_MARKER);
			}
		    }
		} else {
		    printAntSynset(antptr, WNGlobal.lineSeparator, anttype, DEFON);
                }
	    }
	}
    }
}


/** Fetch the given example sentence from the example file and print it out.
 */

public void getExample(String offset, String word) {
    if (wnRtl.verbSentenceFile != null) {
        String line = binSearcher.binSearch(offset, wnRtl.verbSentenceFile);
	if (line != null) {
	    for (int i=0; i<line.length(); i++) {
                if (line.charAt(i)==' ') {
	     	   line=line.substring(0,i);
                   break;
                }
            }

	    printBuffer("          EX: ");
	    printBuffer(line);
	    printBuffer(word);
	}
    }
}

/** Find the example sentence references in the example sentence index file.
 */

public boolean findExample(SynSet synptr) {
    boolean found = false;
    
    if (wnRtl.verbSentenceIndexFile != null) {
	int wordnum = synptr.whichWord - 1;

        String tbuf = synptr.words[wordnum]+"%"+wnUtil.getpos(synptr.pos.charAt(0))+":"+
                synptr.fnum+":"+synptr.lexid[wordnum]+"::";

        String temp = binSearcher.binSearch(tbuf, wnRtl.verbSentenceIndexFile);
	if (temp != null) {

	    // skip over sense key and get sentence numbers.

	    temp += synptr.words[wordnum].length() + 11;
	    tbuf = temp;

       int i=0;
	    for (i=0; i<tbuf.length(); i++) {
          char c = tbuf.charAt(i);
          if (c==' ' || c==',' || c=='\n') {
             break;
          }
       }

       String[] strings = wnUtil.split(tbuf.substring(i+1), ",\n");
	    for (i=0; i<strings.length; i++) {
		getExample(strings[i], synptr.words[wordnum]);
	    }
	    found = true;
	}
    }
    return found;
}

public void printFrame(SynSet synptr, boolean prsynset) {

    if (prsynset) {
	printSenses(synptr, sense + 1);
    }
    
    if (!findExample(synptr)) {
	for (int i = 0; i < synptr.frames.length; i++) {
	    if ((synptr.frames[i].frmto == synptr.whichWord) ||
		(synptr.frames[i].frmto == 0)) {
		if (synptr.frames[i].frmto == synptr.whichWord) {
		    printBuffer("          => ");
                } else {
		    printBuffer("          *> ");
                }
		printBuffer(WNGlobal.frameText[synptr.frames[i].frmid]);
		printBuffer(WNGlobal.lineSeparator);
	    }
	}
    }
}

public void printSeeAlso(SynSet synptr) {
    SynSet cursyn;
    boolean first = true;
    boolean svwnsnsFlag;
    String firstline = "          Also See. ";
    String otherlines = "; ";
    String prefix = firstline;

    // Find all SEEALSO pointers from the searchWord and print the
    //   word or synset pointed to.

    for (int i = 0; i < synptr.pointers.length; i++) {
	if ((synptr.pointers[i].ptrType == WNConsts.SEEALSOPTR) &&
	    ((synptr.pointers[i].pfrm == 0) ||
	     (synptr.pointers[i].pfrm == synptr.whichWord))) {

	    cursyn = readSynset(synptr.pointers[i].ppos, synptr.pointers[i].ptroff, "");

	    svwnsnsFlag = wnRtl.wnsnsFlag;
	    wnRtl.wnsnsFlag = true;
	    printSynset(prefix, cursyn, "", DEFOFF,
			synptr.pointers[i].pto == 0 ? ALLWORDS : synptr.pointers[i].pto,
			SKIP_ANTS, SKIP_MARKER);
	    wnRtl.wnsnsFlag = svwnsnsFlag;


	    if (first) {
		prefix = otherlines;
		first = false;
	    }
	}
    }
    if (!first) {
	printBuffer(WNGlobal.lineSeparator);
    }
}

private static String[] a_an = {
	"", "a noun", "a verb", "an adjective", "an adverb" };
private static String[] freqcats = {
	"extremely rare","very rare","rare","uncommon","common",
	"familiar","very familiar","extremely familiar"
};
public void frequencyOfWord(Index index) {
    int familiar=0;
    int cnt;

    if (index!=null) {
	cnt = index.senseCount;
	if (cnt == 0) { familiar = 0; }
	if (cnt == 1) { familiar = 1; }
	if (cnt == 2) { familiar = 2; }
	if (cnt >= 3 && cnt <= 4) { familiar = 3; }
	if (cnt >= 5 && cnt <= 8) { familiar = 4; }
	if (cnt >= 9 && cnt <= 16) { familiar = 5; }
	if (cnt >= 17 && cnt <= 32) { familiar = 6; }
	if (cnt > 32 ) familiar = 7;
	
	tmpBuf = WNGlobal.lineSeparator+index.word+" used as "+
                 a_an[wnUtil.getpos(index.pos.charAt(0))]+" is "+
                 freqcats[familiar]+" (polysemy count = "+cnt+")"+
                 WNGlobal.lineSeparator;
	printBuffer(tmpBuf);
    }
}

public static int fgets(RandomAccessFile file, byte[] line_buf) {
    //int n=0;
    try {
        int start =0;
        int len = line_buf.length>>2;
        for (int x=0; x<4; x++) {
            int size = file.read(line_buf, start, len);
            if (size==0) { return -1; }
            for (int i=0; i<size; i++) {
                byte c = line_buf[start+i];
                if (c=='\n' || c=='\r') {
                    return start+i;
                }
            }
            start += len;
        }
        return -1;
    } catch (java.io.IOException e) {
        e.printStackTrace();
        return -1;
    } catch (Exception e) {
        e.printStackTrace();
        return -1;
    }
}

public void wnGrep (String word_passed, int pos) {
    try {
       RandomAccessFile inputfile;
       int wordlen, linelen, loc;
       byte[] line_buf = new byte[1024];
       String line;
       int count = 0;
       int size;
    
       inputfile = wnRtl.indexFiles[pos];
       if (inputfile == null) {
          msgbuf = "WordNet library error: Can't perform compounds " +
                   "search because "+WNGlobal.partNames[pos]+" index file is not open"+WNGlobal.lineSeparator;
          WordNet.displayMessage (msgbuf);
          return;
       }
       inputfile.seek(0);
    
       String word = word_passed.replace(' ', '_');	// replace spaces with underscores.
       wordlen = word.length();
    
       while ( (size=fgets(inputfile, line_buf)) >=0) {
           if (size==0) {
               continue;
           }
          for (linelen = 0; line_buf[linelen] != (byte)' '; linelen++) {}
          if (linelen < wordlen) {
              continue;
          }
          //line = new String(line_buf, 0, linelen);
          line = new String(line_buf, 0, 0, linelen);
          loc=-1;
          while ((loc = line.indexOf(word,loc+1)) != -1) {
             if (
                (loc == 0) ||                     // at the start of the line.
                ((linelen - wordlen) == loc) ||   // at the end of the line.
                                                  // as a word in the middle of the line.
                (((line.charAt(loc - 1) == '-') || (line.charAt(loc - 1) == '_')) &&
                ((line.charAt(loc + wordlen) == '-') || (line.charAt(loc + wordlen) == '_')))
             ) {
                line.replace('_', ' ');
                tmpBuf = line+WNGlobal.lineSeparator;
                printBuffer (tmpBuf);
                break;
             }
          }
          if (count++ % 2000 == 0) {
             interfaceDoEvents ();
             if (wnRtl.abortSearch) {
                 break;
             }
          }
       }
    } catch (java.io.IOException e) {
        e.printStackTrace();
    }
}

/** Stucture to keep track of 'relative groups'.  All senses in a relative
 *  group are displayed together at end of search.  Transitivity is
 *  supported, so if either of a new set of related senses is already
 *  in a 'relative group', the other sense is added to that group as well.
 */

private static class RelGrp {
    int[] senses = new int[WNConsts.MAXSENSE];
    RelGrp next = null;
};
public RelGrp rellist;


// Simple hash function
public final static int HASHTABSIZE=1223;	// Prime number. Must be > 2*MAXTOPS
public final static int hash(int n) { return ((n) % HASHTABSIZE); }


public final static int MAXTOPS =300;	// Maximum number of lines in cousin.tops.

private static class CousinTops {
    int topnum;			// Unique id assigned to this top node.
    Set rels;			// set of top nodes this one is paired with.
    int offset;	                // Offset read from cousin.tops file.
} 
private CousinTops[] cousintops = new CousinTops[HASHTABSIZE];


/** Find relative groups for all senses of target word in given part
 *  of speech.
 */

public void relatives(Index idx, int dbase) {
    rellist = null;

    switch(dbase) {
        case WNConsts.NOUN:
            findSisters(idx);
            interfaceDoEvents();
            if (wnRtl.abortSearch) {
                break;
            }
            findTwins(idx);
            interfaceDoEvents();
            if (wnRtl.abortSearch) {
                break;
            }
            findCousins(idx);
            interfaceDoEvents();
            if (wnRtl.abortSearch) {
                break;
            }
            printRelatives(idx, WNConsts.NOUN);
            break;
        case WNConsts.VERB:
            findVerbGroups(idx);
            interfaceDoEvents();
            if (wnRtl.abortSearch) {
                break;
            }
            printRelatives(idx, WNConsts.VERB);
            break;
        default:
            break;
    }
}


/** Look for 'twins' - synsets with 3 or more words in common.
 */

public int wordIdx(String word, String wordtable[], int nwords) {
     for ( ; --nwords >= 0 && !(word.equals(wordtable[nwords])); )
	  ;
     return nwords;
}

public int addWord(String word, String wordtable[], int nwords) {
    wordtable[nwords] = word;
     return nwords;
}

public final static int MAXWRDS = 300;

public void findTwins(Index idx) {
     String[] words = new String[MAXWRDS];
     Set[] s = new HashSet[WNConsts.MAXSENSE];
     int nwords =0;

     if (idx==null) {
         throw new NullPointerException("idx must not be null");
     }
     for (int i = 0; i < idx.offset.length; i++) {

	  SynSet synset = readSynset(WNConsts.NOUN, idx.offset[i], "");

	  s[i] = new HashSet(MAXWRDS);
	  if (synset.wcount >=  3) {
	       for (int j = 0; j < synset.wcount; j++) {
		    String buf = wnUtil.strToLower(synset.words[j]);
		    int k = wordIdx(buf, words, nwords);
		    if (k < 0) {
			 k = addWord(buf, words, nwords);
			 if (nwords >= MAXWRDS) {
                             throw new NullPointerException("");
                         }
			 nwords++;
		    }
		    s[i].add(new Integer(k));
	       }
          }
     }
     
     
     for (int i = 0; i < idx.offset.length; i++) {
         for (int j = i + 1; j < idx.offset.length; j++) {
             Set n = new HashSet(s[i]);
             n.retainAll(s[j]);
             if (n.size() >= 3) {
	         addRelatives(WNConsts.NOUN, idx, j, i);
             }
         }
     }
}

private static class Hypers {public int id;  public int off;}
private Hypers[] hypers = new Hypers[HASHTABSIZE];

/** Look for 'sisters' - senses of the search word with a common parent.
 */
public void findSisters(Index idx) {
     int id = 0;
     Set[] syns = new HashSet[WNConsts.MAXSENSE];

     if (idx==null) {
         throw new NullPointerException("idx should not be null");
     }

     // Read all synsets and list all hyperptrs.

     for (int i = 0; i < idx.offset.length; i++) {
	  SynSet synset = readSynset(WNConsts.NOUN, idx.offset[i], idx.word);
	  if (synset==null) {
              throw new NullPointerException("");
          }
	  syns[i] = new HashSet(4*WNConsts.MAXSENSE);
	  
	  for (int j = 0; j < synset.pointers.length; j++) {
	       if (synset.pointers[j].ptrType == WNConsts.HYPERPTR) { 
		    int l = hash(synset.pointers[j].ptroff);
		    for ( ; hypers[l] == null || hypers[l].off != synset.pointers[j].ptroff; l++) {
			 if (hypers[l] == null) {
                              hypers[l] = new Hypers();
			      hypers[l].off = synset.pointers[j].ptroff;
			      hypers[l].id  =  id++;
			      break;
			 }  else if (l == HASHTABSIZE - 1) {
                             l = -1;
                         }
                    }
		    
		    // Found or inserted it.
		    syns[i].add(new Integer(hypers[l].id));
	       }
          }
     }
     
     for (int i = 0; i < idx.offset.length; i++) {
         for (int j = i+1; j < idx.offset.length; j++) {
               Set n = new HashSet(syns[i]);
               n.retainAll(syns[j]);
	       if (!n.isEmpty()) {
	           addRelatives(WNConsts.NOUN, idx, i, j);
               }
         }
     }
}

/** Look for 'cousins' - two senses, each under a different predefined
 *  top node pair.  Top node offset pairs are stored in cousin.tops.
 * Return index of topnode if it exists.
 */

public int findTopnode( int offset) {
     // test to see whether the Cousin files exist (they do not for 1.7)
     if (wnRtl.cousinFile==null) {  return -1; }

     int hashval = hash(offset);

     for (int i = hashval; i < HASHTABSIZE; i++) {
	 if (cousintops[i].offset == offset) {
	     return i;
         }
     }
     for (int i = 0; i < hashval; i++) {
	 if (cousintops[i].offset == offset) {
	     return i;
         }
     }
     return -1;		// not found
 }

/** Return an empty slot for <offset> to be placed in.
 */

public int newTopnode( int offset) {
     // test to see whether the Cousin files exist (they do not for 1.7)
     if (wnRtl.cousinFile==null) {  return -1; }
        
     int hashval = hash(offset);

     for (int i = hashval; i < HASHTABSIZE; i++) {
	 if (cousintops[i].rels==null) {
	     return i;
         }
     }
     for (int i = 0; i < hashval; i++) {
	 if (cousintops[i].rels==null) {
	     return i;
         }
     }
     return -1;		// table is full
}

public void addTopnode(int index, int id, Set s,  int offset) {
    if ((index >= 0) && (index < HASHTABSIZE)) {
	cousintops[index].rels   = s;
	cousintops[index].topnum = id;
	cousintops[index].offset = offset;
    }
}

public void clearTopnodes() {
    for (int i = 0; i < HASHTABSIZE; i++) {
	cousintops[i].offset = 0;
    }
}

/** Read cousin.tops file (one time only) and store in different form.
 */

private boolean readCousintops_done = false;
public int readCousintops() {
   try {
        int id = 0;
        int top1, top2;
        int tidx1, tidx2;
        
        if (readCousintops_done) { return 0; }
        
        // test to see whether the Cousin files exist (they do not for 1.7)
        if (wnRtl.cousinFile==null) {  return 0; }
        
        wnRtl.cousinFile.seek(0);
        clearTopnodes();
        
        top1=1;
        while (top1>0) {
            top1=0; top2=0;
            if ((tidx1 = findTopnode(top1)) < 0) {
                if ((tidx1 = newTopnode(top1)) != -1) {
                    addTopnode(tidx1, id++, new HashSet(MAXTOPS), top1);
                } else {
                    WordNet.displayMessage("WordNet library error: cannot create topnode table for grouped sarches");
                    return -1;
                }
            }
         
            if ((tidx2 = findTopnode(top2)) < 0) {
                if ((tidx2 = newTopnode(top2)) != -1) {
                    addTopnode(tidx2, id++, new HashSet(MAXTOPS), top2);
                } else {
                    WordNet.displayMessage("WordNet library error: cannot create topnode table for grouped sarches");
                    return -1;
                }
            }
         
            cousintops[tidx1].rels.add(new Integer( cousintops[tidx2].topnum) );
            cousintops[tidx2].rels.add(new Integer( cousintops[tidx1].topnum) );
        }
        readCousintops_done = true;
        return 0;
   } catch (Exception e) {
       e.printStackTrace();
   }
   return 0;
}

private static abstract class TraceHyperPtr {
    public abstract void fn(Search searcher, int hyperptr, Object cp);
}

/** Record all top nodes found for synset.
 */
private static class RecordTopnode extends TraceHyperPtr {
    public void fn(Search searcher, int hyperptr, Object cp) {
         searcher.recordTopnode(hyperptr, (Set[])cp);
    }
}

public void recordTopnode( int hyperptr, Set[] sets) {
     if (sets==null) {
         throw new NullPointerException("sets should not be null");
     }
     int i = findTopnode(hyperptr);
     if (i >= 0) {
	 sets[0].add(new Integer(cousintops[i].topnum) );
	 sets[1].addAll(cousintops[i].rels);
     }
}

public void findCousins(Index idx) {
     SynSet synset;
     Set[][] syns_tops = new HashSet[WNConsts.MAXSENSE][2];
     
     if (idx==null) {
         throw new NullPointerException("idx should not be null");
     }
     if (readCousintops() != 0)
	 return;

     // First read all the synsets.

     int nsyns;
     for (nsyns = 0; nsyns < idx.offset.length; nsyns++) { // why -1 in orig?
	  synset = readSynset(WNConsts.NOUN, idx.offset[nsyns], "");
	  syns_tops[nsyns][0] = new HashSet(MAXTOPS);
	  syns_tops[nsyns][1] = new HashSet(MAXTOPS);

	  recordTopnode(idx.offset[nsyns], syns_tops[nsyns]);

          
	  traceHyperptrs(synset, new RecordTopnode(),  syns_tops[nsyns], 1);
     }
     
     for (int i = 0; i < nsyns; i++) {
         for (int j = i + 1; j < nsyns; j++) {
             Set n = new HashSet(syns_tops[i][0]);
             n.retainAll(syns_tops[j][1]);
	       if (!n.isEmpty()) {
		    addRelatives(WNConsts.NOUN, idx, i, j);
               }
         }
     }
}


/** Trace through HYPERPTRs up to MAXDEPTH, running `fn()' on each one.
 */ 

public void traceHyperptrs(SynSet synptr, TraceHyperPtr tracer, Object cp, int depth) {
     SynSet s;
     
     if (depth >= WNConsts.MAXDEPTH) {
	  return;
     }
     for (int i = 0; i < synptr.pointers.length; i++)
	  if (synptr.pointers[i].ptrType == WNConsts.HYPERPTR) {
	       tracer.fn(this, synptr.pointers[i].ptroff, cp);
	       
	       s = readSynset(synptr.pointers[i].ppos, synptr.pointers[i].ptroff, "");
	       traceHyperptrs(s, tracer, cp, depth+1);
	  }
}



public void findVerbGroups(Index idx) {
     SynSet synset;

     if (idx==null) {
         throw new NullPointerException("idx should not be null");
     }

     // Read all senses.
     
     for (int i = 0; i < idx.offset.length; i++) {

	 synset = readSynset(WNConsts.VERB, idx.offset[i], idx.word);
	
	 // Look for VERBGROUP ptr(s) for this sense.  If found,
	 // create group for senses, or add to existing group.

	 for (int j = 0; j < synset.pointers.length; j++) {
	       if (synset.pointers[j].ptrType == WNConsts.VERBGROUP) {
		   // Need to find sense number for ptr offset.
		   for (int k = 0; k < idx.offset.length; k++) {
		       if (synset.pointers[j].ptroff == idx.offset[k]) {
			   addRelatives(WNConsts.VERB, idx, i, k);
			   break;
		       }
		   }
	       }
	   }
     }
}

public void addRelatives(int pos, Index idx, int rel1, int rel2)
{
    RelGrp rel, last, r;
    // First make sure that senses are not on the excpetion list.
    if (pos == WNConsts.NOUN && groupExceptions(idx.offset[rel1], idx.offset[rel2])) {
	return;
    }

    // If either of the new relatives are already in a relative group,
    // then add the other to the existing group (transitivity).
    // Otherwise create a new group and add these 2 senses to it.

    last = rellist;
    for (rel = rellist; rel!=null; rel = rel.next) {
	if (rel.senses[rel1] == 1 || rel.senses[rel2] == 1) {
	    rel.senses[rel1] = rel.senses[rel2] = 1;

	    // If part of another relative group, merge the groups.
	    for (r = rellist; r!=null; r = r.next) {
		if (r != rel &&
		    (r.senses[rel1] == 1 || r.senses[rel2] == 1)) {
		    for (int i = 0; i < WNConsts.MAXSENSE; i++) {
			rel.senses[i] |= r.senses[i];
                    }
		}
	    }
	    return;
	}
	last = rel;
    }
    rel = mkrellist();
    rel.senses[rel1] = rel.senses[rel2] = 1;
    if (rellist == null) {
	rellist = rel;
    } else {
	last.next = rel;
    }
}


public boolean groupExceptions( int off1,  int off2) {
    if (wnRtl.cousinExcFile==null) {
        return false;
    }
    String buf = "" + (off1 < off2 ? off1 : off2);
    String searchResult = binSearcher.binSearch(buf, wnRtl.cousinExcFile);
    if (searchResult != null) {
         buf =""+(off2 > off1 ? off2 : off1);
         String linebuf = searchResult.substring(9,searchResult.length()-1); // don't copy key
                                                                             // and strip off newline
         String[] strings = wnUtil.split(linebuf, " ");
         for (int i=0; i<strings.length; i++) {
            if (strings[i].equals(buf)) {
               return true;
            }
         }
    }
    return false;
}

public RelGrp mkrellist()
{
    RelGrp rel;

    rel = new RelGrp();
    for (int i = 0; i < WNConsts.MAXSENSE; i++) {
	rel.senses[i] = 0;
    }
    rel.next = null;
    return(rel);
}



public void printRelatives(Index idx, int dbase) {
    SynSet synptr;
    RelGrp rel;
    int[] outsenses = new int[WNConsts.MAXSENSE];

    for (int i = 0; i < idx.offset.length; i++) {
	outsenses[i] = 0;
    }
    prflag = true;

    for (rel = rellist; rel!=null; rel = rel.next) {
	boolean flag = false;
	for (int i = 0; i < idx.offset.length; i++) {
	    if (rel.senses[i]!=0 && outsenses[i]==0) {
		flag = true;
		synptr = readSynset(dbase, idx.offset[i], "");
		printSenses(synptr, i + 1);
		tracePtrs(synptr, WNConsts.HYPERPTR, dbase, 0);
		outsenses[i] = 1;
	    }
	}
	if (flag) {
	    printBuffer("--------------"+WNGlobal.lineSeparator);
        }
    }

    for (int i = 0; i < idx.offset.length; i++) {
	if (outsenses[i]==0) {
	    synptr = readSynset(dbase, idx.offset[i], "");
	    printSenses(synptr, i + 1);
	    tracePtrs(synptr, WNConsts.HYPERPTR, dbase, 0);
	    printBuffer("--------------"+WNGlobal.lineSeparator);
	}
    }
}


/**
 * Search code interfaces to WordNet database.
 *
 * findTheInfo() - print search results and return ptr to output buffer
 * findTheInfo_ds() - return search results in linked list data structrure
*/

public String findTheInfo(String searchstr, int dbase, int ptrType, int whichsense) {
    SynSet cursyn;
    Index idx = null;
    int depth = 0;
    int offsetcnt;
    int bufstart;
    int[] offsets = new int[WNConsts.MAXSENSE];
    boolean skipit = false;

    // Initializations -
    // clear output buffer, search results structure, flags.

    searchBuffer.setLength(0);

    wnRtl.wnResults = new SearchResults();
    wnRtl.wnResults.numForms = wnRtl.wnResults.printCount = 0;
    wnRtl.wnResults.searchBuf = searchBuffer.toString();
    wnRtl.wnResults.searchDs = null;

    wnRtl.abortSearch = false;
    boolean overflag = false;
    
    for (int i = 0; i < WNConsts.MAXSENSE; i++) {
	offsets[i] = 0;
    }

    switch (ptrType) {
    case WNConsts.OVERVIEW:
	WNOverview(searchstr, dbase);
	break;
    case WNConsts.FREQ:
	while ((idx = getIndex(searchstr, dbase)) != null) {
	    searchstr = null;
	    wnRtl.wnResults.senseCount[wnRtl.wnResults.numForms] = idx.offset.length;
	    frequencyOfWord(idx);
	    wnRtl.wnResults.numForms++;
	}
	break;
    case WNConsts.WNGREP:
	wnGrep(searchstr, dbase);
	break;
    case WNConsts.WNESCORT:
	searchBuffer.setLength(0);
        searchBuffer.append("Sentences containing "+searchstr+" will be displayed in the Escort window.");
	break;
    case WNConsts.RELATIVES:
    case WNConsts.VERBGROUP:
	while ((idx = getIndex(searchstr, dbase)) != null) {
	    searchstr = null;
	    wnRtl.wnResults.senseCount[wnRtl.wnResults.numForms] = idx.offset.length;
	    relatives(idx, dbase);
	    wnRtl.wnResults.numForms++;
	}
	break;
    default:

	// If negative search type, set flag for recursive search
	if (ptrType < 0) {
	    ptrType = -ptrType;
	    depth = 1;
	}
	bufstart = searchBuffer.length();
	offsetcnt = 0;

	// look at all spellings of word

	while ((idx = getIndex(searchstr, dbase)) != null) {

	    searchstr = null;	// clear out for next call to getIndex()
	    wnRtl.wnResults.senseCount[wnRtl.wnResults.numForms] = idx.offset.length;
	    wnRtl.wnResults.outSenseCount[wnRtl.wnResults.numForms] = 0;

	    // Print extra sense msgs if looking at all senses
	    if (whichsense == WNConsts.ALLSENSES) {
		printBuffer(
"                                                                         "+WNGlobal.lineSeparator);
            }

	    // Go through all of the searchWord's senses in the
	    //   database and perform the search requested.

	    for (sense = 0; sense < idx.offset.length; sense++) {

		if (whichsense == WNConsts.ALLSENSES || whichsense == sense + 1) {
		    prflag = false;

		    // Determine if this synset has already been done
		    // with a different spelling. If so, skip it.
                    skipit = false;
		    for (int i = 0; i < offsetcnt && !skipit; i++) {
			if (offsets[i] == idx.offset[sense]) {
			    skipit = true;
                        }
		    }
		    if (!skipit) {
		    	offsets[offsetcnt++] = idx.offset[sense];
		    	cursyn = readSynset(dbase, idx.offset[sense], idx.word);
		    	switch(ptrType) {
		    	case WNConsts.ANTPTR:
                            if (dbase == WNConsts.ADJ) {
			    	traceAdjant(cursyn);
                            } else {
			    	tracePtrs(cursyn, WNConsts.ANTPTR, dbase, depth);
                            }
			    break;
		   	 
		    	case WNConsts.COORDS:
			    traceCoords(cursyn, WNConsts.HYPOPTR, dbase, depth);
			    break;
		   	 
		    	case WNConsts.FRAMES:
			    printFrame(cursyn, true);
			    break;
			    
		    	case WNConsts.MERONYM:
			    tracePtrs(cursyn, WNConsts.HASMEMBERPTR, dbase, depth);
			    tracePtrs(cursyn, WNConsts.HASSTUFFPTR, dbase, depth);
			    tracePtrs(cursyn, WNConsts.HASPARTPTR, dbase, depth);
			    break;
			    
		    	case WNConsts.HOLONYM:
			    tracePtrs(cursyn, WNConsts.ISMEMBERPTR, dbase, depth);
			    tracePtrs(cursyn, WNConsts.ISSTUFFPTR, dbase, depth);
			    tracePtrs(cursyn, WNConsts.ISPARTPTR, dbase, depth);
			    break;
			   	 
		    	case WNConsts.HMERONYM:
			    partsAll(cursyn, WNConsts.HMERONYM);
			    break;
			   	 
		    	case WNConsts.HHOLONYM:
			    partsAll(cursyn, WNConsts.HHOLONYM);
			    break;
			   	 
		    	case WNConsts.SEEALSOPTR:
			    printSeeAlso(cursyn);
			    break;
	
//#ifdef FOOP
			case WNConsts.PPLPTR:
			    tracePtrs(cursyn, ptrType, dbase, depth);
			    tracePtrs(cursyn, WNConsts.PPLPTR, dbase, depth);
			    break;
//#endif
		    
		    	case WNConsts.SIMPTR:
		    	case WNConsts.SYNS:
		    	case WNConsts.HYPERPTR:
			    printSenses(cursyn, sense + 1);
			    prflag = true;
		    
			    tracePtrs(cursyn, ptrType, dbase, depth);
		    
			    if (dbase == WNConsts.ADJ) {
			    	tracePtrs(cursyn, WNConsts.PERTPTR, dbase, depth);
			    	tracePtrs(cursyn, WNConsts.PPLPTR, dbase, depth);
			    } else if (dbase == WNConsts.ADV) {
			    	tracePtrs(cursyn, WNConsts.PERTPTR, dbase, depth);
			    }

			    if (wnRtl.saFlag)	{ // print SEE ALSO pointers.
			    	printSeeAlso(cursyn);
                            }
			    
			    if (dbase == WNConsts.VERB && wnRtl.frFlag) {
			    	printFrame(cursyn, false);
                            }
			    break;

			case WNConsts.NOMINALIZATIONS:
			    traceNomins(cursyn, dbase);
			    break;

		    	default:
			    tracePtrs(cursyn, ptrType, dbase, depth);
			    break;

		    	} // end switch

		    } // end if (skipit)

		} // end if (whichsense)

		if (!skipit) {
		    interfaceDoEvents();
		    if ((whichsense == sense + 1) || wnRtl.abortSearch || overflag) {
		    	break;	// break out of loop - we're done
                    }
		}

	    } // end for (sense)

	    // Done with an index entry - patch in number of senses output.

	    if (whichsense == WNConsts.ALLSENSES) {
		int i = wnRtl.wnResults.outSenseCount[wnRtl.wnResults.numForms];
		if (i == idx.offset.length && i == 1) {
		    tmpBuf = WNGlobal.lineSeparator+"1 sense of "+ idx.word;
		} else if (i == idx.offset.length) {
		    tmpBuf = WNGlobal.lineSeparator+i+" senses of "+idx.word;
		} else if (i > 0) {	// printed some senses
		    tmpBuf = WNGlobal.lineSeparator+i+" of "+idx.offset.length+" senses of "+idx.word;
                }

		// Find starting offset in searchBuffer for this index
		// entry and patch string in.  Then update bufstart
		// to end of searchBuffer for start of next index entry.

		if (i > 0) {
		    if (wnRtl.wnResults.numForms > 0) {
			searchBuffer.insert(bufstart++, WNGlobal.lineSeparator).insert(bufstart, tmpBuf.toString());
		    } else {
		        searchBuffer.insert(bufstart, tmpBuf.toString());
                    }
		    bufstart = searchBuffer.length();
		}
	    }

	    interfaceDoEvents();
	    if (overflag || wnRtl.abortSearch) {
		break;		// break out of while (idx) loop.
            }

	    wnRtl.wnResults.numForms++;

	} // end while (idx)

    } // end switch

    interfaceDoEvents();
    if (wnRtl.abortSearch) {
	printBuffer(WNGlobal.lineSeparator+"Search Interrupted...");
    } else if (overflag) {
        searchBuffer.setLength(0);
	searchBuffer.append("Search too large.  Narrow search and try again..."+WNGlobal.lineSeparator);
    }

    // replace underscores with spaces before returning.

    return searchBuffer.toString().replace('_', ' ');
}

public SynSet findTheInfo_ds(String searchstr, int dbase, int ptrType, int whichsense) {
    Index idx;
    SynSet cursyn;
    SynSet synlist = null, lastsyn = null;
    int depth = 0;
    boolean newsense = false;

    wnRtl.wnResults.numForms = 0;
    wnRtl.wnResults.printCount = 0;

    while ((idx = getIndex(searchstr, dbase)) != null) {

	searchstr = null;	// clear out for next call.
	newsense = true;
	
	if (ptrType < 0) {
	    ptrType = -ptrType;
	    depth = 1;
	}

	wnRtl.wnResults.senseCount[wnRtl.wnResults.numForms] = idx.offset.length;
	wnRtl.wnResults.outSenseCount[wnRtl.wnResults.numForms] = 0;
	wnRtl.wnResults.searchBuf = null;
	wnRtl.wnResults.searchDs = null;

	// Go through all of the searchWord's senses in the
	// database and perform the search requested.
	
	for (sense = 0; sense < idx.offset.length; sense++) {
	    if (whichsense == WNConsts.ALLSENSES || whichsense == sense + 1) {
		cursyn = readSynset(dbase, idx.offset[sense], idx.word);
		if (lastsyn!=null) {
		    if (newsense) {
			lastsyn.nextForm = cursyn;
		    } else {
			lastsyn.nextss = cursyn;
                    }
		}
		if (synlist==null) {
		    synlist = cursyn;
                }
		newsense = false;
	    
		cursyn.searchType = ptrType;
		cursyn.ptrList = tracePtrs_ds(cursyn, ptrType, 
					       wnUtil.getpos(cursyn.pos.charAt(0)),
					       depth);
	    
		lastsyn = cursyn;

		if (whichsense == sense + 1) {
		    break;
                }
	    }
	}
	wnRtl.wnResults.numForms++;

	if (ptrType == WNConsts.COORDS) {	// clean up by removing hypernym.
	    lastsyn = synlist.ptrList;
	    synlist.ptrList = lastsyn.ptrList;
	}
    }
    wnRtl.wnResults.searchDs = synlist;
    return synlist;
}

/** Recursive search algorithm to trace a pointer tree and return results
 * in linked list of data structures.
 */

public SynSet tracePtrs_ds(SynSet synptr, int ptrType, int dbase, int depth) {
    SynSet cursyn, synlist = null, lastsyn = null;
    int tstptrType;
    boolean docoords;
    
    // If synset is a satellite, find the head word of its
    //   head synset and the head word's sense number.

    if (wnUtil.getsstype(synptr.pos.charAt(0)) == WNConsts.SATELLITE) {
	for (int i = 0; i < synptr.pointers.length; i++)
	    if (synptr.pointers[i].ptrType == WNConsts.SIMPTR) {
		cursyn = readSynset(synptr.pointers[i].ppos,
				      synptr.pointers[i].ptroff,
				      "");
		synptr.headWord = cursyn.words[0];
		synptr.headSense = cursyn.lexid[0];
		break;
	    }
    }

    if (ptrType == WNConsts.COORDS) {
	tstptrType = WNConsts.HYPERPTR;
	docoords = true;
    } else {
	tstptrType = ptrType;
	docoords = false;
    }

    for (int i = 0; i < synptr.pointers.length; i++) {
	if ((synptr.pointers[i].ptrType == tstptrType) &&
	   ((synptr.pointers[i].pfrm == 0) ||
	    (synptr.pointers[i].pfrm == synptr.whichWord))) {
	    
	    cursyn=readSynset(synptr.pointers[i].ppos, synptr.pointers[i].ptroff, "");
	    cursyn.searchType = ptrType;

	    if (lastsyn!=null) {
		lastsyn.nextss = cursyn;
            }
	    if (synlist==null) {
		synlist = cursyn;
            }
	    lastsyn = cursyn;

	    if (depth>0) {
		depth = depthCheck(depth, cursyn);
		cursyn.ptrList = tracePtrs_ds(cursyn, ptrType,
					       wnUtil.getpos(cursyn.pos.charAt(0)),
					       (depth+1));
	    } else if (docoords) {
		cursyn.ptrList = tracePtrs_ds(cursyn, WNConsts.HYPOPTR, WNConsts.NOUN, 0);
	    }
	}
    }
    return synlist;
}



public void WNOverview(String searchstr, int pos) {
    SynSet cursyn;
    Index idx = null;
    String cpstring = searchstr;
    int bufstart;
    int offsetcnt;
    boolean svdFlag;
    boolean skipit = false;
    int offsets[] = new int[WNConsts.MAXSENSE];

    cpstring = searchstr;
    bufstart = searchBuffer.length();
    for (int i = 0; i < WNConsts.MAXSENSE; i++) {
	offsets[i] = 0;
    }
    offsetcnt = 0;

    StringBuffer sb = new StringBuffer(4096);
    while ((idx = getIndex(cpstring, pos)) != null) {
	cpstring = null;	// clear for next call to getIndex().
	wnRtl.wnResults.senseCount[wnRtl.wnResults.numForms++] = idx.offset.length;
	wnRtl.wnResults.outSenseCount[wnRtl.wnResults.numForms] = 0;

	printBuffer(
"                                                                                                   "+WNGlobal.lineSeparator);

	// Print synset for each sense.  If requested, precede
	// synset with synset offset and/or lexical file information.

	for (sense = 0; sense < idx.offset.length; sense++) {
            sb.setLength(0);
            //skipit = false;
	    for (int i = 0;  i < offsetcnt && !skipit; i++) {
		if (offsets[i] == idx.offset[sense]) {
		    skipit = true;
                }
            }

	    if (!skipit) {
		offsets[offsetcnt++] = idx.offset[sense];
		cursyn = readSynset(pos, idx.offset[sense], idx.word);
		if (idx.taggedCount != -1 && ((sense + 1) <= idx.taggedCount)) {
		    sb.append(sense + 1).append(". (");
                    sb.append(wnUtil.getTagCount(idx, sense + 1)).append(") ");
		} else {
		    sb.append(sense + 1).append(". ");
		}

		svdFlag = wnRtl.dFlag;
		wnRtl.dFlag = true;
                tmpBuf = sb.toString();
		printSynset(tmpBuf, cursyn, WNGlobal.lineSeparator, DEFON, ALLWORDS,
			    SKIP_ANTS, SKIP_MARKER);
		wnRtl.dFlag = svdFlag;
		wnRtl.wnResults.outSenseCount[wnRtl.wnResults.numForms]++;
		wnRtl.wnResults.printCount++;

	    }
	}

	// Print sense summary message.

	int i = wnRtl.wnResults.outSenseCount[wnRtl.wnResults.numForms];

	if (i > 0) {
            sb.setLength(0);
	    if (i == 1) {
		sb.append(WNGlobal.lineSeparator).append("The ").append(WNGlobal.partNames[pos]).append(" ");
                sb.append(idx.word).append(" has 1 sense");
	    } else {
		sb.append(WNGlobal.lineSeparator).append("The ").append(WNGlobal.partNames[pos]).append(" ");
                sb.append(idx.word).append(" has ").append(i).append(" senses");
            }
	    if (idx.taggedCount > 0) {
		sb.append(" (first ").append(idx.taggedCount).append(" from tagged texts)").append(WNGlobal.lineSeparator);
	    } else if (idx.taggedCount == 0) {
		sb.append(" (no senses from tagged texts)").append(WNGlobal.lineSeparator);
            }

	    searchBuffer.insert(bufstart, sb.toString());
	    bufstart = searchBuffer.length();
	} else {
	    searchBuffer.setLength(bufstart);
        }

	wnRtl.wnResults.numForms++;
    }
}

/** Do requested search on synset passed, returning output in buffer.
 */

public StringBuffer doTrace(SynSet synptr, int ptrType, int dbase, int depth) {
    searchBuffer.setLength(0);	// clear output buffer.
    tracePtrs(synptr, ptrType, dbase, depth);
    return searchBuffer;
}

/** Set bit for each search type that is valid for the search word
 * passed and return bit mask.
 */
  
public int isDefined(String searchstr, int dbase) {
    Index index;
    int retval = 0;

    wnRtl.wnResults.numForms = wnRtl.wnResults.printCount = 0;
    wnRtl.wnResults.searchBuf = null;
    wnRtl.wnResults.searchDs = null;

    while ((index = getIndex(searchstr, dbase)) != null) {
	searchstr = null;	// clear out for next getIndex() call.

	wnRtl.wnResults.senseCount[wnRtl.wnResults.numForms] = index.offset.length;
	
	// set bits that must be true for all words.
	
	retval |= (1<<WNConsts.SIMPTR) | (1<<WNConsts.FREQ) | (1<<WNConsts.SYNS)| (1<<WNConsts.WNGREP) | (1<<WNConsts.OVERVIEW);

	// go through list of pointer characters and set appropriate bits.

	for (int i = 0; i < index.ptruse.length; i++) {

	    if (index.ptruse[i] <= WNConsts.LASTTYPE) {
		retval |= (1<<index.ptruse[i]);
	    }

	    if (index.ptruse[i] >= WNConsts.NOMIN_START && index.ptruse[i] <= WNConsts.NOMIN_END) {
		retval |= (1<<WNConsts.NOMINALIZATIONS);
	    }

	    if (index.ptruse[i] >= WNConsts.ISMEMBERPTR && index.ptruse[i] <= WNConsts.ISPARTPTR) {
		retval |= (1<<WNConsts.HOLONYM);
	    } else if (index.ptruse[i] >= WNConsts.HASMEMBERPTR && index.ptruse[i] <= WNConsts.HASPARTPTR) {
		retval |= (1<<WNConsts.MERONYM);
            }
	 
	    if (index.ptruse[i] == WNConsts.SIMPTR) {
		retval |= (1<<WNConsts.ANTPTR);
	    } 
	}

	if (dbase == WNConsts.NOUN) {
	    retval |= (1<<WNConsts.RELATIVES);

	    // check for inherited holonyms and meronyms.
	    if ((hasHoloMero(index, WNConsts.HMERONYM))>0) {
		retval |= (1<<WNConsts.HMERONYM);
            }
	    if ((hasHoloMero(index, WNConsts.HHOLONYM))>0) {
		retval |= (1<<WNConsts.HHOLONYM);
            }

	    // if synset has hypernyms, enable coordinate search.
	    if ((retval & (1<<WNConsts.HYPERPTR))>0) {
		retval |= (1<<WNConsts.COORDS);
            }
	} else if (dbase == WNConsts.VERB) {

	    // if synset has hypernyms, enable coordinate search.
	    if ((retval & (1<<WNConsts.HYPERPTR))>0) {
		retval |= (1<<WNConsts.COORDS);
            }

	    // enable grouping of related synsets and verb frames.
	    retval |= (1<<WNConsts.RELATIVES) | (1<<WNConsts.FRAMES);
	}

	wnRtl.wnResults.numForms++;
    }
    return retval;
}

/** Determine if any of the synsets that this word is in have inherited
 *  meronyms or holonyms.
 */

public int hasHoloMero(Index index, int ptrType) {
    SynSet synset, psynset;
    int found=0;
    int ptrbase;

    ptrbase = (ptrType == WNConsts.HMERONYM) ? WNConsts.HASMEMBERPTR : WNConsts.ISMEMBERPTR;
    
    for (int i = 0; i < index.offset.length; i++) {
	synset = readSynset(WNConsts.NOUN, index.offset[i], "");
	for (int j = 0; j < synset.pointers.length; j++) {
	    if (synset.pointers[j].ptrType == WNConsts.HYPERPTR) {
		psynset = readSynset(WNConsts.NOUN, synset.pointers[j].ptroff, "");
		found += hasPtr(psynset, ptrbase);
		found += hasPtr(psynset, ptrbase + 1);
		found += hasPtr(psynset, ptrbase + 2);

	    }
	}
    }
    return found;
}

public int hasPtr(SynSet synptr, int ptrType) {
    for (int i = 0; i < synptr.pointers.length; i++) {
        if (synptr.pointers[i].ptrType == ptrType) {
	    return(1);
	}
    }
    return 0;
}

/** For each POS that search word is in, set the relevant bit.<p>
 *
 *  Return 0 if word is not in WordNet.
 */

public int inWN(String word, int pos) {
    int retval = 0;

    if (pos == WNConsts.ALL_POS) {
	for (int i = 1; i < WNConsts.NUMPARTS + 1; i++) {
	    if (wnRtl.indexFiles[i] != null && binSearcher.binSearch(word, wnRtl.indexFiles[i]) != null) {
		retval |= (1<<i);
            }
        }
    } else if (wnRtl.indexFiles[pos] != null && binSearcher.binSearch(word,wnRtl.indexFiles[pos]) != null) {
	    retval |= (1<<pos);
    }
    return retval;
}

public int depthCheck(int depth, SynSet synptr) {
    if (depth >= WNConsts.MAXDEPTH) {
	msgbuf = "WordNet library error: Error Cycle detected"+WNGlobal.lineSeparator+"   "+synptr.words[0];
	WordNet.displayMessage(msgbuf);
	depth = -1;		// reset to get one more trace then quit.
    }
    return depth;
}

/** Strip off () enclosed comments from a word.
 */

public String deadJify(String word) {
    adjMarker = WNConsts.UNKNOWN_MARKER; // default if not adj or unknown.
    
    for (int y=0; y<word.length(); y++) {
	if (word.charAt(y) == '(') {
	    if ("(a)".equals(word.substring(y,y+2))) {
		adjMarker = WNConsts.ATTRIBUTIVE;
	    } else if ("(ip)".equals(word.substring(y,y+3))) {
		adjMarker = WNConsts.IMMED_POSTNOMINAL;
            } else if ("(p)".equals(word.substring(y,y+2))) {
		adjMarker = WNConsts.PREDICATIVE;
            }
	    word = word.substring(y);
            break;
        }
    }
    return word;
}

public int getSearchSense(SynSet synptr, int whichWord) {
   wordbuf = synptr.words[whichWord - 1].replace(' ', '_');
    wnUtil.strToLower(wordbuf);
    Index idx = indexLookup(wordbuf, wnUtil.getpos(synptr.pos.charAt(0)));    
    if (idx != null) {
	for (int i = 0; i < idx.offset.length; i++)
	    if (idx.offset[i] == synptr.hereiam) {
		return(i + 1);
	    }
    }
    return 0;
}

public void printSynset(String head, SynSet synptr, String tail, int definition, int wordnum, int antflag, int markerflag) {
    StringBuffer tbuf = new StringBuffer(1024*16);

    tbuf.append(head);		// print head.

    // Precede synset with additional information as indiecated
    // by flags.

    if (wnRtl.offsetFlag)		// print synset offset.
	tbuf.append("{").append(synptr.hereiam).append("} ");
    if (wnRtl.fileInfoFlag) {		// print lexicographer file information.
	tbuf.append("<").append(WNGlobal.lexFiles[synptr.fnum]).append("> ");
	prlexid = 1;		// print lexicographer id after word.
    } else
	prlexid = 0;
    
    if (wordnum!=0) {			// print only specific word asked for.
	catWord(tbuf, synptr, wordnum - 1, markerflag, antflag);
    } else {			// print all words in synset.
	int wordcnt = synptr.wcount;
        for (int i = 0; i <wordcnt; i++) {
	    catWord(tbuf, synptr, i, markerflag, antflag);
	    if (i < wordcnt - 1) {
		tbuf.append(", ");
            }
	}
    }
    if (definition!=0 && wnRtl.dFlag && synptr.defn!=null) {
	tbuf.append(" -- ");
	tbuf.append(synptr.defn);
    }
    
    tbuf.append(tail);
    printBuffer(tbuf.toString());
}

public void printAntSynset(SynSet synptr, String tail, int anttype, int definition) {
    StringBuffer tbuf = new StringBuffer(1024*16);
    String str;
    boolean first = true;


    if (wnRtl.offsetFlag) {
	tbuf.append("{").append(synptr.hereiam).append("} ");
    }
    if (wnRtl.fileInfoFlag) {
	tbuf.append("<").append(WNGlobal.lexFiles[synptr.fnum]).append("> ");
	prlexid = 1;
    } else {
	prlexid = 0;
    }
    
    // print anotnyms from cluster head (of indirect ant)
    
    tbuf.append("INDIRECT (VIA ");
    int wordcnt = synptr.wcount;
    for (int i = 0; i < wordcnt; i++) {
	if (first) {
	    str = printAnt(WNConsts.ADJ, synptr, i + 1, "%s", ", ");
	    first = false;
	} else {
	    str = printAnt(WNConsts.ADJ, synptr, i + 1, ", %s", ", ");
        }
	if (str!=null) {
	    tbuf.append(str);
        }
    }
    tbuf.append(") . ");
    
    // now print synonyms from cluster head (of indirect ant).
    
   wordcnt = synptr.wcount;
    for (int i = 0; i < wordcnt; i++) {
	catWord(tbuf, synptr, i, SKIP_MARKER, SKIP_ANTS);
	if (i < wordcnt - 1) {
	    tbuf.append(", ");
        }
    }
    
    if (wnRtl.dFlag && synptr.defn!=null && definition!=0) {
	tbuf.append(" -- ");
	tbuf.append(synptr.defn);
    }
    
    tbuf.append(tail);
    printBuffer(tbuf.toString());
}

   public void catWord(StringBuffer buf, SynSet synptr, int wordnum, int adjmarker, int antflag) {
      String vs = " (vs. %s)";
      String[] markers = {
              "",			// UNKNOWN_MARKER
              "(prenominal)",		// ATTRIBUTIVE
              "(postnominal)",	        // IMMED_POSTNOMINAL
              "(predicate)",		// PREDICATIVE
      };
      
      buf.append(deadJify(synptr.words[wordnum]));
      
      // Print additional lexicographer information and WordNet sense
      // number as indicated by flags.
      
      if (prlexid!=0 && (synptr.lexid[wordnum] != 0)) {
          buf.append(synptr.lexid[wordnum]);
      }
      if (wnRtl.wnsnsFlag) {
          buf.append(synptr.wnsns[wordnum]);
      }
   
      // For adjectives, append adjective marker if present, and
      // print antonym if flag is passed.
   
      if (wnUtil.getpos(synptr.pos.charAt(0)) == WNConsts.ADJ) {
         if (adjmarker == PRINT_MARKER) {
             buf.append(markers[adjMarker]); 
         }
         if (antflag == PRINT_ANTS) {
             buf.append(printAnt(WNConsts.ADJ, synptr, wordnum + 1, vs, ""));
         }
      }
   }

public String printAnt(int dbase, SynSet synptr, int wordnum, String template, String tail) {
   int wordoff;
   StringBuffer retbuf = new StringBuffer(1024*16);
   boolean first = true;

    
    // Go through all the pointers looking for anotnyms from the word
    //   indicated by.wordnum.  When found, print all the antonym's
    //   antonym pointers which point back to.wordnum.
    
   for (int i = 0; i < synptr.pointers.length; i++) {
       if (synptr.pointers[i].ptrType == WNConsts.ANTPTR && synptr.pointers[i].pfrm == wordnum) {

           SynSet psynptr = readSynset(dbase, synptr.pointers[i].ptroff, "");

	   for (int j = 0; j < psynptr.pointers.length; j++) {
              if (    psynptr.pointers[j].ptrType == WNConsts.ANTPTR &&
                      psynptr.pointers[j].pto == wordnum &&
                      psynptr.pointers[j].ptroff == synptr.hereiam) {
      
               wordoff = (psynptr.pointers[j].pfrm!=0) ? (psynptr.pointers[j].pfrm - 1) : 0;
      
                // Construct buffer containing formatted antonym,
                // then add it onto end of return buffer.
      
                String tmp = deadJify(psynptr.words[wordoff]);
                StringBuffer tbuf = new StringBuffer(tmp.length()+1024);
                tbuf.append(tmp);
      
                // Print additional lexicographer information and
                // WordNet sense number as indicated by flags.
         
                if (prlexid>0 && (psynptr.lexid[wordoff] != 0)) {
                   tbuf.append(psynptr.lexid[wordoff]);
                }
                if (wnRtl.wnsnsFlag) {
                   tbuf.append("#").append(psynptr.wnsns[wordoff]);
                }
                if (!first) {
                    retbuf.append(tail);
                } else {
                    first = false;
                }
                if (template.equals("%s")) {
                    retbuf.append(tbuf);
                } else if (template.equals(", %s")) {
                    retbuf.append(", ").append(tbuf);
                } else {
                    retbuf.append(" (vs. ").append(tbuf).append(")");
                }
             }
          }
      }
   }
   return retbuf.toString();
}

   public void printBuffer(String string) {
      //if (overflag) {
      //   return;
      //}
      //if (searchBuffer.length() + string.length() >= WNConsts.SEARCHBUF) {
      //   overflag = true;
      //} else { 
         searchBuffer.append(string);
      //}
   }

   public void printSenses(SynSet synptr, int sense) {
      printSense(synptr, sense);
      printSynset("", synptr, WNGlobal.lineSeparator, DEFON, ALLWORDS, PRINT_ANTS, PRINT_MARKER);
   }
   
   public void printSense(SynSet synptr, int sense) {
      String tbuf;
   
      // Append lexicographer filename after Sense # if flag is set.
   
      if (wnRtl.fnFlag) {
         tbuf = WNGlobal.lineSeparator+"Sense "+sense+" in file \""+WNGlobal.lexFiles[synptr.fnum]+"\""+WNGlobal.lineSeparator;
      } else {
         tbuf = WNGlobal.lineSeparator+"Sense "+sense+WNGlobal.lineSeparator;
      }
   
      printBuffer(tbuf);
   
      // update counters.
      wnRtl.wnResults.outSenseCount[wnRtl.wnResults.numForms]++; 
      wnRtl.wnResults.printCount++;
   }

   public void printSpaces(int trace, int depth) {
   
      for (int j = 0; j < depth; j++) {
         printBuffer("    ");
      }
      switch(trace) {
         case TRACEP:		// tracePtrs(), traceNomins().
            if (depth!=0) {
               printBuffer("   ");
            } else {
               printBuffer("       ");
            }
            break;
            
         case TRACEC:		// traceCoords().
            if (depth==0) {
               printBuffer("    ");
            }
            break;
            
         case TRACEI:			// traceInherit().
            if (depth==0) {
               printBuffer(WNGlobal.lineSeparator+"    ");
            }
            break;
      }
   }

   /** Dummy function to force Tcl/Tk to look at event queue to see of
    *  the user wants to stop the search.
    */
   
   public void interfaceDoEvents () {
      // FIXME if (interfaceDoEvents_func != null) {
      // FIXME    interfaceDoEvents_func ();
      // FIXME }
   }
   
   
}
