///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2003 Thomas Morton
//
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////
package opennlp.tools.coref;

/** Enumeration of modes in which a linker can run. */
public class LinkerMode { 

  private final String name;

  private LinkerMode(String name) {
    this.name = name;
  }

  /** Testing mode, used to identify coreference relationships in un-annotatoed text. */
  public static final LinkerMode TEST = new LinkerMode("test");
  /** Trianing mode, used to learn coreference relationships in annotatoed text. */
  public static final LinkerMode TRAIN = new LinkerMode("train");
  /** Evaluation mode, used to evaluate identifed coreference relationships based on annotatoed text. */
  public static final LinkerMode EVAL = new LinkerMode("eval");
  /** Trianing mode, used to learn coreference relationships in annotatoed text. */
  public static final LinkerMode SIM = new LinkerMode("sim");


  public String toString() {
    return name;
  }

}
