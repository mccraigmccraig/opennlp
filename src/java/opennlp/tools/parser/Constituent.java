///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2006 Thomas Morton
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

package opennlp.tools.parser;

import opennlp.tools.util.Span;

/**
 * Class used to hold constituents when reading parses.
 */
public class Constituent {

  private String label;
  private Span span;
  
  public Constituent(String label, Span span) {
    this.label = label;
    this.span = span;
  }


  /**
   * Returns the label of the constituent.
   * @return the label of the constituent.
   */
  public String getLabel() {
    return label;
  }


  /**
   * Assigns the label to the constituent.
   * @param label The label to set.
   */
  public void setLabel(String label) {
    this.label = label;
  }


  /**
   * Returns the span of the constituent.
   * @return the span of the constituent.
   */
  public Span getSpan() {
    return span;
  }
}
