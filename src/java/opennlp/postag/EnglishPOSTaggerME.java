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

package opennlp.postag;

import java.io.*;
import java.util.zip.*;
import opennlp.common.english.*;
import opennlp.maxent.*;
import opennlp.maxent.io.*;

/**
 * A part of speech tagger that uses a model trained on English data from the
 * Wall Street Journal and the Brown corpus.  The latest model created
 * achieved >96% accuracy on unseen data.
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.1 $, $Date: 2003/03/07 04:00:36 $
 */

public class EnglishPOSTaggerME extends POSTaggerME {
    private static final String modelFile = "data/EnglishPOS.bin.gz";

    /**
     * No-arg constructor which loads the English POS tagging model
     * transparently.
     */
    public EnglishPOSTaggerME() {
	super(getModel(modelFile),
	      new POSContextGenerator(new BasicEnglishAffixes()));
	_useClosedClassTagsFilter = true;
	_closedClassTagsFilter = new EnglishClosedClassTags();
    }

    private static MaxentModel getModel(String name) {
	try {
	    return
		new BinaryGISModelReader(
	            new DataInputStream(new GZIPInputStream(new BufferedInputStream(
	    EnglishPOSTaggerME.class.getResourceAsStream(name))))).getModel();

	} catch (IOException E) { E.printStackTrace(); return null; }
    }
    
    /**
     * <p>Part-of-speech tag a string passed in on the command line. For
     * example: 
     *
     * <p>java opennlp.grok.preprocess.postag.EnglishPOSTaggerME -test "Mr. Smith gave a car to his son on Friday."
     */
    public static void main(String[] args) throws IOException {
	System.out.println(new EnglishPOSTaggerME().tag(args[0]));

	if (args[0].equals("-test")) {
	    System.out.println(new EnglishPOSTaggerME().tag(args[0]));
	}
	else {
	  //TrainEval.run(args, new EnglishPOSTaggerME());
	}
    }

}
