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

package opennlp.tools.namefind;

import java.util.List;

/**
 * Generates a feature which contains the token itself.
 */
public class TokenFeatureGenerator extends FeatureGeneratorAdapter {

  private static final String WORD_PREFIX = "w";
  private boolean lowercase;

  public TokenFeatureGenerator(boolean lowercase) {
    this.lowercase = lowercase;
  }
  
  public TokenFeatureGenerator() {
    this(true);
  }
  
  public void createFeatures(List features, String[] tokens, int index, String[] preds) {
    if (lowercase) {
      features.add(WORD_PREFIX + "=" + tokens[index].toLowerCase());
    }
    else {
      features.add(WORD_PREFIX + "=" + tokens[index]);
    }
  }
}