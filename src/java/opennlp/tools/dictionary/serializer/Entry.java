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
 * @version $Revision: 1.1 $, $Date: 2006/11/17 09:37:22 $
 */
public class Entry {
  
  private TokenList mTokens;
  private Attributes mAttributes;
  
  public Entry(TokenList tokens, Attributes attributes) {
    mTokens = tokens;
    mAttributes = attributes;
  }
  
  public TokenList getTokens() {
    return mTokens;
  }
  
  public Attributes getAttributes() {
    return mAttributes;
  }
}