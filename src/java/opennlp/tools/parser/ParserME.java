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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import opennlp.common.util.Sequence;
import opennlp.common.util.Span;
import opennlp.maxent.ContextGenerator;
import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.TwoPassDataIndexer;

public class ParserME {
  private static int M = 20;
  private static int K = 20;
  private static double Q = 0.95;

  private SortedSet parses;
  /** Old derivations heap. */
  private SortedSet odh;
  /** New derivations heap. */
  private SortedSet ndh;
  private ParserTagger tagger; //POS tagger
  private ParserChunker chunker; //Basal Chunker

  private MaxentModel buildModel;
  private MaxentModel checkModel;

  private ContextGenerator buildContextGenerator;
  private ContextGenerator checkContextGenerator;

  private HeadRules headRules;

  private double[] bprobs;
  private double[] cprobs;

  public static final String TOP_NODE = "TOP";
  public static final String TOK_NODE = "TK";

  public static final Integer ZERO = new Integer(0);

  /** Prefix for outcomes starting a constituent. */
  public static final String START = "S-";
  /** Prefix for outcomes continuing a constituent. */
  public static final String CONT = "C-";
  /** Outcome for token which is not contained in a basal constituent. */
  public static final String OTHER = "O";

  /** Outcome used when a constituent is complete. */
  public static final String COMPLETE = "c";
  /** Outcome used when a constituent is incomplete. */
  public static final String INCOMPLETE = "i";

  public ParserME(MaxentModel buildModel, MaxentModel checkModel, ParserTagger tagger, ParserChunker chunker, HeadRules headRules) {
    this.tagger = tagger;
    this.chunker = chunker;
    this.buildModel = buildModel;
    this.checkModel = checkModel;
    bprobs = new double[buildModel.getNumOutcomes()];
    cprobs = new double[checkModel.getNumOutcomes()];
    this.buildContextGenerator = new BuildContextGenerator();
    this.checkContextGenerator = new CheckContextGenerator();
    this.headRules = headRules;
    odh = new TreeSet();
    ndh = new TreeSet();
    parses = new TreeSet();
  }

  public Parse parse(Parse p) {
    p.derivation = new StringBuffer(100);
    odh.clear();
    ndh.clear();
    parses.clear();
    int i = 0; //derivation length
    int maxDerivationLength = 2 * p.getChildren().size() + 3;
    odh.add(p);
    Parse guess = null;
    double bestComplete = -100000; //approximating -infinity in ln domain
    while (parses.size() < M && i < maxDerivationLength) {
      ndh = new TreeSet();
      if (odh.size() > 0) {
        int j = 0;
        for (Iterator pi = odh.iterator(); pi.hasNext() && j < K; j++) { // foearch derivation
          Parse tp = (Parse) pi.next();
          if (tp.prob < bestComplete) {
            break;
          }
          if (guess == null && i == 2) {
            guess = tp;
          }

          System.out.print(i + " " + j + " " + tp.derivation + " " + tp.prob + " ");
          tp.show();
          System.out.println();

          Parse[] nd = advance(tp, Q, i);
          if (nd != null) {
            for (int k = 0, kl = nd.length; k < kl; k++) {
              //System.out.println("k="+k+" of "+nd.length);
              if (nd[k].complete()) {
                advanceTop(nd[k]);
                if (nd[k].getProb() > bestComplete) {
                  bestComplete = nd[k].getProb();
                }
                parses.add(nd[k]);
              }
              else {
                ndh.add(nd[k]);
              }
            }
          }
          else {
            System.err.println("Couldn't advance!\n");
          }
        }
        i++;
        odh = ndh;
      }
      else {
        break;
      }
    }
    //int pi = 1;

    //System.err.println(parses.size()+" parses");

    /*  convert to use iterator
    int pc = 1;
    for (Iterator pi = parses.iterator(); pi.hasNext(); pc++) {
      Parse tp = (Parse) pi.next();
      System.out.print(pc + " " + tp.getProb() + " ");
      tp.show();
      System.out.println();
    }
    System.out.println();
    */
    Parse r;
    if (parses.size() == 0) {
      System.err.println("Couldn't find parse for: " + p);
      r = guess;
    }
    else {
      r = (Parse) parses.first();
    }
    return (r);
  }

  private void advanceTop(Parse p) {
    buildModel.eval(buildContextGenerator.getContext(new Object[] { p.getChildren(), ZERO }), bprobs);
    p.prob += Math.log(bprobs[buildModel.getIndex(START + TOP_NODE)]);
    checkModel.eval(checkContextGenerator.getContext(new Object[] { p.getChildren(), TOP_NODE, ZERO, ZERO }), cprobs);
    p.prob += Math.log(cprobs[checkModel.getIndex(COMPLETE)]);
    p.setType(TOP_NODE);
  }

  private Parse[] advance(Parse p, double Q, int dl) {
    Parse[] newParses = null;
    double q = 1 - Q;
    if (0 == dl) {
      // tag
      String[] words = new String[p.getChildren().size()];
      for (int i = 0; i < p.getChildren().size(); i++) {
        words[i] = ((Parse) p.getChildren().get(i)).toString();
      }
      Sequence[] ts = tagger.topKSequences(words);
      newParses = new Parse[ts.length];
      for (int i = 0; i < ts.length; i++) {
        String[] tags = (String[]) ts[i].getOutcomes().toArray(new String[words.length]);
        List probs = ts[i].getProbs();
        newParses[i] = (Parse) p.clone(); //copies top level
        newParses[i].derivation.append(i).append(".");
        for (int j = 0; j < words.length; j++) {
          Parse word = (Parse) p.getChildren().get(j);
          //System.err.println("inserting tag "+tags[j]);
          double prob = ((Double) probs.get(j)).doubleValue();
          newParses[i].insert(new Parse(word.getText(), word.getSpan(), tags[j], prob));
          newParses[i].prob += Math.log(prob);
          //newParses[i].show();
        }
      }
    }
    else if (1 == dl) {
      // chunk
      String words[] = new String[p.getChildren().size()];
      String ptags[] = new String[p.getChildren().size()];
      Parse sp = null;
      for (int i = 0, il = p.getChildren().size(); i < il; i++) {
        sp = (Parse) p.getChildren().get(i);
        words[i] = sp.getHead().toString();
        ptags[i] = sp.getType();
      }
      Sequence[] cs = chunker.topKSequences(words, ptags);
      newParses = new Parse[cs.length];
      for (int si = 0, sl = cs.length; si < sl; si++) {
        newParses[si] = (Parse) p.clone(); //copies top level
        newParses[si].derivation.append(si).append(".");
        String[] tags = (String[]) cs[si].getOutcomes().toArray(new String[words.length]);
        List probs = cs[si].getProbs();
        int start = -1;
        int end = 0;
        String type = null;
        //System.err.print("sequence "+si+" ");
        for (int j = 0; j <= tags.length; j++) {
          //if (j != tags.length) {System.err.println(words[j]+" "+ptags[j]+" "+tags[j]+" "+probs.get(j));}
          if (j != tags.length) {
            newParses[si].prob += Math.log(((Double) probs.get(j)).doubleValue());
          }
          if (j != tags.length && tags[j].startsWith(CONT)) {
            end = j;
          }
          else {
            if (type != null) {
              //System.err.println("inserting tag "+tags[j]);
              Parse p1 = (Parse) p.getChildren().get(start);
              Parse p2 = (Parse) p.getChildren().get(end);
              //System.err.println("Putting "+type+" at "+start+","+end);
              Parse[] cons = new Parse[end - start + 1];
              cons[0] = p1;
              //cons[0].label="Start-"+type;
              if (end - start != 0) {
                cons[end - start] = p2;
                //cons[end-start].label="Cont-"+type;
                for (int ci = 1; ci < end - start; ci++) {
                  cons[ci] = (Parse) p.getChildren().get(ci + start);
                  //cons[ci].label="Cont-"+type;
                }
              }
              newParses[si].insert(new Parse(p1.getText(), new Span(p1.getSpan().getStart(), p2.getSpan().getEnd()), type, 1, headRules.getHead(cons, type)));
            }
            if (j != tags.length) {
              if (tags[j].startsWith(START)) {
                type = tags[j].substring(START.length());
                start = j;
                end = j;
              }
              else {
                type = null;
              }
            }
          }
        }
        //newParses[si].show();System.out.println();
      }
    }
    else { // dl > 1
      Parse lastStart = null;
      int lsi = -1; // last start index
      String lst = null; // last start type
      int psize = p.getChildren().size();
      for (int i = 0; i < psize; i++) {
        Parse part = (Parse) p.getChildren().get(i);
        if (part.getLabel() == null) {
          ArrayList newParsesList = new ArrayList(buildModel.getNumOutcomes());
          //call build
          buildModel.eval(buildContextGenerator.getContext(new Object[] { p.getChildren(), new Integer(i)}), bprobs);
          double bprobSum = 0;
          while (bprobSum < Q) {
            int max = 0;
            for (int pi = 1; pi < bprobs.length; pi++) { //for each build outcome
              if (bprobs[pi] > bprobs[max]) {
                max = pi;
              }
            }
            //if (bprobs[max] == 0) {
            //  break;
            //}
            double bprob = bprobs[max];
            bprobSum += bprobs[max];
            bprobs[max] = 0; //zero out so new max can be found
            String tag = buildModel.getOutcome(max);
            //System.out.println("trying "+tag+" "+bprobSum+" lst="+lst);
            if (tag.equals(START + TOP_NODE)) { // can't have top until complete
              continue;
            }
            //System.err.println(i+" "+tag+" "+bprob);
            if (tag.startsWith(START)) { //update last start
              lsi = i;
              lastStart = part;
              lst = tag.substring(START.length());
            }
            else if (tag.startsWith(CONT)) {
              if (lastStart == null || !lst.equals(tag.substring(CONT.length()))) {
                continue; //Cont must match previous start or continue
              }
            }
            Parse newParse1 = (Parse) p.clone(); //clone parse
            newParse1.derivation.append(max).append("-");
            Parse pc = (Parse) part.clone(); //clone constituent being labeled
            newParse1.getChildren().set(i, pc); //replace constituent labeled
            pc.setLabel(tag);
            newParse1.prob += Math.log(bprob);
            //check
            checkModel.eval(checkContextGenerator.getContext(new Object[] { newParse1.getChildren(), lst, new Integer(lsi), new Integer(i)}), cprobs);
            //System.out.println("check "+cprobs[0]+" "+cprobs[1]);
            Parse newParse2 = newParse1;
            if (cprobs[1] > q) { //make sure a reduce is likely
              newParse2 = (Parse) newParse1.clone();
              newParse2.derivation.append(1).append(".");
              newParse2.prob += Math.log(cprobs[1]);
              Parse[] cons = new Parse[i - lsi + 1];
              boolean flat = true;
              //first
              cons[0] = lastStart;
              if (!cons[0].getType().equals(cons[0].getHead().getType())) {
                flat = false;
              }
              //last
              cons[i - lsi] = part;
              if (flat && !cons[i - lsi].getType().equals(cons[i - lsi].getHead().getType())) {
                flat = false;
              }
              //middle
              for (int ci = 1; ci < i - lsi; ci++) {
                cons[ci] = (Parse) p.getChildren().get(ci + lsi);
                if (flat && !cons[ci].getType().equals(cons[ci].getHead().getType())) {
                  flat = false;
                }
              }
              if (!flat) { //flat chunks are done by chunker
                newParse2.insert(new Parse(p.getText(), new Span(lastStart.getSpan().getStart(), part.getSpan().getEnd()), lst, cprobs[1], headRules.getHead(cons, lst)));
                newParsesList.add(newParse2);
              }
            }
            if (cprobs[0] > q) { //make sure a shift is likly
              newParse1.derivation.append(0).append(".");
              if (i != psize - 1) { //can't shift last element
                newParse1.prob += Math.log(cprobs[0]);
                newParsesList.add(newParse1);
              }
            }
          }
          newParses = new Parse[newParsesList.size()];
          newParsesList.toArray(newParses);
          break;
        }
        else if (part.getLabel().startsWith(START)) {
          lst = part.getLabel().substring(START.length());
          lastStart = part;
          lsi = i;
          //System.err.println("lastStart "+i+" "+lastStart.label+" "+lastStart.prob);
        }
      }
    }
    return (newParses);
  }

  public static GISModel train(opennlp.maxent.EventStream es, int iterations, int cut) throws java.io.IOException {
    return opennlp.maxent.GIS.trainModel(iterations, new TwoPassDataIndexer(es, cut));
  }

  public static void main(String[] args) throws java.io.IOException {
    if (args.length < 4) {
      System.err.println("Usage: ParserME -[tag|chunk|build|check] trainingFile headRules tagModelFile chunkModelFile buildModelFile checkModelFile [iterations cutoff]");
      System.err.println();
      System.err.println("Training file should be one sentence per line where each line consists of a Penn Treebank Style parse");
      System.exit(1);
    }
    boolean tag = false;
    boolean chunk = false;
    boolean build = false;
    boolean check = false;
    boolean all = true;
    int argIndex = 0;
    while (args[argIndex].startsWith("-")) {
      all = false;
      if (args[argIndex].equals("-tag")) {
        tag = true;
      }
      else if (args[argIndex].equals("-chunk")) {
        chunk = true;
      }
      else if (args[argIndex].equals("-build")) {
        build = true;
      }
      else if (args[argIndex].equals("-check")) {
        check=true;
      }
      else {
        System.err.println("Invalid option "+args[argIndex]);
        System.exit(1);
      }
      argIndex++;
    }
    java.io.File inFile = new java.io.File(args[argIndex++]);
    String headRulesFile = args[argIndex++];
    HeadRules rules = new HeadRules(headRulesFile);
    java.io.File tagFile = new java.io.File(args[argIndex++]);
    java.io.File chunkFile = new java.io.File(args[argIndex++]);
    java.io.File buildFile = new java.io.File(args[argIndex++]);
    java.io.File checkFile = new java.io.File(args[argIndex++]);
    int iterations = 100;
    int cutoff = 5;
    if (args.length > argIndex) {
      iterations = Integer.parseInt(args[argIndex++]);
      cutoff = Integer.parseInt(args[argIndex++]);
    }

    if (tag || all) {
      System.err.println("Training tagger");
      opennlp.maxent.EventStream tes = new ParserEventStream(new opennlp.maxent.PlainTextByLineDataStream(new java.io.FileReader(inFile)), rules, EventTypeEnum.TAG);
      GISModel tagModel = train(tes, iterations, cutoff);
      System.out.println("Saving the tagger model as: " + tagFile);
      new opennlp.maxent.io.SuffixSensitiveGISModelWriter(tagModel, tagFile).persist();
    }

    if (chunk || all) {
      System.err.println("Training chunker");
      opennlp.maxent.EventStream ces = new ParserEventStream(new opennlp.maxent.PlainTextByLineDataStream(new java.io.FileReader(inFile)), rules, EventTypeEnum.CHUNK);
      GISModel chunkModel = train(ces, iterations, cutoff);
      System.out.println("Saving the chunker model as: " + chunkFile);
      new opennlp.maxent.io.SuffixSensitiveGISModelWriter(chunkModel, chunkFile).persist();
    }

    if (build || all) {
      System.err.println("Training builder");
      opennlp.maxent.EventStream bes = new ParserEventStream(new opennlp.maxent.PlainTextByLineDataStream(new java.io.FileReader(inFile)), rules, EventTypeEnum.BUILD);
      GISModel buildModel = train(bes, iterations, cutoff);
      System.out.println("Saving the build model as: " + buildFile);
      new opennlp.maxent.io.SuffixSensitiveGISModelWriter(buildModel, buildFile).persist();
    }

    if (check || all) {
      System.err.println("Training checker");
      opennlp.maxent.EventStream kes = new ParserEventStream(new opennlp.maxent.PlainTextByLineDataStream(new java.io.FileReader(inFile)), rules, EventTypeEnum.CHECK);
      GISModel checkModel = train(kes, iterations, cutoff);
      System.out.println("Saving the check model as: " + checkFile);
      new opennlp.maxent.io.SuffixSensitiveGISModelWriter(checkModel, checkFile).persist();
    }

  }
}
