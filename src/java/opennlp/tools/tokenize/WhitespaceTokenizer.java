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

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.Span;

/**
 * This tokenizer uses white spaces to tokenize the input text.
 */
public class WhitespaceTokenizer extends AbstractTokenizer {
  
  /**
   * Use this static reference to retrieve an instance of the 
   * {@link WhitespaceTokenizer}.
   */
  public static final WhitespaceTokenizer INSTANCE = new WhitespaceTokenizer();
  
  /**
   * Use the {@link WhitespaceTokenizer#INSTANCE} field to retrieve an instance.
   */
  private WhitespaceTokenizer() {
  }
  
  public Span[] tokenizePos(String d) {
    int tokStart = -1;
    List tokens = new ArrayList();
    boolean inTok = false;

    //gather up potential tokens
    int end = d.length();
    for (int i = 0; i < end; i++) {
      if (Character.isWhitespace(d.charAt(i))) {
        if (inTok) {
          tokens.add(new Span(tokStart, i));
          inTok = false;
          tokStart = -1;
        }
      }
      else {
        if (!inTok) {
          tokStart = i;
          inTok = true;
        }
      }
    }
    if (inTok) {
      tokens.add(new Span(tokStart, end));
    }
    return (Span[]) tokens.toArray(new Span[tokens.size()]);
  }
}