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

import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.TwoPassDataIndexer;
import opennlp.tools.util.BeamSearch;
import opennlp.tools.util.Sequence;

/**
 * @author Tom Morton
 *
 */
public class ChunkerME implements Chunker {

  protected MaxentModel _npModel;
  protected ChunkerContextGenerator _contextGen;
  private int beamSize;
  protected BeamSearch beam;
  private Sequence bestSequence;

  public ChunkerME(MaxentModel mod) {
    this(mod, new DefaultChunkerContextGenerator(), 10);
  }

  public ChunkerME(MaxentModel mod, ChunkerContextGenerator cg) {
    this(mod, cg, 10);
  }

  public ChunkerME(MaxentModel mod, ChunkerContextGenerator cg, int beamSize) {
    _npModel = mod;
    _contextGen = cg;
    this.beamSize = beamSize;
    beam = new ChunkBeamSearch(beamSize, cg, mod);
  }

  public List chunk(List toks, List tags) {
    bestSequence = beam.bestSequence(toks, new Object[] { tags });
    return bestSequence.getOutcomes();
  }

  public String[] chunk(Object[] toks, String[] tags) {
    bestSequence = beam.bestSequence(Arrays.asList(toks), new Object[] { Arrays.asList(tags)});
    List c = bestSequence.getOutcomes();
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
    return (true);
  }

  class ChunkBeamSearch extends BeamSearch {
    public ChunkBeamSearch(int size, ChunkerContextGenerator cg, MaxentModel model) {
      super(size, cg, model);
    }

    protected boolean validSequence(int i, List sequence, Sequence s, String outcome) {
      return validOutcome(outcome, s);
    }
  }

  public void probs(double[] probs) {
    bestSequence.getProbs(probs);
  }

  public double[] probs() {
    return bestSequence.getProbs();
  }

  public static GISModel train(opennlp.maxent.EventStream es, int iterations, int cut) throws java.io.IOException {
    return opennlp.maxent.GIS.trainModel(iterations, new TwoPassDataIndexer(es, cut));
  }

  /**
     * <p>Trains a new chunker model.</p>
     *
     * <p>Usage: java opennlp.chunker.ChunkerME data_file new_model_name (iterations cutoff)?</p>
     *
     */
  public static void main(String[] args) throws java.io.IOException {
    if (args.length == 0) {
      System.err.println("Usage: ChunkerME trainingFile modelFile");
      System.err.println();
      System.err.println("Training file should be one word per line where each line consists of a ");
      System.err.println("space-delimited triple of \"word pos outome\".  Sentence breaks are indicated by blank lines.");
      System.exit(1);
    }
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
}
