/**
  
  search.c - WordNet library of search code
  
*/
package opennlp.dictionary.wordnet;

import java.io.RandomAccessFile;
import java.util.Set;
import java.util.HashSet;

public class Search {
   private static String Id = "$Id: Search.java,v 1.1 2002/03/20 20:23:51 mratkinson Exp $";

   // For adjectives, indicates synset type.
   
   public final static int DONT_KNOW	= 0;
   public final static int DIRECT_ANT	= 1;	// direct antonyms (cluster head).
   public final static int INDIRECT_ANT= 2;	// indrect antonyms (similar).
   public final static int PERTAINYM	= 3;	// no antonyms or similars (pertainyms).
   
   // Flags for printsynset().
   
   public final static int ALLWORDS	   = 0;	// print all words.
   public final static int SKIP_ANTS	= 0;	// skip printing antonyms in printsynset().
   public final static int PRINT_ANTS	= 1;	// print antonyms in printsynset().
   public final static int SKIP_MARKER	= 0;	// skip printing adjective marker.
   public final static int PRINT_MARKER= 1;	// print adjective marker.
   
   // Trace types used by printspaces() to determine print sytle.
   
   public final static int TRACEP		= 1;	// traceptrs.
   public final static int TRACEC		= 2;	// tracecoords().
   public final static int TRACEI		= 3;	// traceinherit().
   
   public final static int DEFON       = 1;
   public final static int DEFOFF      = 0;
   
   
   // Static variables.
   
   public  boolean prflag;
   public  int sense, prlexid;
   public  boolean overflag = false;     // set when output buffer overflows.
   public  StringBuffer  searchbuffer = new StringBuffer(1024*64);
   public  int lastholomero;	       // keep track of last holo/meronym printed.
   public final static int TMPBUFSIZE =1024*10;
   public  String tmpbuf;	               // general purpose printing buffer.
   public  String  wdbuf;	               // general purpose word buffer.
   public  String  msgbuf;	       // buffer for constructing error messages.
   public  int adj_marker;
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
   
   public Index index_lookup(String word, int dbase) {
      Index idx = null;
      RandomAccessFile fp;
      String line;
      
      if ((fp = wnRtl.indexfps[dbase]) == null) {
         msgbuf="WordNet library error: "+WNGlobal.partnames[dbase]+" indexfile not open";
         WordNet.display_message(msgbuf);
         return null;
      }
      
      if ((line = binSearcher.bin_search(word, fp)) != null) {
         idx = parse_index( binSearcher.last_bin_search_offset, dbase, line);
      } 
      return idx;
   }

   /** This function parses an entry from an index file into an Index data
    * structure. It takes the byte offset and file number, and optionally the
    * line. If the line is null, parse_index will get the line from the file.
    * If the line is non-null, parse_index won't look at the file, but it still
    * needs the dbase and offset parameters to be set, so it can store them in
    * the Index struct.
    */

   public Index parse_index(long offset, int dbase, String  line) {
       String[] splitLine = wnUtil.split(line," \n");
       //for (int i=0; i<splitLine.length; i++) {
       //    System.out.println("splitLine["+i+"]="+splitLine[i]);
       //}
       Index idx = null;
   
       if (line==null) {
         line = binSearcher.read_index( offset, wnRtl.indexfps[dbase] );
       }
       
       idx = new Index();
   
       // set offset of entry in index file
       idx.idxoffset = offset;
       
       idx.wd="";
       idx.pos="";
       idx.tagged_cnt = 0;
       idx.sense_cnt=0;
       idx.offset=null;
       idx.ptruse=null;
       
       // get the word
       
       int n=0;
       idx.wd = splitLine[n++];
       
       // get the part of speech.
       idx.pos = splitLine[n++];
   
       // get the collins count.
       idx.sense_cnt = Integer.parseInt(splitLine[n++]);
       
       // get the number of pointers types.
       idx.ptruse = new int[Integer.parseInt(splitLine[n++])];
       
       if (idx.ptruse.length>0) {
          // get the pointers types.
          for (int j=0; j < idx.ptruse.length; j++) {
             idx.ptruse[j] = wnUtil.getptrtype(splitLine[n++]);
          }
       }
       
       // get the number of offsets.
       int off_cnt = Integer.parseInt(splitLine[n++]);
       
       if ("1.6".equals(WNGlobal.wnrelease) || "1.7".equals(WNGlobal.wnrelease)) {
          // get the number of senses that are tagged.
          idx.tagged_cnt = Integer.parseInt(splitLine[n++]);
       } else {
          idx.tagged_cnt = -1;
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

   public Index getindex(String searchstr, int dbase) {
      Index idx;
      String[] strings = new String[WNConsts.MAX_FORMS]; // vector of search strings.
      Index[] offsets = new Index[WNConsts.MAX_FORMS];
      
      // This works like strrok(): if passed with a non-null string,
      // prepare vector of search strings and offsets.  If string
      // is null, look at current list of offsets and return next
      // one, or null if no more alternatives for this word.
      
      if (searchstr != null) {
      
          int offset = 0;
          wnUtil.strtolower(searchstr);
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
          
          offsets[0] = index_lookup(strings[0], dbase);
          
          for (int i = 1; i < WNConsts.MAX_FORMS; i++) {
             if (!(strings[0].equals(strings[i]))) {
                offsets[i] = index_lookup(strings[i], dbase);
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

   public SynSet read_synset(int dbase, long boffset, String word) {
       try {
            SynsetKey key = new SynsetKey(dbase, boffset, word);
            SynSet synset = (SynSet)synsetCache.get(key);
            if (synset != null) {
                return synset;
            }
            RandomAccessFile fp = wnRtl.datafps[dbase];
            
            if (fp == null) {
                msgbuf = "WordNet library error: "+WNGlobal.partnames[dbase]+" datafile not open";
                WordNet.display_message(msgbuf);
                return null;
            }
            
            fp.seek(boffset);	// position file to byte offset requested.
            
            synset = parse_synset(fp, dbase, word); // parse synset and return.
            synsetCache.put(key, synset);
            return synset;
       } catch (Exception e) {
           e.printStackTrace();
       }
       return new SynSet();
   }

   private java.util.Map synsetCache = new java.util.HashMap();
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

   public SynSet parse_synset(RandomAccessFile fp, int dbase, String word) {
       try {
            String tbuf;
            String ptrtok;
            byte[] tmpptr = new byte[WNConsts.LINEBUF];
            int foundpert = 0;
            String wdnum;
        
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
            synptr.whichword = 0;
            synptr.pointers=null;
            synptr.frames = null;
            synptr.defn = "";
            synptr.nextss = null;
            synptr.nextform = null;
            synptr.searchtype = -1;
            synptr.ptrlist = null;
            synptr.headword = null;
            synptr.headsense = 0;
            
            String[] splitLine = wnUtil.split(line," \n");
            int n=0;

            synptr.hereiam = Integer.parseInt(splitLine[n++]);
        
            // sanity check - make sure starting file offset matches first field
            if (synptr.hereiam != loc) {
                   msgbuf = "WordNet library error: no synset at location "+loc;
                   WordNet.display_message(msgbuf);
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
              if (word!=null && word.equals(wnUtil.strtolower(splitLine[n-1]))) {
                 synptr.whichword = i+1;
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
               synptr.pointers[i].ptrtyp = wnUtil.getptrtype(splitLine[n++]);
               // For adjectives, set the synset type if it has a direct
               // antonym
               if (dbase == WNConsts.ADJ && synptr.sstype == DONT_KNOW) {
                    if (synptr.pointers[i].ptrtyp == WNConsts.ANTPTR) {
                        synptr.sstype = DIRECT_ANT;
                    } else if (synptr.pointers[i].ptrtyp == WNConsts.PERTPTR) {
                        foundpert = 1;
                    }
               }
              
               // get the pointer offset
               synptr.pointers[i].ptroff = Integer.parseInt(splitLine[n++]);
              
               // get the pointer part of speech
               synptr.pointers[i].ppos = wnUtil.getpos(splitLine[n++].charAt(0));
              
               // get the lexp to/from restrictions
               wdnum= splitLine[n].substring(0,2);
               synptr.pointers[i].pfrm = Integer.parseInt(wdnum, 16);
              
               wdnum= splitLine[n++].substring(2,4);
               synptr.pointers[i].pto = Integer.parseInt(wdnum, 16);
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
             synptr.wnsns[i] = getsearchsense(synptr, i + 1);
          }
    
          return synptr;
       } catch (Exception e) {
           e.printStackTrace();
       }
       return new SynSet();
   }


/** Recursive search algorithm to trace a pointer tree
 */

public void traceptrs(SynSet synptr, int ptrtyp, int dbase, int depth) {
    int extraindent = 0;
    SynSet cursyn;
    String prefix="";
    String tbuf;

    interface_doevents();
    if (wnRtl.abortsearch) {
	return;
    }

    if (ptrtyp < 0) {
	ptrtyp = -ptrtyp;
	extraindent = 2;
    }
    
    for (int i = 0; i < synptr.pointers.length; i++) {
	if ((synptr.pointers[i].ptrtyp == ptrtyp) &&
	   ((synptr.pointers[i].pfrm == 0) ||
	    (synptr.pointers[i].pfrm == synptr.whichword))) {

	    if (!prflag) {	// print sense number and synset
		printsns(synptr, sense + 1);
		prflag = true;
	    }
	    printspaces(TRACEP, depth + extraindent);

	    switch(ptrtyp) {
                case WNConsts.PERTPTR:
                    if (dbase == WNConsts.ADV) {
                        prefix = "Derived from "+WNGlobal.partnames[synptr.pointers[i].ppos]+" ";
                    } else {
                        prefix = "Pertains to "+WNGlobal.partnames[synptr.pointers[i].ppos]+" ";
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
	    cursyn=read_synset(synptr.pointers[i].ppos, synptr.pointers[i].ptroff, "");

	    // For Pertainyms and Participles pointing to a specific
	    //   sense, indicate the sense then retrieve the synset
	    //   pointed to and other info as determined by type.
	    //   Otherwise, just print the synset pointed to.

	    if ((ptrtyp == WNConsts.PERTPTR || ptrtyp == WNConsts.PPLPTR) &&
		synptr.pointers[i].pto != 0) {
		tbuf = " (Sense "+cursyn.wnsns[synptr.pointers[i].pto - 1]+")"+WNGlobal.lineSeparator;
		printsynset(prefix, cursyn, tbuf, DEFOFF, synptr.pointers[i].pto,
			    SKIP_ANTS, PRINT_MARKER);
		if (ptrtyp == WNConsts.PPLPTR) { // adjective pointing to verb
		    printsynset("      =>", cursyn, WNGlobal.lineSeparator,
				DEFON, ALLWORDS, PRINT_ANTS, PRINT_MARKER);
		    traceptrs(cursyn, WNConsts.HYPERPTR, wnUtil.getpos(cursyn.pos.charAt(0)), 0);
		} else if (dbase == WNConsts.ADV) { // adverb pointing to adjective
		    printsynset("      =>", cursyn, WNGlobal.lineSeparator, DEFON, ALLWORDS, 
				((wnUtil.getsstype(cursyn.pos.charAt(0)) == WNConsts.SATELLITE)
				 ? SKIP_ANTS : PRINT_ANTS), PRINT_MARKER);
//#ifdef FOOP
 		    traceptrs(cursyn, WNConsts.HYPERPTR, wnUtil.getpos(cursyn.pos.charAt(0)), 0);
//#endif
		} else {	// adjective pointing to noun
		    printsynset("      =>", cursyn, WNGlobal.lineSeparator,
				DEFON, ALLWORDS, PRINT_ANTS, PRINT_MARKER);
		    traceptrs(cursyn, WNConsts.HYPERPTR, wnUtil.getpos(cursyn.pos.charAt(0)), 0);
		}
	    } else if (ptrtyp == WNConsts.ANTPTR && dbase != WNConsts.ADJ && synptr.pointers[i].pto != 0) {
		tbuf = " (Sense "+cursyn.wnsns[synptr.pointers[i].pto - 1]+")"+WNGlobal.lineSeparator;
		printsynset(prefix, cursyn, tbuf, DEFOFF, synptr.pointers[i].pto,
			    SKIP_ANTS, PRINT_MARKER);
		printsynset("      =>", cursyn, WNGlobal.lineSeparator, DEFON, ALLWORDS,
			    PRINT_ANTS, PRINT_MARKER);
	    } else {
		printsynset(prefix, cursyn, WNGlobal.lineSeparator, DEFON, ALLWORDS,
			    PRINT_ANTS, PRINT_MARKER);
            }

	    // For HOLONYMS and MERONYMS, keep track of last one
	    //   printed in buffer so results can be truncated later.

	    if (ptrtyp >= WNConsts.ISMEMBERPTR && ptrtyp <= WNConsts.HASPARTPTR) {
		lastholomero = searchbuffer.length();
            }

	    if (depth>0) {
		depth = depthcheck(depth, cursyn);
		traceptrs(cursyn, ptrtyp, wnUtil.getpos(cursyn.pos.charAt(0)), (depth+1));

	    }
       }
    }
}

public void tracecoords(SynSet synptr, int ptrtyp, int dbase, int depth) {
    SynSet cursyn;

    interface_doevents();
    if (wnRtl.abortsearch) {
	return;
    }

    for (int i = 0; i < synptr.pointers.length; i++) {
	if ((synptr.pointers[i].ptrtyp == WNConsts.HYPERPTR) &&
	   ((synptr.pointers[i].pfrm == 0) ||
	    (synptr.pointers[i].pfrm == synptr.whichword))) {
	    
	    if (!prflag) {
		printsns(synptr, sense + 1);
		prflag = true;
	    }
	    printspaces(TRACEC, depth);

	    cursyn = read_synset(synptr.pointers[i].ppos, synptr.pointers[i].ptroff, "");

	    printsynset(". ", cursyn, WNGlobal.lineSeparator, DEFON, ALLWORDS,
			SKIP_ANTS, PRINT_MARKER);

	    traceptrs(cursyn, ptrtyp, wnUtil.getpos(cursyn.pos.charAt(0)), depth);
	    
	    if (depth>0) {
		depth = depthcheck(depth, cursyn);
		tracecoords(cursyn, ptrtyp, wnUtil.getpos(cursyn.pos.charAt(0)), (depth+1));
	    }
	}
    }
}

public void tracenomins(SynSet synptr, int dbase) {
    int j;
    int idx=0;
    SynSet cursyn;
    int[] prlist = new int[32];

    interface_doevents();
    if (wnRtl.abortsearch) {
	return;
    }

    for (int i = 0; i < synptr.pointers.length; i++) {
	if ((synptr.pointers[i].ptrtyp >= WNConsts.NOMIN_START) &&
	    (synptr.pointers[i].ptrtyp <= WNConsts.NOMIN_END)) {

	    if (!prflag) {
		printsns(synptr, sense + 1);
		prflag = true;
	    }
	    cursyn = read_synset(synptr.pointers[i].ppos, synptr.pointers[i].ptroff, "");

	    for (j = 0; j < idx; j++) {
		if (synptr.pointers[i].ptroff == prlist[j]) {
		    break;
		}
	    }

	    if (j == idx) {
		prlist[idx++] = synptr.pointers[i].ptroff;
		printspaces(TRACEP, 0);
		printsynset("<. ", cursyn, WNGlobal.lineSeparator, DEFON, ALLWORDS,
			    SKIP_ANTS, PRINT_MARKER);
	    }

	}
    }
}

/** Trace through the hypernym tree and print all MEMBER, STUFF
 *  and PART info.
 */

public void traceinherit(SynSet synptr, int ptrbase, int dbase, int depth) {
    SynSet cursyn;

    interface_doevents();
    if (wnRtl.abortsearch) {
	return;
    }
    
    for (int i=0; i<synptr.pointers.length; i++) {
	if ((synptr.pointers[i].ptrtyp == WNConsts.HYPERPTR) &&
	   ((synptr.pointers[i].pfrm == 0) ||
	    (synptr.pointers[i].pfrm == synptr.whichword))) {
	    
	    if (!prflag) {
		printsns(synptr, sense + 1);
		prflag = true;
	    }
	    printspaces(TRACEI, depth);
	    
	    cursyn = read_synset(synptr.pointers[i].ppos, synptr.pointers[i].ptroff, "");

	    printsynset("=> ", cursyn, WNGlobal.lineSeparator, DEFON, ALLWORDS, SKIP_ANTS, PRINT_MARKER);
	    
	    traceptrs(cursyn, ptrbase, WNConsts.NOUN, depth);
	    traceptrs(cursyn, ptrbase + 1, WNConsts.NOUN, depth);
	    traceptrs(cursyn, ptrbase + 2, WNConsts.NOUN, depth);
	    
	    if (depth>0) {
		depth = depthcheck(depth, cursyn);
		traceinherit(cursyn, ptrbase, wnUtil.getpos(cursyn.pos.charAt(0)), (depth+1));
	    }
	}
    }

    // Truncate search buffer after last holo/meronym printed */
    searchbuffer.setLength(lastholomero);
}

public void partsall(SynSet synptr, int ptrtyp) {
    //int hasptr = 0;
    
    int ptrbase = (ptrtyp == WNConsts.HMERONYM) ? WNConsts.HASMEMBERPTR : WNConsts.ISMEMBERPTR;
    
    // First, print out the MEMBER, STUFF, PART info for this synset

    for (int i = 0; i < 3; i++) {
	if (HasPtr(synptr, ptrbase + i)!=0) {
	    traceptrs(synptr, ptrbase + i, WNConsts.NOUN, 1);
	}
	interface_doevents();
	if (wnRtl.abortsearch) {
	    return;
        }
    }

    // Print out MEMBER, STUFF, PART info for hypernyms on
    // HMERONYM search only.

    if (ptrtyp == WNConsts.HMERONYM) {
	lastholomero = searchbuffer.length();
	traceinherit(synptr, ptrbase, WNConsts.NOUN, 1);
    }
}

public void traceadjant(SynSet synptr) {
    SynSet newsynptr;
    int anttype = DIRECT_ANT;
    SynSet simptr, antptr;
    final String similar = "        => ";

    // This search is only applicable for ADJ synsets which have
    // either direct or indirect antonyms (not valid for pertainyms).
    
    if (synptr.sstype == DIRECT_ANT || synptr.sstype == INDIRECT_ANT) {
	printsns(synptr, sense + 1);
	printbuffer(WNGlobal.lineSeparator);
	
	// if indirect, get cluster head.
	
	if (synptr.sstype == INDIRECT_ANT) {
	    anttype = INDIRECT_ANT;
            int i=0;
	    while (synptr.pointers[i].ptrtyp != WNConsts.SIMPTR) i++;
	    newsynptr = read_synset(WNConsts.ADJ, synptr.pointers[i].ptroff, "");
	} else {
	    newsynptr = synptr;
        }
	
	// Find antonyms - if direct, make sure that the antonym
	// ptr we're looking at is from this word.
	
	for (int i = 0; i < newsynptr.pointers.length; i++) {

	    if (newsynptr.pointers[i].ptrtyp == WNConsts.ANTPTR &&
		((anttype == DIRECT_ANT &&
		  newsynptr.pointers[i].pfrm == newsynptr.whichword) ||
		 (anttype == INDIRECT_ANT))) {
		
		// Read the antonym's synset and print it.  if a
		// direct antonym, print it's satellites.
		
		antptr = read_synset(WNConsts.ADJ, newsynptr.pointers[i].ptroff, "");
    
		if (anttype == DIRECT_ANT) {
		    printsynset("", antptr, WNGlobal.lineSeparator, DEFON, ALLWORDS,
				PRINT_ANTS, PRINT_MARKER);
		    for (int j = 0; j < antptr.pointers.length; j++) {
			if (antptr.pointers[j].ptrtyp == WNConsts.SIMPTR) {
			    simptr = read_synset(WNConsts.ADJ, antptr.pointers[j].ptroff, "");
			    printsynset(similar, simptr, WNGlobal.lineSeparator, DEFON,
					ALLWORDS, SKIP_ANTS, PRINT_MARKER);
			}
		    }
		} else {
		    printantsynset(antptr, WNGlobal.lineSeparator, anttype, DEFON);
                }
	    }
	}
    }
}


/** Fetch the given example sentence from the example file and print it out.
 */

public void getexample(String offset, String wd) {
    if (wnRtl.vsentfilefp != null) {
        String line = binSearcher.bin_search(offset, wnRtl.vsentfilefp);
	if (line != null) {
	    for (int i=0; i<line.length(); i++) {
                if (line.charAt(i)==' ') {
	     	   line=line.substring(0,i);
                   break;
                }
            }

	    printbuffer("          EX: ");
	    printbuffer(line);
	    printbuffer(wd);
	}
    }
}

/** Find the example sentence references in the example sentence index file.
 */

public boolean findexample(SynSet synptr) {
    boolean found = false;
    
    if (wnRtl.vidxfilefp != null) {
	int wdnum = synptr.whichword - 1;

        String tbuf = synptr.words[wdnum]+"%"+wnUtil.getpos(synptr.pos.charAt(0))+":"+
                synptr.fnum+":"+synptr.lexid[wdnum]+"::";

        String temp = binSearcher.bin_search(tbuf, wnRtl.vidxfilefp);
	if (temp != null) {

	    // skip over sense key and get sentence numbers.

	    temp += synptr.words[wdnum].length() + 11;
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
		getexample(strings[i], synptr.words[wdnum]);
	    }
	    found = true;
	}
    }
    return found;
}

public void printframe(SynSet synptr, boolean prsynset) {

    if (prsynset) {
	printsns(synptr, sense + 1);
    }
    
    if (!findexample(synptr)) {
	for (int i = 0; i < synptr.frames.length; i++) {
	    if ((synptr.frames[i].frmto == synptr.whichword) ||
		(synptr.frames[i].frmto == 0)) {
		if (synptr.frames[i].frmto == synptr.whichword) {
		    printbuffer("          => ");
                } else {
		    printbuffer("          *> ");
                }
		printbuffer(WNGlobal.frametext[synptr.frames[i].frmid]);
		printbuffer(WNGlobal.lineSeparator);
	    }
	}
    }
}

public void printseealso(SynSet synptr) {
    SynSet cursyn;
    boolean first = true;
    boolean svwnsnsflag;
    String firstline = "          Also See. ";
    String otherlines = "; ";
    String prefix = firstline;

    // Find all SEEALSO pointers from the searchword and print the
    //   word or synset pointed to.

    for (int i = 0; i < synptr.pointers.length; i++) {
	if ((synptr.pointers[i].ptrtyp == WNConsts.SEEALSOPTR) &&
	    ((synptr.pointers[i].pfrm == 0) ||
	     (synptr.pointers[i].pfrm == synptr.whichword))) {

	    cursyn = read_synset(synptr.pointers[i].ppos, synptr.pointers[i].ptroff, "");

	    svwnsnsflag = wnRtl.wnsnsflag;
	    wnRtl.wnsnsflag = true;
	    printsynset(prefix, cursyn, "", DEFOFF,
			synptr.pointers[i].pto == 0 ? ALLWORDS : synptr.pointers[i].pto,
			SKIP_ANTS, SKIP_MARKER);
	    wnRtl.wnsnsflag = svwnsnsflag;


	    if (first) {
		prefix = otherlines;
		first = false;
	    }
	}
    }
    if (!first) {
	printbuffer(WNGlobal.lineSeparator);
    }
}

private static String[] a_an = {
	"", "a noun", "a verb", "an adjective", "an adverb" };
private static String[] freqcats = {
	"extremely rare","very rare","rare","uncommon","common",
	"familiar","very familiar","extremely familiar"
};
public void freq_word(Index index) {
    int familiar=0;
    int cnt;

    if (index!=null) {
	cnt = index.sense_cnt;
	if (cnt == 0) { familiar = 0; }
	if (cnt == 1) { familiar = 1; }
	if (cnt == 2) { familiar = 2; }
	if (cnt >= 3 && cnt <= 4) { familiar = 3; }
	if (cnt >= 5 && cnt <= 8) { familiar = 4; }
	if (cnt >= 9 && cnt <= 16) { familiar = 5; }
	if (cnt >= 17 && cnt <= 32) { familiar = 6; }
	if (cnt > 32 ) familiar = 7;
	
	tmpbuf = WNGlobal.lineSeparator+index.wd+" used as "+
                 a_an[wnUtil.getpos(index.pos.charAt(0))]+" is "+
                 freqcats[familiar]+" (polysemy count = "+cnt+")"+
                 WNGlobal.lineSeparator;
	printbuffer(tmpbuf);
    }
}

public static int fgets(RandomAccessFile file, byte[] line_buf) {
    //int n=0;
    try {
        int size=file.read(line_buf);
        for (int i=0; i<size; i++) {
            if (line_buf[i]=='\n' || line_buf[i]=='\r') {
                return i;
            }
        }
        return (size>0) ? size : -1;
    } catch (java.io.IOException e) {
        e.printStackTrace();
        return -1;
    } catch (Exception e) {
        e.printStackTrace();
        return -1;
    }
}

public void wngrep (String word_passed, int pos) {
    try {
       RandomAccessFile inputfile;
       int wordlen, linelen, loc;
       byte[] line_buf = new byte[1024];
       String line;
       int count = 0;
       int size;
    
       inputfile = wnRtl.indexfps[pos];
       if (inputfile == null) {
          msgbuf = "WordNet library error: Can't perform compounds " +
                   "search because "+WNGlobal.partnames[pos]+" index file is not open"+WNGlobal.lineSeparator;
          WordNet.display_message (msgbuf);
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
                tmpbuf = line+WNGlobal.lineSeparator;
                printbuffer (tmpbuf);
                break;
             }
          }
          if (count++ % 2000 == 0) {
             interface_doevents ();
             if (wnRtl.abortsearch) {
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
            findsisters(idx);
            interface_doevents();
            if (wnRtl.abortsearch) {
                break;
            }
            findtwins(idx);
            interface_doevents();
            if (wnRtl.abortsearch) {
                break;
            }
            findcousins(idx);
            interface_doevents();
            if (wnRtl.abortsearch) {
                break;
            }
            printrelatives(idx, WNConsts.NOUN);
            break;
        case WNConsts.VERB:
            findverbgroups(idx);
            interface_doevents();
            if (wnRtl.abortsearch) {
                break;
            }
            printrelatives(idx, WNConsts.VERB);
            break;
        default:
            break;
    }
}


/** Look for 'twins' - synsets with 3 or more words in common.
 */

public int word_idx(String wd, String wdtable[], int nwords) {
     for ( ; --nwords >= 0 && !(wd.equals(wdtable[nwords])); )
	  ;
     return nwords;
}

public int add_word(String wd, String wdtable[], int nwords) {
     wdtable[nwords] = wd;
     return nwords;
}

public final static int MAXWRDS = 300;

public void findtwins(Index idx) {
     String[] words = new String[MAXWRDS];
     Set[] s = new HashSet[WNConsts.MAXSENSE];
     int nwords =0;

     if (idx==null) {
         throw new NullPointerException("idx must not be null");
     }
     for (int i = 0; i < idx.offset.length; i++) {

	  SynSet synset = read_synset(WNConsts.NOUN, idx.offset[i], "");

	  s[i] = new HashSet(MAXWRDS);
	  if (synset.wcount >=  3) {
	       for (int j = 0; j < synset.wcount; j++) {
		    String buf = wnUtil.strtolower(synset.words[j]);
		    int k = word_idx(buf, words, nwords);
		    if (k < 0) {
			 k = add_word(buf, words, nwords);
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
	         add_relatives(WNConsts.NOUN, idx, j, i);
             }
         }
     }
}

private static class Hypers {public int id;  public int off;}
private Hypers[] hypers = new Hypers[HASHTABSIZE];

/** Look for 'sisters' - senses of the search word with a common parent.
 */
public void findsisters(Index idx) {
     int id = 0;
     Set[] syns = new HashSet[WNConsts.MAXSENSE];

     if (idx==null) {
         throw new NullPointerException("idx should not be null");
     }

     // Read all synsets and list all hyperptrs.

     for (int i = 0; i < idx.offset.length; i++) {
	  SynSet synset = read_synset(WNConsts.NOUN, idx.offset[i], idx.wd);
	  if (synset==null) {
              throw new NullPointerException("");
          }
	  syns[i] = new HashSet(4*WNConsts.MAXSENSE);
	  
	  for (int j = 0; j < synset.pointers.length; j++) {
	       if (synset.pointers[j].ptrtyp == WNConsts.HYPERPTR) { 
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
	           add_relatives(WNConsts.NOUN, idx, i, j);
               }
         }
     }
}

/** Look for 'cousins' - two senses, each under a different predefined
 *  top node pair.  Top node offset pairs are stored in cousin.tops.
 * Return index of topnode if it exists.
 */

public int find_topnode( int offset) {
     // test to see whether the Cousin files exist (they do not for 1.7)
     if (wnRtl.cousinfp==null) {  return -1; }

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

public int new_topnode( int offset) {
     // test to see whether the Cousin files exist (they do not for 1.7)
     if (wnRtl.cousinfp==null) {  return -1; }
        
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

public void add_topnode(int index, int id, Set s,  int offset) {
    if ((index >= 0) && (index < HASHTABSIZE)) {
	cousintops[index].rels   = s;
	cousintops[index].topnum = id;
	cousintops[index].offset = offset;
    }
}

public void clear_topnodes() {
    for (int i = 0; i < HASHTABSIZE; i++) {
	cousintops[i].offset = 0;
    }
}

/** Read cousin.tops file (one time only) and store in different form.
 */

private boolean read_cousintops_done = false;
public int read_cousintops() {
   try {
        int id = 0;
        int top1, top2;
        int tidx1, tidx2;
        
        if (read_cousintops_done) { return 0; }
        
        // test to see whether the Cousin files exist (they do not for 1.7)
        if (wnRtl.cousinfp==null) {  return 0; }
        
        wnRtl.cousinfp.seek(0);
        clear_topnodes();
        
        top1=1;
        while (top1>0) {
            top1=0; top2=0;
            if ((tidx1 = find_topnode(top1)) < 0) {
                if ((tidx1 = new_topnode(top1)) != -1) {
                    add_topnode(tidx1, id++, new HashSet(MAXTOPS), top1);
                } else {
                    WordNet.display_message("WordNet library error: cannot create topnode table for grouped sarches");
                    return -1;
                }
            }
         
            if ((tidx2 = find_topnode(top2)) < 0) {
                if ((tidx2 = new_topnode(top2)) != -1) {
                    add_topnode(tidx2, id++, new HashSet(MAXTOPS), top2);
                } else {
                    WordNet.display_message("WordNet library error: cannot create topnode table for grouped sarches");
                    return -1;
                }
            }
         
            cousintops[tidx1].rels.add(new Integer( cousintops[tidx2].topnum) );
            cousintops[tidx2].rels.add(new Integer( cousintops[tidx1].topnum) );
        }
        read_cousintops_done = true;
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
         searcher.record_topnode(hyperptr, (Set[])cp);
    }
}

public void record_topnode( int hyperptr, Set[] sets) {
     if (sets==null) {
         throw new NullPointerException("sets should not be null");
     }
     int i = find_topnode(hyperptr);
     if (i >= 0) {
	 sets[0].add(new Integer(cousintops[i].topnum) );
	 sets[1].addAll(cousintops[i].rels);
     }
}

public void findcousins(Index idx) {
     SynSet synset;
     Set[][] syns_tops = new HashSet[WNConsts.MAXSENSE][2];
     
     if (idx==null) {
         throw new NullPointerException("idx should not be null");
     }
     if (read_cousintops() != 0)
	 return;

     // First read all the synsets.

     int nsyns;
     for (nsyns = 0; nsyns < idx.offset.length; nsyns++) { // why -1 in orig?
	  synset = read_synset(WNConsts.NOUN, idx.offset[nsyns], "");
	  syns_tops[nsyns][0] = new HashSet(MAXTOPS);
	  syns_tops[nsyns][1] = new HashSet(MAXTOPS);

	  record_topnode(idx.offset[nsyns], syns_tops[nsyns]);

          
	  trace_hyperptrs(synset, new RecordTopnode(),  syns_tops[nsyns], 1);
     }
     
     for (int i = 0; i < nsyns; i++) {
         for (int j = i + 1; j < nsyns; j++) {
             Set n = new HashSet(syns_tops[i][0]);
             n.retainAll(syns_tops[j][1]);
	       if (!n.isEmpty()) {
		    add_relatives(WNConsts.NOUN, idx, i, j);
               }
         }
     }
}


/** Trace through HYPERPTRs up to MAXDEPTH, running `fn()' on each one.
 */ 

public void trace_hyperptrs(SynSet synptr, TraceHyperPtr tracer, Object cp, int depth) {
     SynSet s;
     
     if (depth >= WNConsts.MAXDEPTH) {
	  return;
     }
     for (int i = 0; i < synptr.pointers.length; i++)
	  if (synptr.pointers[i].ptrtyp == WNConsts.HYPERPTR) {
	       tracer.fn(this, synptr.pointers[i].ptroff, cp);
	       
	       s = read_synset(synptr.pointers[i].ppos, synptr.pointers[i].ptroff, "");
	       trace_hyperptrs(s, tracer, cp, depth+1);
	  }
}



public void findverbgroups(Index idx) {
     SynSet synset;

     if (idx==null) {
         throw new NullPointerException("idx should not be null");
     }

     // Read all senses.
     
     for (int i = 0; i < idx.offset.length; i++) {

	 synset = read_synset(WNConsts.VERB, idx.offset[i], idx.wd);
	
	 // Look for VERBGROUP ptr(s) for this sense.  If found,
	 // create group for senses, or add to existing group.

	 for (int j = 0; j < synset.pointers.length; j++) {
	       if (synset.pointers[j].ptrtyp == WNConsts.VERBGROUP) {
		   // Need to find sense number for ptr offset.
		   for (int k = 0; k < idx.offset.length; k++) {
		       if (synset.pointers[j].ptroff == idx.offset[k]) {
			   add_relatives(WNConsts.VERB, idx, i, k);
			   break;
		       }
		   }
	       }
	   }
     }
}

public void add_relatives(int pos, Index idx, int rel1, int rel2)
{
    RelGrp rel, last, r;
    // First make sure that senses are not on the excpetion list.
    if (pos == WNConsts.NOUN && groupexc(idx.offset[rel1], idx.offset[rel2])) {
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


public boolean groupexc( int off1,  int off2) {
    if (wnRtl.cousinexcfp==null) {
        return false;
    }
    String buf = "" + (off1 < off2 ? off1 : off2);
    String searchResult = binSearcher.bin_search(buf, wnRtl.cousinexcfp);
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



public void printrelatives(Index idx, int dbase) {
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
		synptr = read_synset(dbase, idx.offset[i], "");
		printsns(synptr, i + 1);
		traceptrs(synptr, WNConsts.HYPERPTR, dbase, 0);
		outsenses[i] = 1;
	    }
	}
	if (flag) {
	    printbuffer("--------------"+WNGlobal.lineSeparator);
        }
    }

    for (int i = 0; i < idx.offset.length; i++) {
	if (outsenses[i]==0) {
	    synptr = read_synset(dbase, idx.offset[i], "");
	    printsns(synptr, i + 1);
	    traceptrs(synptr, WNConsts.HYPERPTR, dbase, 0);
	    printbuffer("--------------"+WNGlobal.lineSeparator);
	}
    }
}


/**
 * Search code interfaces to WordNet database.
 *
 * findtheinfo() - print search results and return ptr to output buffer
 * findtheinfo_ds() - return search results in linked list data structrure
*/

public String findtheinfo(String searchstr, int dbase, int ptrtyp, int whichsense) {
    SynSet cursyn;
    Index idx = null;
    int depth = 0;
    int offsetcnt;
    int bufstart;
    int[] offsets = new int[WNConsts.MAXSENSE];
    boolean skipit = false;

    // Initializations -
    // clear output buffer, search results structure, flags.

    searchbuffer.setLength(0);

    wnRtl.wnresults = new SearchResults();
    wnRtl.wnresults.numforms = wnRtl.wnresults.printcnt = 0;
    wnRtl.wnresults.searchbuf = searchbuffer.toString();
    wnRtl.wnresults.searchds = null;

    wnRtl.abortsearch = false;
    boolean overflag = false;
    
    for (int i = 0; i < WNConsts.MAXSENSE; i++) {
	offsets[i] = 0;
    }

    switch (ptrtyp) {
    case WNConsts.OVERVIEW:
	WNOverview(searchstr, dbase);
	break;
    case WNConsts.FREQ:
	while ((idx = getindex(searchstr, dbase)) != null) {
	    searchstr = null;
	    wnRtl.wnresults.senseCount[wnRtl.wnresults.numforms] = idx.offset.length;
	    freq_word(idx);
	    wnRtl.wnresults.numforms++;
	}
	break;
    case WNConsts.WNGREP:
	wngrep(searchstr, dbase);
	break;
    case WNConsts.WNESCORT:
	searchbuffer.setLength(0);
        searchbuffer.append("Sentences containing "+searchstr+" will be displayed in the Escort window.");
	break;
    case WNConsts.RELATIVES:
    case WNConsts.VERBGROUP:
	while ((idx = getindex(searchstr, dbase)) != null) {
	    searchstr = null;
	    wnRtl.wnresults.senseCount[wnRtl.wnresults.numforms] = idx.offset.length;
	    relatives(idx, dbase);
	    wnRtl.wnresults.numforms++;
	}
	break;
    default:

	// If negative search type, set flag for recursive search
	if (ptrtyp < 0) {
	    ptrtyp = -ptrtyp;
	    depth = 1;
	}
	bufstart = searchbuffer.length();
	offsetcnt = 0;

	// look at all spellings of word

	while ((idx = getindex(searchstr, dbase)) != null) {

	    searchstr = null;	// clear out for next call to getindex()
	    wnRtl.wnresults.senseCount[wnRtl.wnresults.numforms] = idx.offset.length;
	    wnRtl.wnresults.outSenseCount[wnRtl.wnresults.numforms] = 0;

	    // Print extra sense msgs if looking at all senses
	    if (whichsense == WNConsts.ALLSENSES) {
		printbuffer(
"                                                                         "+WNGlobal.lineSeparator);
            }

	    // Go through all of the searchword's senses in the
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
		    	cursyn = read_synset(dbase, idx.offset[sense], idx.wd);
		    	switch(ptrtyp) {
		    	case WNConsts.ANTPTR:
                            if (dbase == WNConsts.ADJ) {
			    	traceadjant(cursyn);
                            } else {
			    	traceptrs(cursyn, WNConsts.ANTPTR, dbase, depth);
                            }
			    break;
		   	 
		    	case WNConsts.COORDS:
			    tracecoords(cursyn, WNConsts.HYPOPTR, dbase, depth);
			    break;
		   	 
		    	case WNConsts.FRAMES:
			    printframe(cursyn, true);
			    break;
			    
		    	case WNConsts.MERONYM:
			    traceptrs(cursyn, WNConsts.HASMEMBERPTR, dbase, depth);
			    traceptrs(cursyn, WNConsts.HASSTUFFPTR, dbase, depth);
			    traceptrs(cursyn, WNConsts.HASPARTPTR, dbase, depth);
			    break;
			    
		    	case WNConsts.HOLONYM:
			    traceptrs(cursyn, WNConsts.ISMEMBERPTR, dbase, depth);
			    traceptrs(cursyn, WNConsts.ISSTUFFPTR, dbase, depth);
			    traceptrs(cursyn, WNConsts.ISPARTPTR, dbase, depth);
			    break;
			   	 
		    	case WNConsts.HMERONYM:
			    partsall(cursyn, WNConsts.HMERONYM);
			    break;
			   	 
		    	case WNConsts.HHOLONYM:
			    partsall(cursyn, WNConsts.HHOLONYM);
			    break;
			   	 
		    	case WNConsts.SEEALSOPTR:
			    printseealso(cursyn);
			    break;
	
//#ifdef FOOP
			case WNConsts.PPLPTR:
			    traceptrs(cursyn, ptrtyp, dbase, depth);
			    traceptrs(cursyn, WNConsts.PPLPTR, dbase, depth);
			    break;
//#endif
		    
		    	case WNConsts.SIMPTR:
		    	case WNConsts.SYNS:
		    	case WNConsts.HYPERPTR:
			    printsns(cursyn, sense + 1);
			    prflag = true;
		    
			    traceptrs(cursyn, ptrtyp, dbase, depth);
		    
			    if (dbase == WNConsts.ADJ) {
			    	traceptrs(cursyn, WNConsts.PERTPTR, dbase, depth);
			    	traceptrs(cursyn, WNConsts.PPLPTR, dbase, depth);
			    } else if (dbase == WNConsts.ADV) {
			    	traceptrs(cursyn, WNConsts.PERTPTR, dbase, depth);
			    }

			    if (wnRtl.saflag)	{ // print SEE ALSO pointers.
			    	printseealso(cursyn);
                            }
			    
			    if (dbase == WNConsts.VERB && wnRtl.frflag) {
			    	printframe(cursyn, false);
                            }
			    break;

			case WNConsts.NOMINALIZATIONS:
			    tracenomins(cursyn, dbase);
			    break;

		    	default:
			    traceptrs(cursyn, ptrtyp, dbase, depth);
			    break;

		    	} // end switch

		    } // end if (skipit)

		} // end if (whichsense)

		if (!skipit) {
		    interface_doevents();
		    if ((whichsense == sense + 1) || wnRtl.abortsearch || overflag) {
		    	break;	// break out of loop - we're done
                    }
		}

	    } // end for (sense)

	    // Done with an index entry - patch in number of senses output.

	    if (whichsense == WNConsts.ALLSENSES) {
		int i = wnRtl.wnresults.outSenseCount[wnRtl.wnresults.numforms];
		if (i == idx.offset.length && i == 1) {
		    tmpbuf = WNGlobal.lineSeparator+"1 sense of "+ idx.wd;
		} else if (i == idx.offset.length) {
		    tmpbuf = WNGlobal.lineSeparator+i+" senses of "+idx.wd;
		} else if (i > 0) {	// printed some senses
		    tmpbuf = WNGlobal.lineSeparator+i+" of "+idx.offset.length+" senses of "+idx.wd;
                }

		// Find starting offset in searchbuffer for this index
		// entry and patch string in.  Then update bufstart
		// to end of searchbuffer for start of next index entry.

		if (i > 0) {
		    if (wnRtl.wnresults.numforms > 0) {
			searchbuffer.insert(bufstart++, WNGlobal.lineSeparator).insert(bufstart, tmpbuf.toString());
		    } else {
		        searchbuffer.insert(bufstart, tmpbuf.toString());
                    }
		    bufstart = searchbuffer.length();
		}
	    }

	    interface_doevents();
	    if (overflag || wnRtl.abortsearch) {
		break;		// break out of while (idx) loop.
            }

	    wnRtl.wnresults.numforms++;

	} // end while (idx)

    } // end switch

    interface_doevents();
    if (wnRtl.abortsearch) {
	printbuffer(WNGlobal.lineSeparator+"Search Interrupted...");
    } else if (overflag) {
        searchbuffer.setLength(0);
	searchbuffer.append("Search too large.  Narrow search and try again..."+WNGlobal.lineSeparator);
    }

    // replace underscores with spaces before returning.

    return searchbuffer.toString().replace('_', ' ');
}

public SynSet findtheinfo_ds(String searchstr, int dbase, int ptrtyp, int whichsense) {
    Index idx;
    SynSet cursyn;
    SynSet synlist = null, lastsyn = null;
    int depth = 0;
    boolean newsense = false;

    wnRtl.wnresults.numforms = 0;
    wnRtl.wnresults.printcnt = 0;

    while ((idx = getindex(searchstr, dbase)) != null) {

	searchstr = null;	// clear out for next call.
	newsense = true;
	
	if (ptrtyp < 0) {
	    ptrtyp = -ptrtyp;
	    depth = 1;
	}

	wnRtl.wnresults.senseCount[wnRtl.wnresults.numforms] = idx.offset.length;
	wnRtl.wnresults.outSenseCount[wnRtl.wnresults.numforms] = 0;
	wnRtl.wnresults.searchbuf = null;
	wnRtl.wnresults.searchds = null;

	// Go through all of the searchword's senses in the
	// database and perform the search requested.
	
	for (sense = 0; sense < idx.offset.length; sense++) {
	    if (whichsense == WNConsts.ALLSENSES || whichsense == sense + 1) {
		cursyn = read_synset(dbase, idx.offset[sense], idx.wd);
		if (lastsyn!=null) {
		    if (newsense) {
			lastsyn.nextform = cursyn;
		    } else {
			lastsyn.nextss = cursyn;
                    }
		}
		if (synlist==null) {
		    synlist = cursyn;
                }
		newsense = false;
	    
		cursyn.searchtype = ptrtyp;
		cursyn.ptrlist = traceptrs_ds(cursyn, ptrtyp, 
					       wnUtil.getpos(cursyn.pos.charAt(0)),
					       depth);
	    
		lastsyn = cursyn;

		if (whichsense == sense + 1) {
		    break;
                }
	    }
	}
	wnRtl.wnresults.numforms++;

	if (ptrtyp == WNConsts.COORDS) {	// clean up by removing hypernym.
	    lastsyn = synlist.ptrlist;
	    synlist.ptrlist = lastsyn.ptrlist;
	}
    }
    wnRtl.wnresults.searchds = synlist;
    return synlist;
}

/** Recursive search algorithm to trace a pointer tree and return results
 * in linked list of data structures.
 */

public SynSet traceptrs_ds(SynSet synptr, int ptrtyp, int dbase, int depth) {
    SynSet cursyn, synlist = null, lastsyn = null;
    int tstptrtyp;
    boolean docoords;
    
    // If synset is a satellite, find the head word of its
    //   head synset and the head word's sense number.

    if (wnUtil.getsstype(synptr.pos.charAt(0)) == WNConsts.SATELLITE) {
	for (int i = 0; i < synptr.pointers.length; i++)
	    if (synptr.pointers[i].ptrtyp == WNConsts.SIMPTR) {
		cursyn = read_synset(synptr.pointers[i].ppos,
				      synptr.pointers[i].ptroff,
				      "");
		synptr.headword = cursyn.words[0];
		synptr.headsense = cursyn.lexid[0];
		break;
	    }
    }

    if (ptrtyp == WNConsts.COORDS) {
	tstptrtyp = WNConsts.HYPERPTR;
	docoords = true;
    } else {
	tstptrtyp = ptrtyp;
	docoords = false;
    }

    for (int i = 0; i < synptr.pointers.length; i++) {
	if ((synptr.pointers[i].ptrtyp == tstptrtyp) &&
	   ((synptr.pointers[i].pfrm == 0) ||
	    (synptr.pointers[i].pfrm == synptr.whichword))) {
	    
	    cursyn=read_synset(synptr.pointers[i].ppos, synptr.pointers[i].ptroff, "");
	    cursyn.searchtype = ptrtyp;

	    if (lastsyn!=null) {
		lastsyn.nextss = cursyn;
            }
	    if (synlist==null) {
		synlist = cursyn;
            }
	    lastsyn = cursyn;

	    if (depth>0) {
		depth = depthcheck(depth, cursyn);
		cursyn.ptrlist = traceptrs_ds(cursyn, ptrtyp,
					       wnUtil.getpos(cursyn.pos.charAt(0)),
					       (depth+1));
	    } else if (docoords) {
		cursyn.ptrlist = traceptrs_ds(cursyn, WNConsts.HYPOPTR, WNConsts.NOUN, 0);
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
    boolean svdflag, skipit;
    int offsets[] = new int[WNConsts.MAXSENSE];

    cpstring = searchstr;
    bufstart = searchbuffer.length();
    for (int i = 0; i < WNConsts.MAXSENSE; i++) {
	offsets[i] = 0;
    }
    offsetcnt = 0;

    StringBuffer sb = new StringBuffer(4096);
    while ((idx = getindex(cpstring, pos)) != null) {
	cpstring = null;	// clear for next call to getindex().
	wnRtl.wnresults.senseCount[wnRtl.wnresults.numforms++] = idx.offset.length;
	wnRtl.wnresults.outSenseCount[wnRtl.wnresults.numforms] = 0;

	printbuffer(
"                                                                                                   "+WNGlobal.lineSeparator);

	// Print synset for each sense.  If requested, precede
	// synset with synset offset and/or lexical file information.

	for (sense = 0; sense < idx.offset.length; sense++) {
            sb.setLength(0);
            skipit = false;
	    for (int i = 0;  i < offsetcnt && !skipit; i++) {
		if (offsets[i] == idx.offset[sense]) {
		    skipit = true;
                }
            }

	    if (!skipit) {
		offsets[offsetcnt++] = idx.offset[sense];
		cursyn = read_synset(pos, idx.offset[sense], idx.wd);
		if (idx.tagged_cnt != -1 && ((sense + 1) <= idx.tagged_cnt)) {
		    sb.append(sense + 1).append(". (");
                    sb.append(wnUtil.GetTagcnt(idx, sense + 1)).append(") ");
		} else {
		    sb.append(sense + 1).append(". ");
		}

		svdflag = wnRtl.dflag;
		wnRtl.dflag = true;
                tmpbuf = sb.toString();
		printsynset(tmpbuf, cursyn, WNGlobal.lineSeparator, DEFON, ALLWORDS,
			    SKIP_ANTS, SKIP_MARKER);
		wnRtl.dflag = svdflag;
		wnRtl.wnresults.outSenseCount[wnRtl.wnresults.numforms]++;
		wnRtl.wnresults.printcnt++;

	    }
	}

	// Print sense summary message.

	int i = wnRtl.wnresults.outSenseCount[wnRtl.wnresults.numforms];

	if (i > 0) {
            sb.setLength(0);
	    if (i == 1) {
		sb.append(WNGlobal.lineSeparator).append("The ").append(WNGlobal.partnames[pos]).append(" ");
                sb.append(idx.wd).append(" has 1 sense");
	    } else {
		sb.append(WNGlobal.lineSeparator).append("The ").append(WNGlobal.partnames[pos]).append(" ");
                sb.append(idx.wd).append(" has ").append(i).append(" senses");
            }
	    if (idx.tagged_cnt > 0) {
		sb.append(" (first ").append(idx.tagged_cnt).append(" from tagged texts)").append(WNGlobal.lineSeparator);
	    } else if (idx.tagged_cnt == 0) {
		sb.append(" (no senses from tagged texts)").append(WNGlobal.lineSeparator);
            }

	    searchbuffer.insert(bufstart, sb.toString());
	    bufstart = searchbuffer.length();
	} else {
	    searchbuffer.setLength(bufstart);
        }

	wnRtl.wnresults.numforms++;
    }
}

/** Do requested search on synset passed, returning output in buffer.
 */

public StringBuffer do_trace(SynSet synptr, int ptrtyp, int dbase, int depth) {
    searchbuffer.setLength(0);	// clear output buffer.
    traceptrs(synptr, ptrtyp, dbase, depth);
    return searchbuffer;
}

/** Set bit for each search type that is valid for the search word
 * passed and return bit mask.
 */
  
public int is_defined(String searchstr, int dbase) {
    Index index;
    int retval = 0;

    wnRtl.wnresults.numforms = wnRtl.wnresults.printcnt = 0;
    wnRtl.wnresults.searchbuf = null;
    wnRtl.wnresults.searchds = null;

    while ((index = getindex(searchstr, dbase)) != null) {
	searchstr = null;	// clear out for next getindex() call.

	wnRtl.wnresults.senseCount[wnRtl.wnresults.numforms] = index.offset.length;
	
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
	    if ((HasHoloMero(index, WNConsts.HMERONYM))>0) {
		retval |= (1<<WNConsts.HMERONYM);
            }
	    if ((HasHoloMero(index, WNConsts.HHOLONYM))>0) {
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

	wnRtl.wnresults.numforms++;
    }
    return retval;
}

/** Determine if any of the synsets that this word is in have inherited
 *  meronyms or holonyms.
 */

public int HasHoloMero(Index index, int ptrtyp) {
    SynSet synset, psynset;
    int found=0;
    int ptrbase;

    ptrbase = (ptrtyp == WNConsts.HMERONYM) ? WNConsts.HASMEMBERPTR : WNConsts.ISMEMBERPTR;
    
    for (int i = 0; i < index.offset.length; i++) {
	synset = read_synset(WNConsts.NOUN, index.offset[i], "");
	for (int j = 0; j < synset.pointers.length; j++) {
	    if (synset.pointers[j].ptrtyp == WNConsts.HYPERPTR) {
		psynset = read_synset(WNConsts.NOUN, synset.pointers[j].ptroff, "");
		found += HasPtr(psynset, ptrbase);
		found += HasPtr(psynset, ptrbase + 1);
		found += HasPtr(psynset, ptrbase + 2);

	    }
	}
    }
    return found;
}

public int HasPtr(SynSet synptr, int ptrtyp) {
    for (int i = 0; i < synptr.pointers.length; i++) {
        if (synptr.pointers[i].ptrtyp == ptrtyp) {
	    return(1);
	}
    }
    return 0;
}

/** Set bit for each POS that search word is in.  0 returned if
 *  word is not in WordNet.
 */

public int in_wn(String word, int pos) {
    int retval = 0;

    if (pos == WNConsts.ALL_POS) {
	for (int i = 1; i < WNConsts.NUMPARTS + 1; i++) {
	    if (wnRtl.indexfps[i] != null && binSearcher.bin_search(word, wnRtl.indexfps[i]) != null) {
		retval |= (1<<i);
            }
        }
    } else if (wnRtl.indexfps[pos] != null && binSearcher.bin_search(word,wnRtl.indexfps[pos]) != null) {
	    retval |= (1<<pos);
    }
    return retval;
}

public int depthcheck(int depth, SynSet synptr) {
    if (depth >= WNConsts.MAXDEPTH) {
	msgbuf = "WordNet library error: Error Cycle detected"+WNGlobal.lineSeparator+"   "+synptr.words[0];
	WordNet.display_message(msgbuf);
	depth = -1;		// reset to get one more trace then quit.
    }
    return depth;
}

/** Strip off () enclosed comments from a word.
 */

public String deadjify(String word) {
    adj_marker = WNConsts.UNKNOWN_MARKER; // default if not adj or unknown.
    
    for (int y=0; y<word.length(); y++) {
	if (word.charAt(y) == '(') {
	    if ("(a)".equals(word.substring(y,y+2))) {
		adj_marker = WNConsts.ATTRIBUTIVE;
	    } else if ("(ip)".equals(word.substring(y,y+3))) {
		adj_marker = WNConsts.IMMED_POSTNOMINAL;
            } else if ("(p)".equals(word.substring(y,y+2))) {
		adj_marker = WNConsts.PREDICATIVE;
            }
	    word = word.substring(y);
            break;
        }
    }
    return word;
}

public int getsearchsense(SynSet synptr, int whichword) {
    wdbuf = synptr.words[whichword - 1].replace(' ', '_');
    wnUtil.strtolower(wdbuf);
    Index idx = index_lookup(wdbuf, wnUtil.getpos(synptr.pos.charAt(0)));    
    if (idx != null) {
	for (int i = 0; i < idx.offset.length; i++)
	    if (idx.offset[i] == synptr.hereiam) {
		return(i + 1);
	    }
    }
    return 0;
}

public void printsynset(String head, SynSet synptr, String tail, int definition, int wdnum, int antflag, int markerflag) {
    StringBuffer tbuf = new StringBuffer(1024*16);

    tbuf.append(head);		// print head.

    // Precede synset with additional information as indiecated
    // by flags.

    if (wnRtl.offsetflag)		// print synset offset.
	tbuf.append("{").append(synptr.hereiam).append("} ");
    if (wnRtl.fileinfoflag) {		// print lexicographer file information.
	tbuf.append("<").append(WNGlobal.lexfiles[synptr.fnum]).append("> ");
	prlexid = 1;		// print lexicographer id after word.
    } else
	prlexid = 0;
    
    if (wdnum!=0) {			// print only specific word asked for.
	catword(tbuf, synptr, wdnum - 1, markerflag, antflag);
    } else {			// print all words in synset.
	int wdcnt = synptr.wcount;
        for (int i = 0; i < wdcnt; i++) {
	    catword(tbuf, synptr, i, markerflag, antflag);
	    if (i < wdcnt - 1) {
		tbuf.append(", ");
            }
	}
    }
    if (definition!=0 && wnRtl.dflag && synptr.defn!=null) {
	tbuf.append(" -- ");
	tbuf.append(synptr.defn);
    }
    
    tbuf.append(tail);
    printbuffer(tbuf.toString());
}

public void printantsynset(SynSet synptr, String tail, int anttype, int definition) {
    StringBuffer tbuf = new StringBuffer(1024*16);
    String str;
    boolean first = true;


    if (wnRtl.offsetflag) {
	tbuf.append("{").append(synptr.hereiam).append("} ");
    }
    if (wnRtl.fileinfoflag) {
	tbuf.append("<").append(WNGlobal.lexfiles[synptr.fnum]).append("> ");
	prlexid = 1;
    } else {
	prlexid = 0;
    }
    
    // print anotnyms from cluster head (of indirect ant)
    
    tbuf.append("INDIRECT (VIA ");
    int wdcnt = synptr.wcount;
    for (int i = 0; i < wdcnt; i++) {
	if (first) {
	    str = printant(WNConsts.ADJ, synptr, i + 1, "%s", ", ");
	    first = false;
	} else {
	    str = printant(WNConsts.ADJ, synptr, i + 1, ", %s", ", ");
        }
	if (str!=null) {
	    tbuf.append(str);
        }
    }
    tbuf.append(") . ");
    
    // now print synonyms from cluster head (of indirect ant).
    
    wdcnt = synptr.wcount;
    for (int i = 0; i < wdcnt; i++) {
	catword(tbuf, synptr, i, SKIP_MARKER, SKIP_ANTS);
	if (i < wdcnt - 1) {
	    tbuf.append(", ");
        }
    }
    
    if (wnRtl.dflag && synptr.defn!=null && definition!=0) {
	tbuf.append(" -- ");
	tbuf.append(synptr.defn);
    }
    
    tbuf.append(tail);
    printbuffer(tbuf.toString());
}

   public void catword(StringBuffer buf, SynSet synptr, int wdnum, int adjmarker, int antflag) {
      String vs = " (vs. %s)";
      String[] markers = {
              "",			// UNKNOWN_MARKER
              "(prenominal)",		// ATTRIBUTIVE
              "(postnominal)",	        // IMMED_POSTNOMINAL
              "(predicate)",		// PREDICATIVE
      };
      
      buf.append(deadjify(synptr.words[wdnum]));
      
      // Print additional lexicographer information and WordNet sense
      // number as indicated by flags.
      
      if (prlexid!=0 && (synptr.lexid[wdnum] != 0)) {
          buf.append(synptr.lexid[wdnum]);
      }
      if (wnRtl.wnsnsflag) {
          buf.append(synptr.wnsns[wdnum]);
      }
   
      // For adjectives, append adjective marker if present, and
      // print antonym if flag is passed.
   
      if (wnUtil.getpos(synptr.pos.charAt(0)) == WNConsts.ADJ) {
         if (adjmarker == PRINT_MARKER) {
             buf.append(markers[adj_marker]); 
         }
         if (antflag == PRINT_ANTS) {
             buf.append(printant(WNConsts.ADJ, synptr, wdnum + 1, vs, ""));
         }
      }
   }

public String printant(int dbase, SynSet synptr, int wdnum, String template, String tail) {
   int wdoff;
   StringBuffer retbuf = new StringBuffer(1024*16);
   boolean first = true;

    
    // Go through all the pointers looking for anotnyms from the word
    //   indicated by wdnum.  When found, print all the antonym's
    //   antonym pointers which point back to wdnum.
    
   for (int i = 0; i < synptr.pointers.length; i++) {
       if (synptr.pointers[i].ptrtyp == WNConsts.ANTPTR && synptr.pointers[i].pfrm == wdnum) {

           SynSet psynptr = read_synset(dbase, synptr.pointers[i].ptroff, "");

	   for (int j = 0; j < psynptr.pointers.length; j++) {
              if (    psynptr.pointers[j].ptrtyp == WNConsts.ANTPTR &&
                      psynptr.pointers[j].pto == wdnum &&
                      psynptr.pointers[j].ptroff == synptr.hereiam) {
      
                wdoff = (psynptr.pointers[j].pfrm!=0) ? (psynptr.pointers[j].pfrm - 1) : 0;
      
                // Construct buffer containing formatted antonym,
                // then add it onto end of return buffer.
      
                String tmp = deadjify(psynptr.words[wdoff]);
                StringBuffer tbuf = new StringBuffer(tmp.length()+1024);
                tbuf.append(tmp);
      
                // Print additional lexicographer information and
                // WordNet sense number as indicated by flags.
         
                if (prlexid>0 && (psynptr.lexid[wdoff] != 0)) {
                   tbuf.append(psynptr.lexid[wdoff]);
                }
                if (wnRtl.wnsnsflag) {
                   tbuf.append("#").append(psynptr.wnsns[wdoff]);
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

   public void printbuffer(String string) {
      if (overflag) {
         return;
      }
      if (searchbuffer.length() + string.length() >= WNConsts.SEARCHBUF) {
         overflag = true;
      } else { 
         searchbuffer.append(string);
      }
   }

   public void printsns(SynSet synptr, int sense) {
      printsense(synptr, sense);
      printsynset("", synptr, WNGlobal.lineSeparator, DEFON, ALLWORDS, PRINT_ANTS, PRINT_MARKER);
   }
   
   public void printsense(SynSet synptr, int sense) {
      String tbuf;
   
      // Append lexicographer filename after Sense # if flag is set.
   
      if (wnRtl.fnflag) {
         tbuf = WNGlobal.lineSeparator+"Sense "+sense+" in file \""+WNGlobal.lexfiles[synptr.fnum]+"\""+WNGlobal.lineSeparator;
      } else {
         tbuf = WNGlobal.lineSeparator+"Sense "+sense+WNGlobal.lineSeparator;
      }
   
      printbuffer(tbuf);
   
      // update counters.
      wnRtl.wnresults.outSenseCount[wnRtl.wnresults.numforms]++; 
      wnRtl.wnresults.printcnt++;
   }

   public void printspaces(int trace, int depth) {
   
      for (int j = 0; j < depth; j++) {
         printbuffer("    ");
      }
      switch(trace) {
         case TRACEP:		// traceptrs(), tracenomins().
            if (depth!=0) {
               printbuffer("   ");
            } else {
               printbuffer("       ");
            }
            break;
            
         case TRACEC:		// tracecoords().
            if (depth==0) {
               printbuffer("    ");
            }
            break;
            
         case TRACEI:			// traceinherit().
            if (depth==0) {
               printbuffer(WNGlobal.lineSeparator+"    ");
            }
            break;
      }
   }

   /** Dummy function to force Tcl/Tk to look at event queue to see of
    *  the user wants to stop the search.
    */
   
   public void interface_doevents () {
      // FIXME if (interface_doevents_func != null) {
      // FIXME    interface_doevents_func ();
      // FIXME }
   }
   
   
}
