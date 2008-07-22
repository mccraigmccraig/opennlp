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

/**
 * Calculates the arithmetic mean of values
 * added with the {@link #add(double)} method.
 */
public class Mean {
  
  /**
   * The sum of all added values.
   */
  private double sum;
  
  /**
   * The number of times a value was added.
   */
  private long count;
  
  /**
   * Adds a value to the arithmetic mean.
   * 
   * @param value the value which should be added
   * to the arithmetic mean.
   */
  public void add(double value) {
    sum += value;
    count++;
  }
  
  /**
   * Retrieves the mean of all values added with
   * {@link #add(double)} or 0 if there are zero added
   * values.
   */
  public double mean() {
    return count > 0 ? sum / count : 0;
  }
  
  /**
   * Retrieves the number of times a value 
   * was added to the mean.
   */
  public long count() {
    return count;
  }
  
  @Override
  public String toString() {
    return Double.toString(mean());
  }
}