///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2002 Jason Baldridge and Gann Bierner
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

import opennlp.maxent.*;
import opennlp.maxent.io.*;
import java.io.*;
import java.util.zip.*;

/**
 * A sentence detector which uses a model trained on English data (Wall Street
 * Journal text).
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.1 $, $Date: 2003/11/05 03:31:04 $
 */

public class EnglishSentenceDetectorME extends SentenceDetectorME {
    private static final String modelFile = "data/EnglishSD.bin.gz";

    /**
     * No-arg constructor which loads the English sentence detection model
     * transparently.
     */
    public EnglishSentenceDetectorME() {
        super(getModel(modelFile));
    }

    private static MaxentModel getModel(String name) {
        try {
            return
                new BinaryGISModelReader(
                                         new DataInputStream(new GZIPInputStream(
                                                                                 EnglishSentenceDetectorME.class.getResourceAsStream(name)))).getModel();
        } catch (IOException E) { E.printStackTrace(); return null; }
    }

    /**
     * <p>Perform sentence detection on a string passed in on the command
     * line. For example:
     *
     * <p>java opennlp.grok.preprocess.sentdetect.EnglishSentenceDetectorME "First sentence. Second sentence? Here is another one. And so on and so forth - you get the idea."
     */
    public static void main(String[] args)  {
        SentenceDetectorME sdetector = new EnglishSentenceDetectorME();
        String [] sentSA = sdetector.sentDetect(args[0]);
        for (int i=0; i<sentSA.length; i++) System.out.println(sentSA[i]);
    }
}
