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
//GNU General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////   
package opennlp.tools.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import opennlp.tools.util.Sequence;
import opennlp.tools.util.Span;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.postag.DefaultPOSContextGenerator;
import opennlp.tools.postag.POSDictionary;
import opennlp.tools.postag.POSTaggerME;

/**
 * Class for performing full parsing on English text. 
 */
public class EnglishTreebankParser {

  private static Pattern untokenizedParenPattern1 = Pattern.compile("([^ ])([({)}])");
  private static Pattern untokenizedParenPattern2 = Pattern.compile("([({)}])([^ ])");
  public static ParserME getParser(String dataDir, boolean useTagDictionary, boolean useCaseSensitiveTagDictionary, int beamSize, double advancePercentage) throws IOException {
    if (useTagDictionary) {
      return new ParserME(
        new SuffixSensitiveGISModelReader(new File(dataDir + "/build.bin.gz")).getModel(),
        new SuffixSensitiveGISModelReader(new File(dataDir + "/check.bin.gz")).getModel(),
        new EnglishTreebankPOSTagger(dataDir + "/tag.bin.gz", dataDir + "/tagdict", useCaseSensitiveTagDictionary),
        new EnglishTreebankChunker(dataDir + "/chunk.bin.gz"),
        new EnglishHeadRules(dataDir + "/head_rules"),beamSize,advancePercentage);
    }
    else {
      return new ParserME(
        new SuffixSensitiveGISModelReader(new File(dataDir + "/build.bin.gz")).getModel(),
        new SuffixSensitiveGISModelReader(new File(dataDir + "/check.bin.gz")).getModel(),
        new EnglishTreebankPOSTagger(dataDir + "/tag.bin.gz"),
        new EnglishTreebankChunker(dataDir + "/chunk.bin.gz"),
        new EnglishHeadRules(dataDir + "/head_rules"),beamSize,advancePercentage);
    }
  }
  
  public static ParserME getParser(String dataDir) throws IOException {
    return getParser(dataDir,true,false,ParserME.defaultBeamSize,ParserME.defaultAdvancePercentage);
  }
  

  private static class EnglishTreebankPOSTagger extends POSTaggerME implements ParserTagger {

    private static final int K = 10;
    int beamSize;
    
    public EnglishTreebankPOSTagger(String modelFile) throws IOException {
      this(modelFile,K,K);
    }

    public EnglishTreebankPOSTagger(String modelFile,int beamSize, int cacheSize) throws IOException {
      super(beamSize, new SuffixSensitiveGISModelReader(new File(modelFile)).getModel(), new DefaultPOSContextGenerator(cacheSize), null);
      this.beamSize = beamSize;
    }

    public EnglishTreebankPOSTagger(String modelFile, String tagDictionary, boolean useCase) throws IOException {
      this(modelFile,K,tagDictionary,useCase,K);
    }
    
    public EnglishTreebankPOSTagger(String modelFile, int beamSize, String tagDictionary, boolean useCase, int cacheSize) throws IOException {
      super(beamSize, new SuffixSensitiveGISModelReader(new File(modelFile)).getModel(), new DefaultPOSContextGenerator(cacheSize), new POSDictionary(tagDictionary, useCase));
      this.beamSize = beamSize;
    }

    public Sequence[] topKSequences(List sentence) {
      return beam.bestSequences(beamSize, sentence.toArray(), null);
    }

    public Sequence[] topKSequences(String[] sentence) {
      return beam.bestSequences(beamSize, sentence, null);
    }
  }

  private static class EnglishTreebankChunker extends ChunkerME implements ParserChunker {
    private static final int K = 10;
    private int beamSize;
    private Map continueStartMap;
    
    public EnglishTreebankChunker(String modelFile) throws IOException {
      this(modelFile,K,K);
    }
    
    public EnglishTreebankChunker(String modelFile, int beamSize, int cacheSize) throws IOException {
      super(new SuffixSensitiveGISModelReader(new File(modelFile)).getModel(), new ChunkContextGenerator(cacheSize), beamSize);
      continueStartMap = new HashMap(model.getNumOutcomes());
      for (int oi=0,on=model.getNumOutcomes();oi<on;oi++) {
        String outcome = model.getOutcome(oi);
        if (outcome.startsWith(ParserME.CONT)){
          continueStartMap.put(outcome,ParserME.START+outcome.substring(ParserME.CONT.length()));
        }
      }
      this.beamSize = beamSize;
    }

    public Sequence[] topKSequences(List sentence, List tags) {
      return beam.bestSequences(beamSize, sentence.toArray(), new Object[] { tags });
    }

    public Sequence[] topKSequences(String[] sentence, String[] tags, double minSequenceScore) {
      return beam.bestSequences(beamSize, sentence, new Object[] { tags },minSequenceScore);
    }

    protected boolean validOutcome(String outcome, String[] tagList) {
      if (continueStartMap.containsKey(outcome)) {
        int lti = tagList.length - 1;
        if (lti == -1) {
          return (false);
        }
        else {
          String lastTag = tagList[lti];
          if (lastTag.equals(outcome)) {
             return true;
          }
          if (lastTag.equals(continueStartMap.get(outcome))) {
            return true;
          }
          if (lastTag.equals(ParserME.OTHER)) {
            return (false);
          }
          return false;
        }
      }
      return (true);
    }
    
    protected boolean validOutcome(String outcome, Sequence sequence) {
      if (continueStartMap.containsKey(outcome)) {
        List tagList = sequence.getOutcomes();
        int lti = tagList.size() - 1;
        if (lti == -1) {
          return (false);
        }
        else {
          String lastTag = (String) tagList.get(lti);
          if (lastTag.equals(outcome)) {
             return true;
          }
          if (lastTag.equals(continueStartMap.get(outcome))) {
            return true;
          }
          if (lastTag.equals(ParserME.OTHER)) {
            return (false);
          }
          return false;
        }
      }
      return (true);
    }
  }

  private static String convertToken(String token) {
    if (token.equals("(")) {
      return "-LRB-";
    }
    else if (token.equals(")")) {
      return "-RRB-";
    }
    else if (token.equals("{")) {
      return "-LCB-";
    }
    else if (token.equals("}")) {
      return "-RCB-";
    }
    return token;
  }

  private static void usage() {
    System.err.println("Usage: EnglishTreebankParser -[id] -bs -ap dataDirectory < tokenized_sentences");
    System.err.println("dataDirectory: Directory containing parser models.");
    System.err.println("-d: Use tag dictionary.");
    System.err.println("-i: Case insensitive tag dictionary.");
    System.err.println("-bs 20: Use a beam size of 20.");
    System.err.println("-ap 0.95: Advance outcomes in with at least 95% of the probability mass.");
    System.err.println("-k 5: Show the top 5 parses.  This will also display their log-probablities.");
    System.exit(1);
  }

  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      usage();
    }
    boolean useTagDictionary = false;
    boolean caseInsensitiveTagDictionary = false;
    boolean showTopK = false;
    int numParses = 1;
    int ai = 0;
    int beamSize = ParserME.defaultBeamSize;
    double advancePercentage = ParserME.defaultAdvancePercentage;
    while (args[ai].startsWith("-")) {
      if (args[ai].equals("-d")) {
        useTagDictionary = true;
      }
      else if (args[ai].equals("-i")) {
        caseInsensitiveTagDictionary = true;
      }
      else if (args[ai].equals("-bs")) {
      	if (args.length > ai+1) {
          try {
            beamSize=Integer.parseInt(args[ai+1]);
            ai++;
          }
          catch(NumberFormatException nfe) {
            System.err.println(nfe);
            usage();
          }
      	}
      	else {
      	  usage();
      	}
      }
      else if (args[ai].equals("-ap")) {
        if (args.length > ai+1) {
          try {
            advancePercentage=Double.parseDouble(args[ai+1]);
            ai++;
          }
          catch(NumberFormatException nfe) {
            System.err.println(nfe);
            usage();
          }
      	}
      	else {
      	  usage();
      	}
      }
      else if (args[ai].equals("-k")) {
        showTopK = true;
        if (args.length > ai+1) {
          try {
            numParses=Integer.parseInt(args[ai+1]);
            ai++;
          }
          catch(NumberFormatException nfe) {
            System.err.println(nfe);
            usage();
          }
      	}
      	else {
      	  usage();
      	}
      }
      else if (args[ai].equals("--")) {
      	ai++;
        break;
      }
      ai++;
    }
    ParserME parser;
    if (caseInsensitiveTagDictionary) {
      parser = EnglishTreebankParser.getParser(args[ai++], true, false,beamSize,advancePercentage);
    }
    else if (useTagDictionary) {
      parser = EnglishTreebankParser.getParser(args[ai++], true, true,beamSize,advancePercentage);
    }
    else {
      parser = EnglishTreebankParser.getParser(args[ai++], false, false,beamSize,advancePercentage);
    }
    BufferedReader in;
    if (ai == args.length) {
      in = new BufferedReader(new InputStreamReader(System.in));
    }
    else {
      in = new BufferedReader(new FileReader(args[ai]));
    }
    String line;
    try {
      while (null != (line = in.readLine())) {
        line = untokenizedParenPattern1.matcher(line).replaceAll("$1 $2");
        line = untokenizedParenPattern2.matcher(line).replaceAll("$1 $2");
        StringTokenizer str = new StringTokenizer(line);
        StringBuffer sb = new StringBuffer();
        List tokens = new ArrayList();
        while (str.hasMoreTokens()) {
          String tok = convertToken(str.nextToken());
          tokens.add(tok);
          sb.append(tok).append(" ");
        }
        if (sb.length() != 0) {
          String text = sb.substring(0, sb.length() - 1).toString();
          Parse p = new Parse(text, new Span(0, text.length()), "INC", 1, null);
          int start = 0;
          for (Iterator ti = tokens.iterator(); ti.hasNext();) {
            String tok = (String) ti.next();
            p.insert(new Parse(text, new Span(start, start + tok.length()), ParserME.TOK_NODE, 0));
            start += tok.length() + 1;
          }
          Parse[] parses = parser.parse(p,numParses);
          for (int pi=0,pn=parses.length;pi<pn;pi++) {
            if (showTopK) {
              System.out.print(pi+" "+parses[pi].getProb()+" ");
            }
            parses[pi].show();
            System.out.println();
          }
        }
        else {
          System.out.println();
        }
      }
    }
    catch (IOException e) {
      System.err.println(e);
    }
  }
}