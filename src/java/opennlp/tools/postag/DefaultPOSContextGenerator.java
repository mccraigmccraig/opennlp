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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import opennlp.tools.util.Sequence;

/**
 * A context generator for the POS Tagger.
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.4 $, $Date: 2004/04/08 03:07:16 $
 */

public class DefaultPOSContextGenerator implements POSContextGenerator {

  protected final String SE = "*SE*";
  protected final String SB = "*SB*";
  private static final int PREFIX_LENGTH = 4;
  private static final int SUFFIX_LENGTH = 4;

  private static Pattern hasCap = Pattern.compile("[A-Z]");
  private static Pattern hasNum = Pattern.compile("[0-9]");

  public DefaultPOSContextGenerator() {}

  public String[] getContext(Object o) {
    Object[] data = (Object[]) o;
    return getContext(((Integer) data[0]).intValue(), (List) data[1], (Sequence) data[2], null);
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

  public String[] getContext(int pos, List tokens, Sequence s, Object[] ac) {
    return getContext(pos, tokens, s.getOutcomes());
  }

  public String[] getContext(int pos, List tokens, List tags) {
    String next, nextnext, lex, prev, prevprev;
    String tagprev, tagprevprev;
    tagprev = tagprevprev = null;
    next = nextnext = lex = prev = prevprev = null;

    lex = (String) tokens.get(pos);
    if (tokens.size() > pos + 1) {
      next = (String) tokens.get(pos + 1);
      if (tokens.size() > pos + 2)
        nextnext = (String) tokens.get(pos + 2);
      else
        nextnext = SE; // Sentence End

    }
    else {
      next = SE; // Sentence End
    }

    if (pos - 1 >= 0) {
      prev = (String) tokens.get(pos - 1);
      tagprev = (String) tags.get(pos - 1);

      if (pos - 2 >= 0) {
        prevprev = (String) tokens.get(pos - 2);
        tagprevprev = (String) tags.get(pos - 2);
      }
      else {
        prevprev = SB; // Sentence Beginning
      }
    }
    else {
      prev = SB; // Sentence Beginning
    }

    ArrayList e = new ArrayList();

    // add the word itself
    e.add("w=" + lex);

    // do some basic suffix analysis
    String[] suffs = getSuffixes(lex);
    for (int i = 0; i < suffs.length; i++) {
      e.add("suf=" + suffs[i]);
    }

    String[] prefs = getPrefixes(lex);
    for (int i = 0; i < prefs.length; i++) {
      e.add("pre=" + prefs[i]);
    }
    // see if the word has any special characters
    if (lex.indexOf('-') != -1) {
      e.add("h");
    }

    if (hasCap.matcher(lex).find()) {
      e.add("c");
    }

    if (hasNum.matcher(lex).find()) {
      e.add("d");
    }

    // add the words and pos's of the surrounding context
    if (prev != null) {
      e.add("p=" + prev);
      if (tagprev != null) {
        e.add("t=" + tagprev);
      }
      if (prevprev != null) {
        e.add("pp=" + prevprev);
        if (tagprevprev != null) {
          e.add("tt=" + tagprevprev);
        }
      }
    }

    if (next != null) {
      e.add("n=" + next);
      if (nextnext != null) {
        e.add("nn=" + nextnext);
      }
    }
    return (String[]) e.toArray(new String[e.size()]);
  }
}
