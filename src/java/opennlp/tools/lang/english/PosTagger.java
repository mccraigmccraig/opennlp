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

package opennlp.tools.lang.english;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.ngram.Dictionary;
import opennlp.tools.postag.DefaultPOSContextGenerator;
import opennlp.tools.postag.POSDictionary;
import opennlp.tools.postag.POSTaggerME;

/**
 * A part of speech tagger that uses a model trained on English data from the
 * Wall Street Journal and the Brown corpus.  The latest model created
 * achieved >96% accuracy on unseen data.
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.2 $, $Date: 2005/10/06 11:07:42 $
 */

public class PosTagger extends POSTaggerME {

  public PosTagger(String modelFile, Dictionary dict, POSDictionary tagdict) {
      super(getModel(modelFile), new DefaultPOSContextGenerator(dict),tagdict);
  }

  public PosTagger(String modelFile, Dictionary dict) {
    super(getModel(modelFile), new DefaultPOSContextGenerator(dict));
  }

  private static MaxentModel getModel(String name) {
    try {
      return new SuffixSensitiveGISModelReader(new File(name)).getModel();
    }
    catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * <p>Part-of-speech tag a string passed in on the command line. For
   * example: 
   *
   * <p>java opennlp.tools.postag.EnglishPOSTaggerME -test "Mr. Smith gave a car to his son on Friday."
   */
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.err.println("Usage: PosTagger [-td tagdict] model dict < tokenized_sentences");
      System.exit(1);
    }
    int ai=0;
    boolean test = false;
    String tagdict = null;
    while(ai < args.length && args[ai].startsWith("-")) {
      if (args[ai].equals("-td")) {
        tagdict = args[ai+1];
        ai+=2;
      }
    }
    POSTaggerME tagger;
    String model = args[ai++];
    String dictFile = args[ai++];
    if (tagdict != null) {
      tagger = new PosTagger(args[ai++],new Dictionary(dictFile),new POSDictionary(tagdict));
    }
    else {
      tagger = new PosTagger(model,new Dictionary(dictFile));
    }
    if (test) {
      System.out.println(tagger.tag(args[ai]));
    }
    else {
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      
      for (String line = in.readLine(); line != null; line = in.readLine()) {
        System.out.println(tagger.tag(line));
      }
    }
  }
}