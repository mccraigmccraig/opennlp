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
package opennlp.tools.coref.sim;

/**
 * Enumeration of number types. 
 */
public class NumberEnum {
  
  private String name;
  /** Singular number type. */
  public static final NumberEnum SINGULAR = new NumberEnum("singular");
  /** Plural number type. */
  public static final NumberEnum PLURAL = new NumberEnum("plural");
  /** Unknown number type. */
  public static final NumberEnum UNKNOWN = new NumberEnum("unknown");
  
  private NumberEnum(String name) {
    this.name = name;
  }
  
  public String toString(){
    return name;
  }

}
