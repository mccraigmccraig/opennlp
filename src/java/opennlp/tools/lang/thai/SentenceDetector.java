///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2006 Thomas Morton
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

package opennlp.tools.lang.thai;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.sentdetect.SentenceDetectorME;

/**
 * Sentence detector for Thai text. 
 */
public class SentenceDetector extends SentenceDetectorME {

  public SentenceDetector(String name) throws IOException {
    super((new SuffixSensitiveGISModelReader(new File(name))).getModel(),new SentenceContextGenerator(),new EndOfSentenceScanner());
    this.useTokenEnd = false;
  }

  /**
   * Perform sentence detection the input stream.  A newline will be treated as a paragraph boundry.
   * <p>java opennlp.tools.lang.thai.SentenceDetector model < "First sentence. Second sentence? Here is another one. And so on and so forth - you get the idea."
   */
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.err.println("Usage java opennlp.tools.lang.thai.SentenceDetector model < text");
      System.exit(1);
    }
    SentenceDetectorME sdetector = new SentenceDetector(args[0]);
    StringBuffer para = new StringBuffer();
    BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in,"UTF-8"));
    PrintStream out = new PrintStream(System.out,true,"UTF-8");
    for (String line = inReader.readLine(); line != null; line = inReader.readLine()) {
      if (line.equals("")) {
        if (para.length() != 0) {
          //System.err.println("para: "+para.toString());
          String[] sents = sdetector.sentDetect(para.toString());
          for (int si = 0, sn = sents.length; si < sn; si++) {
            //out.println(sents[si]+" ("+si+")");
            out.println(sents[si]);
          }
        }
        out.println();
        para.setLength(0);
      }
      else {
        para.append(line).append(" ");
      }
    }
    if (para.length() != 0) {
      String[] sents = sdetector.sentDetect(para.toString());
      for (int si = 0, sn = sents.length; si < sn; si++) {
        out.println(sents[si]);
      }
    }
  }
}
