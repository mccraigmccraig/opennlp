///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2003 Tom Morton
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

package opennlp.tools.tokenize;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import opennlp.maxent.Event;
import opennlp.tools.util.AbstractEventStream;
import opennlp.tools.util.Span;

/**
 * This class reads the {@link TokenSample}s from the given {@link Iterator}
 * and converts the {@link TokenSample}s into {@link Event}s which
 * can be used by the maxent library for training.
 */
public class TokSpanEventStream extends AbstractEventStream<TokenSample> {

  private static Logger logger = Logger.getLogger(TokSpanEventStream.class.getName());
  
  private TokenContextGenerator cg;
  
  private boolean skipAlphaNumerics;

  /**
   * Initializes the current instance.
   * 
   * @param tokenSamples
   * @param skipAlphaNumerics
   * @param cg
   */
  public TokSpanEventStream(Iterator<TokenSample> tokenSamples, 
        boolean skipAlphaNumerics, TokenContextGenerator cg) {
    super(tokenSamples);
    
    this.skipAlphaNumerics = skipAlphaNumerics;
    this.cg = cg;
  }
  
  /**
   * Initializes the current instance.
   * 
   * @param tokenSamples
   * @param skipAlphaNumerics
   */
  public TokSpanEventStream(Iterator<TokenSample> tokenSamples, 
      boolean skipAlphaNumerics) {
    this(tokenSamples, skipAlphaNumerics, new DefaultTokenContextGenerator());
  }

  /**
   * Adds training events to the event stream for each of the specified tokens.
   * 
   * @param tokens character offsets into the specified text.
   * @param text The text of the tokens.
   */
  protected Iterator<Event> createEvents(TokenSample tokenSample) {

    List<Event> events = new ArrayList<Event>(50);
    
    Span tokens[] = tokenSample.getTokenSpans();
    String text = tokenSample.getText();
    
    if (tokens.length > 0) {
      
      int start = tokens[0].getStart();
      int end = tokens[tokens.length - 1].getEnd();
      
      String sent = text.substring(start, end);
      
      Span[] candTokens = WhitespaceTokenizer.INSTANCE.tokenizePos(sent);
      
      int firstTrainingToken = -1;
      int lastTrainingToken = -1;
      for (int ci = 0; ci < candTokens.length; ci++) {
        Span cSpan = candTokens[ci];
        String ctok = sent.substring(cSpan.getStart(), cSpan.getEnd());
        //adjust cSpan to text offsets
        cSpan = new Span(cSpan.getStart() + start, cSpan.getEnd() + start);
        //should we skip this token
        if (ctok.length() > 1
          && (!skipAlphaNumerics || !TokenizerME.alphaNumeric.matcher(ctok).matches())) {

          //find offsets of annotated tokens inside of candidate tokens
          boolean foundTrainingTokens = false;
          for (int ti = lastTrainingToken + 1; ti < tokens.length; ti++) {
            if (cSpan.contains(tokens[ti])) {
              if (!foundTrainingTokens) {
                firstTrainingToken = ti;
                foundTrainingTokens = true;
              }
              lastTrainingToken = ti;
            }
            else if (cSpan.getEnd() < tokens[ti].getEnd()) {
              break;
            }
            else if (tokens[ti].getEnd() < cSpan.getStart()) {
              //keep looking
            }
            else {
              System.err.println();
              if (logger.isLoggable(Level.WARNING)) {
                logger.warning("Bad training token: " + tokens[ti] + " cand: " + cSpan + 
                    " token="+text.substring(tokens[ti].getStart(), tokens[ti].getEnd()));
              }
            }
          }
          
          // create training data
          if (foundTrainingTokens) {
            
            for (int ti = firstTrainingToken; ti <= lastTrainingToken; ti++) {
              Span tSpan = tokens[ti];
              int cStart = cSpan.getStart();
              for (int i = tSpan.getStart() + 1; i < tSpan.getEnd(); i++) {
                String[] context = cg.getContext(ctok, i - cStart);
                events.add(new Event(TokenizerME.NO_SPLIT, context));
              }
              
              if (tSpan.getEnd() != cSpan.getEnd()) {
                String[] context = cg.getContext(ctok, tSpan.getEnd() - cStart);
                events.add(new Event(TokenizerME.SPLIT, context));
              }
            }
          }
        }
      }
    }
    
    return events.iterator();
  }
}