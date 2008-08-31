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

import opennlp.tools.util.Span;

/**
 * The interface for name finders which provide name tags for a sequence of tokens.
 * @author      Thomas Morton
 */
public interface TokenNameFinder {
  
  /** Generates name tags for the given sequence, typically a sentence, returning token spans for any identified names.
   * @param tokens an array of the tokens or words of the sequence, typically a sentence.
   * @return an array of spans for each of the names identified.
   */
  public Span[] find(String tokens[]);  
}
