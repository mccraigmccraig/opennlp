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
package opennlp.tools.coref;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import opennlp.tools.coref.mention.DefaultParse;
import opennlp.tools.coref.mention.Extent;
import opennlp.tools.coref.mention.MentionContext;
import opennlp.tools.coref.mention.PTBMentionFinder;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.ParserME;
import opennlp.tools.util.Span;

/**
 * This class perform coreference over tree-bank style parses.  
 * It requires that named-entity information also be provided.  
 * This information can be added to the parse using the -parse 
 * option with EnglishNameFinder.
 */
public class TreebankLinker extends DefaultLinker {
  
  public TreebankLinker(String project, LinkerMode mode) throws IOException {
    super(project,mode);
  }
  
  public TreebankLinker(String project, LinkerMode mode, boolean useDiscourseModel) throws IOException {
    super(project,mode,useDiscourseModel);
  } 
  
  public TreebankLinker(String project, LinkerMode mode, boolean useDiscourseModel, double fixedNonReferentialProbability) throws IOException {
    super(project,mode,useDiscourseModel,fixedNonReferentialProbability);
  }
  
  protected void initMentionFinder() {
    mentionFinder = PTBMentionFinder.getInstance(headFinder);
  }
  
  private static void showEntities(DiscourseEntity[] entities) {
    for (int ei=0,en=entities.length;ei<en;ei++) {
     System.out.println(ei+" "+entities[ei]);
    }
  }
  
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.err.println("Usage: TreebankLinker model_directory < parses");
      System.exit(1);
    }
    BufferedReader in;
    int ai =0;
    String dataDir = args[ai++];
    if (ai == args.length) {
      in = new BufferedReader(new InputStreamReader(System.in));
    }
    else {
      in = new BufferedReader(new FileReader(args[ai]));
    }
    Linker treebankLinker = new TreebankLinker(dataDir,LinkerMode.TEST);
    int sentenceNumber = 0;
    List document = new ArrayList();
    List parses = new ArrayList();
    for (String line=in.readLine();null != line;line = in.readLine()) {
      if (line.equals("")) {
        DiscourseEntity[] entities = treebankLinker.getEntities((Extent[]) document.toArray(new Extent[document.size()]));
        //showEntities(entities);
        new CorefParse(parses,entities).show();
        sentenceNumber=0;
        document.clear();
        parses.clear();
      }
      else {
        Parse p = Parse.parseParse(line);
        parses.add(p);
        Extent[] extents = treebankLinker.getMentions(new DefaultParse(p,sentenceNumber));
        //construct new parses for mentions which don't have constituents.
        for (int ei=0,en=extents.length;ei<en;ei++) {
          //System.err.println("PennTreebankLiner.main: "+ei+" "+extents[ei]);
          
          if (extents[ei].getParse() == null) {
            Parse snp = new Parse(p.getText(),extents[ei].getSpan(),"NML",1.0);
            p.insert(snp);
            extents[ei].setParse(new DefaultParse(snp,sentenceNumber));
          }
          
        }
        document.addAll(Arrays.asList(extents));
        sentenceNumber++;
      }
    }
    if (document.size() > 0) {
      DiscourseEntity[] entities = treebankLinker.getEntities((Extent[]) document.toArray(new Extent[document.size()]));
      //showEntities(entities);
      (new CorefParse(parses,entities)).show();
    }
  }
}

class CorefParse {
  
  private Map parseMap;
  private List parses;
  
  public CorefParse(List parses, DiscourseEntity[] entities) {
    this.parses = parses;
    parseMap = new HashMap();
    for (int ei=0,en=entities.length;ei<en;ei++) {
      if (entities[ei].getNumExtents() > 1) {
        for (Iterator mi=entities[ei].getExtents();mi.hasNext();) {
          MentionContext mc = (MentionContext) mi.next();
          Parse mentionParse = ((DefaultParse) mc.getParse()).getParse();
          parseMap.put(mentionParse,new Integer(ei+1));
          //System.err.println("CorefParse: "+mc.getParse().hashCode()+" -> "+ (ei+1));
        }
      }
    }
  }
  
  public void show() {
    for (int pi=0,pn=parses.size();pi<pn;pi++) {
      Parse p = (Parse) parses.get(pi);
      show(p);
      System.out.println();
    }
  }
  
  private void show(Parse p) {
    int start;
    start = p.getSpan().getStart();
    if (!p.getType().equals(ParserME.TOK_NODE)) {
      System.out.print("(");
      System.out.print(p.getType());
      if (parseMap.containsKey(p)) {
        System.out.print("#"+parseMap.get(p));
      }
      //System.out.print(p.hashCode()+"-"+parseMap.containsKey(p));
      System.out.print(" ");
    }
    Parse[] children = p.getChildren();
    for (int pi=0,pn=children.length;pi<pn;pi++) {
      Parse c = children[pi];
      Span s = c.getSpan();
      if (start < s.getStart()) {
        System.out.print(p.getText().substring(start, s.getStart()));
      }
      show(c);
      start = s.getEnd();
    }
    System.out.print(p.getText().substring(start, p.getSpan().getEnd()));
    if (!p.getType().equals(ParserME.TOK_NODE)) {
      System.out.print(")");
    }
  }
}