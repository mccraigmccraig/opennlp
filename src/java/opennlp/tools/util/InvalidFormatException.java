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
//GNU General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
////////////////////////////////////////////////////////////////////////////// 

package opennlp.tools.util;

/**
 * This exception is thrown if the profile data stream has
 * an invalid format e.g. non valid xml.
 *
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.2 $, $Date: 2006/11/17 09:37:42 $
 */
public class InvalidFormatException extends Exception {
  
  private static final long serialVersionUID = 0;
  
  public InvalidFormatException() {
  }
  
  public InvalidFormatException(String message, Throwable throwable) {
    super(message, throwable);
  }

  public InvalidFormatException(String message) {
    super(message);
  }
}
