///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2002 Jason Baldridge and Gann Bierner
// 
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////

package opennlp.common.generate;

/**
 * An interface for any component that might want to communicate
 * a generated string to the user.
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.2 $, $Date: 2002/02/08 12:17:50 $
 */
public interface Mouth{
    
    /**
     * Communicates a string
     *
     * @param str the string
     */
    public void speak(String str);
    
    /**
     * Performs whatever cleanup is necessary between communications
     */
    public void clear();
    
    /**
     * Performs whetever initialization is necessary
     */
    public void open();
    
    /**
     * Perform whatever cleanup is necessary when this Mouth is finished
     * being used
     */
    public void close();
}
