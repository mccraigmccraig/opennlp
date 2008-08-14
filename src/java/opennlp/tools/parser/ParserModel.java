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

package opennlp.tools.parser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.BinaryGISModelReader;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ModelUtil;

/**
 * This is an abstract base class for {@link ParserModel} implementations.
 */
public class ParserModel {

  private static final String BUILD_MODEL_ENTRY_NAME = "build.bin";
  
  private static final String CHECK_MODEL_ENTRY_NAME = "check.bin";

  private static final String PARSER_TAGGER_MODEL_ENTRY_NAME = "tagger";

  private static final String CHUNKER_TAGGER_MODEL_ENTRY_NAME = "chunker";
  
  private static final String HEAD_RULES_MODEL_ENTRY_NAME = "head-rules";
  
  private GISModel buildModel;
  
  private GISModel checkModel;
  
  private POSModel parserTagger;
  
  private ChunkerModel chunkerTagger;
  
  private opennlp.tools.lang.english.HeadRules headRules;
  
  public ParserModel(GISModel buildModel, GISModel checkModel, POSModel parserTagger, 
      ChunkerModel chunkerTagger, opennlp.tools.lang.english.HeadRules headRules) {
    
    this.buildModel = buildModel;
    this.checkModel = checkModel;
    this.parserTagger = parserTagger;
    this.chunkerTagger = chunkerTagger;
    this.headRules = headRules;
  }
  
  public MaxentModel getBuildModel() {
    return buildModel;
  }
  
  public MaxentModel getCheckModel() {
    return checkModel;
  }
  
// only used by treeinsert parser
//
//  public MaxentModel getAttachModel() {
//    return null;
//  }
  
  public POSModel getParserTaggerModel() {
    return parserTagger;
  }
  
  public ChunkerModel getParserChunkerModel() {
    return chunkerTagger;  
  }
  
  public opennlp.tools.lang.english.HeadRules getHeadRules() {
    return headRules;  
  }
  
  public void serialize(OutputStream out) throws IOException {
    ZipOutputStream zip = new ZipOutputStream(out);
    
    zip.putNextEntry(new ZipEntry(BUILD_MODEL_ENTRY_NAME));
    ModelUtil.writeModel(buildModel, zip);
    zip.closeEntry();

    zip.putNextEntry(new ZipEntry(CHECK_MODEL_ENTRY_NAME));
    ModelUtil.writeModel(checkModel, zip);
    zip.closeEntry();

    zip.putNextEntry(new ZipEntry(PARSER_TAGGER_MODEL_ENTRY_NAME));
    getParserTaggerModel().serialize(zip);
    zip.closeEntry();

    zip.putNextEntry(new ZipEntry(CHUNKER_TAGGER_MODEL_ENTRY_NAME));
    getParserChunkerModel().serialize(zip);
    zip.closeEntry();

    zip.putNextEntry(new ZipEntry(HEAD_RULES_MODEL_ENTRY_NAME));
    headRules.serialize(new OutputStreamWriter(zip, "UTF-8"));
    zip.closeEntry();
  }
  
  public static ParserModel create(InputStream in) throws IOException, InvalidFormatException {
    
    ZipInputStream zip = new ZipInputStream(in);
    
    GISModel buildModel = null;
    GISModel checkModel = null;
    
    POSModel parserTagger = null;
    ChunkerModel parserChunker = null;
    
    opennlp.tools.lang.english.HeadRules headRules = null;
    
    ZipEntry entry;
    while((entry = zip.getNextEntry()) != null) {
      if (BUILD_MODEL_ENTRY_NAME.equals(entry.getName())) {
        
        buildModel = new BinaryGISModelReader(
            new DataInputStream(zip)).getModel();
        
        zip.closeEntry();
      }
      else if (CHECK_MODEL_ENTRY_NAME.equals(entry.getName())) {
        
        checkModel = new BinaryGISModelReader(
            new DataInputStream(zip)).getModel();
        
        zip.closeEntry();
      }
      else if (PARSER_TAGGER_MODEL_ENTRY_NAME.equals(entry.getName())) {
        
        parserTagger = POSModel.create(zip);
        zip.closeEntry();
      }
      else if (PARSER_TAGGER_MODEL_ENTRY_NAME.equals(entry.getName())) {
        
        parserChunker = ChunkerModel.create(zip);
        zip.closeEntry();
      }
      else if (HEAD_RULES_MODEL_ENTRY_NAME.equals(entry.getName())) {
        
        headRules = new opennlp.tools.lang.english.HeadRules(new BufferedReader
            (new InputStreamReader(zip, "UTF-8")));
        
        zip.closeEntry();
      }
      else {
        throw new InvalidFormatException("Model contains unkown resource!");
      }
    }
    
    // TODO: add checks, everything must be =! null
    
    return new ParserModel(buildModel, checkModel, parserTagger, parserChunker, headRules);
  }
}