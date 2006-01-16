///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2005 Thomas Morton
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
package opennlp.tools.parser.treeinsert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.TwoPassDataIndexer;
import opennlp.tools.parser.AbstractBottomUpParser;
import opennlp.tools.parser.HeadRules;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.ParserChunker;
import opennlp.tools.parser.ParserEventTypeEnum;
import opennlp.tools.parser.ParserTagger;

public class Parser extends AbstractBottomUpParser {

  /** Outcome used when a constiuent needs an no additional parent node/building. */
  public static final String DONE = "d";

  /** Outcome used when a node should be attached as a sister to anototer node. */ 
  public static final String ATTACH_SISTER = "s";
  /** Outcome used when a node should be attached as a daughter to anototer node. */
  public static final String ATTACH_DAUGHTER = "d";
  /** Outcome used when a node should not be attached to anototer node. */
  public static final String NON_ATTACH = "n";
  
  /** Label used to distinguish build nodes from non-built nodes. */ 
  public static final String BUILT = "built";
  
  private MaxentModel buildModel;
  private MaxentModel attachModel;
  
  private BuildContextGenerator buildContextGenerator;
  private AttachContextGenerator attachContextGenerator;
  
  private double[] bprobs;
  private double[] aprobs;
  
  private int doneIndex;
  private int sisterAttachIndex;
  private int daughterAttachIndex;
  private int nonAttachIndex;
  
  private int[] attachments;

  public Parser(MaxentModel buildModel, MaxentModel attachModel, ParserTagger tagger, ParserChunker chunker, HeadRules headRules, int beamSize, double advancePercentage) {
    super(tagger,chunker,headRules,beamSize,advancePercentage);
    this.buildModel = buildModel;
    this.attachModel = attachModel;

    this.buildContextGenerator = new BuildContextGenerator();
    this.attachContextGenerator = new AttachContextGenerator();
        
    this.bprobs = new double[buildModel.getNumOutcomes()];
    this.aprobs = new double[attachModel.getNumOutcomes()];
    
    this.doneIndex = buildModel.getIndex(DONE);
    this.sisterAttachIndex = attachModel.getIndex(ATTACH_SISTER);
    this.daughterAttachIndex = attachModel.getIndex(ATTACH_DAUGHTER);
    this.nonAttachIndex = attachModel.getIndex(NON_ATTACH);
    attachments = new int[] {daughterAttachIndex,sisterAttachIndex};
  }
  
  public Parser(MaxentModel buildModel, MaxentModel attachModel, ParserTagger tagger, ParserChunker chunker, HeadRules headRules) {
    this(buildModel,attachModel,tagger,chunker,headRules,defaultBeamSize,defaultAdvancePercentage);
  }
  
  /**
   * Returns the right frontier of the specified parse tree with nodes ordered from deepest
   * to shallowest.
   * @param root The root of the parse tree.
   * @return The right frontier of the specified parse tree.
   */
  public static List getRightFrontier(Parse root,Set punctSet) {
    List rf = new LinkedList();
    Parse top;
    if (root.getType() == AbstractBottomUpParser.TOP_NODE ||
        root.getType() == AbstractBottomUpParser.INC_NODE) {
      top = collapsePunctuation(root.getChildren(),punctSet)[0];
    }
    else {
      top = root;
    }
    while(!top.isPosTag()) {
      rf.add(0,top);
      Parse[] kids = top.getChildren();
      top = kids[kids.length-1];
    }
    return new ArrayList(rf);
  }
    
  protected Parse[] advanceParses(Parse p, double probMass) {
    double q = 1 - probMass;
    /** The index of the node which will be labeled in this iteration of advancing the parse. */
    int advanceNodeIndex;
    /** The node which will be labeled in this iteration of advancing the parse. */
    Parse advanceNode=null;
    Parse[] originalChildren = p.getChildren();
    Parse[] children = collapsePunctuation(originalChildren,punctSet);
    int numNodes = children.length;
    if (numNodes == 0) {
      return null;
    }
    else if (numNodes == 1) {  //put sentence initial and final punct in top node
      if (children[0].isPosTag()) {
        return null;
      }
      else {
        p.expandTopNode(children[0]);
        return new Parse[] { p };
      }
    }
    //determines which node needs to be labeled and prior labels.
    for (advanceNodeIndex = 0; advanceNodeIndex < numNodes; advanceNodeIndex++) {
      advanceNode = children[advanceNodeIndex];
      if (advanceNode.getLabel() == null) {
        break;
      }
    }
    int originalZeroIndex = mapParseIndex(0,children,originalChildren);
    int originalAdvanceIndex = mapParseIndex(advanceNodeIndex,children,originalChildren);
    List newParsesList = new ArrayList();
    //call build model
    buildModel.eval(buildContextGenerator.getContext(children, advanceNodeIndex), bprobs);
    double doneProb = bprobs[doneIndex];
    if (debugOn) System.out.println("adi="+advanceNodeIndex+" "+advanceNode+" choose build="+(1-doneProb)+" attach="+doneProb);
    if (1-doneProb > q) {
      double bprobSum = 0;
      while (bprobSum < probMass) {
        /** The largest unadvanced labeling. */ 
        int max = 0;
        for (int pi = 1; pi < bprobs.length; pi++) { //for each build outcome
          if (bprobs[pi] > bprobs[max]) {
            max = pi;
          }
        }
        if (bprobs[max] == 0) {
          break;
        }
        double bprob = bprobs[max];
        bprobs[max] = 0; //zero out so new max can be found
        bprobSum += bprob;
        String tag = buildModel.getOutcome(max);
        if (!tag.equals(DONE)) {
          if (debugOn) System.out.println("building "+tag+" "+bprob);
          Parse newParse = (Parse) p.clone();
          newParse.insert(new Parse(p.getText(),advanceNode.getSpan(),tag,bprob,advanceNode.getHead()));
          newParse.addProb(Math.log(bprob));
          newParsesList.add(newParse);
        }
      }
    }
    if (doneProb > q) {
      Parse newParse1 = (Parse) p.clone(); //clone parse
      newParse1.setChild(originalAdvanceIndex,BUILT); //replace constituent being labeled to create new derivation
      newParse1.addProb(Math.log(doneProb));
      if (advanceNodeIndex == 0) {
        newParsesList.add(newParse1);
      }
      else {
        List rf = getRightFrontier(p,punctSet);
        Set jumpNodes = new HashSet();
        for (int fi=0,fs=rf.size();fi<fs;fi++) {
          Parse fn = (Parse) rf.get(fi);
          attachModel.eval(attachContextGenerator.getContext(children, fn, advanceNodeIndex,jumpNodes), aprobs);
          if (debugOn) System.out.println("Frontier node: "+fn.getType()+" "+fn+" <- "+advanceNode.getType()+" "+advanceNode+" d="+aprobs[daughterAttachIndex]+" s="+aprobs[sisterAttachIndex]);
          for (int ai=0;ai<attachments.length;ai++) {
            double prob = aprobs[attachments[ai]];
            if (prob > q && (attachments[ai] != daughterAttachIndex || !fn.isChunk())) {
              Parse newParse2 = newParse1.cloneRoot(fn,originalZeroIndex);
              //remove nodes from top level we're going to attach
              for (int ri=originalZeroIndex+1;ri<=originalAdvanceIndex;ri++) {
                //System.out.println(at"-removing "+(originalZeroIndex+1)+" "+newParse2.getChildren()[originalZeroIndex+1]);
                newParse2.remove(originalZeroIndex+1);
              }
              List crf = getRightFrontier(newParse2,punctSet);
              if (attachments[ai] == daughterAttachIndex) {//attach daughter
                Parse asite = (Parse) crf.get(fi);
                asite.add(advanceNode,headRules);
              }
              else { //attach sister
                Parse psite;
                if (fi+1 < crf.size()) {
                  psite = (Parse) crf.get(fi+1);
                  psite.adjoin(advanceNode,headRules);
                }
                else {
                  psite = newParse2;
                  psite.adjoinRoot(advanceNode,headRules,originalZeroIndex);
                }
              }
              //update spans affected by attachment
              for (int ni=fi+1;ni<crf.size();ni++) {
                Parse node = (Parse) crf.get(ni);
                node.updateSpan();
              }
              if (debugOn) {System.out.print(ai+"-result: ");newParse2.show();System.out.println();}
              newParse2.addProb(Math.log(prob));
              newParsesList.add(newParse2);
            }
          }
          //TODO: use this to estimate others aprobs[nonAttachIndex]
          jumpNodes.add(fn);
        }
      }
    }
    Parse[] newParses = new Parse[newParsesList.size()];
    newParsesList.toArray(newParses);
    return newParses;
  }

  protected void advanceTop(Parse p) {
    p.setType(TOP_NODE);
  }
  
  public static GISModel train(opennlp.maxent.EventStream es, int iterations, int cut) throws java.io.IOException {
    return opennlp.maxent.GIS.trainModel(iterations, new TwoPassDataIndexer(es, cut));
  }
  
  private static void usage() {
    System.err.println("Usage: ParserME -[dict|tag|chunk|choose|build|attach|fun] trainingFile parserModelDirectory [iterations cutoff]");
    System.err.println();
    System.err.println("Training file should be one sentence per line where each line consists of a Penn Treebank Style parse");
    System.err.println("-tag Just build the tagging model.");
    System.err.println("-chunk Just build the chunking model.");
    System.err.println("-choose Just build the choose model.");
    System.err.println("-build Just build the build model");
    System.err.println("-attach Just build the attach model");
    System.err.println("-fun Predict function tags");
  }
  
  public static void main(String[] args) throws java.io.IOException {
    if (args.length < 3) {
      usage();
      System.exit(1);
    }
    boolean tag = false;
    boolean chunk = false;
    boolean build = false;
    boolean attach = false;
    boolean fun = false;
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
      else if (args[argIndex].equals("-attach")) {
        attach = true;
      }
      else if (args[argIndex].equals("-fun")) {
        fun = true;
      }
      else if (args[argIndex].equals("--")) {
        argIndex++;
        break;
      }
      else {
        System.err.println("Invalid option " + args[argIndex]);
        usage();
        System.exit(1);
      }
      argIndex++;
    }
    java.io.File inFile = new java.io.File(args[argIndex++]);
    String modelDirectory = args[argIndex++];
    HeadRules rules = new opennlp.tools.lang.english.HeadRules(modelDirectory+"/head_rules");
    java.io.File tagFile = new java.io.File(modelDirectory+"/tag.bin.gz");
    java.io.File chunkFile = new java.io.File(modelDirectory+"/chunk.bin.gz");
    java.io.File buildFile = new java.io.File(modelDirectory+"/build.bin.gz");
    java.io.File attachFile = new java.io.File(modelDirectory+"/attach.bin.gz");
    int iterations = 100;
    int cutoff = 5;
    if (args.length > argIndex) {
      iterations = Integer.parseInt(args[argIndex++]);
      cutoff = Integer.parseInt(args[argIndex++]);
    }
    if (fun) {
      Parse.useFunctionTags(true);
    }
    if (tag || all) {
      System.err.println("Training tagger");
      //System.err.println("Loading Dictionary");
      //Dictionary tridict = new Dictionary(dictFile.toString());
      opennlp.maxent.EventStream tes = new ParserEventStream(new opennlp.maxent.PlainTextByLineDataStream(new java.io.FileReader(inFile)), rules, ParserEventTypeEnum.TAG);
      GISModel tagModel = train(tes, iterations, cutoff);
      System.out.println("Saving the tagger model as: " + tagFile);
      new opennlp.maxent.io.SuffixSensitiveGISModelWriter(tagModel, tagFile).persist();
    }

    if (chunk || all) {
      System.err.println("Training chunker");
      opennlp.maxent.EventStream ces = new ParserEventStream(new opennlp.maxent.PlainTextByLineDataStream(new java.io.FileReader(inFile)), rules, ParserEventTypeEnum.CHUNK);
      GISModel chunkModel = train(ces, iterations, cutoff);
      System.out.println("Saving the chunker model as: " + chunkFile);
      new opennlp.maxent.io.SuffixSensitiveGISModelWriter(chunkModel, chunkFile).persist();
    }
        
    if (build || all) {
      System.err.println("Training builder");
      opennlp.maxent.EventStream bes = new ParserEventStream(new opennlp.maxent.PlainTextByLineDataStream(new java.io.FileReader(inFile)), rules, ParserEventTypeEnum.BUILD,null);
      GISModel buildModel = train(bes, iterations, cutoff);
      System.out.println("Saving the build model as: " + buildFile);
      new opennlp.maxent.io.SuffixSensitiveGISModelWriter(buildModel, buildFile).persist();
    }

    if (attach || all) {
      System.err.println("Training attacher");
      opennlp.maxent.EventStream kes = new ParserEventStream(new opennlp.maxent.PlainTextByLineDataStream(new java.io.FileReader(inFile)), rules, ParserEventTypeEnum.ATTACH);
      GISModel attachModel = train(kes, iterations, cutoff);
      System.out.println("Saving the attach model as: " + attachFile);
      new opennlp.maxent.io.SuffixSensitiveGISModelWriter(attachModel, attachFile).persist();
    }
  }
}
