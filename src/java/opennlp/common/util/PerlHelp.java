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
package opennlp.common.util;

import gnu.regexp.*;

import java.io.*;
import java.util.*;

/**
 * A class to help out by providing useful Perl-type functions on strings.
 * Maybe one day we could get some of this functionality incorporated into
 * gnu.regexp.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.5 $, $Date: 2001/11/29 11:11:03 $
 */
public final class PerlHelp {

    /**
     * Regular expressions.
     */
    public static RE alphanumRE, capRE, peqRE, punctRE, wsRE,
	hasCap, hasNum, hasHyph, hasAt;

    static {
        try {
            alphanumRE = new RE("^[A-Za-z0-9]+$");
            hasCap = new RE(".*[A-Z].*");
            hasNum = new RE(".*[0-9].*");
            hasHyph = new RE(".*-.*");
            hasAt = new RE(".*@.*");
            capRE = new RE("^[A-Z]\\S*$");
	    peqRE = new RE("\\.|!|\\?|\\\"|\\)");
	    punctRE = new RE("[^a-zA-Z0-9]+");
            wsRE = new RE("\\s+");
          }
        catch (REException e) { System.out.println(e); }
    }


    public static String[] getParagraphs (String text) {
	StringTokenizer st = new StringTokenizer(text, "\n", true);
	List pars = new ArrayList();
	StringBuffer nextPar = new StringBuffer();
	boolean prevWasReturn = false;
	while (st.hasMoreTokens()) {
	    String s = st.nextToken();
	    if (s.equals("\n") && !prevWasReturn) {
		prevWasReturn = true;
	    }
	    else if (prevWasReturn) {
		prevWasReturn = false;
		pars.add(nextPar.toString());
		nextPar = new StringBuffer(s.trim());
	    }
	    else {
		nextPar.append(s.trim());
	    }
	}
	pars.add(nextPar.toString());
	String[] parsArray = new String[pars.size()];
	pars.toArray(parsArray);
	return parsArray;
    }

    public static String[] splitByWhitespace (String s) {
	return split(s, " \t\n\r\f");
    }

    /*
     * Calls split(String s, string delim), assuming space (" ") as the
     * delimiter.
     *
     * @param  s     The string to split.
     */
    public static String[] split(String s) { return split(s," "); }

    
    /*
     * Splits a string into a string array.
     *
     * @param  delim The delimiter (or separator) to use to split the string.
     */
    public static String[] split(String s, String delim) {
	StringTokenizer st = new StringTokenizer(s, delim);
	String[] toks = new String[st.countTokens()];
	int i = 0;
	while (st.hasMoreTokens()) {
	    toks[i] = st.nextToken();
	    i++;
	}

	return toks;
    }
    
    /*
     * Finds the index of the nearest space before a specified index.
     *
     * @param sb   The string buffer which contains the text being examined.
     * @param seek The index to begin searching from.
     * @return The index which contains the nearest space.
     */    
    public static int previousSpaceIndex(StringBuffer sb, int seek) {
	seek--;
	while (seek > 0) {
	    if (sb.charAt(seek) == ' ') {
		while(seek>0 && sb.charAt(seek-1) == ' ') seek--;
		return seek;
	    }
	    seek--;
	}
	return 0;
    }

    /*
     * Finds the index of the nearest space after a specified index.
     *
     * @param sb        The string buffer which contains the text being examined.
     * @param seek       The index to begin searching from.
     * @param lastIndex The highest index of the StringBuffer sb.
     * @return The index which contains the nearest space.
     */    
    public static int nextSpaceIndex(StringBuffer sb, int seek, int lastIndex) {
	seek++;
	char c;
	while (seek < lastIndex) {
	    c = sb.charAt(seek);
	    if (c == ' ' || c == '\n') {
		while(sb.length()>seek+1 && sb.charAt(seek+1) == ' ') seek++;
		return seek;
	    }
	    seek++;
	}
	return lastIndex;
    }

  
}
