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
package opennlp.tools.chunker;

import java.util.Arrays;
import java.util.List;

import opennlp.common.util.BeamSearch;
import opennlp.common.util.Sequence;
import opennlp.maxent.ContextGenerator;
import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;

/**
 * @author Tom Morton
 *
 */
public class ChunkerME implements Chunker {

  protected MaxentModel _npModel;
  protected ContextGenerator _contextGen;
  private Sequence bestSequence;
  private int beamSize;
  private BeamSearch beam;

  public ChunkerME(MaxentModel mod) {
    this(mod, new DefaultChunkerContextGenerator(),10);
  }

  public ChunkerME(MaxentModel mod, ContextGenerator cg) {
    this(mod,cg,10);
  }
  
  public ChunkerME(MaxentModel mod,ContextGenerator cg, int beamSize) {
    _npModel = mod;
    _contextGen = cg;
    this.beamSize=beamSize;
    beam = new ChunkBeamSearch(beamSize,cg,mod);
   }

  public List chunk(List toks, List tags) {
    return beam.bestSequence(toks, new Object[] {tags});
  }

  public String[] chunk(Object[] toks, String[] tags) {
    List c = beam.bestSequence(Arrays.asList(toks), new Object[] {Arrays.asList(tags)});
    return (String[]) c.toArray(new String[c.size()]);
  }
  
  /** 
   * This method determines wheter the outcome is valid for the preceeding sequence.  
   * This can be used to implement constraints on what sequences are valid.  
   * @param outcome The outcome.
   * @param sequence The precceding sequence of outcomes assignments. 
   * @return true is the outcome is valid for the sequence, false otherwise.
   */
  protected boolean validOutcome(String outcome, Sequence sequence) {
    return(true);
  }
  
  class ChunkBeamSearch extends BeamSearch {
    public ChunkBeamSearch(int size, ContextGenerator cg, MaxentModel model) {
      super(size, cg, model);
    }

    protected boolean validSequence(int i, List sequence, Sequence s, String outcome) {
      return validOutcome(outcome,s);
    }
  }
  
  public void probs(double[] probs) {
      List dlist = bestSequence.getProbs();
      for (int pi = 0; pi < probs.length; pi++) {
        probs[pi] = ((Double) dlist.get(pi)).doubleValue();
      }
    }

    public double[] probs() {
      List dlist = bestSequence.getProbs();
      double[] probs = new double[dlist.size()];
      for (int pi = 0; pi < probs.length; pi++) {
        probs[pi] = ((Double) dlist.get(pi)).doubleValue();
      }
      return (probs);
    }
  
  public static GISModel train(opennlp.maxent.EventStream es, int iterations, int cut) throws java.io.IOException {
    return opennlp.maxent.GIS.trainModel(es, iterations, cut);
  }

  /**
     * <p>Trains a new chunker model.</p>
     *
     * <p>Usage: java opennlp.chunker.ChunkerME data_file new_model_name (iterations cutoff)?</p>
     *
     */
  public static void main(String[] args) {
    try {
      java.io.File inFile = new java.io.File(args[0]);
      java.io.File outFile = new java.io.File(args[1]);
      GISModel mod;
      opennlp.maxent.EventStream es = new ChunkerEventStream(new opennlp.maxent.PlainTextByLineDataStream(new java.io.FileReader(inFile)));
      if (args.length > 3)
        mod = train(es, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
      else
        mod = train(es, 100, 5);

      System.out.println("Saving the model as: " + args[1]);
      new opennlp.maxent.io.SuffixSensitiveGISModelWriter(mod, outFile).persist();

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}

