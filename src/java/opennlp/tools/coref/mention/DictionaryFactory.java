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
package opennlp.tools.coref.mention;

import java.io.IOException;


import net.didion.jwnl.JWNLException;

/** Factory class used to get an instance of a dictionary object. 
 * @see opennlp.tools.coref.mention.Dictionary
 * */
public class DictionaryFactory {
  
  private static Dictionary dictionary;
  
  /** 
   * Returns the default implementation of the Dictionary interface.
   * @return the default implementation of the Dictionary interface.
   */
  public static Dictionary getDictionary() {
    if (dictionary == null) {
      try {
        dictionary = new JWNLDictionary(System.getProperty("WNSEARCHDIR"));
      }
      catch(IOException e) {
        System.err.println(e);
      }
      catch(JWNLException e) {
        System.err.println(e);
      }
    }
    return dictionary;
  }
}
