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
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////

package opennlp.common.unify;

/**
 * A feature structure containing attributes and their associated values.
 *
 * @author      Jason Baldridge and Gann Bierner
 * @version     $Revision: 1.1 $, $Date: 2001/11/14 17:51:10 $
 */
public interface FeatureStructure extends Unifiable {

    /**
     * Store a attribute/value pair
     *
     * @param attribute the attribute of the feature
     * @param value the value of the feature
     */
    public void setFeature (String attribute, Object value);


    /**
     * Get the value corresponding to an attribute.
     *
     * @param attribute the attribute of the feature
     * @return the value of the feature
     */
    public Object getValue (String attribute);
    
    /**
     * Checks to see if the feature structure contains a feature with 
     * the given attribute.
     *
     * @param attribute the attribute
     * @return if this structure contains that attribute
     */
    public boolean hasAttribute (String attribute);

    /**
     * Checks to see if an attribute has a particular value in this structure
     *
     * @param attribute the attribute
     * @param value the value
     * @return if this structure contains that attribute/value pair
     */
    public boolean attributeHasValue (String attribute, Object value);


    /**
     * The all attributes in set form
     *
     * @return the set of feature/value pairs
     */
    public java.util.Set getAttributes ();

    
    /**
     * The number of features in this feature structure
     *
     * @return number of features in this feature structure
     */
    public int size ();

    
    /**
     * Returns whether or not this feature structure contains any features
     *
     * @return whether this feature structure contains any features
     */
    public boolean isEmpty ();

    
    /**
     * Explictly set whether or not this feature structure is empty
     *
     * @param b the empty value
     */
    public void setEmpty (boolean b);
    

    /**
     * Makes a deep copy of this feature structure.
     *
     * @return a copy of this feature structure
     */
    public FeatureStructure copy ();

    /**
     * Creates a feature structure that only has the features specified by
     * a filter.
     *
     * @param ff a filter that determines if a feature should be kept
     * @return the new, filtered, feature structure
     */
    public FeatureStructure filter (FilterFcn ff);
    

    /**
     * Computes whether this feature structure contains (is a superset
     * of) another feature structure.
     *
     * @param fs the possibly contained feature structure
     * @return if that structure is a subset of this one 
     */
    public boolean contains (FeatureStructure fs);

    /**
     * Given a feature structure, computes a new feature that is the unification
     * of it and this feature structure.  This is non-destructive.
     *
     * @param f the second feature structure
     * @return the unified feature structure
     */
    //public FeatureStructure unify (FeatureStructure fs) throws UnifyFailure;

    /**
     * Changes this feature structure such that all its features that
     * are in another feature structure are changed to have the values of
     * the other feature structure.  This is non-destructive.
     *
     * @param f the overriding feature structure
     * @return the changed feature structure
     */
    public FeatureStructure override (FeatureStructure fs);

    /**
     * Determines if this feature structure is exactly the same as another.  This
     * means that for every feature, the structures have exactly the same value.
     *
     * @param f the other feature structure
     * @return if this structure is the same as the other
     */
    public boolean equals (FeatureStructure fs);

}
