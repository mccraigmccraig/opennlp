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

import java.util.*;

import opennlp.common.structure.*;
import opennlp.common.xml.*;

/**
 * Contains words and their associated categories and semantics.
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */
public interface Lexicon {
    
    /**
     * For a given lexical string, return all of its lexical entries
     *
     * @param w the word
     * @return a collection of Constituents
     * @exception LexException thrown if word not found
     * @exception CatParseException thrown if the syntax or semantics of
     *            the word fails to parse
     */
    public Collection getWord(String w) throws LexException, CatParseException;

    /**
     * For a lexical string of 1 or more words, return all of its lexical
     * entries for each "word".  The method is allowed to group several words
     * together .
     *
     * @param ppd An XML (NLP) document of preprocessed text.
     * @return a list of WordHashes
     * @exception LexException thrown if word not found
     * @exception CatParseException thrown if the syntax or semantics of
     *            the word fails to parse
     */
    public List getWords(NLPDocument ppd)
	throws LexException, CatParseException;
    
    /**
     * For a lexical string of 1 or more words, return all of its lexical
     * entries for each "word".  The method is allowed to group several words
     * together .
     *
     * @param w the words in string format.  We assume tokens are separeted
     *          by whitespace
     * @return a list of WordHashes
     * @exception LexException thrown if word not found
     * @exception CatParseException thrown if the syntax or semantics of
     *            the word fails to parse
     */
    public List getWords(String w) throws LexException, CatParseException;
    
    /**
     * Return all lexical items whose semantics have the same head
     * as computed by Category.
     *
     * @param h the head to look for
     * @return a collection of Constituents
     * @see Category#getHead()
     */
    public Collection getSemHeads(String h);
    
    /**
     * Return all lexical items where one of its presuppositions
     * has the same head as computed by Category.
     *
     * @param h the head to look for
     * @return a collection of Constituents
     * @see Category#getHead()
     */
    public Collection getPreHeads(String h);
}

