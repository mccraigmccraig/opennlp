///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2000 Jason Baldridge and Gann Bierner
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

package opennlp.common.parse;

import opennlp.common.structure.*;
import opennlp.common.xml.*;

import java.io.*;
import java.util.ArrayList;

/**
 * A Parser is a module that takes a string as input and produces
 * a structured form representing that string.
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.2 $, $Date: 2001/11/05 14:42:54 $
 */
public interface Parser {
    
    /**
     * Tells the parser where to get lexical items and how to combine them.
     *
     * @param _L the lexicon containing the whole vocabulary
     * @param _R the rules saying how to combine lexical items
     */
    public void setGrammar(Lexicon _L, RuleGroup _R);
    
    /**
     * Parses a string
     *
     * @param s the string
     * @exception CatParseException thrown if the syntax/semantics of
     *            a lexical item can't be parsed
     * @exception LexException thrown if a lex item isn't found
     * @exception ParseException thrown if a parse can't be found for the
     *            entire string
     * @exception IOException
     */
    public void parse(String s)
	throws CatParseException, LexException, ParseException;
    
    /**
     * Parses given a set of lexical items.  The lexical choice phrase
     * has already been done.
     *
     * @param inits the lexical items
     * @exception CatParseException thrown if the syntax/semantics of
     *            a lexical item can't be parsed
     * @exception ParseException thrown if a parse can't be found for the
     *            entire string
     */
    public void parse(Constituent[] inits)
	throws ParseException, CatParseException,
	IOException, LexException;
    /**
     * Parses given a set of lexical items.  The lexical choice phrase
     * has already been done.
     *
     * @param d an xml document containing preprocessed text
     * @exception CatParseException thrown if the syntax/semantics of
     *            a lexical item can't be parsed
     * @exception ParseException thrown if a parse can't be found for the
     *            entire string
     */
    public void parse(NLPDocument d)
	throws ParseException, CatParseException, IOException, LexException;

    /**
     * Parses given either a string or lexical items plus a KB.
     *
     * @param o the string to be parsed, tokens separated by whitespace
     * @param b knowledge that might be useful when parsing
     * @exception CatParseException thrown if the syntax/semantics of
     *            a lexical item can't be parsed
     * @exception LexException thrown if a lex item isn't found
     * @exception ParseException thrown if a parse can't be found for the
     *            entire string
     * @exception IOException
     */
    public void parse(String s, KB b)
	throws ParseException, LexException, IOException, CatParseException;
    /**
     * Parses given either a string or lexical items plus a KB.
     *
     * @param c the lexical entries already computed
     * @param b knowledge that might be useful when parsing
     * @exception CatParseException thrown if the syntax/semantics of
     *            a lexical item can't be parsed
     * @exception LexException thrown if a lex item isn't found
     * @exception ParseException thrown if a parse can't be found for the
     *            entire string
     * @exception IOException
     */
    public void parse(Constituent[] c, KB b)
	throws ParseException, LexException, IOException, CatParseException;
    /**
     * Parses given either a string or lexical items plus a KB.
     *
     * @param d A preprocessed xml document
     * @param b knowledge that might be useful when parsing
     * @exception CatParseException thrown if the syntax/semantics of
     *            a lexical item can't be parsed
     * @exception LexException thrown if a lex item isn't found
     * @exception ParseException thrown if a parse can't be found for the
     *            entire string
     * @exception IOException
     */
    public void parse(NLPDocument d, KB b)
	throws ParseException, LexException, IOException, CatParseException;

    /**
     * Returns the results of the parse
     *
     * @return a collection of Constituents
     */
    public ArrayList getResult();
    
    /**
     * Makes changes to the knowledge base that occurred locally during the
     * parse.  Not all parsers need implement this-- it makes sense for
     * incremental parsers.
     *
     * @param kb the knowledge base to update
     * @param lex the constituent that was finally chosen as the correct
     *            interpretation
     */
    public void updateKB(KB kb, Constituent lex);

    /**
     * Returns the results of the parse reduced by a semantic/syntactic
     * filter
     *
     * @return a collection of Constituents
     */
    public ArrayList getFilteredResult();
}

