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

package opennlp.common.generate;

import opennlp.common.synsem.*;
import java.util.Collection;

/**
 * The interface for a natural language generator.  The generator must
 * be capable of converting a semantic/syntactic information into a string.
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.4 $, $Date: 2002/02/08 12:17:50 $
 */
public interface Generator {

    /**
     * Performs NL generation.
     *
     * @param c the category describing the desired result.  This should
     *          contain both semantic and syntactic information if
     *          the syntax is desired.
     * @param cgoals the set of communicative goals that the generator
     *               should try to incorporate into the final result.
     *               These should be <code>opennlp.common.synsem.LFs</code>.
     * @return the lexical string contained in a <code> Sign </code>.
     */
    public Sign generate (Category c, Collection cgoals);

    
}
