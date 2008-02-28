///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2006 Tom Morton
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

package opennlp.tools.tokenize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.Span;

/**
 * Performs tokenization using character classes.
 * @author tsmorton
 *
 */
public class SimpleTokenizer extends AbstractTokenizer {

  public Span[] tokenizePos(String s) {
    CharacterEnum charType = CharacterEnum.WHITESPACE;
    CharacterEnum state = charType;
    
    List tokens = new ArrayList();
    int sl = s.length();
    int start = -1;
    char pc = 0;
    for (int ci = 0; ci < sl; ci++) {
      char c = s.charAt(ci);
      if (Character.isWhitespace(c)) {
        charType = CharacterEnum.WHITESPACE;
      }
      else if (Character.isLetter(c)) {
        charType = CharacterEnum.ALPHABETIC;
      }
      else if (Character.isDigit(c)) {
        charType = CharacterEnum.NUMERIC;
      }
      else {
        charType = CharacterEnum.OTHER;
      }
      if (state == CharacterEnum.WHITESPACE) {
        if (charType != CharacterEnum.WHITESPACE) {
          start = ci;
        }
      }
      else {
        if (charType != state || charType == CharacterEnum.OTHER && c != pc) {
          tokens.add(new Span(start, ci));
          start = ci;
        }
      }
      state = charType;
      pc = c;
    }
    if (charType != CharacterEnum.WHITESPACE) {
      tokens.add(new Span(start, sl));
    }
    return (Span[]) tokens.toArray(new Span[tokens.size()]);
  }


  /**
   * 
   * @param args
   * 
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException {
    if (args.length != 0) {
      System.err.println("Usage:  java opennlp.tools.tokenize.SimpleTokenizer < sentences");
      System.exit(1);
    }
    opennlp.tools.tokenize.Tokenizer tokenizer = new SimpleTokenizer();
    java.io.BufferedReader inReader = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
    for (String line = inReader.readLine(); line != null; line = inReader.readLine()) {
      if (line.equals("")) {
        System.out.println();
      }
      else {
        String[] tokens = tokenizer.tokenize(line);
        if (tokens.length > 0) {
          System.out.print(tokens[0]);
        }
        for (int ti=1,tn=tokens.length;ti<tn;ti++) {
          System.out.print(" "+tokens[ti]);
        }
        System.out.println();
      }
    }
  }

}

class CharacterEnum {
  static final CharacterEnum WHITESPACE = new CharacterEnum("whitespace");
  static final CharacterEnum ALPHABETIC = new CharacterEnum("alphabetic");
  static final CharacterEnum NUMERIC = new CharacterEnum("numeric");
  static final CharacterEnum OTHER = new CharacterEnum("other");

  private String name;

  private CharacterEnum(String name) {
    this.name = name;
  }

  public String toString() {
    return name;
  }
}
