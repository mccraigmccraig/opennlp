///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2000 Jason Baldridge and Gann Bierner
// 
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////

package opennlp.common.parse;

import java.awt.*;
import java.io.*;

/**
 * Draws a graphical representation of the derivation of a parse.
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */
public interface Derivation extends Serializable {

    /**
     * Accessor function for the height the resulting picture.  Do not call
     * this until after you've performed the draw command.
     *
     * @return height in pixels */
    public int getHeight();
    
    /**
     * Accessor function for the width of the resulting picture.
     *
     * @return width in pixels
     */
    public int getWidth();


    /**
     * Computes the width of the resulting picture.  This will probably
     * go away.
     *
     * @param fm the font we are using
     * @param catLen don't really know :-)
     * @return width in pixels
     */
    public int computeWidth(FontMetrics fm, int catLen);
    
    /**
     * Computes the width of the resulting picture.  
     *
     * @param g the graphics context in which this derivation will be drawn
     * @return width in pixels
     */
    public int computeWidth(Graphics g);

    /**
     * Draws the graphical representation of this derivation.  This will
     * probably go away.
     *
     * @param g where to draw the picture
     * @param w the current left margin
     * @return the current height of the derivation
     */
    public int draw(Graphics g, int w);
    
    /**
     * Draws the graphical representation of this derivation.  
     *
     * @param g where to draw the picture
     * @param w the current left margin.  This will probably go away.
     */
    public void drawStart(Graphics g, int w);

    /**
     * Makes a copy of this derivation
     *
     * @return the new derivation
     */
    public Derivation copy();
}
