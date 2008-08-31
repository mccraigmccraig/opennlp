///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2006 Thomas Morton
// 
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////

package opennlp.tools.parser;

/**
 *  Interface for full-syntactic parsers. 
 */
public interface Parser {

  /**
   * Returns the specified number of parses or fewer for the specified tokens. <br>
   * <b>Note:</b> The nodes within
   * the returned parses are shared with other parses and therefore their parent node references will not be consistent
   * with their child node reference.  {@link #setParents setParents} can be used to make the parents consistent
   * with a partuicular parse, but subsequent calls to <code>setParents</code> can invalidate the results of earlier
   * calls.<br>  
   * @param tokens A parse containing the tokens with a single parent node.
   * @param numParses The number of parses desired.
   * @return the specified number of parses for the specified tokens.
   */
  public abstract Parse[] parse(Parse tokens, int numParses);

  /**
   * Returns a parse for the specified parse of tokens.
   * @param tokens The root node of a flat parse containing only tokens. 
   * @return A full parse of the specified tokens or the flat chunks of the tokens if a fullparse could not be found.
   */
  public abstract Parse parse(Parse tokens);

}