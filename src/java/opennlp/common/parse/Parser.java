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

package opennlp.common.parse;

import opennlp.common.synsem.*;
import opennlp.common.xml.*;

import java.io.*;
import java.util.ArrayList;

/**
 * A Parser is a module that takes a string as input and produces
 * a structured form representing that string.
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.6 $, $Date: 2002/02/08 12:17:50 $
 */
public interface Parser {
    
    /**
     * Parses a string
     *
     * @param s the string
     * @exception CatParseException thrown if the syntax/semantics of
     *            a lexical item can't be parsed
     * @exception LexException thrown if a lex item isn't found
     * @exception ParseException thrown if a parse can't be found for the
     *            entire string
     */
    public void parse (String s) throws ParseException;
    
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
    public void parse (Sign[] inits) throws ParseException;

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
    public void parse (NLPDocument d) throws ParseException;

    /**
     * Returns the results of the parse
     *
     * @return a collection of Constituents
     */
    public ArrayList getResult();
    

    /**
     * Returns the results of the parse reduced by a semantic/syntactic
     * filter
     *
     * @return a collection of Constituents
     */
    public ArrayList getFilteredResult();
}

