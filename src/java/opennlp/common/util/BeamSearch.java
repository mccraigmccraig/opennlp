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

import java.util.*;

import opennlp.maxent.ContextGenerator;
import opennlp.maxent.MaxentModel;

public class BeamSearch {

  protected MaxentModel model;
  protected ContextGenerator cg;
  protected int size;

  ///////////////////////////////////////////////////////////////////
  // Do a beam search to compute best sequence of results (as in pos)
  // taken from Ratnaparkhi (1998), PhD diss, Univ. of Pennsylvania
  ///////////////////////////////////////////////////////////////////
  public BeamSearch(int size, ContextGenerator cg, MaxentModel model) {
    this.size = size;
    this.cg = cg;
    this.model = model;
  }

  public List bestSequence(List sequence, Object context) {
    int n = sequence.size();
    SortedSet prev = new TreeSet();
    SortedSet next = new TreeSet();
    SortedSet tmp;
    prev.add(new Sequence());

    for (int i = 0; i < n; i++) {
      int sz = Math.min(size, prev.size());
      for (int j = 1; j <= sz; j++) {
        Sequence top = (Sequence) prev.first();
        prev.remove(top);
        Object[] params = { new Integer(i), sequence, top };
        double[] scores = model.eval(cg.getContext(params));
        double[] temp_scores = new double[scores.length];

        for (int c = 0; c < scores.length; c++) {
          if (!validSequence(i, sequence, top, model.getOutcome(c))) {
            scores[c] = 0;
          }
          temp_scores[c] = scores[c];
        }
        Arrays.sort(temp_scores);
        double min = temp_scores[temp_scores.length - size];

        for (int p = 0; p < scores.length; p++) {
          if (scores[p] < min)
            continue;
          Sequence newS = top.copy();
          newS.add(model.getOutcome(p), scores[p]);
          next.add(newS);
        }
      }
      //    make prev = next; and re-init next (we reuse existing prev set once we clear it)
      prev.clear();
      tmp = prev;
      prev = next;
      next = tmp;
    }

    List result = ((Sequence) prev.first()).getOutcomes();
    return result;
  }

  protected boolean validSequence(int i, List sequence, Sequence s, String outcome) {
    return true;
  }

  
}