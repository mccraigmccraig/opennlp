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

package opennlp.common.generate;

import opennlp.common.structure.*;
import java.util.Collection;

/**
 * The interface for a natural language generator.  The generator must
 * be capable of converting a semantic/syntactic information into a string.
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */
public interface Generator {

    /**
     * Performs NL generation.
     *
     * @param c the category describing the desired result.  This should
     *          contain both semantic and syntactic information if
     *          the syntax is desired.  The <code> Bundle </code>
     *          category in opennlp.grok can do this.
     * @param cgoals the set of communicative goals that the generator
     *               should try to incorporate into the final result.
     *               These should be Denoters.
     * @param kb the knowledge base in which to look for information in
     *           how to generate entities and kinds if they are contained
     *           in the semantics
     * @return the lexical string contained in a <code> Constituent </code>.
     *         This way you can also include derivations and so forth.
     */
    public Constituent generate(Category c, Collection cgoals, KB kb);

    /**
     * Performs NL generation.
     *
     * @param c the category describing the desired result.  This should
     *          contain both semantic and syntactic information if
     *          the syntax is desired.  The <code> Bundle </code>
     *          category in opennlp.grok can do this.
     * @param kb the knowledge base in which to look for information in
     *           how to generate entities and kinds if they are contained
     *           in the semantics
     * @return the lexical string contained in a <code> Constituent </code>.
     *         This way you can also include derivations and so forth.
     */
    public Constituent generate(Category c, KB kb);
    
}
