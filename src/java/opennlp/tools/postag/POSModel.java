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
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ModelUtil;

/**
 * The {@link POSModel} is the model used
 * by a learnable {@link POSTagger}.
 * 
 * @see POSTaggerME
 */
public final class POSModel {

  private static final String MAXENT_MODEL_ENTRY_NAME = "pos.bin";
  private static final String TAG_DICTIONARY_ENTRY_NAME = "tag-dictionary.xml";
  private static final String NGRAM_DICTIONARY_ENTRY_NAME = "ngram-dictionary.xml";
  
  private final GISModel maxentPosModel;
  
  private final POSDictionary tagDictionary;
  
  private final Dictionary ngramDict;
  
  public POSModel(GISModel maxentPosModel, POSDictionary tagDictionary, 
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
  public POSDictionary getTagDictionary() {
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
      zip.putNextEntry(new ZipEntry(TAG_DICTIONARY_ENTRY_NAME));
      
      getTagDictionary().serialize(zip);
      
      zip.closeEntry();
    }
    
    if (getNgramDictionary() != null) {
      zip.putNextEntry(new ZipEntry(NGRAM_DICTIONARY_ENTRY_NAME));
      getNgramDictionary().serialize(out);
      zip.closeEntry();
    }
    
    zip.close();
  }
  
  public static POSModel create(InputStream in) throws IOException, InvalidFormatException {
    ZipInputStream zip = new ZipInputStream(in);

    GISModel maxentPosModel = null;
    POSDictionary posDictionary = null;
    Dictionary ngramDictionary = null;
    
    ZipEntry entry;
    while((entry = zip.getNextEntry()) != null ) {
      if (MAXENT_MODEL_ENTRY_NAME.equals(entry.getName())) {
        maxentPosModel = new BinaryGISModelReader(
            new DataInputStream(zip)).getModel();
        
        zip.closeEntry();
      }
      else if (TAG_DICTIONARY_ENTRY_NAME.equals(entry.getName())) {
        posDictionary = POSDictionary.create(zip);
        zip.closeEntry();
      }
      else if (NGRAM_DICTIONARY_ENTRY_NAME.equals(entry.getName())) {
        // Note: ngram dictionary is not case sensitive
        ngramDictionary = new Dictionary(zip);
      }
      else {
        throw new InvalidFormatException("Model contains unkown resource!");
      }
    }
     
    if (maxentPosModel == null)
      throw new InvalidFormatException("Could not find maxent pos model!");
    
    return new POSModel(maxentPosModel, posDictionary, ngramDictionary);
  }
}