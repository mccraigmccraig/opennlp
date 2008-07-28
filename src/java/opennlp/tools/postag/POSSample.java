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

import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ParseException;

public class POSSample {
  
  private String sentence[];
  
  private String tags[];
  
  public POSSample(String sentence[], String tags[]) {
    
    if (sentence.length != tags.length)
        throw new IllegalArgumentException(
        "There must be exactly one tag for each token!");
    
    this.sentence = sentence;
    this.tags = tags;
  }
  
  public String[] getSentence() {
    return sentence;
  }
  
  public String[] getTags() {
    return tags;
  }
  
  @Override
  public String toString() {
    
    StringBuilder result = new StringBuilder();
    
    for (int i = 0; i < getSentence().length; i++) {
      result.append(getSentence()[i]);
      result.append('_');
      result.append(getTags()[i]);
      result.append(' ');
    }
    
    if (result.length() > 0) {
      // get rid of last space
      result.setLength(result.length() - 1);
    }
    
    return result.toString();
  }
  
  public static POSSample parse(String sentenceString) throws ParseException {
    
    String tokenTags[] = WhitespaceTokenizer.INSTANCE.tokenize(sentenceString);
    
    String sentence[] = new String[tokenTags.length];
    String tags[] = new String[tokenTags.length];
    
    for (int i = 0; i < tokenTags.length; i++) {
      int split = tokenTags[i].lastIndexOf("_");
      
      if (split == -1) {
        throw new ParseException("Cannot find \"_\" inside token!");
      }
      
      sentence[i] = tokenTags[i].substring(0, split);
      tags[i] = tokenTags[i].substring(split+1);
    }
    
    return new POSSample(sentence, tags);
  }
}