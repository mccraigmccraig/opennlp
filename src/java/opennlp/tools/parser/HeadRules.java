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

import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.io.*;

public class HeadRules {
  
  private Map headRules;
  
  public HeadRules(String ruleDir) throws IOException {
    readHeadRules(ruleDir);
  }
  
  public HeadRules(Map ruleMap) {
    headRules = ruleMap;
  }
  
  public Parse getHead(Parse[] cons, String type) {
    if (cons[0].getType() == ParserME.TOK_NODE) {
      return null;
    }
    HeadRule hr;
      if (type.equals("NP") || type.equals("NX")) {
        String[] tags1 = { "NN", "NNP", "NNPS", "NNS", "NX", "JJR", "POS" };
        for (int ci = cons.length - 1; ci >= 0; ci--) {
          for (int ti = tags1.length - 1; ti >= 0; ti--) {
            if (cons[ci].getType().equals(tags1[ti])) {
              return (cons[ci].getHead());
            }
          }
        }
        for (int ci = 0; ci < cons.length; ci++) {
          if (cons[ci].getType().equals("NP")) {
            return (cons[ci].getHead());
          }
        }
        String[] tags2 = { "$", "ADJP", "PRN" };
        for (int ci = cons.length - 1; ci >= 0; ci--) {
          for (int ti = tags2.length - 1; ti >= 0; ti--) {
            if (cons[ci].getType().equals(tags2[ti])) {
              return (cons[ci].getHead());
            }
          }
        }
        String[] tags3 = { "JJ", "JJS", "RB", "QP" };
        for (int ci = cons.length - 1; ci >= 0; ci--) {
          for (int ti = tags3.length - 1; ti >= 0; ti--) {
            if (cons[ci].getType().equals(tags3[ti])) {
              return (cons[ci].getHead());
            }
          }
        }
        return (cons[cons.length - 1].getHead());
      }
      else if ((hr = (HeadRule) headRules.get(type)) != null) {
        String[] tags = hr.tags;
        int cl = cons.length;
        int tl = tags.length;
        if (hr.leftToRight) {
          for (int ti = 0; ti < tl; ti++) {
            for (int ci = 0; ci < cl; ci++) {
              if (cons[ci].getType().equals(tags[ti])) {
                return (cons[ci].getHead());
              }
            }
          }
          return (cons[0].getHead());
        }
        else {
          for (int ti = 0; ti < tl; ti++) {
            for (int ci = cl - 1; ci >= 0; ci--) {
              if (cons[ci].getType().equals(tags[ti])) {
                return (cons[ci].getHead());
              }
            }
          }
          return (cons[cl - 1].getHead());
        }
      }
      return (cons[cons.length - 1].getHead());
    }
    
  private void readHeadRules(String file) throws IOException {
    BufferedReader str = new BufferedReader(new FileReader(file));
    String line;
    headRules = new HashMap(30);
    try {
      while ((line = str.readLine()) != null) {
        StringTokenizer st = new StringTokenizer(line);
        String num = st.nextToken();
        String type = st.nextToken();
        String dir = st.nextToken();
        String[] tags = new String[Integer.parseInt(num)];
        int ti = 0;
        while (st.hasMoreTokens()) {
          tags[ti] = st.nextToken();
          ti++;
        }
        headRules.put(type, new HeadRule(dir.equals("1"), tags));
      }
    }
    catch (IOException e) {
      System.err.println(e);
      throw (new RuntimeException("Can't read head rules from: " + file));
    }
  }


  private static class HeadRule {
    public boolean leftToRight;
    public String[] tags;
    public HeadRule(boolean l2r, String[] tags) {
      leftToRight = l2r;
      this.tags = tags;
    }
  }
}
