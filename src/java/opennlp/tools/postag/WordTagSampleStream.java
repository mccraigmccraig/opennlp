///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2008 OpenNlp
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

package opennlp.tools.postag;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import opennlp.tools.util.ParseException;

/**
 * The {@link WordTagSampleStream} reads a sentence which
 * contains tags in the word_tag format and outputs a {@link POSSample}
 * object.
 */
public class WordTagSampleStream implements Iterator<POSSample> {
  
  private static Logger logger = Logger.getLogger(WordTagSampleStream.class.getName());
  
  private Iterator<String> sentences;
  
  /**
   * Initializes the current instance.
   * 
   * @param sentences
   */
  public WordTagSampleStream(Iterator<String> sentences) {
    this.sentences = sentences;
  }
  
  public boolean hasNext() {
    return sentences.hasNext();
  }
  
  /**
   * Parses the next sentence and return the next 
   * {@link POSSample} object.
   * 
   * If an error occurs an empty {@link POSSample} object is returned
   * and an warning message is logged. Usually it does not matter if one 
   * of many sentences is ignored. 
   */
  public POSSample next() {
    
    String sentence = sentences.next();
    
    POSSample sample;
    try {
      sample = POSSample.parse(sentence);
    } catch (ParseException e) {
      
      if (logger.isLoggable(Level.WARNING)) {
        logger.warning("Error during parsing, ignoring sentence: " + sentence);
      }
      
      sample = new POSSample(new String[]{}, new String[]{});
    }
    
    return sample;
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
}