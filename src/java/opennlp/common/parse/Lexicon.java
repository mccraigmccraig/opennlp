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
 * @version     $Revision: 1.3 $, $Date: 2001/11/29 13:21:10 $
 */
public interface Lexicon {
    
    /**
     * For a given lexical string, return all of its lexical entries
     *
     * @param w the word
     * @return a collection of Constituents
     * @exception LexException thrown if word not found
     */
    public Collection getWord(String w) throws LexException;

    /**
     * For a lexical string of 1 or more words, return all of its lexical
     * entries for each "word".  The method is allowed to group several words
     * together .
     *
     * @param ppd An XML (NLP) document of preprocessed text.
     * @return a list of WordHashes
     * @exception LexException thrown if word not found
     */
    public List getWords(NLPDocument ppd) throws LexException;
    
    /**
     * For a lexical string of 1 or more words, return all of its lexical
     * entries for each "word".  The method is allowed to group several words
     * together .
     *
     * @param w the words in string format.  We assume tokens are separeted
     *          by whitespace
     * @return a list of WordHashes
     * @exception LexException thrown if word not found
     */
    public List getWords(String w) throws LexException;
    
}

