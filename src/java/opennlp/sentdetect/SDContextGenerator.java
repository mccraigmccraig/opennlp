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

package opennlp.sentdetect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import opennlp.common.util.Pair;
import opennlp.maxent.ContextGenerator;

/**
 * Generate event contexts for maxent decisions for sentence detection.
 *
 * @author      Jason Baldridge
 * @author      Eric D. Friedman
 * @version     $Revision: 1.2 $, $Date: 2003/07/15 15:57:04 $
 */

public class SDContextGenerator implements ContextGenerator {

  private StringBuffer buf = new StringBuffer();
  private List collectFeats = new ArrayList();
  private Set inducedAbbreviations;

  /**
   * Creates a new <code>SDContextGenerator</code> instance with
   * no induced abbreviations.
   *
   */
  public SDContextGenerator() {
    this(Collections.EMPTY_SET);
  }

  /**
   * Creates a new <code>SDContextGenerator</code> instance which uses
   * the set of induced abbreviations.
   *
   * @param inducedAbbreviations a <code>Set</code> of Strings
   * representing induced abbreviations in the training data.
   * Example: &quot;Mr.&quot;
   */
  public SDContextGenerator(Set inducedAbbreviations) {
    this.inducedAbbreviations = inducedAbbreviations;
  }

  /**
   * Builds up the list of features, anchored around a position within the
   * StringBuffer. 
   */
  public String[] getContext(Object o) {
    String prefix, previous, suffix, next;
    Object first = ((Pair) o).a;
    int position = ((Integer) ((Pair) o).b).intValue();

    if (first instanceof String[]) {
      String[] $_ = (String[]) first;
      previous = $_[0];
      String current = $_[1];
      prefix = current.substring(0, position);
      suffix = current.substring(position + 1);
      if (suffix.startsWith(" "))
        collectFeats.add("sn");
      if (prefix.endsWith(" "))
        collectFeats.add("pn");
      collectFeats.add("eos=" + current.charAt(position));
      next = $_[2];
    }
    else {
      StringBuffer sb = (StringBuffer) ((Pair) o).a;

      int lastIndex = sb.length() - 1;
      if (position > 0 && sb.charAt(position - 1) == ' ')
        collectFeats.add("sp");
      if (position < lastIndex && sb.charAt(position + 1) == ' ')
        collectFeats.add("sn");
      collectFeats.add("eos=" + sb.charAt(position));
      int prefixStart = previousSpaceIndex(sb, position);
      int c = position;
      while (--c > prefixStart)
        if (sb.charAt(c) == '.') {
          prefixStart = c;
          break;
        }

      int prevStart = previousSpaceIndex(sb, prefixStart);

      int suffixEnd = nextSpaceIndex(sb, position, lastIndex);
      c = position;
      while (++c < suffixEnd) {
        if (sb.charAt(c) == '.') {
          suffixEnd = c;
          break;
        }
      }

      int nextEnd = nextSpaceIndex(sb, suffixEnd, lastIndex);

      prefix = sb.substring(prefixStart, position).trim();
      previous = sb.substring(prevStart, prefixStart).trim();

      if (position == lastIndex) {
        suffix = "";
        next = "";
      }
      else {
        suffix = sb.substring(position + 1, suffixEnd).trim();
        next = sb.substring(suffixEnd, nextEnd).trim();
      }
    }

    if (!prefix.equals("")) {
      buf.append("x=");
      buf.append(prefix);
      collectFeats.add(buf.toString());
      buf.setLength(0);
      collectFeats.add(Integer.toString(prefix.length()));
      if (isFirstUpper(prefix)) {
        collectFeats.add("xcap");
      }
      if (inducedAbbreviations.contains(prefix)) {
        collectFeats.add("xabbrev");
      }
    }

    if (!previous.equals("")) {
      buf.append("v=");
      buf.append(previous);
      collectFeats.add(buf.toString());
      buf.setLength(0);
      if (isFirstUpper(previous)) {
        collectFeats.add("vcap");
      }
      if (inducedAbbreviations.contains(previous)) {
        collectFeats.add("vabbrev");
      }
    }

    if (!suffix.equals("")) {
      buf.append("s=");
      buf.append(suffix);
      collectFeats.add(buf.toString());
      buf.setLength(0);
      if (isFirstUpper(suffix)) {
        collectFeats.add("scap");
      }
      if (inducedAbbreviations.contains(suffix)) {
        collectFeats.add("sabbrev");
      }
    }

    if (!next.equals("")) {
      buf.append("n=");
      buf.append(next);
      collectFeats.add(buf.toString());
      buf.setLength(0);
      if (isFirstUpper(next)) {
        collectFeats.add("ncap");
      }
      if (inducedAbbreviations.contains(next)) {
        collectFeats.add("nabbrev");
      }
    }

    String[] context = new String[collectFeats.size()];
    context = (String[]) collectFeats.toArray(context);
    collectFeats.clear();
    return context;
  }

  private static final boolean isFirstUpper(String s) {
    return Character.isUpperCase(s.charAt(0));
  }

  /*
   * Finds the index of the nearest space before a specified index.
   *
   * @param sb   The string buffer which contains the text being examined.
   * @param seek The index to begin searching from.
   * @return The index which contains the nearest space.
   */
  private static final int previousSpaceIndex(StringBuffer sb, int seek) {
    seek--;
    while (seek > 0) {
      if (sb.charAt(seek) == ' ') {
        while (seek > 0 && sb.charAt(seek - 1) == ' ')
          seek--;
        return seek;
      }
      seek--;
    }
    return 0;
  }

  /*
   * Finds the index of the nearest space after a specified index.
   *
   * @param sb The string buffer which contains the text being examined.
   * @param seek The index to begin searching from.
   * @param lastIndex The highest index of the StringBuffer sb.
   * @return The index which contains the nearest space.
   */
  private static final int nextSpaceIndex(StringBuffer sb, int seek, int lastIndex) {
    seek++;
    char c;
    while (seek < lastIndex) {
      c = sb.charAt(seek);
      if (c == ' ' || c == '\n') {
        while (sb.length() > seek + 1 && sb.charAt(seek + 1) == ' ')
          seek++;
        return seek;
      }
      seek++;
    }
    return lastIndex;
  }
}
