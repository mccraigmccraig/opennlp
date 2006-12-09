///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2006 Calcucare GmbH
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

package opennlp.tools.namefind;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.1 $, $Date: 2006/12/09 16:45:14 $
 */
public class RegExNameFinder {

  private Pattern mPatterns[];
  
  public RegExNameFinder(Pattern patterns[]) {
    if (patterns == null || patterns.length == 0) {
      throw new IllegalArgumentException("patterns must not be null or emtpy!");
    }
    
    mPatterns = patterns;
  }
  
  /**
   * @param sentence
   * @return
   */
  public Annotation[] find(String sentence[]) {
    
    Map sentencePosTokenMap = new HashMap();
    
    StringBuffer sentenceString = new StringBuffer(sentence.length *  10);
    
    for (int i = 0; i < sentence.length; i++) {
      
      int startIndex = sentenceString.length();
      sentencePosTokenMap.put(new Integer(startIndex), 
          new Integer(i));

      sentenceString.append(sentence[i]);
      
      int endIndex = sentenceString.length();
      sentencePosTokenMap.put(new Integer(endIndex), 
          new Integer(i));
      
      if (i < sentence.length - 1) {
        sentenceString.append(' ');
      }
    }
    
    Collection annotations = new LinkedList();
    
    for (int i = 0; i < mPatterns.length; i++) {
      Matcher matcher = mPatterns[i].matcher(sentenceString);
      
      while (matcher.find()) {
        Integer tokenStartIndex = 
            (Integer) sentencePosTokenMap.get(new Integer(matcher.start()));
        Integer tokenEndIndex = 
            (Integer) sentencePosTokenMap.get(new Integer(matcher.end()));
        
        if (tokenStartIndex != null && tokenEndIndex != null) {
          Annotation annotation = new Annotation(tokenStartIndex.intValue(), 
              tokenEndIndex.intValue());
          
          annotations.add(annotation);
        }
      }
    }
    
    return (Annotation[]) annotations.toArray(
        new Annotation[annotations.size()]);
  }
}