///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2001 Jason Baldridge and Gann Bierner
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////   
package opennlp.common.english;

import gnu.regexp.*;
import java.util.*;

/**
 * A helper class for doing very *basic* English morphological analysis.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.1 $, $Date: 2001/11/19 02:16:00 $
 */


public final class BasicEnglishAffixes {

    private static byte MIN_WORD_LENGTH = 4;
    
    private static RE endsWith2, endsWith3, endsWith4, endsWith5;    
    
    static {
	try {
	    endsWith2 =
		new RE("(ad|al|an|ar|cy|ed|ee|en|er|es|et|eth|ey|fy"
		       +"|ia|ic|ie|id|in|ly|mo|ol|on|or|ry|st|th|ty|yl)$");
	}
	catch (REException e) {
	    System.out.println("Problem with 2 letter suffix RE:\n" + e);
	}

	try {
	    endsWith3 =
		new RE("("
		       +"ade|age|ana|and|ane|ant|ard|art|ary|ase|ate"
		       +"|cal|cle"
		       +"|dom"
		       +"|ean|eer|eme|ene|ent|ery|ese|ess|est"
		       +"|fer|fic|fid|ful"
		       +"|gen|gon"
		       +"|ial|ian|ide|ier|ify|ile|ine|ign|ing|ion"
		       +"|ise|ish|ism|ist|ite|ity|ium|ive|ize"
		       +"|kin|let|lex|log|mas|mer|nik"
		       +"|ock|ode|oid|ole|oma|ome|one|ory|ose|our|ous"
		       +"|ped|ple|pod|sis"
		       +"|ule|ure|ute|yne|zoa"
		       +")$");
	}
	catch (REException e) {
	    System.out.println("Problem with 3 letter suffix RE:\n" + e);
	}
	
	try {
	    endsWith4 =
		new RE("(able|acea|ales|ance|ancy|arch|asis|atic|ator"
		       +"|cade|carp|cele|cene|cide|crat|cule|cyst|cyte|derm"
		       +"|emia|ence|ency|eous|ette|fold|form|free|fuge"
		       +"|gamy|gene|geny|gony|gram|hood"
		       +"|iana|ible|ical|idae|iest|ieth|inae|ious|itis|itol"
		       +"|less|like|ling|lite|lith|logy|lyte"
		       +"|ment|mere|most|naut|ness|nomy"
		       +"|ogue|onym|opia|osis|otic"
		       +"|saur|sect|ship|some|stat|ster"
		       +"|taxy|tion|tome|tomy|tory|trix|tron|tude|type"
		       +"|uret|urgy|uria|ward|ways|wise|zoon"
		       +")$");
	}
	catch (REException e) {
	    System.out.println("Problem with 4 letter suffix RE:\n" + e);
	}
	
	try {
	    endsWith5 =
		new RE("(andry|archy|arian|aster|ation|ative|atory"
		       +"|clase|cline|cracy|diene|escent|esque"
		       +"|genic|graph|hemia|iasis|ician|istic"
		       +"|latry|lepsy|logue|lysis|lytic"
		       +"|mancy|mania|meter|metry|morph"
		       +"|nasty|odont|oidea|opsis"
		       +"|path|pathy|pede|petal|phage|phagy|phane|phany|phile"
		       +"|phobe|phone|phony|phore|phyll|phyte|plasm|plast"
		       +"|ploid|prone|proof"
		       +"|rhoea|scape|scope|scopy|sophy|stome|stomy"
		       +"|taxis|trope|ulent|ville|wards"
		       +")$");

	}
	catch (REException e) {
	    System.out.println("Problem with 5 letter suffix RE:\n" + e);
	}
	
    }

    public static String[] getSuffixes(String word) {
	ArrayList suffs = new ArrayList();

	if (word.endsWith("ies")) {
	    suffs.add("plural");
	    word = word.substring(0,word.length()-3)+"y";
	}
	else if (word.endsWith("s") && !word.endsWith("ss")) {
	    suffs.add("plural");
	    word = word.substring(0,word.length()-1);
	}

	int first = nextSuffix(word);
	if (first != 0) {
	    suffs.add(word.substring(first));
	    int second = nextSuffix(word.substring(0,first));
	    if (second != 0) {
		suffs.add(word.substring(second, first));
	    }
	}
	
	String[] affixes = new String[suffs.size()];
	for (int i=0; i<suffs.size(); i++)
	    affixes[i] = (String)suffs.get(i);
	
	return affixes;
    }

    
    public static int nextSuffix(String word) {
	REMatch match = null;
	short wlen = (short)word.length();
	
	if (wlen < 2+MIN_WORD_LENGTH)
	    return 0;
	
	if (wlen >= 5+MIN_WORD_LENGTH)
	    match = endsWith5.getMatch(word);
	if (match == null && wlen >= 4+MIN_WORD_LENGTH)
	    match = endsWith4.getMatch(word);
	if (match == null && wlen >= 3+MIN_WORD_LENGTH)
	    match = endsWith3.getMatch(word);
	if (match == null && wlen >= 2+MIN_WORD_LENGTH)
	    match = endsWith2.getMatch(word);

	if (match != null) return match.getStartIndex();
	else return 0;
    }

    public static void main(String[] args) {
	String[] suffs = getSuffixes(args[0]);
	//System.out.println(nextSuffix(args[0]));
	for (int i=0; i< suffs.length; i++)
	    System.out.println(suffs[i]);
    }
    
}
