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

import java.io.File;
import java.io.IOException;
import java.util.List;

import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.util.Sequence;

/** This is a chunker based on the CONLL chunking task which uses Penn Treebank constituents as the basis for the chunks.
 *   See   http://cnts.uia.ac.be/conll2000/chunking/ for data and task definition.
 * @author Tom Morton
 */
public class EnglishTreebankChunker extends ChunkerME {
  
  public EnglishTreebankChunker(String modelFile) throws IOException {
    this(new SuffixSensitiveGISModelReader(new File(modelFile)).getModel());
  }

  public EnglishTreebankChunker(MaxentModel mod) {
    super(mod);
  }

  public EnglishTreebankChunker(MaxentModel mod, ChunkerContextGenerator cg) {
    super(mod, cg);
  }

  public EnglishTreebankChunker(MaxentModel mod, ChunkerContextGenerator cg, int beamSize) {
    super(mod, cg, beamSize);
  }

  protected boolean validOutcome(String outcome, Sequence sequence) {
    if (outcome.startsWith("I-")) {
      List tagList = sequence.getOutcomes();
      int lti = tagList.size() - 1;
      if (lti == -1) {
        return (false);
      }
      else {
        String lastTag = (String) tagList.get(lti);
        if (lastTag.equals("O")) {
          return (false);
        }
        if (!lastTag.substring(2).equals(outcome.substring(2))) {
          return (false);
        }
      }
    }
    return (true);
  }

  public static void main(String[] args) throws IOException {
      if (args.length == 0) {
        System.err.println("Usage:  java opennlp.tools.chunker.EnglishTreebankChunker model < tokenized_sentences");
        System.exit(1);
      }
      EnglishTreebankChunker chunker = new EnglishTreebankChunker(args[0]);
      java.io.BufferedReader inReader = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
      for (String line = inReader.readLine(); line != null; line = inReader.readLine()) {
        if (line.equals("")) {
          System.out.println();
        }
        else {
          String[] tts = line.split(" ");
          String[] tokens = new String[tts.length];
          String[] tags = new String[tts.length];
          for (int ti=0,tn=tts.length;ti<tn;ti++) {
            String[] tt = tts[ti].split("/");
            tokens[ti]=tt[0];
            tags[ti]=tt[1]; 
          }
          String[] chunks = chunker.chunk(tokens,tags);
          for (int ci=0,cn=chunks.length;ci<cn;ci++) {
            if (ci > 0 && !chunks[ci].startsWith("I-") && !chunks[ci-1].equals("O")) {
              System.out.print(" ]");
            }            
            if (chunks[ci].startsWith("B-")) {
              System.out.print(" ["+chunks[ci].substring(2));
            }
            /*
            else {
              System.out.print(" ");
            }
            */
            System.out.print(" "+tokens[ci]+"/"+tags[ci]);
          }
          if (!chunks[chunks.length-1].equals("O")) {
            System.out.print(" ]");
          }
          System.out.println();
        }
      }
    }
}
