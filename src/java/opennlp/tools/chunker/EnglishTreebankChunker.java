///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2003 Thomas Morton
// 
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.
// 
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
// 
//You should have received a copy of the GNU Lesser General Public
//License along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////
package opennlp.tools.chunker;

import java.util.List;

import opennlp.common.util.Sequence;
import opennlp.maxent.ContextGenerator;
import opennlp.maxent.MaxentModel;

/** This is a chunker based on the CONLL chunking task which uses Penn Treebank constituents as the basis for the chunks.
 *   See   http://cnts.uia.ac.be/conll2000/chunking/ for data and task definition.
 * @author Tom Morton
 */
public class EnglishTreebankChunker extends ChunkerME {

  public EnglishTreebankChunker(MaxentModel mod) {
    super(mod);
  }

  public EnglishTreebankChunker(MaxentModel mod, ContextGenerator cg) {
    super(mod, cg);
  }

  public EnglishTreebankChunker(MaxentModel mod, ContextGenerator cg, int beamSize) {
    super(mod, cg, beamSize);
  }

  protected boolean validOutcome(String outcome, Sequence sequence) {
    if (outcome.startsWith("I-")) {
      List tagList = sequence.getOutcomes();
      int lti = tagList.size() - 1;
      if (lti == -1) {
        return (false);
      }
      else {
        String lastTag = (String) tagList.get(lti);
        if (lastTag.equals("O")) {
          return (false);
        }
        if (!lastTag.substring(2).equals(outcome.substring(2))) {
          return (false);
        }
      }
    }
    return (true);
  }

}
