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
package opennlp.common.parse;

import opennlp.common.structure.*;
import java.util.ArrayList;
import java.util.Iterator;
    
/**
 * A RuleGroup which can be used to generate sentences in addition to
 * being usable for processing.
 *
 * @author      Gann Bierner
 * @version $Revision: 1.1 $, $Date: 2001/11/05 14:42:54 $ 
*/
public interface ReversibleRuleGroup extends RuleGroup {
    public void startRightMatch(Constituent[] words, Constituent ans);
    public Constituent nextRightMatch();
    public void startMatchGen(Constituent w);
    public Constituent[] nextMatchGen(Constituent w);
}
