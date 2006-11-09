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

package opennlp.tools.ngram;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link Attributes} class stores name value pairs.
 * 
 * Problem: The HashMap for storing the name value pairs has a very high 
 * memory footprint, replace it.
 */
public class Attributes {
  
  // TODO: use something with lower memory footprint!
  private Map mNameValueMap = new HashMap();
  
  /**
   * Retrives the value for the given key or null if attribute it not set.
   * 
   * @param key
   * 
   * @return the value
   */
  public  String getValue(String key) {
    return (String) mNameValueMap.get(key);
  }
  
  /**
   * Sets a key/value pair.
   * 
   * @param key
   * @param value
   */
  public void setValue(String key, String value) {
    mNameValueMap.put(key, value);
  }
}