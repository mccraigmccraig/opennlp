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
import opennlp.common.unify.*;


/**
 * Interface for categorial rules.
 *
 * @author Gann Bierner and Jason Baldridge
 * @version $Revision: 1.2 $, $Date: 2002/02/21 16:01:35 $
 */
public interface Rule {

    /**
     * Apply this rule to some input categories.
     *
     * @param inputs the input categories to try to combine
     * @return the Category resulting from using this Rule to combine the
     *         inputs
     * @exception UnifyFailure if the inputs cannot be combined by this Rule
     **/
    public Category applyRule (Category[] inputs) throws UnifyFailure;

    /**
     * The number of arguments this rule takes.  For example, the arity of the
     * forward application rule of categorial grammar (X/Y Y => Y) is 2.
     *
     * @return the number of arguments this rule takes
     **/
    public int arity();
    
}

