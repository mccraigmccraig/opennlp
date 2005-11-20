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
package opennlp.tools.coref.sim;

import java.io.IOException;

/**
 * Interface for training a similarity, gender, or number model. 
 */
public interface TrainSimilarityModel {
  public void trainModel() throws IOException;
  /**
   * Creates simialrity training pairs based on the specified extents.
   * Extents are considered compatible is they are in the same coreference chain,
   * have the same named-entity tag, or share a common head word.  Incompatible extents are chosen at random 
   * from the set of extents which don't meet this criteria.
   * @param extents
   */
  public void setExtents(Context[] extents);
}