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

import opennlp.common.synsem.*;

/**
 * Raised when there is an a semantic expression that cannot be accomodated.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.2 $, $Date: 2001/11/29 13:24:15 $
 */

public class AccommodateException extends opennlp.common.NLPException {
    /** The semantic form that failed to be accommodated */
    protected Denoter notAccommodated;

    /**
     * Class constructor specifying the semantic form not accommodated.
     *
     * @param d semantic form not accommodated
     */
    public AccommodateException(Denoter d) { notAccommodated = d; }

    /**
     * Accessor function for the semantic form not accommodated.
     *
     * @return semantic form not accommodated
     */
    public Denoter getFailedDenoter() { return notAccommodated; }
    
    public String toString() {
	return "Cannot accommodate expression " + notAccommodated;
    }
    
}
