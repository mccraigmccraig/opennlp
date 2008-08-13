///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2008 OpenNlp
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

package opennlp.tools.namefind;

import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;
import opennlp.tools.util.ModelUtil;

/**
 * The {@link TokenNameFinderModel} is the model used
 * by a learnable {@link TokenNameFinder}.
 * 
 * @see NameFinderME
 */
public class TokenNameFinderModel {
  
  public TokenNameFinderModel(GISModel maxentNameFinderModel) {
    if (!isModelValid(maxentNameFinderModel)) {
      throw new IllegalArgumentException("Model not compatible with name finder!");
    }
  }
  
  private static boolean isModelValid(MaxentModel model) {
    
    return ModelUtil.validateOutcomes(model, NameFinderME.START) ||
        ModelUtil.validateOutcomes(model, NameFinderME.OTHER) ||
        ModelUtil.validateOutcomes(model, NameFinderME.START, NameFinderME.OTHER) ||
        ModelUtil.validateOutcomes(model, NameFinderME.START, NameFinderME.CONTINUE) ||
        ModelUtil.validateOutcomes(model, NameFinderME.START, NameFinderME.CONTINUE, 
            NameFinderME.OTHER);
  }
}