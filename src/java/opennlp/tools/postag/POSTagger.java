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

package opennlp.tools.postag;

import java.util.List;

/**
 * The interface for part of speech taggers.
 *
 * @author      Jason Baldridge
 * @version $Revision: 1.4 $, $Date: 2008/08/13 00:11:16 $ */

public interface POSTagger {

  /** Assigns the sentence of tokens pos tags.
   * @param sentence The sentece of tokens to be tagged.
   * @return a list of pos tags for each token provided in sentence.
   */
  public List<String> tag(List<String> sentence);

  /** Assigns the sentence of tokens pos tags.
   * @param sentence The sentece of tokens to be tagged.
   * @return an array of pos tags for each token provided in sentence.
   */
  public String[] tag(String[] sentence);

  /** Assigns the sentence of space-delimied tokens pos tags.
   * @param sentence The sentece of space-delimited tokens to be tagged.
   * @return a string of space-delimited pos tags for each token provided in sentence.
   */
  public String tag(String sentence);

}
