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
 * @version     $Revision: 1.2 $, $Date: 2001/11/29 13:21:11 $
 */
public interface ReversibleLexicon extends Lexicon {
    
    /**
     * Return all lexical items whose semantics have the same head
     * as computed by Category.
     *
     * @param h the head to look for
     * @return a collection of Constituents
     */
    public Collection getSemHeads(String h);
    
    /**
     * Return all lexical items where one of its presuppositions
     * has the same head as computed by Category.
     *
     * @param h the head to look for
     * @return a collection of Constituents
     */
    public Collection getPreHeads(String h);
}


