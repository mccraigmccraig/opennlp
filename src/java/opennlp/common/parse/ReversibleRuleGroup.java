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
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////
package opennlp.common.parse;

import opennlp.common.synsem.*;
    
/**
 * A RuleGroup which can be used to generate sentences in addition to
 * being usable for processing.
 *
 * @author      Gann Bierner
 * @version $Revision: 1.2 $, $Date: 2002/01/05 16:12:04 $ 
*/
public interface ReversibleRuleGroup extends RuleGroup {

    public void startRightMatch (Sign[] words, Sign ans);

    public Sign nextRightMatch();

    public void startMatchGen (Sign  w);

    public Sign[] nextMatchGen (Sign w);
}
