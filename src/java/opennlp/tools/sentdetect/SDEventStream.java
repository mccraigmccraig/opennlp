///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2002 Jason Baldridge and Gann Bierner
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
package opennlp.tools.sentdetect;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import opennlp.maxent.DataStream;
import opennlp.maxent.Event;
import opennlp.maxent.EventStream;
import opennlp.maxent.PlainTextByLineDataStream;

/**
 * An implementation of EventStream which assumes that it is receiving
 * its data as one (valid) sentence per token.  The default DataStream
 * to use with this class is PlainTextByLineDataStream, but you can
 * provide other types of DataStreams if you wish to receive data from
 * sources other than plain text files; however, be sure that each
 * token your DataStream returns is a valid sentence.
 *
 * @author      Jason Baldridge
 * @author      Eric D. Friedman
 * @author      Thomas Morton
 * @version     $Revision: 1.7 $, $Date: 2008/03/05 16:45:13 $
 */
public class SDEventStream implements EventStream {
    private DataStream data;
    private String next;
    private SDEvent head = null, tail = null;
    private SDContextGenerator cg;
    private StringBuffer sBuffer = new StringBuffer();
    private EndOfSentenceScanner scanner;

    /**
     * Creates a new <code>SDEventStream</code> instance.  A
     * DefaultEndOfSentenceScanner is used to locate sentence endings.
     *
     * @param d a <code>DataStream</code> value
     */
    public SDEventStream(DataStream d) {
      this(d,new opennlp.tools.lang.english.EndOfSentenceScanner(), new DefaultSDContextGenerator(opennlp.tools.lang.english.EndOfSentenceScanner.eosCharacters));
    }
    
    /**
     * Class constructor which uses the EndOfSentenceScanner to locate
     * sentence endings.
     * 
     * @param d 
     * @param s 
     */
    public SDEventStream (DataStream d, EndOfSentenceScanner s) {
      this(d,s,new DefaultSDContextGenerator(s.getEndOfSentenceCharacters()));
    }

    /**
     * Initializes the current instance.
     * 
     * @param d
     * @param s
     * @param cg
     */
    public SDEventStream(DataStream d, EndOfSentenceScanner s, SDContextGenerator cg) {
        data = d;
        scanner = s;
        this.cg = cg;
        if (data.hasNext()) {
          String current = (String) data.nextToken();
          if (data.hasNext()) {
            next = (String)data.nextToken();
          }
          addNewEvents(current);
        } 
    }

    public Event nextEvent () {
        SDEvent top = head;
        head = head.next;
        if (null == head) {
            tail = null;
        }
        return top;
    }

    private void addNewEvents (String s) {
        StringBuffer sb = sBuffer;
        sb.append(s.trim());        
        //add following word to sb
        if (!s.equals("")) {
          if(next !=null) { 
            int posAfterFirstWordInNext = next.indexOf(" ");
            if (posAfterFirstWordInNext != -1) {
                // should maybe changes this so that it usually adds a space
                // before the next sentence, but sometimes leaves no space.
                sb.append(" ");
                sb.append(next.substring(0, posAfterFirstWordInNext));
            }
            else {
              sb.append(" ");
              sb.append(next);
            }
          }
          else {
            sb.append(" ");
          }
        }
        //TODO: Should only send sentence string to scanner, and sentence + next word to context generator.
        for (Iterator i = scanner.getPositions(sb).iterator();i.hasNext();) {
            Integer candidate = (Integer)i.next();
            String type = i.hasNext() ? "F" : "T";
            SDEvent evt = new SDEvent(type,cg.getContext(sb,candidate.intValue()));

            if (null != tail) {
                tail.next = evt;
                tail = evt;
            } else if (null == head) {
                head = evt;
            } else if (null == head.next) {
                head.next = tail = evt;
            }
        }
        
        sb.setLength(0);
    }
    
    public boolean hasNext () {
        if (null != head) {
            return true;
        }

        while (null == head && next != null) {
          String current = next;
          if (data.hasNext()) next = (String)data.nextToken();
          else next = null;
          addNewEvents(current);
        }
        return null != head;
    }
    
    private static void usage() {
      System.err.println("SDEventStream [-encoding charset] [-lang (english|spanish|thai)] < trainingData");
      System.exit(1);
    }

    /**
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
      int ai=0;
      String encoding = "US-ASCII";
      String lang = "english";
      while (ai <args.length && args[ai].startsWith("-")) {
        if (args[ai].equals("-encoding")) {
          ai++;
          if (ai < args.length) {
            encoding = args[ai];
            ai++;
          }
          else {
            usage();
          }
        }
        else if (args[ai].equals("-lang")) {
          ai++;
          if (ai < args.length) {
            lang = args[ai];
            ai++;
          }
          else {
            usage();
          }
        }
        else {
          usage();
        }
      }
      EndOfSentenceScanner scanner = null;
      SDContextGenerator cg = null;
      if (lang == null || lang.equals("english") || lang.equals("spanish")) {
        scanner = new opennlp.tools.lang.english.EndOfSentenceScanner();
        cg = new DefaultSDContextGenerator(scanner.getEndOfSentenceCharacters());
      }
      else if (lang.equals("thai")) {
        scanner = new opennlp.tools.lang.thai.EndOfSentenceScanner();
        cg = new opennlp.tools.lang.thai.SentenceContextGenerator();
      }
      else {
        usage();
      }
      EventStream es =  new SDEventStream(new PlainTextByLineDataStream(new InputStreamReader(System.in,encoding)),scanner,cg);
      while(es.hasNext()) {
        System.out.println(es.nextEvent());
      }
    }
 
}
