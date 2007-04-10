///////////////////////////////////////////////////////////////////////////////
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

package opennlp.tools.lang.german;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.sentdetect.SentenceDetectorME;

public class SentenceDetector extends SentenceDetectorME {
  
  public SentenceDetector(String modelName) throws IOException {
    super((new SuffixSensitiveGISModelReader(new File(modelName))).getModel());
    this.useTokenEnd = true;
  }

  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.err.print("Usage java opennlp.tools.lang.german.SentenceDetector model < text");
      System.exit(1);
    }
    SentenceDetectorME sdetector = new SentenceDetector(args[0]);
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