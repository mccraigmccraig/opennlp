///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2005 Calcucare GmbH
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

package opennlp.tools.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;

import opennlp.maxent.DataStream;

/**
 * This is a DataStream of elements contained in a collection.
 *
 * @param <E> 
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.1 $, $Date: 2007/05/31 16:09:19 $
 */
public final class DataStreamBuilder implements DataStream {

  private final Collection mData;
  
  private Iterator mDataIterator;
  
  private boolean mIsIterating;
  
  /**
   * Initalizes a new instance.
   */
  public DataStreamBuilder() {
    mData = new LinkedList();
    
    mIsIterating = false;
  }
  
  /**
   * Initalizes a new instance.
   * @param object 
   */
  public DataStreamBuilder(Object object) {
    this();
    
    add(object);
  }

  /**
   * Initalizes a new instance.
   * @param array 
   */
  public DataStreamBuilder(Object[] array) {
    this();
    
    add(array);
  }
  
  /**
   * Initalizes a new instance.
   * @param data 
   */
  public DataStreamBuilder(Collection data) {
    this();
    
    add(data);
  }

  /**
   * Adds the given data object.
   * 
   * @param data
   */
  public void add(Object data) {
    checkIterating();
    
    mData.add(data);
  }
  
  /**
   * Adds the given array of data.
   *
   * @param data
   */
  public void add(Object[] data) {
    checkIterating();
    
    mData.addAll(Arrays.asList(data));
  }
  
  /**
   * Adds the given collection of data.
   * 
   * @param data
   */
  public void add(Collection data) {
    checkIterating();
    
    mData.addAll(data);
  }
  
  private void checkIterating() {
   if (mIsIterating) {
     throw new ConcurrentModificationException(
         "Do not modify, after iterating started!");
   }
  }
  
  /**
   * Retrives the next token.
   */
  public Object nextToken() {
    mIsIterating = true;
    
    if (mDataIterator == null) {
      mDataIterator = mData.iterator();
    }
    
    return mDataIterator.next();
  }

  /**
   * Checks if one more token is available.
   */
  public boolean hasNext() {
    mIsIterating = true;
    
    if (mDataIterator == null) {
      mDataIterator = mData.iterator();
    }

    return mDataIterator.hasNext();
  }
}