///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2003 Thomas Morton
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

import java.util.Iterator;
import java.util.List;

public class ReverseListIterator implements Iterator {
  
  int index;
  List list;

  public ReverseListIterator(List list) {
    index = list.size()-1;
    this.list=list;
  }

  public Object next() {
    return(list.get(index--));
  }

  public boolean hasNext() {
    return(index >=0);
  }

  public void remove() {
    throw(new UnsupportedOperationException());
  }

}
