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
package opennlp.tools.coref.mention;

import java.util.List;


public interface HeadFinder {

  /** Returns the child parse which contains the lexical head of the specifie parse.
   * @param parse the parse in which to find the head.a
   * @return The parse containing the lexical head of the specified parse.  If no head is
   * available or the constituent has no sub-components that are eligible heads then null is returned.
   */
  public Parse getHead(Parse parse);
  
  /** Returns which index the specified list of token is the head word.
   * @param toks The tokens of a basal noun phrase.
   * @return The index of the head token.  
   */
  public int getHeadIndex(List toks);

  /** Returns the parse bottom-most head of a <code>Parse</code>.  If no
   * head is available which is a child of <code>p</code> then
   *  <code>p</code> is returned. 
   *  @param p Parse to find the head of.
   *  @return bottom-most head of p.
   */
  public Parse getLastHead(Parse p);

  /** Returns head token for the parse p.
   * @param p parse to get head from.
   * @return head token parse.
   */
  public Parse getHeadToken(Parse p);


}
