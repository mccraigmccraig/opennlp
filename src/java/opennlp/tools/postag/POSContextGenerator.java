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

package opennlp.tools.postag;

import java.util.List;

import opennlp.tools.util.BeamSearchContextGenerator;
import opennlp.tools.util.Sequence;


/**
 * The interface for a context generator for the POS Tagger.
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.7 $, $Date: 2004/08/13 16:58:14 $
 */

public interface  POSContextGenerator extends BeamSearchContextGenerator { 
  public String[] getContext(int pos, Object[] tokens, String[] prevTags, Object[] ac);
}
