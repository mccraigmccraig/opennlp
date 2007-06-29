///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2007 OpenNlp
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
 * Name finding interface which processes an entire document allowing the name finder to use context
 * from the entire document.
 * @author tsmorton
 *
 */
public interface DocumentNameFinder {

  /**
   * Returns tokens span for the specified document of sentences and their tokens.  
   * Span start and end indices are relitive to the sentence they are in.
   * For example, a span identifying a name consisting of the first and second word of the second sentence would
   * be 0..2 and be referenced as spans[1][0].
   * @param document An array of tokens for each sentence of a document.
   * @return The token spans for each sentence of the specified document.  
   */
  public abstract Span[][] find(String[][] document);

}