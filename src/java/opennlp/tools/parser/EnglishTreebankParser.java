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
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import opennlp.common.english.BasicEnglishAffixes;
import opennlp.common.util.Sequence;
import opennlp.common.util.Span;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.DefaultChunkerContextGenerator;
import opennlp.tools.postag.POSContextGenerator;
import opennlp.tools.postag.POSTaggerME;

public class EnglishTreebankParser extends ParserME {

  /** English parser which produces Penn treebank style parses.
   * @param dataDir directory where the model files are located.
   * @throws IOException when the models can't be loaded.
   */
  public EnglishTreebankParser(String dataDir) throws IOException {
    super(
          new SuffixSensitiveGISModelReader(new File(dataDir+"build")).getModel(),
          new SuffixSensitiveGISModelReader(new File("check")).getModel(),
          new EnglishTreebankPOSTagger(dataDir + "tag"),
          new EnglishTreebankChunker(dataDir + "chunk"),
          new HeadRules(dataDir + "head_rules"));
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
      super(10, new SuffixSensitiveGISModelReader(new File(modelFile)).getModel(), new POSContextGenerator(new BasicEnglishAffixes()));
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
      super(new SuffixSensitiveGISModelReader(new File(modelFile)).getModel(), new DefaultChunkerContextGenerator(), 10);
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
      if (outcome.startsWith("I-")) {
        List tagList = sequence.getOutcomes();
        int lti = tagList.size() - 1;
        if (lti == -1) {
          return (false);
        }
        else {
          String lastTag = (String) tagList.get(lti);
          if (lastTag.equals("O")) {
            return (false);
          }
          if (!lastTag.substring(2).equals(outcome.substring(2))) {
            return (false);
          }
        }
      }
      return (true);
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.err.println("Usage: EnglishTreebankParser dataDirectory < sentences");
    }
    int ai = 0;
    ParserME parser = new EnglishTreebankParser(args[ai++]);
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String line;
    try {
      while (null != (line = in.readLine())) {
        Parse p = new Parse(line, new Span(0, line.length()), "INC", 1, null);
        StringTokenizer str = new StringTokenizer(line);
        int start = 0;
        while (str.hasMoreTokens()) {
          String tok = str.nextToken();
          p.insert(new Parse(line, new Span(start, start + tok.length()), "TOK", 0));
          start += tok.length() + 1;
        }
        parser.parse(p).show();System.out.println();
      }
    }
    catch (IOException e) {
      System.err.println(e);
    }
  }
}