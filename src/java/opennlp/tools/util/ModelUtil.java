///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2008 OpenNlp
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

package opennlp.tools.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.BinaryGISModelWriter;
import opennlp.maxent.io.GISModelWriter;

/**
 * Utility class for handling of {@link MaxentModel}s.
 */
public final class ModelUtil {

  private ModelUtil() {
  }
  
  /**
   * Writes the given model to the given {@link OutputStream}.
   * 
   * This methods does not closes the provided stream.
   * 
   * @param model
   * 
   * @throws IOException
   */
  public static void writeModel(GISModel model, final OutputStream out) throws IOException {
    GISModelWriter modelWriter = new BinaryGISModelWriter(model,
        new DataOutputStream(new OutputStream() {
          @Override
          public void write(int b) throws IOException {
            out.write(b);
          }
        }));
    
    modelWriter.persist();
  }
}