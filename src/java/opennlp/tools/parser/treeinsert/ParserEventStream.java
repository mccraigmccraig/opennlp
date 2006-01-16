///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2006 Thomas Morton
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

package opennlp.tools.parser.treeinsert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opennlp.maxent.DataStream;
import opennlp.maxent.Event;
import opennlp.tools.ngram.Dictionary;
import opennlp.tools.parser.AbstractBottomUpParser;
import opennlp.tools.parser.AbstractParserEventStream;
import opennlp.tools.parser.HeadRules;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.ParserEventTypeEnum;
import opennlp.tools.util.Span;

public class ParserEventStream extends AbstractParserEventStream {

  protected AttachContextGenerator attachContextGenerator;
  protected BuildContextGenerator buildContextGenerator;
  
  private static final boolean debug = false;
  
  public ParserEventStream(DataStream d, HeadRules rules, ParserEventTypeEnum etype, Dictionary dict) {
    super(d, rules, etype, dict);
  }
  
  public void init() {
    buildContextGenerator = new BuildContextGenerator();
    attachContextGenerator = new AttachContextGenerator();
  }

  public ParserEventStream(DataStream d, HeadRules rules, ParserEventTypeEnum etype) {
    super(d, rules, etype);
  }
  
  private Set getNonAdjoinedParent(Parse node) {
    Set parents = new HashSet();
    Parse parent = node.getParent();
    parents.add(parent);
    while(parent.getType().equals(node.getType())) {
      parent = parent.getParent();
      parents.add(parent);
    }
    return parents;
  }

  protected void addParseEvents(List parseEvents, Parse[] chunks) {
    List rightFrontier = new ArrayList();
    List newFrontier = new ArrayList();
    Parse[] currentChunks = new Parse[chunks.length];
    for (int ci=0;ci<chunks.length;ci++) {
      currentChunks[ci] = (Parse) chunks[ci].clone();
      currentChunks[ci].setPrevPunctuation(chunks[ci].getPreviousPunctuationSet());
      currentChunks[ci].setNextPunctuation(chunks[ci].getNextPunctuationSet());
    }
    for (int ci=0;ci<chunks.length;ci++) {
      //System.err.println("parserEventStream.addParseEvents: chunks="+Arrays.asList(chunks));
      Parse parent = chunks[ci].getParent();
      Parse prevParent = chunks[ci];
      int off = 0;
      //build un-built parents
      if (!chunks[ci].isPosTag()) {
        newFrontier.add(off++,chunks[ci]);
      }
      //perform build stages
      while (!parent.getType().equals(AbstractBottomUpParser.TOP_NODE) && parent.getLabel() == null) {
        if (parent.getLabel() == null && !prevParent.getType().equals(parent.getType())) {
          //build level
          if (etype == ParserEventTypeEnum.BUILD) {
            parseEvents.add(new Event(parent.getType(), buildContextGenerator.getContext(currentChunks, ci)));            
          }
          newFrontier.add(off++,parent);
          Parse newParent = new Parse(currentChunks[ci].getText(),currentChunks[ci].getSpan(),parent.getType(),1,0);
          newParent.add(currentChunks[ci],rules);
          newParent.setPrevPunctuation(currentChunks[ci].getPreviousPunctuationSet());
          newParent.setNextPunctuation(currentChunks[ci].getNextPunctuationSet());
          currentChunks[ci].setParent(newParent);
          currentChunks[ci] = newParent;
          newParent.setLabel(Parser.BUILT);
          chunks[ci] = parent;
          //System.err.println("build: "+newParent+" for "+parent);
        }
        parent.setLabel(Parser.BUILT);
        prevParent = parent;
        parent = parent.getParent();
      }
      //decide to attach
      if (etype == ParserEventTypeEnum.BUILD) {
        parseEvents.add(new Event(Parser.DONE, buildContextGenerator.getContext(currentChunks, ci)));
      }
      //attach node
      String attachType = null;
      Parse attachNode = null;
      if (ci == 0){
        Parse top = new Parse(currentChunks[ci].getText(),new Span(0,currentChunks[ci].getText().length()),AbstractBottomUpParser.TOP_NODE,1,0);
        top.insert(currentChunks[ci]);
      }
      else {
        List currentRightFrontier = Parser.getRightFrontier(currentChunks[0],punctSet);
        if (currentRightFrontier.size() != rightFrontier.size()) {
          System.err.println("fontiers mis-aligned: "+currentRightFrontier+" "+rightFrontier);
        }
        Set jumpedNodes = new HashSet(currentRightFrontier.size());
        for (int fi=0;fi<rightFrontier.size();fi++) {
          Parse frontierNode = (Parse) rightFrontier.get(fi);
          Set parents = getNonAdjoinedParent(chunks[ci]);
          Parse cfn = (Parse) currentRightFrontier.get(fi);
          if (debug) System.err.println("Looking at attachment site: "+cfn.getType()+" "+cfn+" :for "+currentChunks[ci].getType()+" "+currentChunks[ci]+" -> "+parents);
          if (attachNode == null && parents.contains(frontierNode)) {
            if (etype == ParserEventTypeEnum.ATTACH) {
              parseEvents.add(new Event(Parser.ATTACH_DAUGHTER, attachContextGenerator.getContext(currentChunks, cfn, ci, jumpedNodes)));
            }
            attachType = Parser.ATTACH_DAUGHTER;
            attachNode = cfn;
            //System.err.println("daughter attach "+attachNode+" at "+fi);
          }
          else if (attachNode == null && parents.contains(frontierNode.getParent()) && frontierNode.getType().equals(frontierNode.getParent().getType())) {
            if (etype == ParserEventTypeEnum.ATTACH) {
              parseEvents.add(new Event(Parser.ATTACH_SISTER, attachContextGenerator.getContext(currentChunks, cfn, ci, jumpedNodes)));
            }
            attachType = Parser.ATTACH_SISTER;
            attachNode = cfn;
            chunks[ci].getParent().setLabel(Parser.BUILT);
            rightFrontier.set(fi,frontierNode.getParent());
            //System.err.println("sister attach "+attachNode+" at "+fi);
          }
          else {
            if (etype == ParserEventTypeEnum.ATTACH) {
              parseEvents.add(new Event(Parser.NON_ATTACH, attachContextGenerator.getContext(currentChunks, frontierNode, ci, jumpedNodes)));
            }
            //System.err.println("no attach an="+attachNode);
            if (attachNode == null) {
              rightFrontier.remove(fi);
              currentRightFrontier.remove(fi);
              fi--;
            }
          }
          jumpedNodes.add(frontierNode);
        }
        if (attachNode != null) {
          if (attachType == Parser.ATTACH_DAUGHTER) {
            Parse daughter = currentChunks[ci];
            if (debug) System.err.println("daughter attach a="+attachNode+" d="+daughter);
            attachNode.add(daughter,rules);
            daughter.setParent(attachNode);
          }
          else if (attachType == Parser.ATTACH_SISTER) {
            Parse sister = currentChunks[ci];
            if (debug) System.err.println("sister attach a="+attachNode+" s="+sister+" ap="+attachNode.getParent());
            Parse newParent = attachNode.getParent().adjoin(sister,rules);
            newParent.setParent(attachNode.getParent());
            attachNode.setParent(newParent);
            sister.setParent(newParent);
            if (attachNode == currentChunks[0]) {
              currentChunks[0]= newParent;
            }
          }
        }
        else {
          //System.err.println("No attachment!");
          throw new RuntimeException("No Attachment: "+chunks[ci]);
        }
      }
      rightFrontier.addAll(0,newFrontier);
      newFrontier.clear();
    }
  }
  
  public static void main(String[] args) throws java.io.IOException {
    if (args.length == 0) {
      System.err.println("Usage ParserEventStream -[tag|chunk|choose|build|attach] [-fun] head_rules [dictionary] < parses");
      System.exit(1);
    }
    ParserEventTypeEnum etype = null;
    boolean fun = false;
    int ai = 0;
    while (ai < args.length && args[ai].startsWith("-")) {
      if (args[ai].equals("-build")) {
        etype = ParserEventTypeEnum.BUILD;
      }
      else if (args[ai].equals("-attach")) {
        etype = ParserEventTypeEnum.ATTACH;
      }
      else if (args[ai].equals("-chunk")) {
        etype = ParserEventTypeEnum.CHUNK;
      }
      else if (args[ai].equals("-tag")) {
        etype = ParserEventTypeEnum.TAG;
      }
      else if (args[ai].equals("-fun")) {
        fun = true;
      }
      else {
        System.err.println("Invalid option " + args[ai]);
        System.exit(1);
      }
      ai++;
    }
    HeadRules rules = new opennlp.tools.lang.english.HeadRules(args[ai++]);
    Dictionary dict = null;
    if (ai < args.length) {
      dict = new Dictionary(args[ai++]);
    }
    if (fun) {
      Parse.useFunctionTags(true);
    }
    opennlp.maxent.EventStream es = new ParserEventStream(new opennlp.maxent.PlainTextByLineDataStream(new java.io.InputStreamReader(System.in)), rules, etype, dict);
    while (es.hasNext()) {
      System.out.println(es.nextEvent());
    }
  }
}