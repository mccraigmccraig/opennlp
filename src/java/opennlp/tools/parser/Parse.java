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

import opennlp.tools.util.Span;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import java.text.DecimalFormat;

/** Datastructure for holding parse constitents. */
public class Parse implements Cloneable, Comparable {
  private String text;
  private Span span;
  private String type;
  private ArrayList parts;
  private Parse head;
  private String label;
  private Parse parent;

  private double prob;
  private StringBuffer derivation;

  private static Pattern typePattern = Pattern.compile("^([^ =-]+)");
  private static Pattern tokenPattern = Pattern.compile("^[^ ()]+ ([^ ()]+)\\s*\\)");

  protected Object clone() {
    try {
      Parse p = (Parse) super.clone();
      p.parts = (ArrayList) this.parts.clone();
      if (derivation != null) {
        p.derivation = new StringBuffer(100);
        p.derivation.append(derivation.toString());
      }
      return (p);
    }
    catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
  }
  

  
  public Parse(String text, Span span, String type, double p) {
    this.text = text;
    this.span = span;
    this.type = type;
    this.prob = p;
    this.head = this;
    this.parts = new ArrayList();
    this.label = null;
    this.parent = null;
  }

  public Parse(String text, Span span, String type, double p, Parse h) {
    this(text, span, type, p);
    this.head = h;
  }

  /**
   * Set the type of this constituent to the specified type.
   * @param type The type of this constituent.
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Returns the constituent label for this node of the parse.
   * @return The constituent label for this node of the parse.
   */
  public String getType() {
    return type;
  }

  /**
   * Inserts the specified constituent into this parse based on its text span.  This
   * method assumes that the sepecifed constituent can be inserted into this parse.
   * @param constituent The constituent to be inserted.
   */
  public void insert(Parse constituent) {
    Span ic = constituent.span;
    if (span.contains(ic)) {
      //double oprob=c.prob;
      int pi=0;
      int pn = parts.size();
      for (; pi < pn; pi++) {
        Parse subPart = (Parse) parts.get(pi);
        Span sp = subPart.span;
        if (sp.getStart() >= ic.getEnd()) {
          break;
        }
        // c Contains subPart
        else if (ic.contains(sp)) {
          parts.remove(pi);
          pi--;
          constituent.parts.add(subPart);
          subPart.setParent(constituent);
          pn = parts.size();
        }
      }
      parts.add(pi, constituent);
      constituent.setParent(this);
      //prob*=oprob;
    }
    else {
      throw (new InternalError("Inserting constituent not contained in the sentence!"));
    }
  }

  /**
   * Displays this parse using Penn Treebank-style formatting. 
   */
  public void show() {
    int start;
    start = span.getStart();
    if (!type.equals(ParserME.TOK_NODE)) {
      System.out.print("(");
      System.out.print(type +" ");
      //System.out.print(label+" ");

      //System.out.print(head+" ");
      //System.out.print(df.format(prob)+" ");
    }
    for (Iterator i = parts.iterator(); i.hasNext();) {
      Parse c = (Parse) i.next();
      Span s = c.span;
      if (start < s.getStart()) {
        //System.out.println("pre "+start+" "+s.getStart());
        System.out.print(text.substring(start, s.getStart()));
      }
      c.show();
      start = s.getEnd();
    }
    System.out.print(text.substring(start, span.getEnd()));
    if (!type.equals(ParserME.TOK_NODE)) {
      System.out.print(")");
    }
  }

  /**
   * Returns the probability associed with the pos-tag sequence assigned to this parse.
   * @return The probability associed with the pos-tag sequence assigned to this parse.
   */
  public double getTagSequenceProb() {
    //System.err.println("Parse.getTagSequenceProb: "+type+" "+this);
    if (parts.size() == 1 && ((Parse) parts.get(0)).type.equals(ParserME.TOK_NODE)) {
      //System.err.println(this+" "+prob);
      return (Math.log(prob));
    }
    else if (parts.size() == 0) {
      System.err.println("Parse.getTagSequenceProb: Wrong base case!");
      return (0.0);
    }
    else {
      double sum = 0.0;
      for (Iterator pi = parts.iterator(); pi.hasNext();) {
        sum += ((Parse) pi.next()).getTagSequenceProb();
      }
      return (sum);
    }
  }

  /** 
   * Returns whether this parse is complete.
   * @return Returns true if the parse contains a single top-most node.
   */
  public boolean complete() {
    return (parts.size() == 1);
  }

  public String toString() {
    return (text.substring(span.getStart(), span.getEnd()));
  }

  /**
   * Returns the text of the sentence over which this parse was formed. 
   * @return The text of the sentence over which this parse was formed.
   */
  public String getText() {
    return text;
  }

  /**
   * Returns the character offsets for this constituent.
   * @return The character offsets for this constituent.
   */
  public Span getSpan() {
    return span;
  }

  /**
   * Returns the log of the product of the probability associated with all the decisions which formed this constituent.
   * @return The log of the product of the probability associated with all the decisions which formed this constituent.
   */
  public double getProb() {
    return prob;
  }
  
  /**
   * Adds the specified probability log to this current log for this parse.
   * @param logProb The probaility of an action performed on this parse.
   */
  public void addProb(double logProb) {
    this.prob+=logProb;
  }

  /**
   * Returns the child constituents of this constiuent. 
   * @return The child constituents of this constiuent.
   */
  public List getChildren() {
    return parts;
  }

  /** Returns the head constituent associated with this constituent.
   * @return The head constituent associated with this constituent.
   */
  public Parse getHead() {
    return head;
  }

  /**
   * Returns the label assigned to this parse node during parsing 
   * which specifies how this node will be formed into a constituent.
   * @return The outcome label assigned to this node during parsing.  
   */
  public String getLabel() {
    return label;
  }

  /**
   * Assigns this parse the specified label.
   * @param label A label indicating the constituent this parse node will become part of.
   */
  public void setLabel(String label) {
    this.label = label;
  }

  
  private static String getType(String rest) {
    if (rest.startsWith("-LCB-")) {
      return "-LCB-";
    }
    else if (rest.startsWith("-RCB-")) {
      return "-RCB-";
    }
    else if (rest.startsWith("-LRB-")) {
      return "-LRB-";
    }
    else if (rest.startsWith("-RRB-")) {
      return "-RRB-";
    }
    else {
      Matcher typeMatcher = typePattern.matcher(rest);
      if (typeMatcher.find()) {
        return typeMatcher.group(1);
      }
    }
    return null;
  }

  private static String getToken(String rest) {
    Matcher tokenMatcher = tokenPattern.matcher(rest);
    if (tokenMatcher.find()) {
      return tokenMatcher.group(1);
    }
    return null;
  }

  public static Parse parseParse(String parse, HeadRules rules) {
    StringBuffer text = new StringBuffer();
    int offset = 0;
    Stack stack = new Stack();
    List cons = new ArrayList();
    for (int ci = 0, cl = parse.length(); ci < cl; ci++) {
      char c = parse.charAt(ci);
      if (c == '(') {
        String rest = parse.substring(ci + 1);
        String type = getType(rest);
        if (type == null) {
          System.err.println("null type for: " + rest);
        }
        String token = getToken(rest);
        stack.push(new Object[] { type, new Integer(offset)});
        if (token != null && !type.equals("-NONE-")) {
          cons.add(new Object[] { ParserME.TOK_NODE, new Span(offset, offset + token.length())});
          text.append(token).append(" ");
          offset += token.length() + 1;
        }
      }
      else if (c == ')') {
        Object[] parts = (Object[]) stack.pop();
        String type = (String) parts[0];
        if (!type.equals("-NONE-")) {
          int start = ((Integer) parts[1]).intValue();
          cons.add(new Object[] { parts[0], new Span(start, offset - 1)});
        }
      }
    }
    String txt = text.toString();
    Parse p = new Parse(txt, new Span(0, txt.length()), ParserME.TOP_NODE, 1);
    for (int ci = 0, cl = cons.size(); ci < cl; ci++) {
      Object[] parts = (Object[]) cons.get(ci);
      String type = (String) parts[0];
      Parse con = new Parse(txt, (Span) parts[1], type, 1);
      p.insert(con);
      if (con.parts != null && con.parts.size() != 0) {
        Parse head = rules.getHead((Parse[]) con.getChildren().toArray(new Parse[con.parts.size()]), type);
        if (head == null) {
          con.head = con;
        }
        else {
          con.head = head;
        }
      }
    }
    return p;
  }

  /**
   * Returns the parent parse node of this constituent.
   * @return The parent parse node of this constituent.
   */
  public Parse getParent() {
    return parent;
  }

  /**
   * Specifies the parent parse node for this constituent.
   * @param parent The parent parse node for this constituent.
   */
  public void setParent(Parse parent) {
    this.parent = parent;
  }

  /**
   * Indicates wether this parse node is a pos-tag.
   * @return true if this node is a pos-tag, false otherwise.
   */
  public boolean isPosTag() {
    return (parts.size() == 1 && ((Parse) parts.get(0)).getType().equals(ParserME.TOK_NODE));
  }

  public int compareTo(Object o) {
    Parse p = (Parse) o;
    if (this.getProb() > p.getProb()) {
      return -1;
    }
    else if (this.getProb() < p.getProb()) {
      return 1;
    }
    return 0;
  }
  
  /**
   * Returns the derivation string for this parse if one has been created.
   * @return the derivation string for this parse or null if no derivation string has been created.
   */
  public StringBuffer getDerivation() {
    return derivation;
  }
  
  /**
   * Specifies the derivation string to be associated with this parse.
   * @param derivation The derivation string to be associated with this parse.
   */
  public void setDerivation(StringBuffer derivation) {
    this.derivation = derivation;
  }

  public static void main(String[] args) throws java.io.IOException {
    if (args.length == 0) {
      System.err.println("Usage: ParserME head_rules < train_parses");
      System.exit(1);
    }
    EnglishHeadRules rules = new EnglishHeadRules(args[0]);
    java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
    for (String line = in.readLine(); line != null; line = in.readLine()) {
      Parse p = Parse.parseParse(line, rules);
      p.show();
      System.out.println();
    }
  }

}
