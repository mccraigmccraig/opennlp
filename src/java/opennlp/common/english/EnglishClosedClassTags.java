///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2002 Jason Baldridge and Gann Bierner
// 
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////
package opennlp.common.english;

import opennlp.common.util.*;
import java.util.*;

public final class EnglishClosedClassTags implements FilterFcn {

    static HashMap TagsToWords = new HashMap();

    static {
	setupHashMap();
    }

    public boolean filter (String s) {
	return false;
    }
    
    public boolean filter (String word, String tag) {
	//System.out.println(tag+"\t"+word);
	if (!TagsToWords.containsKey(tag)) return true;
	word = word.toLowerCase();
	return ((HashSet)TagsToWords.get(tag)).contains(word);
    }

    private static HashSet toSet(String[] words) {
	HashSet hs = new HashSet();
	for (int i=0; i<words.length; i++) hs.add(words[i]);
	return hs;
    }
	
    private static void setupHashMap() {
	String[] wordsForLeftQuote = {"non-``", "`", "``"};
	TagsToWords.put("``", toSet(wordsForLeftQuote));
	
	String[] wordsForWP = {"whoever", "whom", "what", "who"};
	TagsToWords.put("WP", toSet(wordsForWP));
	
	String[] wordsForWPPoss = {"whose"};
	TagsToWords.put("WP$", toSet(wordsForWPPoss));
	
	String[] wordsForPOS = {"'s", "'"};
	TagsToWords.put("POS", toSet(wordsForPOS));
	
	String[] wordsForPRPPoss = {"its", "his", "my", "our", "their", "your", "her"};
	TagsToWords.put("PRP$", toSet(wordsForPRPPoss));
	
	String[] wordsForDT =
	{"either", "the", "those", "any", "la", "le", "another", "that",
	 "each", "many", "del", "this", "half", "no", "a", "every", "both",
	 "an", "neither", "some", "these", "all"}; 
	TagsToWords.put("DT", toSet(wordsForDT));
	
	String[] wordsForSYM = {"a", "b", "c", "&"};
	TagsToWords.put("SYM", toSet(wordsForSYM));
	
	String[] wordsForLS =
	{"1", "a", "2", "b", "3", "4", "5", "6", "7", "8", "9", "first",
	 "third", "second", "fourth", "fifth"}; 
	TagsToWords.put("LS", toSet(wordsForLS));
	
	String[] wordsForRP =
	{"through", "up", "for", "around", "across", "back", "away", "open",
	 "of", "in", "out", "down", "by", "about", "off", "over", "with",
	 "on"}; 
	TagsToWords.put("RP", toSet(wordsForRP));
	
	String[] wordsForNumSign = {"#"};
	TagsToWords.put("#", toSet(wordsForNumSign));
	
	String[] wordsForTO = {"to"};
	TagsToWords.put("TO", toSet(wordsForTO));
	
	String[] wordsForBucks = {"nz$", "us$", "$", "a$", "c$"};
	TagsToWords.put("$", toSet(wordsForBucks));
	
	String[] wordsForCC =
	{"either", "or", "v.", "plus", "&", "versus", "but", "minus", "both",
	 "neither", "nor", "yet", "and", "whether", "less", "vs.", "et",
	 "'n'"};
	TagsToWords.put("CC", toSet(wordsForCC));
	
	String[] wordsForRBS = {"least", "most", "best", "worst"};
	TagsToWords.put("RBS", toSet(wordsForRBS));
		  
	String[] wordsForPDT = {"all", "such", "half", "both"};
	TagsToWords.put("PDT", toSet(wordsForPDT));
	
	String[] wordsForLeftParen = {"(", "{"};
	TagsToWords.put("(", toSet(wordsForLeftParen));
	
	String[] wordsForRightParen = {")", "}"};
	TagsToWords.put(")", toSet(wordsForRightParen));
	
	String[] wordsForWRB =
	{"when", "whenever", "whereby", "why", "where", "how"};
	TagsToWords.put("WRB", toSet(wordsForWRB));
	
	String[] wordsForComma = {","};
	TagsToWords.put(",", toSet(wordsForComma));
	
	String[] wordsForEOS = {"!", ".", "?"};
	TagsToWords.put(".", toSet(wordsForEOS));
	
	String[] wordsForRightQuote = {"''", "'"};
	TagsToWords.put("''", toSet(wordsForRightQuote));
	
	String[] wordsForWDT =
	{"that", "which", "what", "whichever", "whatever"};
	TagsToWords.put("WDT", toSet(wordsForWDT));
	
	String[] wordsForMD =
	{"ca", "wo", "ought", "may", "must", "'d", "would", "should", "can",
	 "shall", "could", "will", "might", "need", "'ll"}; 
	TagsToWords.put("MD", toSet(wordsForMD));
	
	String[] wordsForPRP =
	{"themselves", "she", "one", "itself", "ourselves", "himself", "us",
	 "mine", "herself", "i", "he", "them", "theirs", "myself", "hers",
	 "me", "yourself", "ya", "'s", "they", "we", "you", "it", "him",
	 "thyself", "her"};
	TagsToWords.put("PRP", toSet(wordsForPRP));
	
	String[] wordsForColon = {"...", ":", "--", ";", "-"};
	TagsToWords.put(":", toSet(wordsForColon));
	
	String[] wordsForUH =
	{"indeed", "yes", "please", "ok", "well", "say", "no", "oh"};
	TagsToWords.put("UH", toSet(wordsForUH));
	
	String[] wordsForEX = {"there"};
	TagsToWords.put("EX", toSet(wordsForEX));

	String[] wordsForIN =
	{"upon", "up", "except", "between", "across", "until", "along",
	 "within", "alongside", "beneath", "into", "by", "with", "beyond",
	 "once", "for", "aboard", "behind", "toward", "which", "and", "of",
	 "are", "out", "off", "about", "outside", "including", "on", "next",
	 "@", "through", "so", "towards", "like", "complicated", "near",
	 "against", "unlike", "de", "during", "around", "than", "above",
	 "despite", "then", "whereas", "though", "that", "since", "from",
	 "without", "worth", "whether", "ago", "lest", "past", "down",
	 "while", "far", "below", "throughout", "unless", "amid", "onto",
	 "via", "a", "to", "plus", "notwithstanding", "per", "but", "if",
	 "after", "among", "en", "opposite", "before", "atop", "as", "till",
	 "at", "vs.", "in", "because", "inside", "over", "although", "under",
	 "besides"};
	TagsToWords.put("IN", toSet(wordsForIN)); 

    }




}
