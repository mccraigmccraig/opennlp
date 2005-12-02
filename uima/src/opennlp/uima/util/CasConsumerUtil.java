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

package opennlp.uima.util;

import com.ibm.uima.UimaContext;
import com.ibm.uima.cas.Type;
import com.ibm.uima.cas.TypeSystem;
import com.ibm.uima.resource.ResourceInitializationException;

/**
 * TODO: add javadoc comment
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.1 $, $Date: 2005/12/02 16:01:51 $
 */
public final class CasConsumerUtil {
  
  private CasConsumerUtil(){
  }
  
  public static Type getType(TypeSystem typeSystem, String name)
      throws ResourceInitializationException {
    Type type = typeSystem.getType(name);
    
    if (type == null) {
      throw new ResourceInitializationException(
          ResourceInitializationException.INCOMPATIBLE_DATA_TYPE,
          new Object[] { "Unable to retrive " + name + " type!" });
    }
    
    return type;
  }
  
  /**
   * TODO: add javadoc comment
   * @param context
   * @param parameter
   * @return
   * @throws ResourceInitializationException
   */
  public static String getRequiredParameter(UimaContext context,
      String parameter) throws ResourceInitializationException {
    String value = (String) context.getConfigParameterValue(parameter);

    if (value == null) {
      throw new ResourceInitializationException(
          ResourceInitializationException.STANDARD_MESSAGE_CATALOG,
          new Object[] { "The " + parameter + " is a " + "requiered parameter!" });
    }

    return value;
  }
}
