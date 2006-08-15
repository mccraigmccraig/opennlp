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
//GNU General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
////////////////////////////////////////////////////////////////////////////// 

package opennlp.tools.ngram;

import junit.framework.TestCase;

/**
 * This class is a test for the {@link Ngram} class.
 * 
 *
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.1 $, $Date: 2006/08/15 21:16:00 $
 */
public class NgramTest extends TestCase{
  
  /**
   * Test the creation of one char grams.
   * 
   * @see Ngram#create(String, int)
   */
  public void test1GramCreation() {
    String text = "ababababab";
    
    Profile gram1 = Ngram.create("test", text, 1);
    
    assertTrue(gram1.numberOfGrams() == text.length());
  }
  
  /**
   * Test the creation of two char grams.
   * 
   * @see Ngram#create(String, int)
   */
  public void test2GramCreationWith2CharText() {
    
    String text = "ab";
    
    Profile ngram = Ngram.create("test", text, 2);
    
    assertTrue(ngram.getGram(text).getGramText().equals(text));
    assertTrue(ngram.numberOfGrams() == 1);
  }
  
  /**
   * Test the creation of two char grams.
   * 
   * @see Ngram#create(String, int)
   */
  public void test2GramCreation() {
    // 5x ab and 4x ba
    String text = "ababababab";
    
    Profile grams = Ngram.create("test", text, 2);

    assertTrue(grams.getGram("ab").getOccurenceCount() == 5);
    assertTrue(grams.getGram("ba").getOccurenceCount() == 4);
    assertTrue(grams.numberOfGrams() == 9);
  }
  
  /**
   * Test the creation of two and three char grams.
   * 
   * @see Ngram#create(String, int)
   */
  public void test2And3GramCreation() {
    String text = "abcabc"; 
    
    Profile ngrams = Ngram.create("test", text, 2, 3);
    
    // check if 2 grams are contained
    assertTrue(ngrams.getGram("ab").getOccurenceCount() == 2);
    assertTrue(ngrams.getGram("bc").getOccurenceCount() == 2);
    assertTrue(ngrams.getGram("ca").getOccurenceCount() == 1);
    
    // check if 3 grams are contained
    assertTrue(ngrams.getGram("abc").getOccurenceCount() == 2);
    assertTrue(ngrams.getGram("bca").getOccurenceCount() == 1);
    assertTrue(ngrams.getGram("cab").getOccurenceCount() == 1);
    
    assertTrue(ngrams.numberOfGrams() == 9);
  }
  
  /**
   * Test the equals method.
   * 
   * @see Ngram#equals(Object)
   */
  public void testEquals() {
    String text = "How are you ?"; 
    
    Profile ngram1 = Ngram.create("test", text, 2, 5);
    Profile ngram2 = Ngram.create("test", text, 2, 5);
    
    assertTrue(ngram1.getGram("How").equals(ngram2.getGram("How")));
  }
  
  /**
   * Test the hashCode method.
   * 
   * @see Ngram#hashCode()
   */
  public void testHashCode() {
    String text = "How are you ?"; 
    
    Profile ngram1 = Ngram.create("test", text, 2, 5);
    Profile ngram2 = Ngram.create("test", text, 2, 5);
    
    assertTrue(ngram1.getGram("How").hashCode() == 
        ngram2.getGram("How").hashCode());
  }
  
  /**
   * Tests if toString throws an exception.
   * 
   * @see Ngram#toString()
   */
  public void testToString() {
    Profile ngram = Ngram.create("test", "How are you ?", 2, 5);
    ngram.getGram("How").toString();
  }
}