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

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.ngram.Token;
import opennlp.tools.ngram.TokenList;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import junit.framework.TestCase;

/**
  * 
  * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
  * @version $Revision: 1.3 $, $Date: 2007/01/22 06:52:44 $
  */
public class DictionaryNameFinderTetst extends TestCase {
  
  private Dictionary mDictionary = new Dictionary();
  private NameFinder mNameFinder;
  
  public DictionaryNameFinderTetst() {
    
    TokenList vanessa = new TokenList(new Token[]{Token.create("Vanessa")});
    mDictionary.put(vanessa);
    
    TokenList vanessaWilliams = new 
        TokenList(new Token[]{Token.create("Vanessa"), 
        Token.create("Williams")});
    mDictionary.put(vanessaWilliams);
    
    TokenList max = new TokenList(new Token[]{Token.create("Max")});
    mDictionary.put(max);
  }
  
  protected void setUp() throws Exception {
    mNameFinder = new DictionaryNameFinder(mDictionary);
  }
  
  public void testSingleTokeNameAtSentenceStart() {
    
    String sentence = "Max a b c d";
    
    SimpleTokenizer tokenizer = new SimpleTokenizer();
    String tokens[] = tokenizer.tokenize(sentence);
    
    String names[] = mNameFinder.find(tokens, null);
    
    assertTrue(names.length == 1);    
    assertTrue("Max".equals(names[0]));
  }

  public void testSingleTokeNameInsideSentence() {
    String sentence = "a b  Max c d";
    
    SimpleTokenizer tokenizer = new SimpleTokenizer();
    String tokens[] = tokenizer.tokenize(sentence);
    
    String names[] = mNameFinder.find(tokens, null);
    
    assertTrue(names.length == 1);    
    assertTrue("Max".equals(names[0]));

  }

  public void testSingleTokeNameAtSentenceEnd() {
    String sentence = "a b c Max";
    
    SimpleTokenizer tokenizer = new SimpleTokenizer();
    String tokens[] = tokenizer.tokenize(sentence);
    
    String names[] = mNameFinder.find(tokens, null);
    
    assertTrue(names.length == 1);    
    assertTrue("Max".equals(names[0]));
  }
  
  public void testLastMatchingTokenNameIsChoosen() {
    String sentence = "a b c Vanessa";
    
    SimpleTokenizer tokenizer = new SimpleTokenizer();
    Span tokens[] = tokenizer.tokenizePos(sentence);
    
    Span names[] = mNameFinder.find(sentence, tokens, null);
    
    assertTrue(names.length == 1);    
    assertTrue("Vanessa".equals(names[0].getCoveredText(sentence)));
  }
  
  public void testLongerTokenNameIsPreferred() {
    String sentence = "a b c Vanessa Williams";
    
    SimpleTokenizer tokenizer = new SimpleTokenizer();
    Span tokens[] = tokenizer.tokenizePos(sentence);
    
    Span names[] = mNameFinder.find(sentence, tokens, null);
    
    assertTrue(names.length == 1);    
    assertTrue("Vanessa Williams".equals(names[0].getCoveredText(sentence)));
  }
}