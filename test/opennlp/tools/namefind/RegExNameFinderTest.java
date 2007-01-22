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

import java.util.regex.Pattern;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.1 $, $Date: 2007/01/22 06:52:44 $
 */
public class RegExNameFinderTest extends TestCase {
  
  public void testFindSingleTokenPattern() {
    Pattern testPattern = Pattern.compile("test");
    
    String sentence[] = new String[]{"a", "test", "b", "c"};
    
    RegExNameFinder finder = 
      new RegExNameFinder(new Pattern[]{testPattern});
    
    Annotation[] result = finder.find(sentence);
    
    assertTrue(result.length == 1);
    
    assertTrue(result[0].getBegin() == 1);
    assertTrue(result[0].getEnd() == 1);
  }
  
  public void testFindTokenizdPattern() {
    Pattern testPattern = Pattern.compile("[0-9]+ year");
    
    String sentence[] = new String[]{"a", "80", "year", "b", "c"};
    
    RegExNameFinder finder = 
      new RegExNameFinder(new Pattern[]{testPattern});
    
    Annotation[] result = finder.find(sentence);
    
    assertTrue(result.length == 1);
    
    assertTrue(result[0].getBegin() == 1);
    assertTrue(result[0].getEnd() == 2);
  }
  
  public void testFindMatchingPatternWithoutMatchingTokenBounds() {
    Pattern testPattern = Pattern.compile("[0-8] year"); // does match "0 year"
    
    String sentence[] = new String[]{"a", "80", "year", "c"};
    
    RegExNameFinder finder = 
      new RegExNameFinder(new Pattern[]{testPattern});
    
    Annotation[] result = finder.find(sentence);
    
    assertTrue(result.length == 0);
  }
}