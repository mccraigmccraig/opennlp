///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2003 Jeremy LaCivita
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/** 
 * Class which creates mapping between keys and a list of values.  
 */
public class HashList<K, V> extends HashMap<K, List<V>> {

  private static final long serialVersionUID = 1;
  
  public HashList() {
  }
  
  public V get(K key, int index) {
    if (get(key) != null) {
      return get(key).get(index);
    }
    else {
      return null;
    }
  }

  public Object putAll(K key, Collection<V> values) {
    List<V> o = get(key);

    if (o == null) {
      o = new ArrayList<V>();
      super.put(key, o);
    }

    o.addAll(values);

    if (o.size() == values.size())
      return null;
    else
      return o;
  }

  public List<V> put(K key, V value) {
    List<V> o = get(key);
    
    if (o == null) {
      o = new ArrayList<V>();
      super.put(key, o);
    } 

    o.add(value);

    if(o.size() == 1)
      return null;
    else
      return o;
  }

  public boolean remove(K key, V value) {
    List<V> l = get(key);
    if (l == null) {
      return false;
    }
    else {
      boolean r = l.remove(value);
      if (l.size() == 0) {
	remove(key);
      }
      return r;
    }
  }
}