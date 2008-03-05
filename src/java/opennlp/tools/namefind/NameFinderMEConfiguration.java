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
package opennlp.tools.namefind;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import opennlp.maxent.MaxentModel;
import opennlp.tools.util.InvalidFormatException;

public class NameFinderMEConfiguration { 
  
  private static final String MODEL_ENTRY = "model.bin";
  private static final String MANIFEST_ENTRY = "manifest.properties";
  
  private static final String TYPE_PROPERTY = "opennlp.model.type";
  
  private MaxentModel model;
  
  private Properties manifest;
  
  NameFinderMEConfiguration(InputStream in) throws IOException, 
      InvalidFormatException {
    ZipInputStream zipIn = new ZipInputStream(in);
    
    boolean isValid = true;
    
    ZipEntry entry;
    
    while((entry = zipIn.getNextEntry()) != null) {
      
      if (MODEL_ENTRY.equals(entry.getName())) {
        // TODO: load model
      } 
      else if (MANIFEST_ENTRY.equals(entry.getName())) {
        // TODO: load properties
      }
      else {
        // TODO: maybe just poison construction ... until the manifest
        // is here to give back specific error message
        throw new InvalidFormatException("Unkown entry!");
      }
    }
      
    checkManifest();
  }
  
  /**
   * Checks if the manifest contains all mandatory properties.
   * 
   * @throws InvalidFormatException
   */
  private void checkManifest() throws InvalidFormatException {
    if (manifest.getProperty(TYPE_PROPERTY) == null)
      throw new InvalidFormatException("Missing mandatory manifest properties!");
  }
  
  /**
   * Retrieves the type of the model e.g person, organization, etc. 
   * 
   * @return
   */
  public String getModelType() {
    return manifest.getProperty(TYPE_PROPERTY);
  }
  
  /**
   * Retrieves the {@link MaxentModel}.
   * 
   * @return
   */
  MaxentModel getModel() {
    return model;
  }
}