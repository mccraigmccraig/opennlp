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

/**
 * Abstraction represents a semantic expression that has abstracted over a
 * portion of it's body.  It is made up of a variable and a body where the
 * variable takes scope over the body.  In the lambda calculus, this may look
 * something like <i> lambda x. like(mary, x) </i>.  An abstraction only
 * supports a single variable at a time and that variable is not required to
 * appear in the body (although this wouldn't make a lot of sense).
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */
public interface Abstraction extends Denoter {
    /**
     * Access function for scoping variable.
     *
     * @return The variable that represents the portion of the body that has
     *         been abstracted away.
     */
    public Denoter getParameter();

    /**
     * Access function for the semantic form that has been abstracted over.
     *
     * @return The semantic form that has been abstracted over.  It is not
     *         required to contain the parameter, but this would be strange.
     */
    public Denoter getBody();
}
