///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2000 Jason Baldridge and Gann Bierner
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

package opennlp.tokenize;

import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.BinaryGISModelReader;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * A tokenizer which uses default English data for the maxent model.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.1 $, $Date: 2003/03/07 03:46:25 $
 */
public class EnglishTokenizerME extends TokenizerME {
    private static final String modelFile = "data/EnglishTok.bin.gz";

    public EnglishTokenizerME() {
	super(getModel(modelFile));
	ALPHA_NUMERIC_OPTIMIZATION=true;
    }

    private static MaxentModel getModel(String name) {
	try {
	    return
		new BinaryGISModelReader(
		    new DataInputStream(new GZIPInputStream(
            EnglishTokenizerME.class.getResourceAsStream(name)))).getModel();
	    
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	}
	return null;
    }
	

    public static void main(String[] args) {
	String [] tokenSA = new EnglishTokenizerME().tokenize(args[0]);
	for (int i=0; i<tokenSA.length; i++) System.out.println(tokenSA[i]);
    }
    
}
