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

package opennlp.common.unify;

import java.util.*;
import java.io.*;

import opennlp.common.util.Pair;

/**
 * A feature bundle that is contained in Categories.  This is non standard
 * in that usually a category <i> is </i> a feature.
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.1 $, $Date: 2001/10/23 13:46:24 $
 */
public interface Feature extends Serializable {

    /**
     * Gives this feature bundle an identifying number.
     *
     * @param i the identifying number
     */
    public void addIndex(int i);
    
    /**
     * Gives this feature bundle an identifying number.
     *
     * @param i the identifying number
     */
    public void addIndex(Integer i);
    
    /**
     * Gives a feature in this bundle an identifying number
     *
     * @param i the identifying number
     */
    public void addIndex(int i, String f);
    
    /**
     * Gives a feature in this bundle an identifying number
     *
     * @param i the identifying number
     */
    public void addIndex(Integer i, String f);
    
    /**
     * Gives a feature in this bundle an identifying number
     *
     * @param i the identifying number
     */
    public void addIndices(Map m);
    
    /**
     * Gets the identifying number for this feature bundle
     *
     * @return the identifying number
     */
    public Integer getIndex();
    
    /**
     * Gets the identifying numbers for this features in this bundle
     *
     * @return the features along with their identifying numbers
     */
    public HashMap getFeatureIndices();
    
    /**
     * Stores the instantiation of features if they are indexed.
     * For every feature with an index in this feature bundle, store
     * in <code> s </code> the index with the value for that feature
     * in <code> f </code>.
     *
     * @param s the place to store the new instantiation of features
     * @param f the feature bundle that might have more information on
     *          how features have been instantiated.
     */
    public void addFeatIndices(CategorySubstitution s, Feature f);

    /**
     * The feature/value pairs in set form
     *
     * @return the set of feature/value pairs
     */
    public Set entrySet();

    
    /**
     * The number of features in this feature bundle
     *
     * @return number of features in this feature bundle
     */
    public int size();

    /**
     * Gives this feature bundle an identifying number to be used
     * for identification and not for co-reference.
     *
     * @param i the identifying number
     */
    public void addNameIndex(int i);
    
    /**
     * Gives this feature bundle an identifying number to be used
     * for identification and not for co-reference.
     *
     * @param i the identifying number
     */
    public void addNameIndex(Integer i);
    
    /**
     * Gets the number used identification and not for co-reference.
     *
     * @return the identifying number
     */
    public Integer getNameIndex();
    
    /**
     * Returns whether or not this feature bundle contains any features
     *
     * @return whether this feature bundle contains any features
     */
    public boolean isEmpty();
    
    /**
     * Explictly set whether or not this feature bundle is empty
     *
     * @param b the empty value
     */
    public void setEmpty(boolean b);
    
    /**
     * Checks to see if a feature has a particular value in this bundle
     *
     * @param f the feature
     * @param f the value
     * @return if this bundle contains that feature/value pair
     */
    public boolean featureHasValue(String f, String val);

    /**
     * Makes a deep copy of this feature bundle.
     *
     * @return a copy of this feature bundle
     */
    public Feature copy();

    /**
     * Creates a feature bundle that only has the features specified by
     * a filter.
     *
     * @param ff a filter that determines if a feature should be kept
     * @return the new, filtered, feature bundle
     */
    public Feature filter(FilterFcn ff);
    
    /**
     * Given a generic feature, determine what to do with it and give it
     * a value.
     *
     * @param index should be either a String, Pair, or Integer
     * @param val the value of the feature
     * @return the old value of the feature
     */
    public Object put(Object index, Object val);
    
    /**
     * Store a feature/value pair
     *
     * @param index the feature
     * @param val the value of the feature
     * @return the old value of the feature
     */
    public Object put(String index, Object val);
    
    /**
     * Store a feature/value pair and also give the feature an index
     *
     * @param index Pair consisting of an index and a feature
     * @param val the value of the feature
     * @return the old value of the feature
     */
    public Object put(Pair index, Object val);
    
    /**
     * Gives an index a value (no matter what the feature is)
     *
     * @param index the index representing some feature
     * @param val the value of the feature
     * @return null
     */
    public Object put(Integer index, Object val);
    
    /**
     * Get the value of a feature
     *
     * @param o the feature
     * @return the value of the feature
     */
    public Object get(Object o);
    
    /**
     * computes whether a feature bundle is a is contained in this feature
     * bundle. 
     *
     * @param f the possibly contained feature bundle
     * @return if that bundle is a subset of this one
     */
    public boolean subset(Feature f);

    /**
     * Given a feature bundle, computes a new feature that is the unification
     * of it and this feature bundle.  This is non-destructive.
     *
     * @param f the second feature bundle
     * @return the unified feature bundle
     */
    public Feature unify(Feature f);

    /**
     * Given a feature bundle, computes a new feature that is the unification
     * of it and this feature bundle and stores the necessary instantiations
     * of indices.  This is non-destructive.
     *
     * @param f the second feature bundle
     * @param S where to store the instantiations of indices
     * @return the unified feature bundle
     */
    public CategorySubstitution unify(Feature f, CategorySubstitution S);

    /**
     * Creates a new feature bundle where some indices are instantiated
     * to have values or more specific values.
     *
     * @param S where to get the instantiations of indices
     * @return the more instantiated feature bundle
     */
    public Feature subVars(CategorySubstitution S);

    /**
     * Changes this feature bundle such that all its features that
     * are in another feature bundle are changed to have the values of
     * the other feature bundle.  This is non-destructive.
     *
     * @param f the overriding feature bundle
     * @return the changed feature bundle
     */
    public Feature override(Feature f);

    /**
     * Determines if this feature bundle has a co-referring index.
     *
     * @return if this feature bundle has a co-referring index.
     */
    public boolean hasIndex();
    
    /**
     * Determines if this feature bundle has an indentifying index.
     *
     * @return if this feature bundle has a indentifying index.
     */
    public boolean hasNameIndex();

    /**
     * Determines if this feature bundle is exactly the same as another.  This
     * means that for every feature, the bundles have exactly the same value.
     *
     * @param f the other feature bundle
     * @return if this bundle is the same as the other
     */
    public boolean equals(Feature f);

}
