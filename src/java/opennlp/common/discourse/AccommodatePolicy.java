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

package opennlp.common.discourse;

import opennlp.common.structure.*;
import opennlp.common.synsem.*;

/**
 * Defines modules which handle accommodation of unknown
 * information in different ways.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */

public interface AccommodatePolicy {

    /**
     * Attempts to return the kind (generic) denoted by an abstraction.  If it
     * fails to find the generic, it will or will not make one based on the
     * behavior of accommodateKind.  If it does not accommodate, it should
     * throw AccommodateException.
     *
     * @param kb knowledge base in which to look for the kind.
     * @param c semantic abstraction denoting desired kind
     * @return The kind denoted by <code> c </code>
     * @exception AccommodateException if kind not found and cannot accommodate
     */
    public Kind getNode(KB kb, Abstraction c) throws AccommodateException;

    /**
     * Attempts to return the filecard denoted by a semantic form.  If it
     * fails to find the filecard, it will or will not make one based on the
     * behavior of accommodateFC and possibly accommodateEvent (if events are
     * represented as filecards.  If it does not accommodate, it should
     * throw AccommodateException.
     *
     * @param kb knowledge base in which to look for the filecard.
     * @param dntr semantic form denoting desired fc
     * @param polarity whether or not we are in a negative context
     * @return The filecard denoted by <code> dntr </code>
     * @exception AccommodateException if filecard not found and
     *            cannot accommodate
     */
    public FC getFC(KB kb, Denoter dntr, boolean polarity)
	throws AccommodateException ;
    
    /**
     * Attempts to return the filecard denoted by a semantic form.  If it
     * fails to find the filecard, it will or will not make one based on the
     * behavior of accommodateFC and possibly accommodateEvent (if events are
     * represented as filecards.  If it does not accommodate, it should
     * throw AccommodateException.
     *
     * @param kb knowledge base in which to look for the filecard.
     * @param dntr semantic form denoting desired fc
     * @return The filecard denoted by <code> dntr </code>
     * @exception AccommodateException if filecard not found and
     *            cannot accommodate
     */
    public FC getFC(KB kb, Denoter dntr) throws AccommodateException ;

    /**
     * Determines if it is ok to accommodate a semantic form as a
     * filecard.
     *
     * @param d semantic form to be accommodated
     * @param f a feature structure for the semantic form.  This is
     *          probably only useful for anaphora which might be represented
     *          as variables.  In this case, the only way to distinguish
     *          between them is the feature structure.
     * @return (non) acceptance of accommodating a filecard
     */    
    public boolean accommodateFC(Denoter d, opennlp.common.unify.Feature f);
    
    /**
     * Determines if it is ok to accommodate a semantic form as a
     * kind (or generic).
     *
     * @param d semantic form to be accommodated
     * @return (non) acceptance of accommodating a kind
     */    
    public boolean accommodateKind(Denoter d);
    
    /**
     * Determines if it is ok to accommodate a semantic form as an
     * event.  This doesn't quite fit in since there is no <code> Event
     * </code> in opennlp.  Beware this function as it may be removed later.
     *
     * @param d semantic form to be accommodated as an event
     * @return (non) acceptance of accommodating an event
     */    
    public boolean accommodateEvent(Denoter d);
    
}
