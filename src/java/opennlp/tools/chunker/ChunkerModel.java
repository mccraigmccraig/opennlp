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

package opennlp.tools.chunker;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.BinaryGISModelReader;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ModelUtil;

/**
 * The {@link ChunkerModel} is the model used
 * by a learnable {@link Chunker}.
 * 
 * @see ChunkerME
 */
public class ChunkerModel {

  private static final String CHUNKER_MODEL_ENTRY_NAME = "chunker.bin";
  
  private GISModel chunkerModel;
  
  public ChunkerModel(GISModel chunkerModel) {
    this.chunkerModel = chunkerModel;
  }
  
  public MaxentModel getMaxentChunkerModel() {
    return chunkerModel;
  }
  
  /**
   * .
   * 
   * After the serialization is finished the provided 
   * {@link OutputStream} is closed.
   * 
   * @param out
   * @throws IOException
   */
  public void serialize(OutputStream out) throws IOException {
    ZipOutputStream zip = new ZipOutputStream(out);
    
    zip.putNextEntry(new ZipEntry(CHUNKER_MODEL_ENTRY_NAME));
    ModelUtil.writeModel(chunkerModel, zip);
    zip.closeEntry();
    
    zip.close();
  }
  
  /**
   * .
   * 
   * The {@link InputStream} in remains open after the model is read.
   * 
   * @param in
   * @return
   * @throws IOException
   * @throws InvalidFormatException
   */
  public static ChunkerModel create(InputStream in) throws IOException, InvalidFormatException {
    ZipInputStream zip = new ZipInputStream(in);
    
    ZipEntry chunkerModelEntry = zip.getNextEntry();
    
    if (chunkerModelEntry == null || 
        !CHUNKER_MODEL_ENTRY_NAME.equals(chunkerModelEntry.getName()))
      throw new InvalidFormatException("Could not find maxent chunker model!");
    
    GISModel chunkerModel = new BinaryGISModelReader(
        new DataInputStream(zip)).getModel();
    
    return new ChunkerModel(chunkerModel);
  }
}