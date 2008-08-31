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

package opennlp.tools.doccat;

import opennlp.maxent.DataStream;
import opennlp.maxent.Event;
import opennlp.maxent.EventStream;

/**
* Iterator-like class for modeling document classification events.
*/
public class DocumentCategorizerEventStream implements EventStream {
  
  private DocumentCategorizerContextGenerator mContextGenerator;
  
  private DataStream data;
  
  /**
   * Initializes the current instance.
   * 
   * @param data {@link opennlp.maxent.DataStream} of {@link DocumentSample}s
   * 
   * @param featureGenerators
   */
  public DocumentCategorizerEventStream(DataStream data, FeatureGenerator featureGenerators[]) {
    
    this.data = data;
    
    mContextGenerator = 
      new DocumentCategorizerContextGenerator(featureGenerators);
  }
  
  /**
   * Initializes the current instance.
   * 
   * @param data {@link DataStream} of {@link DocumentSample}s
   */
  public DocumentCategorizerEventStream(DataStream data) {
	  this(data, new FeatureGenerator[]{new BagOfWordsFeatureGenerator()});
  }
  
  public boolean hasNext() {
    return data.hasNext();
  }

  public Event nextEvent() {
	  
    DocumentSample sample = (DocumentSample) data.nextToken();
    
    return new Event(sample.getCategory(), 
        mContextGenerator.getContext(sample.getText()));
  }
}