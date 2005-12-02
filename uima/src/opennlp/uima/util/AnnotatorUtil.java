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

import java.io.InputStream;

import com.ibm.uima.analysis_engine.annotator.AnnotatorConfigurationException;
import com.ibm.uima.analysis_engine.annotator.AnnotatorContext;
import com.ibm.uima.analysis_engine.annotator.AnnotatorContextException;
import com.ibm.uima.cas.Type;
import com.ibm.uima.cas.TypeSystem;

/**
 * TODO: add javadoc comment
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.1 $, $Date: 2005/12/02 16:01:51 $
 */
public final class AnnotatorUtil {
  
  private AnnotatorUtil(){
  }
  
  public static Type getType(TypeSystem typeSystem, String name)
      throws AnnotatorConfigurationException
  {
    Type type = typeSystem.getType(name);
    
    if (type == null) {
      throw new AnnotatorConfigurationException(
          AnnotatorConfigurationException.STANDARD_MESSAGE_CATALOG,
          new Object[] { "Unable to retrive " + name + " type!" });
    }
    
    return type;
  }
  
  /**
   * 
   * @param context
   * @param parameter
   * @return
   * @throws AnnotatorConfigurationException
   */
  public static String getRequiredParameter(AnnotatorContext context,
      String parameter) throws AnnotatorConfigurationException {
    String value;

    try {
      value = (String) context.getConfigParameterValue(parameter);
    } catch (AnnotatorContextException e) {
      throw new AnnotatorConfigurationException(
          AnnotatorConfigurationException.STANDARD_MESSAGE_CATALOG,
          new Object[] { "There is an internal error in the UIMA SDK." });
    }

    if (value == null) {
      throw new AnnotatorConfigurationException(
          AnnotatorConfigurationException.ONE_PARAM_REQUIRED,
          new Object[] { "The " + parameter + " is a requiered parameter!" });
    }

    return value;
  }
  
  /**
   * 
   * @param context
   * @param name
   * @return
   * @throws AnnotatorConfigurationException
   */
  public static InputStream getResourceAsStream(AnnotatorContext context, 
      String name) throws AnnotatorConfigurationException {
    try {
      InputStream inResource = context.getResourceAsStream(name);

      if (inResource == null) {
        throw new AnnotatorConfigurationException(
            AnnotatorConfigurationException.RESOURCE_NOT_FOUND,
            new Object[] { "Unable to load model!" });
      }

      return inResource;
    } catch (AnnotatorContextException e) {
      throw new AnnotatorConfigurationException(
          AnnotatorConfigurationException.STANDARD_MESSAGE_CATALOG,
          new Object[] {
          "There is an internal error in the UIMA SDK.", e });
    }
  }
}