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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import opennlp.common.util.Sequence;
import opennlp.common.util.Span;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.postag.POSContextGenerator;
import opennlp.tools.postag.POSTaggerME;

public class EnglishTreebankParser extends ParserME {

  /** English parser which produces Penn treebank style parses.
   * @param dataDir directory where the model files are located.
   * @throws IOException when the models can't be loaded.
   */
  public EnglishTreebankParser(String dataDir) throws IOException {
    super(
          new SuffixSensitiveGISModelReader(new File(dataDir+"/build.bin.gz")).getModel(),
          new SuffixSensitiveGISModelReader(new File(dataDir+"/check.bin.gz")).getModel(),
          new EnglishTreebankPOSTagger(dataDir + "/tag.bin.gz"),
          new EnglishTreebankChunker(dataDir + "/chunk.bin.gz"),
          new HeadRules(dataDir + "/head_rules"));
    /*
    super(
      new SuffixSensitiveGISModelReader(new File(dataDir + "/parser/EnglishParserBuild.bin.gz")).getModel(),
      new SuffixSensitiveGISModelReader(new File(dataDir + "/parser/EnglishParserCheck.bin.gz")).getModel(),
      new EnglishTreebankPOSTagger(dataDir + "/postag/EnglishPOS.bin.gz"),
      new EnglishTreebankChunker(dataDir + "/parser/EnglishChunker.bin.gz"),
      new HeadRules(dataDir + "/parser/head_rules"));
      */
  }

  private static class EnglishTreebankPOSTagger extends POSTaggerME implements ParserTagger {

    private static final int K = 10;

    public EnglishTreebankPOSTagger(String modelFile) throws IOException {
      super(10, new SuffixSensitiveGISModelReader(new File(modelFile)).getModel(), new POSContextGenerator());
    }
    public Sequence[] topKSequences(List sentence) {
      return beam.bestSequences(K, sentence, null);
    }

    public Sequence[] topKSequences(String[] sentence) {
      return beam.bestSequences(K, Arrays.asList(sentence), null);
    }
  }

  private static class EnglishTreebankChunker extends ChunkerME implements ParserChunker {
    private static final int K = 10;

    public EnglishTreebankChunker(String modelFile) throws IOException {
      super(new SuffixSensitiveGISModelReader(new File(modelFile)).getModel(), new ChunkContextGenerator(), 10);
    }

    public Sequence[] topKSequences(List sentence, List tags) {
      return beam.bestSequences(K, sentence, new Object[] { tags });
    }

    public Sequence[] topKSequences(String[] sentence, String[] tags) {
      return beam.bestSequences(K, Arrays.asList(sentence), new Object[] { Arrays.asList(tags)});
    }

    public Sequence[] topKSequences(List sentence) {
      return beam.bestSequences(K, sentence, null);
    }

    public Sequence[] topKSequences(String[] sentence) {
      return beam.bestSequences(K, Arrays.asList(sentence), null);
    }

    protected boolean validOutcome(String outcome, Sequence sequence) {
      if (outcome.startsWith(ParserME.CONT)) {
        List tagList = sequence.getOutcomes();
        int lti = tagList.size() - 1;
        if (lti == -1) {
          return (false);
        }
        else {
          String lastTag = (String) tagList.get(lti);
          if (lastTag.equals(ParserME.OTHER)) {
            return (false);
          }
          String pred =  outcome.substring(ParserME.CONT.length());
          if (lastTag.startsWith(ParserME.START)) {
            return lastTag.substring(ParserME.START.length()).equals(pred);
          }
          else if (lastTag.startsWith(ParserME.CONT)) {
            return lastTag.substring(ParserME.CONT.length()).equals(pred);
          }
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

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.err.println("Usage: EnglishTreebankParser dataDirectory < sentences");
      System.exit(1);
    }
    int ai = 0;
    ParserME parser = new EnglishTreebankParser(args[ai++]);
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String line;
    try {
      while (null != (line = in.readLine())) {
        StringTokenizer str = new StringTokenizer(line);
        int numToks = str.countTokens();
        StringBuffer sb = new StringBuffer();
        List tokens = new ArrayList();        
        while (str.hasMoreTokens()) {
          String tok = convertToken(str.nextToken());
          tokens.add(tok);
          sb.append(tok).append(" ");
        }
        String text = sb.substring(0,sb.length()-1).toString();
        Parse p = new Parse(text, new Span(0,text.length()), "INC", 1, null);
        int start = 0;
        for (Iterator ti=tokens.iterator();ti.hasNext();) {
          String tok = (String) ti.next();
          p.insert(new Parse(text, new Span(start, start + tok.length()), ParserME.TOK_NODE, 0));
          start += tok.length() + 1;
        }
        p = parser.parse(p);
        //System.out.print(p.getProb()+" ");
        p.show();
        System.out.println();
      }
    }
    catch (IOException e) {
      System.err.println(e);
    }
  }
}