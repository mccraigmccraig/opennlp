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

package opennlp.tools.doccat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import opennlp.maxent.ContextGenerator;
import opennlp.maxent.Event;
import opennlp.maxent.EventStream;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;

/**
*
* @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
* @version $Revision: 1.1 $, $Date: 2006/11/08 18:43:25 $
*/
public class DocumentCategorizerEventStream implements EventStream {
  
  private ContextGenerator mContextGenerator;
  private Collection mEvents = new ArrayList(); // TODO: intialize it ?
  
  private Iterator mIterator;
  
  public DocumentCategorizerEventStream() {
	  this(new FeatureGenerator[]{new BagOfWordsFeatureGenerator()});
  }
  
  DocumentCategorizerEventStream(FeatureGenerator featureGenerators[]) {
	  mContextGenerator = 
		  new DocumentCategorizerContextGenerator(featureGenerators);
  }
  
  public void add(String category, String[] text) {    
	  mEvents.add(new Event(category, mContextGenerator.getContext(text)));
  }

  public void add(String category, String documentText) {
    Tokenizer tokenizer = new SimpleTokenizer();
    add(category, tokenizer.tokenize(documentText));
  }
  
  public boolean hasNext() {
	  
    if (mIterator == null) {
      mIterator = mEvents.iterator();
    }
    
    if (!mIterator.hasNext()) {
      
      mEvents = null;
      mIterator = null;
      return false;
    }
    
    return mIterator.hasNext();
  }

  public Event nextEvent() {
	  
	if (mIterator == null) {
	  mIterator = mEvents.iterator();
	}
	  
    return (Event) mIterator.next();
  }
}