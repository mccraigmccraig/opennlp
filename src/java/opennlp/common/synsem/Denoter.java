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

package opennlp.common.synsem;

import opennlp.common.synsem.*;

/**
 * An interface for Categories which may be used as logical arguments (for
 * example, in an LF)
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */

public interface Denoter extends opennlp.common.structure.Category {

    /**
     * Compute a description of this semantic form.  This will probably go.
     *
     * @return the description
     */
    public String desc();

    /**
     * Simplifies this Denoter though some evaluation formula.  For instance
     * set operations can be evaled now: {a,b,c}-{a} => {b,c}.  Also, if
     * you have honest to god lambda expressions and applications, you can
     * perform those applications now.
     *
     * @return the reduced semantic form
     */
    public Denoter eval();

    /**
     * Changes the way we store dominance relations within this Denoter
     *
     * @param dh the new way of storing dominance relations
     */
    public void setDominanceHandler(DominanceHandler dh);
    
    /**
     * gets the way we store dominance relations within this Denoter
     *
     * @return the way of storing dominance relations
     */
    public DominanceHandler getDominanceHandler();
    
}
