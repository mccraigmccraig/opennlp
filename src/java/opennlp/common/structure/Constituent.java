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

package opennlp.common.structure;

import opennlp.common.parse.*;
import opennlp.common.unify.*;

import java.io.*;

/**
 * Lexical information associated with a particular word or phrase
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */
public interface Constituent extends Serializable {

    /**
     * Returns a copy of this Constituent
     *
     * @return a new Constituent
     */
    public Constituent copy();
    
    /**
     * Returns the orthographic string for this word or phrase
     *
     * @return the string 
     */
    public String getOrthography();

    /**
     * Sets the orthographic string for this word or phrase
     *
     * @param s a new string
     */
    public void setOrthography(String s);
    
    /**
     * Returns the syntactic/semantic representation
     *
     * @return  the syntactic/semantic representation
     */
    public Category getCategory();

    /**
     * Sets the syntactic/semantic representation
     *
     * @param  cat the syntactic/semantic representation
     */
    public void setCategory(Category cat);

    /**
     * Returns some probablity statistic
     *
     * @return some probablity statistic
     */
    public double getProbability();

    /**
     * Sets some probablity statistic
     *
     * @param d some probablity statistic
     */
    public void setProbability(double d);

    /**
     * Returns the derivation of this lexical string
     *
     * @return the derivation of this lexical string
     */
    public Derivation getDerivation();

    /**
     * Sets the derivation of this lexical string
     *
     * @param d the derivation of this lexical string
     */
    public void setDerivation(Derivation d);

    /**
     * Returns whether this entry is closed class
     *
     * @return whether this entry is closed class
     */
    public boolean isClosedClass();

    /**
     * Sets the closed classedness of this entry
     *
     * @param b the closed classedness
     */
    public void setClosedClass(boolean b);
    

    public int getCurRule();
    public void setCurRule(int i);
    public int getCurPos();
    public void setCurPos(int i);
    public String getRule();
    public void setRule(String r);
    public CategorySubstitution getSubst();
    public void setSubst(CategorySubstitution s);
}
