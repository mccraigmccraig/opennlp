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

package opennlp.common.structure;

import opennlp.common.discourse.*;
import opennlp.common.synsem.*;

import javax.swing.tree.TreeModel;

/**
 * This describes a very general hiearchy-- nothing says that you can't
 * implement a Graph with it.  The only restriction is that you need
 * to be able to break it up into tree form to be able to use Java's
 * JTree viewing capability.  This will mean redundancy of nodes-- but that's
 * no big deal.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */

public interface Hierarchy extends TreeModel {

    /**
     * Stores another hierarchy to be accessed if this one doesn't
     * contain the desired information.
     *
     * @param h the new hierarchy
     */
    public void setHigherKnowledge(Hierarchy h);
    
    /**
     * Causes one node to be connected to another
     *
     * @param desc1 The child node
     * @param desc2 The parent node
     */
    public void connect(Kind desc1, Kind desc2);
    
    /**
     * adds a new node
     *
     * @param node the new node
     */
    public void addNew(Kind node);
    
    /**
     * returns a new node represented by a semantic description
     *
     * @param c the abstraction that refers to some node
     * @return the node
     */
    public Kind getNode(Abstraction c);
}
