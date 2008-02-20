///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2003 Thomas Morton
// 
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.
// 
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
// 
//You should have received a copy of the GNU Lesser General Public
//License along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////
package opennlp.tools.lang.english;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.BinaryGISModelReader;
import opennlp.tools.namefind.NameFinderEventStream;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.parser.Parse;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

/**
 * Class is used to create a name finder for English.
 */
public class NameFinder {
  
  public static String[] NAME_TYPES = {"person", "organization", "location", "date", "time", "percentage", "money"};

  private NameFinderME nameFinder;
  
  /** Creates an English name finder using the specified model.
   * @param mod The model used for finding names.
   */
  public NameFinder(MaxentModel mod) {
    nameFinder = new NameFinderME(mod);
  }

  private static void addNames(String tag, Span[] names, Parse[] tokens) {
    for (int ni=0,nn=names.length;ni<nn;ni++) {
      Span nameTokenSpan = names[ni];
      Parse startToken = tokens[nameTokenSpan.getStart()];
      Parse endToken = tokens[nameTokenSpan.getEnd()];
      Parse commonParent = startToken.getCommonParent(endToken);
      //System.err.println("addNames: "+startToken+" .. "+endToken+" commonParent = "+commonParent);
      if (commonParent != null) {
        Span nameSpan = new Span(startToken.getSpan().getStart(),endToken.getSpan().getEnd());
        if (nameSpan.equals(commonParent.getSpan())) {
          commonParent.insert(new Parse(commonParent.getText(),nameSpan,tag,1.0,endToken.getHeadIndex()));
        }
        else {
          Parse[] kids = commonParent.getChildren();
          boolean crossingKids = false;
          for (int ki=0,kn=kids.length;ki<kn;ki++) {
            if (nameSpan.crosses(kids[ki].getSpan())){
              crossingKids = true;
            }
          }
          if (!crossingKids) {
            commonParent.insert(new Parse(commonParent.getText(),nameSpan,tag,1.0,endToken.getHeadIndex()));
          }
          else {
            if (commonParent.getType().equals("NP")) {
              Parse[] grandKids = kids[0].getChildren();
              if (grandKids.length > 1 && nameSpan.contains(grandKids[grandKids.length-1].getSpan())) {
                commonParent.insert(new Parse(commonParent.getText(),commonParent.getSpan(),tag,1.0,commonParent.getHeadIndex()));
              }
            }
          }
        }
      }
    }
  }
  
  private static void clearPrevTokenMaps(NameFinder[] finders) {
    for (int mi = 0; mi < finders.length; mi++) {
      finders[mi].nameFinder.clearAdaptiveData();
    }
  }

  private static void processParse(NameFinder[] finders, String[] tags, BufferedReader input) throws IOException {
    Span[][] nameSpans = new Span[finders.length][];
    
    for (String line = input.readLine(); null != line; line = input.readLine()) {
      if (line.equals("")) {
        System.out.println();
        clearPrevTokenMaps(finders);
        continue;
      }
      Parse p = Parse.parseParse(line);
      Parse[] tagNodes = p.getTagNodes();
      String[] tokens = new String[tagNodes.length];
      for (int ti=0;ti<tagNodes.length;ti++){
        tokens[ti] = tagNodes.toString();
      }
      //System.err.println(java.util.Arrays.asList(tokens));
      for (int fi = 0, fl = finders.length; fi < fl; fi++) {
        nameSpans[fi] = finders[fi].nameFinder.find(tokens);
        //System.err.println("EnglishNameFinder.processParse: "+tags[fi] + " " + java.util.Arrays.asList(finderTags[fi]));
      }
      
      for (int fi = 0, fl = finders.length; fi < fl; fi++) {
        addNames(tags[fi],nameSpans[fi],tagNodes);
      }
      p.show();
    }
  }
      
  /**
   * Adds sgml style name tags to the specified input buffer and outputs this information to stdout. 
   * @param finders The name finders to be used.
   * @param tags The tag names for the corresponding name finder.
   * @param input The input reader.
   * @throws IOException
   */
  private static void processText(NameFinder[] finders, String[] tags, BufferedReader input) throws IOException {
    Span[][] nameSpans = new Span[finders.length][];
    String[][] nameOutcomes = new String[finders.length][];
    opennlp.tools.tokenize.Tokenizer tokenizer = new SimpleTokenizer();
    for (String line = input.readLine(); null != line; line = input.readLine()) {
      if (line.equals("")) {
        clearPrevTokenMaps(finders);
        System.out.println();
        continue;
      }
      Span[] spans = tokenizer.tokenizePos(line);
      String[] tokens = Span.spansToStrings(spans,line);
      for (int fi = 0, fl = finders.length; fi < fl; fi++) {
        nameSpans[fi] = finders[fi].nameFinder.find(tokens);
        //System.err.println("EnglighNameFinder.processText: "+tags[fi] + " " + java.util.Arrays.asList(finderTags[fi]));
        nameOutcomes[fi] = NameFinderEventStream.generateOutcomes(nameSpans[fi], tokens.length);
      }
      
      for (int ti = 0, tl = tokens.length; ti < tl; ti++) {
        for (int fi = 0, fl = finders.length; fi < fl; fi++) {
          //check for end tags
          if (ti != 0) {
            if ((nameOutcomes[fi][ti].equals(NameFinderME.START) || nameOutcomes[fi][ti].equals(NameFinderME.OTHER)) && 
                (nameOutcomes[fi][ti - 1].equals(NameFinderME.START) || nameOutcomes[fi][ti - 1].equals(NameFinderME.CONTINUE))) {
              System.out.print("</" + tags[fi] + ">");
            }
          }
        }
        if (ti > 0 && spans[ti - 1].getEnd() < spans[ti].getStart()) {
          System.out.print(line.substring(spans[ti - 1].getEnd(), spans[ti].getStart()));
        }
        //check for start tags
        for (int fi = 0, fl = finders.length; fi < fl; fi++) {
          if (nameOutcomes[fi][ti].equals(NameFinderME.START)) {
            System.out.print("<" + tags[fi] + ">");
          }
        }
        System.out.print(tokens[ti]);
      }
      //final end tags
      if (tokens.length != 0) {
        for (int fi = 0, fl = finders.length; fi < fl; fi++) {
          if (nameOutcomes[fi][tokens.length - 1].equals(NameFinderME.START) || nameOutcomes[fi][tokens.length - 1].equals(NameFinderME.CONTINUE)) {
            System.out.print("</" + tags[fi] + ">");
          }
        }
      }
      if (tokens.length != 0) {
        if (spans[tokens.length - 1].getEnd() < line.length()) {
          System.out.print(line.substring(spans[tokens.length - 1].getEnd()));
        }
      }
      System.out.println();
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.err.println("Usage NameFinder -[parse] model1 model2 ... modelN < sentences");
      System.err.println(" -parse: Use this option to find names on parsed input.  Un-tokenized sentence text is the default.");
      System.exit(1);
    }
    int ai = 0;
    boolean parsedInput = false;
    while (args[ai].startsWith("-") && ai < args.length) {
      if (args[ai].equals("-parse")) {
        parsedInput = true;
      }
      else {
        System.err.println("Ignoring unknown option "+args[ai]);
      }
      ai++;
    }
    NameFinder[] finders = new NameFinder[args.length-ai];
    String[] names = new String[args.length-ai];
    for (int fi=0; ai < args.length; ai++,fi++) {
      String modelName = args[ai];
      finders[fi] = new NameFinder(new BinaryGISModelReader(new File(modelName)).getModel());
      int nameStart = modelName.lastIndexOf(System.getProperty("file.separator")) + 1;
      int nameEnd = modelName.indexOf('.', nameStart);
      if (nameEnd == -1) {
        nameEnd = modelName.length();
      }
      names[fi] = modelName.substring(nameStart, nameEnd);
    }
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    if (parsedInput) {
      processParse(finders,names,in);
    }
    else {
      processText(finders,names,in);
    }
  }
}