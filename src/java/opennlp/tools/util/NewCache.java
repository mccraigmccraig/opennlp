///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2007 OpenNlp
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
package opennlp.tools.util;

import java.util.Map;
import java.util.WeakHashMap;

public class NewCache {

  private class KeyWrapper {
    private final Object key;
    
    KeyWrapper(Object key) {
      if (key == null)
        throw new IllegalArgumentException();
      
      this.key = key;
    }
    
    public boolean equals(Object obj) {
      
      if (obj == null) 
        return false;
      
      KeyWrapper wrapper = (KeyWrapper) obj;
      
      return key.equals(wrapper.key);
    }
    
    public int hashCode() {
      return key.hashCode();
    }
  }
  
  private Map map = new WeakHashMap();
  
  public Object get(Object key) {
    return map.get(new KeyWrapper(key));
  }
  
  public void put(Object key, Object value) {
    map.put(new KeyWrapper(key), value);
  }
  
  public void clear() {
    map.clear();
  }
}