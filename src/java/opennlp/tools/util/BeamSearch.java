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
package opennlp.tools.util;

import java.util.*;

import opennlp.maxent.MaxentModel;

/** Performs k-best search over sequence.  This is besed on the description in
  * Ratnaparkhi (1998), PhD diss, Univ. of Pennsylvania. 
 */
public class BeamSearch {

  protected MaxentModel model;
  protected BeamSearchContextGenerator cg;
  protected int size;

  /** Creates new search object. 
   * @param size The size of the beam (k).
   * @param cg the context generator for the model. 
   * @param model the model for assigning probabilities to the sequence outcomes.
   */
  public BeamSearch(int size, BeamSearchContextGenerator cg, MaxentModel model) {
    this.size = size;
    this.cg = cg;
    this.model = model;
  }
  
  /** Returns the best sequence of outcomes based on model for this object.
     * @param numSequences The maximum number of sequences to be returned.
     * @param sequence The input sequence.
     * @param additionalContext An Object[] of additional context.  This is passed to the context generator blindly with the assumption that the context are appropiate.
     * @return An array of the top ranked sequences of outcomes.
     */
  public Sequence[] bestSequences(int numSequences,List sequence, Object[] additionalContext) {
    int n = sequence.size();
    SortedSet prev = new TreeSet();
    SortedSet next = new TreeSet();
    SortedSet tmp;
    prev.add(new Sequence());
    if (additionalContext == null) {
      additionalContext = new Object[0];
    }
    for (int i = 0; i < n; i++) {
      int sz = Math.min(size, prev.size());
      int sc =0;
      for (Iterator si = prev.iterator();si.hasNext() && sc<sz;sc++) {
        Sequence top = (Sequence) si.next();
        double[] scores = model.eval(cg.getContext(i,sequence,top,additionalContext));
        double[] temp_scores = new double[scores.length];

        for (int c = 0; c < scores.length; c++) {
          if (!validSequence(i, sequence, top, model.getOutcome(c))) {
            scores[c] = 0;
          }
          temp_scores[c] = scores[c];
        }
        Arrays.sort(temp_scores);
        double min = temp_scores[Math.min(scores.length-1,size)];

        for (int p = 0; p < scores.length; p++) {
          if (scores[p] < min)
            continue; //only advance first "size" outcomes
          next.add(new Sequence(top,model.getOutcome(p),scores[p]));
        }
      }
      //    make prev = next; and re-init next (we reuse existing prev set once we clear it)
      prev.clear();
      tmp = prev;
      prev = next;
      next = tmp;
    }
    int numSeq = Math.min(numSequences,prev.size());
    List topSequences = new ArrayList(numSeq);
    for (Iterator si=prev.iterator();numSeq> 0;numSeq--) {
      topSequences.add(si.next());
    }
    return (Sequence[]) topSequences.toArray(new Sequence[topSequences.size()]);
  }

  /** Returns the best sequence of outcomes based on model for this object.
   * @param sequence The input sequence.
   * @param additionalContext An Object[] of additional context.  This is passed to the context generator blindly with the assumption that the context are appropiate.
   * @return The top ranked sequence of outcomes.
   */
  public Sequence bestSequence(List sequence, Object[] additionalContext) {
    return bestSequences(1,sequence,additionalContext)[0];
  }

  /** Determines wheter a particular continuation of a sequence is valid.  
   * This is used to restrict invalid sequences such as thoses used in start/continure tag-based chunking 
   * or could be used to implement tag dictionary restrictions.
   * @param i The index in the input sequence for which the new outcome is being proposed.
   * @param inputSequence The input sequnce.
   * @param outcomesSequence The outcomes so far in this sequence.
   * @param outcome The next proposed outcome for the outcomes sequence.
   * @return true is the sequence would still be valid with the new outcome, false otherwise.
   */
  protected boolean validSequence(int i, List inputSequence, Sequence outcomesSequence, String outcome) {
    return true;
  }

}