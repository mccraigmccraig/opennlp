///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2002 Jason Baldridge and Gann Bierner
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

package opennlp.tools.postag;

import opennlp.maxent.*;
import opennlp.common.morph.MorphAnalyzer;
import opennlp.common.util.Pair;
import opennlp.common.util.Sequence;

import java.util.*;

/**
 * A context generator for the POS Tagger.
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.5 $, $Date: 2004/01/09 15:22:50 $
 */

public class POSContextGenerator implements ContextGenerator {
  private MorphAnalyzer _manalyzer;
  private static final String SE = "*SE*";
  private static final String SB = "*SB*";
  private static final int PREFIX_LENGTH = 4;
  private static final int SUFFIX_LENGTH = 4;

  public POSContextGenerator() {
    _manalyzer = null;
  }

  public POSContextGenerator(MorphAnalyzer manalyser) {
    _manalyzer = manalyser;
  }

  public String[] getContext(Object o) {
    Object[] data = (Object[]) o;
    return getContext(((Integer) data[0]).intValue(), (List) data[1], ((Sequence) data[2]).getOutcomes());
  }

  protected static String[] getPrefixes(String lex) {
    String[] prefs = new String[4];
    for (int li = 0, ll = PREFIX_LENGTH; li < ll; li++) {
      prefs[li] = lex.substring(0, Math.min(li + 1, lex.length()));
    }
    return prefs;
  }

  protected static String[] getSuffixes(String lex) {
    String[] suffs = new String[4];
    for (int li = 0, ll = PREFIX_LENGTH; li < ll; li++) {
      suffs[li] = lex.substring(Math.max(lex.length() - li - 1, 0));
    }
    return suffs;
  }

  public String[] getContext(int index, List tokens, List tags) {
    String next, nextnext, lex, prev, prevprev;
    String tagprev, tagprevprev;
    tagprev = tagprevprev = null;
    next = nextnext = lex = prev = prevprev = null;

    lex = (String) tokens.get(index);
    if (tokens.size() > index + 1) {
      next = (String) tokens.get(index + 1);
      if (tokens.size() > index + 2)
        nextnext = (String) tokens.get(index + 2);
      else
        nextnext = SE; // Sentence End

    }
    else {
      next = SE; // Sentence End
    }

    if (index - 1 >= 0) {
      prev = (String) tokens.get(index - 1);
      tagprev = (String) tags.get(index - 1);

      if (index - 2 >= 0) {
        prevprev = (String) tokens.get(index - 2);
        tagprevprev = (String) tags.get(index - 2);
      }
      else {
        prevprev = SB; // Sentence Beginning
        tagprevprev = SB;
      }
    }
    else {
      prev = SB; // Sentence Beginning
      tagprev = SB;
    }

    ArrayList e = new ArrayList();

    // add the word itself
    e.add("w=" + lex);

    // do some basic suffix analysis
    if (_manalyzer != null) {
      String[] suffs = _manalyzer.getSuffixes(lex);
      for (int i = 0; i < suffs.length; i++) {
        e.add("suf=" + suffs[i]);
      }
    }
    else {
      String[] suffs = getSuffixes(lex);
      for (int i = 0; i < suffs.length; i++) {
        e.add("suf=" + suffs[i]);
      }

      String[] prefs = getPrefixes(lex);
      for (int i = 0; i < prefs.length; i++) {
        e.add("pre=" + prefs[i]);
      }
    }
    // see if the word has any special characters
    boolean hasHyphen = false;
    boolean hasCapital = false;
    boolean hasNumber = false;
    boolean hasLetter = false;
    char[] ca = lex.toCharArray();
    for (int i = 0; i < ca.length; i++) {
      if (Character.isUpperCase(ca[i])) {
        hasCapital = true;
      }
      if (Character.isDigit(ca[i])) {
        hasNumber = true;
      }
      if (ca[i] == '-') {
        hasHyphen = true;
      }
      if (Character.isLetter(ca[i])) {
        hasLetter = true;
      }
    }
    if (hasHyphen) {
      e.add("h");
    }

    if (hasCapital) {
      e.add("c");
    }

    if (hasNumber) {
      e.add("d");
    }

    if (hasLetter) {
      e.add("l");
    }

    // add the words and pos's of the surrounding context
    e.add("p=" + prev);
    e.add("t=" + tagprev);
    e.add("pp=" + prevprev);
    e.add("tt=" + tagprevprev);
    e.add("ttt="+tagprevprev+","+tagprev);
    e.add("n=" + next);
    e.add("nn=" + nextnext);
    
    return (String[]) e.toArray(new String[e.size()]);
  }

  public static void main(String[] args) {

    POSContextGenerator gen = new POSContextGenerator();
    String[] lexA = { "the", "stories", "about", "well-heeled", "communities", "and", "developers" };
    String[] tagsA = { "DT", "NNS", "IN", "JJ", "NNS", "CC", "NNS" };
    ArrayList lex = new ArrayList();
    ArrayList tags = new ArrayList();
    for (int i = 0; i < lexA.length; i++) {
      lex.add(lexA[i]);
      tags.add(tagsA[i]);
    }

    Object[] a = { lex, tags, new Integer(2)};
    Object[] b = { lex, tags, new Integer(0)};

    String[] ans1 = gen.getContext(new Pair(a, Boolean.FALSE));
    String[] ans2 = gen.getContext(b);

    for (int i = 0; i < ans1.length; i++)
      System.out.println(ans1[i]);
    System.out.println();
    for (int i = 0; i < ans2.length; i++)
      System.out.println(ans2[i]);
  }

}
