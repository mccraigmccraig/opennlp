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
import java.util.ArrayList;
import java.util.Iterator;
    
/**
 * A set of rules that describe how lexical items should be combined
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */
public interface Rules {
    /**
     * Performs initialization necessary to attempt to combine
     * a set of lexical items.
     *
     * @param words the lexical items to combine
     */
    public void startMatch(Constituent[] words);
    
    /**
     * Returns the next successful combination of the lexical items
     * passed into the init method
     *
     * @return the resulting Constituent
     */
    public Constituent nextMatch() throws CatParseException;

    /**
     * Returns a way to iterate through the rules one at a time.  Notice that
     * right now there is no opennlp interface for a rule.  This will change.
     *
     * @return the iterator
     */
    public Iterator iterator();
    
    public void startRightMatch(Constituent[] words, Constituent ans);
    public Constituent nextRightMatch() throws CatParseException;
    public void startMatchGen(Constituent w);
    public Constituent[] nextMatchGen(Constituent w) throws CatParseException;
}
