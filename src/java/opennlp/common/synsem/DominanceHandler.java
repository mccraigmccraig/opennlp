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


import java.util.*;
import java.io.*;

/**
 * The purpose of Dominance Handlers is to track co-reference restrictions
 * and obligations for a particular Denoter.  This can be computed in a variety
 * of ways and over a variety of structures as long as you are dealing with
 * Denoters in the end.  For example, in Grok, we follow the CCG paradigm (as
 * usual) and compute dominance with a DominanceComputer class which operates
 * over LF structures, but it should be quite possible to do the same for TAG
 * trees or Multimodal Logical Grammar structures and so on.
 *
 * While it is not necessary to follow the following idea of dominance, here is
 * a rough illustration of what we mean by ExternalDominators, LocalDominators,
 * and Dominated.  In the following sentence,
 *
 *     John told Bill that Mary showed him a Picasso.
 *
 * the Denoter corresponding to "him" has the following Dominance profile:
 *
 *     ExternalDominators = {Denoter of "John", Denoter of "Bill"}
 *     LocalDominators = {Denoter of "Mary"}
 *     Dominated = {Denoter of "a Picasso"}
 *
 * However, these could vary depending on your theory of how dominance of this
 * sorts works.  Our current computation of dominance relations (in Grok) is a
 * bit of a stopgap, so we'd be glad to look at other solutions and ideas.
 * 
 *
 * @author      Jason Baldridge
 * @version $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */
public interface DominanceHandler extends Serializable {

    /**
     * Sets the ExternalDominators for this DominanceHandler.
     *
     * @param gs the Set of Denoters which are ExternalDominators
     */
    public void setExternalDominators(Set gs);
    
    /**
     * Sets the LocalDominators for this DominanceHandler.
     *
     * @param gs the Set of Denoters which are LocalDominators
     */
    public void setLocalDominators(Set gs);

    /**
     * Sets the Dominated Denoters for this DominanceHandler.
     *
     * @param gs the Set of Denoters which are Dominated
     */
    public void setDominated(Set gs);


    /**
     * Gets the ExternalDominators for this DominanceHandler.
     *
     * @return a Set containing the ExternalDominators
     */   
    public Set getExternalDominators();

    /**
     * Gets the LocalDominators for this DominanceHandler.
     *
     * @return a Set containing the LocalDominators
     */   
    public Set getLocalDominators();

    /**
     * Gets the Dominatored Denoters for this DominanceHandler.
     *
     * @return a Set containing the Dominated Denoters
     */   
    public Set getDominated();

    /**
     * Gets the Denoters which can Reflexively bind the Denoter of this
     * DominanceHandler. 
     *
     * @return a Set containing the possible reflexive binders
     */   
    public Set getReflexiveBinders(Denoter reflVar);

    /**
     * When a Denoter in one of this Handler's Sets has been resolved to a FC,
     * we should update the Handler so as to take advantage of further
     * information contained in the (more specific) FC.  Hopefully, this should
     * make it easier to disambiguate the resolution of the Denoter which this
     * Handler takes care of.
     *
     * @param d1 The Denoter which has been resolved and needs to be
     *           replaced.
     * @param d2 The Denoter which d1 was resolved to (via the Discourse Model
     *           or whatever)
     * */
    public void replace(Denoter d1, Denoter d2);

    /**
     * When the Denoter which this DominanceHandler takes care of has been
     * resolved (to a FC generally), we inform the DominanceHandlers of the
     * Denoters contained in this DominanceHandler's Sets of the change.
     * 
     * @param d1 The Denoter which has been resolved and needs to be
     *           replaced.
     * @param d2 The Denoter which d1 was resolved to (via the Discourse Model
     *           or whatever)
     */    
    public void propogateResolution(Denoter d1, Denoter d2);


}
