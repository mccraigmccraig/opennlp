///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2003 Gann Bierner and Thomas Morton
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
package opennlp.common.util;

import java.util.ArrayList;
import java.util.List;

public class Sequence implements Comparable {
    double score = 1;
    List outcomes;
    List probs;

    public Sequence() {
      outcomes = new ArrayList();
      probs = new ArrayList();
    };

    Sequence(double s) {
      this();
      score = s;
    }
    public int compareTo(Object o) {
      Sequence s = (Sequence) o;
      if (score < s.score)
        return 1;
      else if (score == s.score)
        return 0;
      else
        return -1;
    }

    public Sequence copy() {
      Sequence s = new Sequence(score);
      s.outcomes.addAll(outcomes);
      s.probs.addAll(probs);
      return s;
    }

    public void add(String t, double d) {
      outcomes.add(t);
      probs.add(new Double(d));
      score *= d;
    }

    public List getOutcomes() {
      return (outcomes);
    }

    public List getProbs() {
      return (probs);
    }

    public String toString() {
      return super.toString() + " " + score;
    }
  }