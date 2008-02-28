///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2007 OpenNlp
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

import java.util.List;

/**
 * An interface for generating features for name entity identification and for updating document level contexts.  
 */
public interface AdaptiveFeatureGenerator {
 
  /**
   * Adds the appropriate features for the token at the specified index with the 
   * specified array of previous outcomes to the specified list of features.
   * @param features The list of features to be added to.
   * @param tokens The tokens of the sentence or other text unit being processed.
   * @param index The index of the token which is currently being processed.
   * @param previousOutcomes The outcomes for the tokens prior to the specified index.
   */
  void createFeatures(List features, String[] tokens, int index, String[] previousOutcomes);
  
  /**
   * Informs the feature generator that the specified tokens have been classified with the coorisponds set of specified outcomes.
   * @param tokens The tokens of the sentence or other text unit which has been processed.
   * @param outcomes The outcomes associated with the specified tokens.
   */
   void updateAdaptiveData(String[] tokens, String[] outcomes);
  
  /**
   * Informs the feature generator that the context of the adaptive data (typically a document) is no longer valid.
   */
   void clearAdaptiveData();
}