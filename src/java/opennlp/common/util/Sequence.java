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

import gnu.trove.*;
import java.util.ArrayList;
import java.util.List;

/** Represents a weighted sequence of outcomes. */
public class Sequence implements Comparable {
  private double score = 0;
  private List outcomes;
  private TDoubleArrayList probs;

  /** Creates a new sequence of outcomes. */
  public Sequence() {
    outcomes = new ArrayList();
    probs = new TDoubleArrayList();
  }

  public Sequence(List outcomes) {
    this.outcomes = outcomes;
    this.probs = new TDoubleArrayList(outcomes.size());
    for (int oi=0,ol=outcomes.size();oi<ol;oi++) {
      probs.add(1.0);
    }
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

  /** Returns a copy of the this sequence.
   * @return a copy of this sequence.
   */
  public Sequence copy() {
    Sequence s = new Sequence();
    s.outcomes.addAll(outcomes);
    s.probs.add(probs.toNativeArray());
    s.score = score;
    return s;
  }

  /** Adds an outcome and probability to this sequence. 
   * @param outcome the outcome to be added.
   * @param p the probability associated with this outcome.
   */
  public void add(String outcome, double p) {
    outcomes.add(outcome);
    probs.add(p);
    score += Math.log(p);
  }

  /** Returns a list of outcomes for this sequence.
   * @return a list of outcomes.
   */
  public List getOutcomes() {
    return (outcomes);
  }

  /** Returns an array of probabilities associated with the outcomes of this sequence.
   * @return an array of probabilities.
   */
  public double[] getProbs() {
    return (probs.toNativeArray());
  }
  
  /** Populates  an array with the probabilities associated with the outcomes of this sequece.
   * @param p a pre-allocated array to use to hold the values of the probabilities of the outcomes for this sequence.
   */
  public void getProbs(double[] p) {
    probs.toNativeArray(p,0,p.length);
  }

  public String toString() {
    return super.toString() + " " + score;
  }
}