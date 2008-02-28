///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2008 Thomas Morton
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

import opennlp.tools.util.BeamSearchContextGenerator;

/**
 * Interface for generating the context for an name finder by specifying a set of geature generators. 
 *
 */
public interface NameContextGenerator extends BeamSearchContextGenerator {
  
  /**
   * Adds a feature generator to this set of feature generators.
   * @param generator The feature generator to add. 
   */
  public void addFeatureGenerator(AdaptiveFeatureGenerator generator);
  
  /**
   * Informs all the feature generators for a name finder that the specified tokens have been classified with the coorisponds set of specified outcomes.
   * @param tokens The tokens of the sentence or other text unit which has been processed.
   * @param outcomes The outcomes associated with the specified tokens.
   */
  public void updateAdaptiveData(String[] tokens, String[] outcomes);
  
  /**
   * Informs all the feature generators for a name finder that the context of the adaptive data (typically a document) is no longer valid.
   */
  public void clearAdaptiveData();
  
}