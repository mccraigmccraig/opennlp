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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.postag.DefaultPOSContextGenerator;
import opennlp.tools.postag.POSDictionary;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.TagDictionary;
import opennlp.tools.util.InvalidFormatException;

/**
 * A part of speech tagger that uses a model trained on English data from the
 * Wall Street Journal and the Brown corpus.  The latest model created
 * achieved >96% accuracy on unseen data.
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.9 $, $Date: 2007/03/30 09:46:33 $
 */

public class PosTagger extends POSTaggerME {

  public PosTagger(String modelFile, Dictionary dict, TagDictionary tagdict) {
      super(getModel(modelFile), new DefaultPOSContextGenerator(dict),tagdict);
  }
  
  public PosTagger(String modelFile, TagDictionary tagdict) {
    super(getModel(modelFile), new DefaultPOSContextGenerator(null),tagdict);
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
  
  public static void usage() {
    System.err.println("Usage: PosTagger [-d tagdict] [-di case_insensiteve_tagdict] [-k 5] model < tokenized_sentences");
    System.err.println("-d tagdict Specifies that a tag dictionary file should be used.");
    System.err.println("-di tagdict Specifies that a case-insensitive tag dictionary should be used.");
    System.err.println("-k n tagdict Specifies that the top n tagging should be reported.");
    System.exit(1);    
  }

  /**
   * <p>Part-of-speech tag a string passed in on the command line. For
   * example: 
   *
   * <p>java opennlp.tools.postag.EnglishPOSTaggerME -test "Mr. Smith gave a car to his son on Friday."
   * @throws InvalidFormatException 
   */
  public static void main(String[] args) throws InvalidFormatException, IOException {
    if (args.length == 0) {
      usage();
    }
    int ai=0;
    boolean test = false;
    String tagdict = null;
    boolean caseSensitive = true;
    int numTaggings = 1;
    while(ai < args.length && args[ai].startsWith("-")) {
      if (args[ai].equals("-d")) {
        tagdict = args[ai+1];
        ai+=2;
      }
      else if (args[ai].equals("-di")) {
        tagdict = args[ai+1];
        ai+=2;
        caseSensitive = false;
      }
      else if (args[ai].equals("-k")) {
        numTaggings = Integer.parseInt(args[ai+1]);
        ai+=2;
      }
    }
    POSTaggerME tagger;
    String model = args[ai++];
    String dictFile = null;
    if (ai < args.length) {
      dictFile = args[ai++];
    }
    
    if (tagdict != null) {
      if (dictFile != null) {
        tagger = new PosTagger(model,new Dictionary(
            new FileInputStream(dictFile)), 
            new POSDictionary(tagdict,caseSensitive));
      }
      else {
        tagger = new PosTagger(model,new POSDictionary(tagdict,caseSensitive));
      }
    }
    else {
      if (dictFile != null) {
        tagger = new PosTagger(model,
            new Dictionary(new FileInputStream(dictFile)));
      }
      else {
        tagger = new PosTagger(model,(Dictionary)null);
      }
    }
    if (test) {
      System.out.println(tagger.tag(args[ai]));
    }
    else {
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      
      for (String line = in.readLine(); line != null; line = in.readLine()) {
        if (numTaggings == 1) {
          System.out.println(tagger.tag(line));
        }
        else {
          String[] tokens = line.split(" ");
          String[][] taggings = tagger.tag(numTaggings, tokens);
          for (int ti=0;ti<taggings.length;ti++) {
            for (int wi=0;wi<tokens.length;wi++) {
              if (wi != 0) {
                System.out.print(" ");
              }
              System.out.print(tokens[wi]+"/"+taggings[ti][wi]);
            }
            System.out.println();
          }
        }
      }
    }
  }
}