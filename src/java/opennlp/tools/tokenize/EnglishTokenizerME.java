///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2004 Jason Baldridge, Gann Bierner, and Tom Morton
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

package opennlp.tools.tokenize;

import java.io.File;
import java.io.IOException;

import opennlp.maxent.io.SuffixSensitiveGISModelReader;

/**
 * A tokenizer which uses default English data for the maxent model.
 *
 * @author      Jason Baldridge and Tom Morton
 * @version     $Revision: 1.2 $, $Date: 2004/04/07 17:28:37 $
 */
public class EnglishTokenizerME extends TokenizerME  {
  public EnglishTokenizerME(String name) throws IOException  {
    super((new SuffixSensitiveGISModelReader(new File(name))).getModel());
    setAlphaNumericOptimization(true);
  }

  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.err.println("Usage:  java opennlp.tools.tokenize.EnglishTokenizerME model < sentences");
      System.exit(1);
    }
    EnglishTokenizerME tokenizer = new EnglishTokenizerME(args[0]);
    java.io.BufferedReader inReader = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
    for (String line = inReader.readLine(); line != null; line = inReader.readLine()) {
      if (line.equals("")) {
        System.out.println();
      }
      else {
        String[] tokens = tokenizer.tokenize(line);
        if (tokens.length > 0) {
          System.out.print(tokens[0]);
        }
        for (int ti=1,tn=tokens.length;ti<tn;ti++) {
          System.out.print(" "+tokens[ti]);
        }
        System.out.println();
      }
    }
  }
}
