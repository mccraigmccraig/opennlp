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

import java.util.Iterator;

import opennlp.maxent.ContextGenerator;
import opennlp.maxent.DataStream;
import opennlp.maxent.Event;
import opennlp.maxent.EventStream;
import opennlp.tools.util.Pair;

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
 * @version     $Revision: 1.2 $, $Date: 2004/01/26 14:15:50 $
 */
public class SDEventStream implements EventStream {
    private DataStream data;
    private String next;
    private SDEvent head = null, tail = null;
    private ContextGenerator cg;
    private StringBuffer sBuffer = new StringBuffer();
    private EndOfSentenceScanner scanner;

    /**
     * Creates a new <code>SDEventStream</code> instance.  A
     * DefaultEndOfSentenceScanner is used to locate sentence endings.
     *
     * @param d a <code>DataStream</code> value
     */
    public SDEventStream(DataStream d) {
        this(d,new DefaultEndOfSentenceScanner(), new SDContextGenerator());
    }
    
    /**
     * Class constructor which uses the EndOfSentenceScanner to locate
     * sentence endings.
     */
    public SDEventStream (DataStream d, EndOfSentenceScanner s) {
        this(d,s,new SDContextGenerator());
    }

    public SDEventStream(DataStream d, EndOfSentenceScanner s, ContextGenerator cg) {
        data = d;
        scanner = s;
        this.cg = cg;
        if (data.hasNext()) addNewEvents((String)data.nextToken());
        if (data.hasNext()) next = (String)data.nextToken();
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
        int sentEndPos = sb.length()-1;
        if(next !=null && !s.equals("")) {
            int posAfterFirstWordInNext = next.indexOf(" ");
            if (posAfterFirstWordInNext != -1) {
                // should maybe changes this so that it usually adds a space
                // before the next sentence, but sometimes leaves no space.
                sb.append(" ");
                sb.append(next.substring(0, posAfterFirstWordInNext));
            }
        }

        for (Iterator i = scanner.getPositions(sb).iterator();
             i.hasNext();) {
            Integer candidate = (Integer)i.next();
            Pair p = new Pair(sb, candidate);
            String type = (candidate.intValue() == sentEndPos) ? "T" : "F";
            SDEvent evt = new SDEvent(type,cg.getContext(p));

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
            addNewEvents(next);
            if (data.hasNext())	next = (String)data.nextToken();
            else next = null;
        }
        return (null != head);
    }
 
}
