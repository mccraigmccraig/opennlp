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

import opennlp.common.structure.*;
import java.util.*;


/**
 * Specifies how variable are to be replaced to make two categories
 * unify.  Most of this will change so it is not documented at this time.
 *
 * @author      Gann Bierner
 * @version     $Revision: 1.3 $, $Date: 2001/11/23 10:41:45 $
 */
public interface CategorySubstitution extends Substitution {
    public boolean fail();
    public Substitution setFail();
    public Object fill(Object o);

    public CategorySubstitution copy();
    public void addFeatIndex(int i, Object o);
    public void addFeatIndex(Integer i, Object o);
    public Object getFeatIndex(Integer i);
    public Object remove(Object o);
    public Object put(Object o1, Object o2);
    public Set keySet();
    public boolean isEmpty();
    public boolean containsKey(Object o);

    // for delaying a particular unification
    public CategorySubstitution delayUnification(Category c1, Category c2);
    public CategorySubstitution performDelayedUnification();
    
    // for feature bundles
    public void addIndex(int i, Feature f);
    public void addIndex(Integer i, Feature f);
    public Feature getIndex(Integer i);

    public void removeVariableSubs();
    public void condense();			
    public void join(CategorySubstitution s);
}
