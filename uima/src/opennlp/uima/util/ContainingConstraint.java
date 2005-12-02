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

import java.util.Collection;
import java.util.LinkedList;

import com.ibm.uima.cas.FSMatchConstraint;
import com.ibm.uima.cas.FeatureStructure;
import com.ibm.uima.cas.text.AnnotationFS;

/**
 * TODO: add javadoc comment
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.2 $, $Date: 2005/12/02 16:02:32 $
 */
public final class ContainingConstraint implements FSMatchConstraint {
  private static final long serialVersionUID = 1;

  private Collection<AnnotationFS> mContainingAnnotations = 
      new LinkedList<AnnotationFS>();

  /**
   * Default constructor
   */
  public ContainingConstraint()
  {
  }
  
  public ContainingConstraint(AnnotationFS containingAnnotation)
  {
    add(containingAnnotation);
  }
  
  public boolean match(FeatureStructure featureStructure) {
    if (!(featureStructure instanceof AnnotationFS)) {
      return false;
    }

    AnnotationFS annotation = (AnnotationFS) featureStructure;

    for (AnnotationFS containingAnnotation : mContainingAnnotations) {
      if (isContaining(annotation, containingAnnotation)) {
        return true;
      }
    }

    return false;
  }

  private boolean isContaining(AnnotationFS annotation, AnnotationFS containing) {
    if ((containing.getBegin() <= annotation.getBegin())
        && (containing.getEnd() >= annotation.getEnd())) {
      return true;
    } else {
      return false;
    }
  }

  public void add(AnnotationFS containingAnnotation) {
    mContainingAnnotations.add(containingAnnotation);
  }
}