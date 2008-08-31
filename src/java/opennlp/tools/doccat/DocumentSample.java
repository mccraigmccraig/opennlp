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

import opennlp.tools.tokenize.SimpleTokenizer;

/**
 * Class which holds a classified document and its category. 
 */
public class DocumentSample {
  
  private String category;
  private String text[];
  
  public DocumentSample(String category, String text) {
    this(category, new SimpleTokenizer().tokenize(text));
  }
  
  public DocumentSample(String category, String text[]) {
    if (category == null || text == null) {
      throw new IllegalArgumentException();
    }
    
    this.category = category;
    this.text = text;
  }
  
  String getCategory() {
    return category;
  }
  
  String[] getText() {
    return text;
  }
}
