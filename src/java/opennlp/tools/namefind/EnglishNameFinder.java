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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.util.Span;

/**
 * @author Tom Morton
 *
 */
public class EnglishNameFinder extends NameFinderME {

  /**
   * @param mod
   */
  public EnglishNameFinder(MaxentModel mod) {
    super(mod);
  }

  /**
   * @param mod
   * @param cg
   */
  public EnglishNameFinder(MaxentModel mod, NameContextGenerator cg) {
    super(mod, cg);
  }

  /**
   * @param mod
   * @param cg
   * @param beamSize
   */
  public EnglishNameFinder(MaxentModel mod, NameContextGenerator cg, int beamSize) {
    super(mod, cg, beamSize);
  }

  public static Span[] tokenizeToSpans(String s) {
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
        if (charType != state || (charType == CharacterEnum.OTHER && c != pc)) {
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
    return ((Span[]) tokens.toArray(new Span[tokens.size()]));
  }

  public static String[] spansToStrings(Span[] spans, String s) {
    String[] tokens = new String[spans.length];
    for (int si = 0, sl = spans.length; si < sl; si++) {
      tokens[si] = s.substring(spans[si].getStart(), spans[si].getEnd());
    }
    return tokens;
  }

  public static String[] tokenize(String s) {
    return spansToStrings(tokenizeToSpans(s), s);
  }

  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.err.println("Usage EnglishNameFinder model1 model2 ... modelN < sentnces");
      System.exit(1);
    }

    EnglishNameFinder[] finders = new EnglishNameFinder[args.length];
    String[] names = new String[args.length];
    for (int ai = 0; ai < args.length; ai++) {
      String modelName = args[ai];
      finders[ai] = new EnglishNameFinder(new SuffixSensitiveGISModelReader(new File(modelName)).getModel());
      int nameStart = modelName.lastIndexOf(System.getProperty("file.separator")) + 1;
      int nameEnd = modelName.indexOf('.', nameStart);
      if (nameEnd == -1) {
        nameEnd = modelName.length();
      }
      names[ai] = modelName.substring(nameStart, nameEnd);
    }
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String[][] finderTags = new String[finders.length][];
    for (String line = in.readLine(); null != line; line = in.readLine()) {

      Span[] spans = tokenizeToSpans(line);
      String[] tokens = spansToStrings(spans, line);
      for (int fi = 0, fl = finders.length; fi < fl; fi++) {
        finderTags[fi] = finders[fi].find(tokens, Collections.EMPTY_MAP);
        System.err.println(names[fi] + " " + java.util.Arrays.asList(finderTags[fi]));
      }
      for (int ti = 0, tl = tokens.length; ti < tl; ti++) {
        for (int fi = 0, fl = finders.length; fi < fl; fi++) {
          //check for end tags
          if (ti != 0) {
            if ((finderTags[fi][ti].equals(NameFinderME.START) || finderTags[fi][ti].equals(NameFinderME.OTHER)) && (finderTags[fi][ti - 1].equals(NameFinderME.START) || finderTags[fi][ti - 1].equals(NameFinderME.CONTINUE))) {
              System.out.print("</" + names[fi] + ">");
            }
          }
        }
        if (ti > 0 && spans[ti - 1].getEnd() < spans[ti].getStart()) {
          System.out.print(line.substring(spans[ti - 1].getEnd(), spans[ti].getStart()));
        }
        //check for start tags
        for (int fi = 0, fl = finders.length; fi < fl; fi++) {
          if (finderTags[fi][ti].equals(NameFinderME.START)) {
            System.out.print("<" + names[fi] + ">");
          }
        }
        System.out.print(tokens[ti]);
      }
      //final end tags
      if (tokens.length != 0) {
        for (int fi = 0, fl = finders.length; fi < fl; fi++) {
          if (finderTags[fi][tokens.length - 1].equals(NameFinderME.START) || finderTags[fi][tokens.length - 1].equals(NameFinderME.CONTINUE)) {
            System.out.print("</" + names[fi] + ">");
          }
        }
      }
      if (spans[tokens.length - 1].getEnd() < line.length()) {
        System.out.print(line.substring(spans[tokens.length - 1].getEnd()));
      }
      System.out.println();
    }
  }
}

class CharacterEnum {
  public static final CharacterEnum WHITESPACE = new CharacterEnum("whitespace");
  public static final CharacterEnum ALPHABETIC = new CharacterEnum("alphabetic");
  public static final CharacterEnum NUMERIC = new CharacterEnum("numeric");
  public static final CharacterEnum OTHER = new CharacterEnum("other");

  private String name;

  private CharacterEnum(String name) {
    this.name = name;
  }

  public String toString() {
    return name;
  }
}
