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

package opennlp.tools.sentdetect;

/**
 * A {@link SentenceSample} contains a document with
 * begin indexes of the individual sentences.
 */
public class SentenceSample {

  private String document;
  
  private int sentences[];
  
  /**
   * Initializes the current instance.
   * 
   * @param sentences
   * @param sentenceSpans
   */
  public SentenceSample(String document, int sentences[]) {
    this.document = document;
    this.sentences = sentences;
  }
  
  /**
   * Retrieves the document.
   * 
   * @return
   */
  public String getDocument() {
    return document;
  }
  
  /**
   * Retrieves the sentences.
   * 
   * @return the begin indexes of the sentences 
   * in the document.
   */
  public int[] getSentences() {
    return sentences;
  }
}