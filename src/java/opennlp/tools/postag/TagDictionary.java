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

/** 
 * Interface to determine which tags are valid for a particular word
 * based on a tag dictionary.
 * 
 * @author Tom Morton
 */
public interface TagDictionary {
  
  /**
   * Returns a list of valid tags for the specified word.
   * 
   * @param word The word.
   * @return A list of valid tags for the specified word or null if no information
   * is available for that word.
   */
  public String[] getTags(String word);
}