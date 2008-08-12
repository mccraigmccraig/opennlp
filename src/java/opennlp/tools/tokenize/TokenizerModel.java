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

package opennlp.tools.tokenize;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.BinaryGISModelReader;
import opennlp.maxent.io.BinaryGISModelWriter;
import opennlp.maxent.io.GISModelWriter;
import opennlp.tools.util.InvalidFormatException;

/**
 * The {@link TokenizerModel} is the model used
 * by a learnable {@link Tokenizer}.
 *
 * @see TokenizerME
 */
public final class TokenizerModel {

  private static final String MAXENT_MODEL_ENTRY_NAME = "token.bin";
  private static final String PROPERTIES_ENTRY_NAME = "tokenizer.xml";
  
  private static final String USE_ALPHA_NUMERIC_OPTIMIZATION = 
      "useAlphaNumericOptimization";
  
  private final GISModel model;
  
  private final boolean useAlphaNumericOptimization;
  
  /**
   * Initializes the current instance.
   * 
   * @param tokenizerMaxentModel
   * @param alphaNumbericOptimization
   */
  public TokenizerModel(GISModel tokenizerMaxentModel, 
      boolean useAlphaNumericOptimization) {
    
    if (tokenizerMaxentModel == null)
        throw new IllegalArgumentException("tokenizerMaxentModel param must not bet null!");
    
    if (!isModelCompatible(tokenizerMaxentModel))
        throw new IllegalArgumentException("The maxent model is not compatible!");
    
    this.model = tokenizerMaxentModel;
    this.useAlphaNumericOptimization = useAlphaNumericOptimization;
  }
  
  private static boolean isModelCompatible(MaxentModel model) {
    
    boolean areLabelsCompatible = false;
    
    if (model.getNumOutcomes() == 2) {
    areLabelsCompatible = TokenizerME.SPLIT.equals(model.getOutcome(0)) && 
        TokenizerME.NO_SPLIT.equals(model.getOutcome(1)) ||
        TokenizerME.SPLIT.equals(model.getOutcome(1)) && 
        TokenizerME.NO_SPLIT.equals(model.getOutcome(0));
    }
    
    return areLabelsCompatible; 
  }
  
  public MaxentModel getMaxentModel() {
    return model;
  }
  
  public boolean useAlphaNumericOptimization() {
    return useAlphaNumericOptimization;
  }
  
  /**
   * Writes the {@link TokenizerModel} to the given {@link OutputStream}.
   * After the serialization is finished the provided 
   * {@link OutputStream} is closed.
   * 
   * @param out the stream in which the model is written
   * 
   * @throws IOException if something goes wrong writing the in the
   * provided {@link OutputStream}.
   */
  public void serialize(OutputStream out) throws IOException {
    final ZipOutputStream zip = new ZipOutputStream(out);
    
    // write model
    ZipEntry modelEntry = new ZipEntry(MAXENT_MODEL_ENTRY_NAME);
    zip.putNextEntry(modelEntry);
    
    // The ZipOutputStream cannot be given directly to
    // the model writer because the model writer call close()
    // on that stream
    
    GISModelWriter modelWriter = new BinaryGISModelWriter(model,
        new DataOutputStream(new OutputStream() {
          @Override
          public void write(int b) throws IOException {
            zip.write(b);
          }
        }));
    
    modelWriter.persist();
    zip.closeEntry();
    
    Properties properties = new Properties();
    properties.setProperty(USE_ALPHA_NUMERIC_OPTIMIZATION,
        Boolean.toString(useAlphaNumericOptimization()));
    
    ZipEntry propertiesEntry = new ZipEntry(PROPERTIES_ENTRY_NAME);
    zip.putNextEntry(propertiesEntry);
    
    properties.store(zip, "This file contains the tokenizer properties.");
    
    zip.closeEntry();
    zip.close();
  }
  
  /**
   * Creates a {@link TokenizerModel} from the provided {@link InputStream}.
   * 
   * The {@link InputStream} in remains open after the model is read.
   * 
   * @param in stream to read the model from
   * 
   * @return the new {@link TokenizerModel} read from the {@link InputStream} in.
   * 
   * @throws IOException
   * @throws InvalidFormatException
   */
  public static TokenizerModel create(InputStream in) throws IOException, 
      InvalidFormatException {
    
    final ZipInputStream zip = new ZipInputStream(in);
    
    GISModel model = null;
    Properties properties = null;
    
    ZipEntry entry;
    while((entry = zip.getNextEntry()) != null ) {
      
      if (MAXENT_MODEL_ENTRY_NAME.equals(entry.getName())) {
        
        // read model
        model = new BinaryGISModelReader(
            new DataInputStream(zip)).getModel();
        
        zip.closeEntry();
      }
      else if (PROPERTIES_ENTRY_NAME.equals(entry.getName())) {
        
        // read properties
        properties = new Properties();
        
        InputStream inTest = new InputStream() {

          @Override
          public int read() throws IOException {
            return zip.read();
          }
          
          @Override
          public void close() throws IOException {
            System.out.println("ups closed!!!");
          }
        };
        
        properties.load(inTest);
        
        zip.closeEntry();
      }
      else {
        throw new InvalidFormatException("Model contains unkown resource!");
      }
    }
    
    zip.close();
    
    if (model == null || properties == null) {
      throw new InvalidFormatException("Token model is incomplete!");
    }
    
    String useAlphaNumericOptimizationString = 
        properties.getProperty(USE_ALPHA_NUMERIC_OPTIMIZATION);
    
    if (useAlphaNumericOptimizationString == null) {
      throw new InvalidFormatException("The seAlphaNumericOptimization parameter " +
      		"cannot be found!");
    }
    
    if (!isModelCompatible(model)) {
      throw new InvalidFormatException("The maxent model is not compatible with the tokenizer!");
    }
    
    return new TokenizerModel(model, 
        Boolean.parseBoolean(useAlphaNumericOptimizationString));
  }
}