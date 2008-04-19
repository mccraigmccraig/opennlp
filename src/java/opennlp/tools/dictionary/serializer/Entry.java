///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2006 Calcucare GmbH
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

package opennlp.tools.dictionary.serializer;

import opennlp.tools.ngram.TokenList;

/**
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.4 $, $Date: 2008/04/19 22:07:26 $
 */
public class Entry {
  
  private TokenList mTokens;
  private Attributes mAttributes;
  
  /**
   * Initializes the current instance.
   * 
   * @param tokens
   * @param attributes
   */
  public Entry(TokenList tokens, Attributes attributes) {
    mTokens = tokens;
    mAttributes = attributes;
  }
  
  /**
   * Retrieves the {@link Token}s.
   * 
   * @return the {@link Token}s
   */
  public TokenList getTokens() {
    return mTokens;
  }
  
  /**
   * Retrieves the {@link Attributes}.
   * 
   * @return the {@link Attributes}
   */
  public Attributes getAttributes() {
    return mAttributes;
  }
}