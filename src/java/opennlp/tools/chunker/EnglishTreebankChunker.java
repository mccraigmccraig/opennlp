package opennlp.tools.chunker;

import opennlp.maxent.ContextGenerator;
import opennlp.maxent.MaxentModel;


/** This is a chunker based on the CONLL chunking task which uses Penn Treebank constituents as the basis for the chunks.
 *   See   http://cnts.uia.ac.be/conll2000/chunking/ for data and task definition.
 * @author Tom Morton
 */
public class EnglishTreebankChunker extends ChunkerME {
  
  public EnglishTreebankChunker(MaxentModel mod) {
    super(mod);
  }

  public EnglishTreebankChunker(MaxentModel mod, ContextGenerator cg) {
    super(mod, cg);
  }

  public EnglishTreebankChunker(MaxentModel mod, ContextGenerator cg, int beamSize) {
    super(mod, cg, beamSize);
  }

  protected boolean validOutcome(String outcome, Sequence sequence) {
    if (outcome.startsWith("I-")) {
        int lti = sequence.tagList.size()-1;
        if (lti == -1) {
          return(false);
        }
        else {
          String lastTag = (String) sequence.tagList.get(lti);
          if (lastTag.equals("O")) {
            return(false); 
          }
          if (!lastTag.substring(2).equals(outcome.substring(2))) {
            return(false);
          }
        }
    }
    return(true);
  }

}
