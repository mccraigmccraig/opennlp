///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2004 Jason Baldridge, Gann Bierner and Tom Morton
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

package opennlp.tools.sentdetect;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import opennlp.maxent.io.SuffixSensitiveGISModelReader;

/**
 * A sentence detector which uses a model trained on English data (Wall Street
 * Journal text).
 *
 * @author      Jason Baldridge and Tom Morton
 * @version     $Revision: 1.3 $, $Date: 2004/06/11 20:56:18 $
 */

public class EnglishSentenceDetectorME extends SentenceDetectorME {
  /**
   * No-arg constructor which loads the English sentence detection model
   * transparently.
   */
  public EnglishSentenceDetectorME(String name) throws IOException {
    super((new SuffixSensitiveGISModelReader(new File(name))).getModel());
  }

  /**
   * Perform sentence detection the input stream.  A blank line will be treated as a paragraph boundry.
   * <p>java opennlp.tools.sentdetect.EnglishSentenceDetectorME model < "First sentence. Second sentence? Here is another one. And so on and so forth - you get the idea."
   */
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.err.print("Usage java opennlp.tools.sentdetect.EnglishSentenceDetectorME model < text");
      System.exit(1);
    }
    SentenceDetectorME sdetector = new EnglishSentenceDetectorME(args[0]);
    StringBuffer para = new StringBuffer();
    BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
    for (String line = inReader.readLine(); line != null; line = inReader.readLine()) {
      if (line.equals("")) {
        if (para.length() != 0) {
          //System.err.println(para.toString());
          String[] sents = sdetector.sentDetect(para.toString());
          for (int si = 0, sn = sents.length; si < sn; si++) {
            System.out.println(sents[si]);
          }
        }
        System.out.println();
        para.setLength(0);
      }
      else {
        para.append(line).append(" ");
      }
    }
    if (para.length() != 0) {
      String[] sents = sdetector.sentDetect(para.toString());
      for (int si = 0, sn = sents.length; si < sn; si++) {
        System.out.println(sents[si]);
      }
    }
  }
}
