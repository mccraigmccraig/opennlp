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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import opennlp.maxent.EventStream;
import opennlp.maxent.GIS;
import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.PlainTextByLineDataStream;
import opennlp.maxent.TwoPassDataIndexer;
import opennlp.maxent.io.SuffixSensitiveGISModelWriter;
import opennlp.tools.util.BeamSearch;
import opennlp.tools.util.Sequence;

public class NameFinderME implements NameFinder {

  protected MaxentModel _npModel;
  protected NameContextGenerator _contextGen;
  private Sequence bestSequence;
  private int beamSize;
  private BeamSearch beam;

  public static final String START = "start";
  public static final String CONTINUE = "cont";
  public static final String OTHER = "other";

  public NameFinderME(MaxentModel mod) {
    this(mod, new DefaultNameContextGenerator(), 10);
  }

  public NameFinderME(MaxentModel mod, NameContextGenerator cg) {
    this(mod, cg, 10);
  }

  public NameFinderME(MaxentModel mod, NameContextGenerator cg, int beamSize) {
    _npModel = mod;
    _contextGen = cg;
    this.beamSize = beamSize;
    beam = new NameBeamSearch(beamSize, cg, mod);
  }

  public List find(List toks, Map prevTags) {
    bestSequence = beam.bestSequence(toks, new Object[] { prevTags });
    return bestSequence.getOutcomes();
  }

  public String[] find(Object[] toks, Map prevTags) {
    bestSequence = beam.bestSequence(Arrays.asList(toks), new Object[] { prevTags });
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
    if (outcome.equals(CONTINUE)) {
      List tags = sequence.getOutcomes();
      int li = tags.size() - 1;
      if (li == -1) {
        return false;
      }
      else if (((String) tags.get(li)).equals(OTHER)) {
        return false;
      }
    }
    return true;
  }

  private class NameBeamSearch extends BeamSearch {

    public NameBeamSearch(int size, NameContextGenerator cg, MaxentModel model) {
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

  public static GISModel train(EventStream es, int iterations, int cut) throws IOException {
    return GIS.trainModel(iterations, new TwoPassDataIndexer(es, cut));
  }

  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.err.println("usage: NameFinderME training_file model");
      System.exit(1);
    }
    try {
      File inFile = new File(args[0]);
      File outFile = new File(args[1]);
      GISModel mod;

      EventStream es = new NameFinderEventStream(new PlainTextByLineDataStream(new FileReader(inFile)));
      if (args.length > 3)
        mod = train(es, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
      else
        mod = train(es, 100, 5);

      System.out.println("Saving the model as: " + args[1]);
      new SuffixSensitiveGISModelWriter(mod, outFile).persist();

    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

}
