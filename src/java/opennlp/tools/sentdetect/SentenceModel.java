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
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.BinaryGISModelReader;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ModelUtil;
import opennlp.tools.util.StringList;

/**
 * The {@link SentenceModel} is the model used
 * by a learnable {@link SentenceDetector}.
 * 
 * TODO: read and write all parts of the model!
 * 
 * @see SentenceDetectorME
 */
public class SentenceModel {

  private static final String MAXENT_MODEL_ENTRY_NAME = "sent.bin";
  private static final String ABBREVIATIONS_ENTRY_NAME = "abbreviations.xml";
  private static final String SETTINGS_ENTRY_NAME = "settings.properties";
  
  private static final String TOKEN_END_PROPERTY = "useTokenEnd";
  
  private static final String END_OF_SENTENCE_CHARS_PROPERTY = "endOfSentenceChars";
  
  private GISModel sentModel;
  
  private char endOfSentenceChars[];
  
  private Set<String> abbreviations;
  
  private final boolean useTokenEnd;
  
  public SentenceModel(GISModel sentModel, char[] endOfSentenceChars, boolean useTokenEnd, 
      Set<String> abbreviations) {
    
    if (sentModel == null)
        throw new IllegalArgumentException("sentModel param must not be null!");
    
    if (!isModelCompatible(sentModel))
        throw new IllegalArgumentException("The maxent model is not compatible!");
      
    this.sentModel = sentModel;
    
    this.endOfSentenceChars = endOfSentenceChars;
    
    this.useTokenEnd = useTokenEnd;
    
    this.abbreviations = abbreviations;
  }
  
  private static boolean isModelCompatible(MaxentModel model) {
    // TODO: add checks, what are the outcomes ?
    return true;
  }
  
  public MaxentModel getMaxentModel() {
    return sentModel;
  }
  
  public char[] getEndOfSentenceCharacters() {
    return endOfSentenceChars;
  }
  
  public Set<String> getAbbreviations() {
    return abbreviations;
  }
  
  public boolean useTokenEnd() {
    return useTokenEnd;
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
    final ZipOutputStream zip = new ZipOutputStream(out);
    
    // write model
    zip.putNextEntry(new ZipEntry(MAXENT_MODEL_ENTRY_NAME));
    ModelUtil.writeModel(sentModel, zip);
    zip.closeEntry();
    
    // write abbreviations
    zip.putNextEntry(new ZipEntry(ABBREVIATIONS_ENTRY_NAME));
    
    Dictionary abbreviationDictionary = new Dictionary();
    
    for (String abbreviation : abbreviations) {
      abbreviationDictionary.put(new StringList(abbreviation));
    }
    
    abbreviationDictionary.serialize(zip);
    
    zip.closeEntry();
    
    // write properties
    zip.putNextEntry(new ZipEntry(SETTINGS_ENTRY_NAME));
    
    Properties settings = new Properties();
    
    settings.put(TOKEN_END_PROPERTY, Boolean.toString(useTokenEnd()));
    
    StringBuilder endOfSentenceCharString = new StringBuilder();
    
    for (char character : getEndOfSentenceCharacters()) {
      endOfSentenceCharString.append(character);
    }
      
    settings.put(END_OF_SENTENCE_CHARS_PROPERTY, endOfSentenceCharString.toString());
    
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
    
    GISModel sentModel = null;
    Properties settings = null;
    
    Set<String> abbreviations = null;
    
    ZipEntry entry;
    while((entry = zip.getNextEntry()) != null ) {
      if (MAXENT_MODEL_ENTRY_NAME.equals(entry.getName())) {
        
        // read model
        sentModel = new BinaryGISModelReader(
            new DataInputStream(zip)).getModel();
        
        zip.closeEntry();
      }
      else if (SETTINGS_ENTRY_NAME.equals(entry.getName())) {
        
        // read properties
        settings = new Properties();
        settings.load(zip);
        
        zip.closeEntry();
      }
      else if (ABBREVIATIONS_ENTRY_NAME.equals(entry.getName())) {
        Dictionary abbreviationDictionary = new Dictionary(zip);
        
        abbreviations = new HashSet<String>();
        
        for (StringList abbreviation : abbreviationDictionary) {
          if (abbreviation.size() != 1) 
            throw new InvalidFormatException("Each abbreviation must be exactly one token!");
          
          abbreviations.add(abbreviation.getToken(0));
        }
        
        zip.closeEntry();
      }
      else {
        throw new InvalidFormatException("Model contains unkown resource!");
      }
    }
    
    if (sentModel == null)
      throw new InvalidFormatException("Unable to find " + MAXENT_MODEL_ENTRY_NAME + " maxent model!");
    
    if (settings == null)
      throw new InvalidFormatException("Unable to find " + SETTINGS_ENTRY_NAME + " !");
    
    String useTokenEndString = settings.getProperty(TOKEN_END_PROPERTY);
    
    if (useTokenEndString == null)
      throw new InvalidFormatException(TOKEN_END_PROPERTY + " is a mandatory property!");
    
    boolean useTokenEnd = Boolean.parseBoolean(useTokenEndString);
    
    String endOfSentenceCharsString = settings.getProperty(END_OF_SENTENCE_CHARS_PROPERTY);
    
    if (endOfSentenceCharsString == null)
      throw new InvalidFormatException(END_OF_SENTENCE_CHARS_PROPERTY + " is a mandatory property!");
    
    if (abbreviations == null)
      abbreviations = Collections.emptySet();
    
    return new SentenceModel(sentModel, endOfSentenceCharsString.toCharArray(), 
        useTokenEnd, abbreviations);
  }
}