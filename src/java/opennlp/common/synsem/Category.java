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

package opennlp.common.synsem;

import opennlp.common.unify.*;
import opennlp.common.util.*;

/**
 * Represents syntactic and/or semantic forms in a lexicalized grammar.
 * This interface was designed for Categorial Grammar categories, however the
 * interface itself is not entirely specific to Categorial Grammar.  If some
 * projects find the basic set-up useful for other representations (syntactic
 * trees or whatnot), they should not feel inhibited by the name category.
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.2 $, $Date: 2002/02/08 12:17:50 $
 */
public interface Category extends Unifiable, Mutable, java.io.Serializable {

    /**
     * Determines if this category is  equal to another on the top level.
     * It does not check sub categories.
     *
     * @param o object to check for equality
     * @return whether or not this is shallowly equal to object
     */
    public boolean shallowEquals (Object o);


    /**
     * Deep copies this category.
     *
     * @return a deep copy of this category
     */
    public Category copy ();


   
    /**
     * Iterates through this Category applying a function to this category
     * and every subcategory.
     *
     * @param f a function to be applied
     */    
    public void forall (CategoryFcn f); //to ls

    /**
     * Accessor function for the feature structure associated with this category
     *
     * @return the feature structure for this cateogory
     */    
    public FeatureStructure getFeatureStructure ();
    
    /**
     * Gives this category a new feature structure
     *
     * @param fs the new feature structure
     */    
    public void setFeatureStructure (FeatureStructure fs);
    
    /**
     * Creates a unique string that we can hash on.  It's good to do
     * things like normalize variables and to about the shortest
     * string possible.
     *
     * @return a string representing this category that we can hash on
     */
    public String hashString ();

    /**
     * Returns the string position of the constituent.  Starts with 0.
     *
     * @return Pair containing first and last indices (inclusive)
     */
    public Pair getSpan();

    /**
     * Returns the start string position of the constituent.  Starts with 0.
     *
     * @return first index (inclusive)
     */
    public int getSpanStart();

    /**
     * Returns the end string position of the constituent.  Starts with 0.
     *
     * @return last index (inclusive)
     */
    public int getSpanEnd();

    /**
     * Sets the string position of the constituent.  Starts with 0.
     *
     * @param a the first lex item included in constitutent
     * @param b the last lex item included in constitutent
     */
    public void setSpan(int a, int b);
    
}
