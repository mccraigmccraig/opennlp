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

import opennlp.tools.util.Span;

/**
 * A {@link TokenSample} is text with token spans.
 */
public class TokenSample {
  
  private String text;
  
  private Span tokenSpans[];
  
  /**
   * Initializes the current instance.
   * 
   * @param text the text which contains the tokens.
   * @param tokenSpans the spans which mark the begin and end of the tokens.
   */
  public TokenSample(String text, Span tokenSpans[]) {
    this.text = text;
    this.tokenSpans = tokenSpans;
    
    for (int i = 0; i < tokenSpans.length; i++) {
      if (tokenSpans[i].getStart() < 0 || tokenSpans[i].getStart() > text.length() ||
          tokenSpans[i].getEnd() > text.length() || tokenSpans[i].getEnd() < 0) {
        throw new IllegalArgumentException("Span " + tokenSpans[i].toString() + 
            " is out of bounds!");
      }
    }
  }
  
  /**
   * Retrieves the text.
   */
  public String getText() {
    return text;
  }
  
  /**
   * Retrieves the token spans.
   */
  public Span[] getTokenSpans() {
    return tokenSpans;
  }
  
  @Override
  public String toString() {
    return getText();
  }
}