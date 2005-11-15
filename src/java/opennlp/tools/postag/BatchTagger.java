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

package opennlp.tools.postag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import opennlp.maxent.DataStream;
import opennlp.maxent.Evalable;
import opennlp.maxent.EventCollector;
import opennlp.maxent.EventStream;
import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.PlainTextByLineDataStream;
import opennlp.maxent.TwoPassDataIndexer;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.ngram.Dictionary;
import opennlp.tools.ngram.MutableDictionary;
import opennlp.tools.util.BeamSearch;
import opennlp.tools.util.Pair;
import opennlp.tools.util.Sequence;

/**
 * Invoke a part-of-speech tagging model from the command line.
 *
 * @author   Jason Baldridge
 * @version $Revision: 1.1 $, $Date: 2005/11/15 00:15:29 $
 */
public class BatchTagger {

  private static void usage() {
    System.err.println("Usage: BatchTagger [-dict dict_file] data_file model");
    System.err.println("This applies a model to the specified text file.");
    System.exit(1);
  }

  /**
     * <p>Applies a pos model.</p>
     *
     */
  public static void main (String[] args) throws IOException {
    if (args.length == 0) {
      usage();
    }
    int ai=0;
    try {
      String encoding = null;

      Dictionary dict = null;
      while (args[ai].startsWith("-")) {
	if (args[ai].equals("-dict")) {
          ai++;
          if (ai < args.length) {
            dict = new Dictionary(args[ai++]);
          }
          else {
            usage();
          }
        }
        else {
          System.err.println("Unknown option "+args[ai]);
          usage();
        }
      }

      File textFile = new File(args[ai++]);
      File modelFile = new File(args[ai++]);

      GISModel mod = new SuffixSensitiveGISModelReader(modelFile).getModel();

      POSTagger tagger = new POSTaggerME(mod, dict);
      DataStream text = 
	  new opennlp.maxent.PlainTextByLineDataStream(
	       new java.io.FileReader(textFile));
      while(text.hasNext()) {
          String str = (String)text.nextToken();
	  System.out.println(tagger.tag(str));
      }

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
