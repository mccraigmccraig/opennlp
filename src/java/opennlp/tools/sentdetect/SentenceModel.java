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

package opennlp.tools.sentdetect;

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
 * The {@link SentenceModel} is the model used
 * by a learnable {@link SentenceDetector}.
 * 
 * @see SentenceDetectorME
 */
public class SentenceModel {

  private static final String MAXENT_MODEL_ENTRY_NAME = "sent.bin";
  
  private GISModel sentModel;
  
  public SentenceModel(GISModel sentModel) {
    
    if (sentModel == null)
        throw new IllegalArgumentException("sentModel param must not be null!");
    
    if (!isModelCompatible(sentModel))
        throw new IllegalArgumentException("The maxent model is not compatible!");
      
    this.sentModel = sentModel;
  }
  
  private static boolean isModelCompatible(MaxentModel model) {
    // TODO: add checks, what are the outcomes ?
    return true;
  }
  
  public MaxentModel getMaxentModel() {
    return sentModel;
  }
  
  public void serialize(OutputStream out) throws IOException {
    final ZipOutputStream zip = new ZipOutputStream(out);
    
    // write model
    ZipEntry modelEntry = new ZipEntry(MAXENT_MODEL_ENTRY_NAME);
    zip.putNextEntry(modelEntry);
    
    ModelUtil.writeModel(sentModel, zip);
    
    zip.closeEntry();
    zip.close();
  }
  
  /**
   * Creates a {@link SentenceModel} from the provided {@link InputStream}.
   * 
   * The {@link InputStream} in remains open after the model is read.
   * 
   * @param in
   * 
   * @return
   * 
   * @throws IOException
   * @throws InvalidFormatException
   */
  public static SentenceModel create(InputStream in) throws IOException, InvalidFormatException {
    
    ZipInputStream zip = new ZipInputStream(in);
    
    ZipEntry sentModelEntry = zip.getNextEntry();
    
    if (!MAXENT_MODEL_ENTRY_NAME.equals(sentModelEntry.getName()))
        throw new InvalidFormatException("Unable to find sent.bin maxent model!");
    
    GISModel sentModel = new BinaryGISModelReader(new DataInputStream(zip)).getModel();
    
    zip.closeEntry();
    
    if (zip.getNextEntry() == null)
        throw new InvalidFormatException("More resources than expected in the sentence model!");
    
    return new SentenceModel(sentModel);
  }
}