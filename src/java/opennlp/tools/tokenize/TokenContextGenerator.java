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
package opennlp.tools.tokenize;

/**
 * Interface for {@link TokenizerME} context generators.
 */
public interface TokenContextGenerator {

  /**
   * Returns an array of features for the specified sentence string at the specified index.
   * 
   * @param sentence The string for a sentence.
   * @param index The index to consider splitting as a token.
   * 
   * @return an array of features for the specified sentence string at the 
   *   specified index.
   */
  public abstract String[] getContext(String sentence, int index);
}