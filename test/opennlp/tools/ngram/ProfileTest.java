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
 * This is a test for the class {@link Profile}.
 * 
 *
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.1 $, $Date: 2006/08/15 21:16:00 $
 */
public class ProfileTest extends TestCase {

  /**
   * Tests that cutoff works. 
   * 
   *@see Profile#cutoff(int)
   */
  public void testCutoff() {
    String text = "aaab";
    
    for (int i = 1; i < 3; i++) {
      Profile gram = Ngram.create("test", i, text, 1);
      assertTrue(gram.numberOfGrams() == 3);
    }
  }

  /**
   * Tests that merge works. 
   * 
   *@see Profile#merge(Profile[])
   */
  public void testMerge() {
    String text1 = "aaab";
    String text2 = "bbba";
    
    Profile ngram1 = Ngram.create("test", text1, 1);
    Profile ngram2 = Ngram.create("test", text2, 1);

    Profile ngram = ngram1.merge(ngram2);
    
    assertTrue(ngram.getGram("a").getOccurenceCount() == 4);
    assertTrue(ngram.getGram("a").getOccurenceCount() == 4);
  }
  
  /**
   * Tests that likihood works. 
   * 
   *@see Profile#likelihood(Profile)
   */
  public void testLikihoodTotalMatch() {
    String text = "How are you?";
    
    Profile ngrams = Ngram.create("test", text, 2,5);
    
    double liklihood = ngrams.likelihood(ngrams);
    
    assertTrue(equals(0.001f, liklihood, 1.0f));
  }
  
  /**
   * Tests that likihood works. 
   * 
   *@see Profile#likelihood(Profile)
   */
  public void testLikihoodHalfMatch() {
    String text1 = "ab11";
    String text2 = "ab22";
    
    Profile ngram1 = Ngram.create("test", text1, 1);
    Profile ngram2 = Ngram.create("test", text2, 1);
    
    assertTrue(equals(0.001f, ngram1.likelihood(ngram2), 0.5f));
  }
  
  /**
   * Tests that likihood works. 
   * 
   *@see Profile#likelihood(Profile)
   */
  public void testLikihoodNoMatch() {
    String text1 = "abcdacbdacbdabcd";
    String text2 = "efghefghefghefgh";
    
    Profile ngram1 = Ngram.create("test", text1, 2, 5);
    Profile ngram2 = Ngram.create("test", text2, 2, 5);
        
    assertTrue(equals(0.001f, ngram1.likelihood(ngram2), 0));
  }
  
  private static boolean equals(double deltaRange, double a, double b) {
    if (Math.abs(a -b) < deltaRange) {
      return true;
    }
    else {
      return false;
    }
  }
  
  /**
   * Tests that equals works. 
   * 
   *@see Profile#equals(Object)
   */
  public void testEquals() {
    Profile a = Ngram.create("test","abcde", 1, 5);
    Profile b = Ngram.create("test", "abcde", 1, 5);
    
    assertTrue(a.equals(a));
    assertTrue(a.equals(b));
  }
  
  /**
   * Tests that hashCode works. 
   * 
   *@see Profile#hashCode()
   */
  public void testHashCode() {
    Profile a = Ngram.create("test", "abcde", 1, 5);
    Profile b = Ngram.create("test", "abcde", 1, 5);
    
    assertTrue(a.hashCode() == b.hashCode());
  }
  
  /**
   * Tests that likihood works. 
   * 
   *@see Profile#likelihood(Profile)
   */
  public void testToString() {
    // should not throw an exception
    Ngram.create("test", "abcde", 1, 5).toString();
  }
}