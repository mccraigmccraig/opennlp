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
 * @version     $Revision: 1.9 $, $Date: 2002/02/05 20:16:24 $
 */
public final class PerlHelp {

    public static boolean isAlphanumeric (String s) {
	char[] ca = s.toCharArray();
	for (int i=0; i<ca.length; i++) {
	    if (!Character.isLetterOrDigit(ca[i])) {
		return false;
	    }
	}
	return true;
    }
    
    public static boolean isPunctuation (String s) {
	char[] ca = s.toCharArray();
	for (int i=0; i<ca.length; i++) {
	    if (Character.isLetterOrDigit(ca[i])
		|| Character.isWhitespace(ca[i])) {
		return false;
	    }
	}
	return true;
    }

    public static boolean hasCap (String s) {
	char[] ca = s.toCharArray();
	for (int i=0; i<ca.length; i++) {
	    if (Character.isUpperCase(ca[i])) {
		return true;
	    }
	}
	return false;
    }

    public static boolean hasNum (String s) {
	char[] ca = s.toCharArray();
	for (int i=0; i<ca.length; i++) {
	    if (Character.isDigit(ca[i])) {
		return true;
	    }
	}
	return false;
    }
    
    public static String[] getParagraphs (String text) {
	StringTokenizer st = new StringTokenizer(text, "\n", true);
	List pars = new ArrayList();
	StringBuffer nextPar = new StringBuffer();
	boolean prevWasReturn = false;
	while (st.hasMoreTokens()) {
	    String s = st.nextToken();
	    if (s.equals("\n")) {
		if (prevWasReturn) {
		    prevWasReturn = false;
		    pars.add(nextPar.toString());
		    nextPar = new StringBuffer();
		} else {
		    prevWasReturn = true;
		}
	    } else {
		prevWasReturn = false;
	    }
	    nextPar.append(s);
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
