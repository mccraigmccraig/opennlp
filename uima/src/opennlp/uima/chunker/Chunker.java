///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2005 Calcucare GmbH
//
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
////////////////////////////////////////////////////////////////////////////// 

package opennlp.uima.chunker;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import opennlp.maxent.io.BinaryGISModelReader;
import opennlp.tools.chunker.ChunkerME;
import opennlp.uima.util.AnnotatorUtil;
import opennlp.uima.util.UIMAUtil;

import com.ibm.uima.analysis_engine.ResultSpecification;
import com.ibm.uima.analysis_engine.annotator.AnnotatorConfigurationException;
import com.ibm.uima.analysis_engine.annotator.AnnotatorContext;
import com.ibm.uima.analysis_engine.annotator.AnnotatorInitializationException;
import com.ibm.uima.analysis_engine.annotator.AnnotatorProcessException;
import com.ibm.uima.analysis_engine.annotator.Annotator_ImplBase;
import com.ibm.uima.analysis_engine.annotator.TextAnnotator;
import com.ibm.uima.cas.FSIndex;
import com.ibm.uima.cas.Feature;
import com.ibm.uima.cas.FeatureStructure;
import com.ibm.uima.cas.Type;
import com.ibm.uima.cas.TypeSystem;
import com.ibm.uima.cas.text.AnnotationFS;
import com.ibm.uima.cas.text.TCAS;

/**
 * TODO: add javadoc comment
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.2 $, $Date: 2005/12/02 17:31:12 $
 */
public final class Chunker extends Annotator_ImplBase implements TextAnnotator {
  private AnnotatorContext mContext;

  private Type mTokenType;

  private Type mChunkType;

  private Type mPosType;

  private ChunkerME mChunker;

  @Override
  public void initialize(AnnotatorContext context)
      throws AnnotatorInitializationException, AnnotatorConfigurationException {
    mContext = context;

    String modelName = AnnotatorUtil.getRequiredParameter(context,
        UIMAUtil.MODEL_PARAMETER);

    InputStream inModel = AnnotatorUtil
        .getResourceAsStream(mContext, modelName);

    try {
      mChunker = new ChunkerME(new BinaryGISModelReader(new DataInputStream(
          inModel)).getModel());
    } catch (IOException e) {
      throw new AnnotatorInitializationException(
          AnnotatorInitializationException.STANDARD_MESSAGE_CATALOG,
          new Object[] { "IOException during model reading: " + e.getMessage(),
              e });
    }
  }

  @Override
  public void typeSystemInit(TypeSystem typeSystem)
      throws AnnotatorInitializationException, AnnotatorConfigurationException {
    String chunkTypeName = AnnotatorUtil.getRequiredParameter(mContext,
        "opennlp.uima.ChunkType");

    mChunkType = AnnotatorUtil.getType(typeSystem, chunkTypeName);

    String tokenTypeName = AnnotatorUtil.getRequiredParameter(mContext,
        UIMAUtil.TOKEN_TYPE_PARAMETER);

    mTokenType = AnnotatorUtil.getType(typeSystem, tokenTypeName);

    String posTypeName = AnnotatorUtil.getRequiredParameter(mContext,
        UIMAUtil.POS_TYPE_PARAMETER);

    mPosType = AnnotatorUtil.getType(typeSystem, posTypeName);
  }

  public void process(TCAS tcas, ResultSpecification specification)
      throws AnnotatorProcessException {
    FSIndex tokenAnnotationIndex = tcas.getAnnotationIndex(mTokenType);

    String tokens[] = new String[tokenAnnotationIndex.size()];
    String pos[] = new String[tokenAnnotationIndex.size()];
    AnnotationFS tokenAnnotations[] = new AnnotationFS[tokenAnnotationIndex
        .size()];

    int index = 0;

    Iterator tokenAnnotationIterator = tokenAnnotationIndex.iterator();

    while (tokenAnnotationIterator.hasNext()) {
      AnnotationFS tokenAnnotation = (AnnotationFS) tokenAnnotationIterator
          .next();

      tokenAnnotations[index] = tokenAnnotation;

      tokens[index] = tokenAnnotation.getCoveredText();

      //          TODO: add configuration parameter for this
      Feature tokenFeature = mTokenType.getFeatureByBaseName("pos");

      FeatureStructure posFeature = tokenAnnotation
          .getFeatureValue(tokenFeature);

      //          TODO: add configuration parameter for this
      Feature posTypeFeature = mPosType.getFeatureByBaseName("name");

      String posString = posFeature.getStringValue(posTypeFeature);

      pos[index] = posString;
      index++;
    }

    String result[] = mChunker.chunk(tokens, pos);

    int startOffset = -1;

    for (int i = 0; i < tokens.length; i++) {
      boolean isNewChunk = result[i].startsWith("B-");

      // if first continue
      if (i == 0) {
        if (isNewChunk) {
          startOffset = tokenAnnotations[i].getBegin();
        }

        continue;
      }

      boolean isEndOfChunk = (result[i].equals("O") || isNewChunk)
          && result[i - 1].startsWith("I-");

      if (isEndOfChunk) {
        int endOffset = tokenAnnotations[i - 1].getEnd();

        addChunkAnnotation(tcas, result[i - 1].substring(2), startOffset,
            endOffset);
      }

      if (isNewChunk) {
        startOffset = tokenAnnotations[i].getBegin();
      }

      // if last create and add
      if ((i == tokens.length - 1) && !isEndOfChunk) {
        addChunkAnnotation(tcas, result[i - 1].substring(2), startOffset,
            tokenAnnotations[i - 1].getEnd());
      }
    }
  }

  private void addChunkAnnotation(TCAS tcas, String name, int start, int end) {
    AnnotationFS chunk = tcas.createAnnotation(mChunkType, start, end);

    // TODO: add configuration parameter for this
    Feature nameFeature = mChunkType.getFeatureByBaseName("name");
    chunk.setStringValue(nameFeature, name);

    tcas.getIndexRepository().addFS(chunk);
  }
}