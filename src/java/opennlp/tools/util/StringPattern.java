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
package opennlp.tools.util;

/**
 * Recognizes predefined patterns in strings.
 */
public class StringPattern {
  
  private static int INITAL_CAPITAL_LETTER = 0x1;
  private static int ALL_CAPITAL_LETTER = 0x1 << 1;
  private static int ALL_LOWERCASE_LETTER = 0x1 << 2;
  private static int ALL_LETTERS = 0x1 << 3;
  private static int ALL_DIGIT = 0x1 << 4;  
  private static int CONTAINS_PERIOD = 0x1 << 5;
  private static int CONTAINS_COMMA = 0x1 << 6;
  private static int CONTAINS_SLASH = 0x1 << 7;
  private static int CONTAINS_DIGIT = 0x1 << 8;
  private static int CONTAINS_HYPHEN = 0x1 << 9;  
  private static int CONTAINS_LETTERS = 0x1 << 10;
  private static int CONTAINS_UPPERCASE = 0x1 << 11;
  
  private final int pattern;
  
  private StringPattern(int pattern) {
    this.pattern = pattern;
  }
  
  public boolean isInitialCapitalLetter() {
    return (pattern & INITAL_CAPITAL_LETTER) > 0;
  }
  
  public boolean isAllCapitalLetter() {
    return (pattern & ALL_CAPITAL_LETTER) > 0;
  }
  
  public boolean isAllLowerCaseLetter() {
    return (pattern & ALL_LOWERCASE_LETTER) > 0;
  }
  
  public boolean isAllDigit() {
    return (pattern & ALL_DIGIT) > 0;
  }

  public boolean containsPeriod() {
    return (pattern & CONTAINS_PERIOD) > 0;
  }

  public boolean containsComma() {
    return (pattern & CONTAINS_COMMA) > 0;
  }

  public boolean containsSlash() {
    return (pattern & CONTAINS_SLASH) > 0;
  }

  public boolean containsDigit() {
    return (pattern & CONTAINS_DIGIT) > 0;
  }

  public boolean containsHyphen() {
    return (pattern & CONTAINS_HYPHEN) > 0;
  }

  public boolean containsLetters() {
    return (pattern & CONTAINS_LETTERS) > 0;
  }
  
  public static StringPattern recognize(String token) {
    
    int pattern = ALL_CAPITAL_LETTER | ALL_LOWERCASE_LETTER | ALL_DIGIT | ALL_LETTERS;
    
    for (int i = 0; i < token.length(); i++) {
      final char ch = token.charAt(i);
      
      final int letterType = Character.getType(ch);
      
      boolean isLetter = letterType == Character.UPPERCASE_LETTER || 
          letterType == Character.LOWERCASE_LETTER ||
          letterType == Character.TITLECASE_LETTER  ||
          letterType == Character.MODIFIER_LETTER ||
          letterType == Character.OTHER_LETTER;
          
      if (isLetter) {
        pattern |= CONTAINS_LETTERS;
        pattern &= ~ALL_DIGIT;
        
        if (letterType == Character.UPPERCASE_LETTER) {
          if (i == 0) {
            pattern |= INITAL_CAPITAL_LETTER;
          }
          
          pattern |= CONTAINS_UPPERCASE;
          
          pattern &= ~ALL_LOWERCASE_LETTER;
        }
        else {
          pattern &= ~ALL_CAPITAL_LETTER;
        }
      }
      else {
        // contains chars other than letter, this means
        // it can not be one of these:
        pattern &= ~ALL_LETTERS;
        pattern &= ~ALL_CAPITAL_LETTER;
        pattern &= ~ALL_LOWERCASE_LETTER;
        
        if (letterType == Character.DECIMAL_DIGIT_NUMBER) {
          pattern |= CONTAINS_DIGIT;
        }
        else {
          pattern &= ~ALL_DIGIT;
        }
        
        switch (ch) {
        case ',':
          pattern |= CONTAINS_COMMA;
          break;
          
        case '.':
          pattern |= CONTAINS_PERIOD;
          break;
          
        case '/':
          pattern |= CONTAINS_SLASH;
          break;
          
        case '-':
          pattern |= CONTAINS_HYPHEN;
          break;
          
        default:
          break;
        }
      }
    }
      
    return new StringPattern(pattern);
  }
}