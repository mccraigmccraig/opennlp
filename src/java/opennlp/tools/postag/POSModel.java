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

package opennlp.tools.postag;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ModelUtil;

/**
 * The {@link POSModel} is the model used
 * by a learnable {@link POSTagger}.
 * 
 * @see POSTaggerME
 */
public class POSModel {

  private static final String MAXENT_MODEL_ENTRY_NAME = "pos.bin";
  
  private final GISModel maxentPosModel;
  
  private final TagDictionary tagDictionary;
  
  private final Dictionary ngramDict;
  
  public POSModel(GISModel maxentPosModel, TagDictionary tagDictionary, 
      Dictionary ngramDict) {
    
    if (maxentPosModel == null) 
        throw new IllegalArgumentException("The maxentPosModel param must not be null!");
    
    // the model is always valid, because there
    // is nothing that can be assumed about the used
    // tags
    
    this.maxentPosModel = maxentPosModel;
    
    this.tagDictionary = tagDictionary;
    
    this.ngramDict = ngramDict;
  }
  
  public MaxentModel getMaxentPosModel() {
    return maxentPosModel;
  }
  
  /**
   * Retrieves the tag dictionary.
   * 
   * @return tag dictionary or null if not used
   */
  public TagDictionary getTagDictionary() {
    return tagDictionary;
  }
  
  /**
   * Retrieves the ngram dictionary.
   * 
   * @return ngram dictionary or null if not used
   */
  public Dictionary getNgramDictionary() {
    return ngramDict;
  }
  
  /**
   * .
   * 
   * After the serialization is finished the provided 
   * {@link OutputStream} is closed.
   * 
   * @param out
   * 
   * @throws IOException
   */
  public void serialize(OutputStream out) throws IOException {
    
    ZipOutputStream zip = new ZipOutputStream(out);
    
    zip.putNextEntry(new ZipEntry(MAXENT_MODEL_ENTRY_NAME));

    ModelUtil.writeModel(maxentPosModel, zip);
    
    zip.closeEntry();
    
    if (getTagDictionary() != null) {
//      zip.putNextEntry(new ZipEntry(""));
//      zip.closeEntry();
    }
    
    if (getNgramDictionary() != null) {
      zip.putNextEntry(new ZipEntry(""));
      getNgramDictionary().serialize(out);
      zip.closeEntry();
    }
  }
  
  public static POSModel create(InputStream in) throws IOException, InvalidFormatException {
    return null;
  }
}