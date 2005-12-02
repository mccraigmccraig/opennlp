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

package opennlp.uima.sentdetect;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.maxent.io.BinaryGISModelReader;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.uima.UIMAUtil;

import com.ibm.uima.analysis_engine.ResultSpecification;
import com.ibm.uima.analysis_engine.annotator.AnnotatorConfigurationException;
import com.ibm.uima.analysis_engine.annotator.AnnotatorContext;
import com.ibm.uima.analysis_engine.annotator.AnnotatorContextException;
import com.ibm.uima.analysis_engine.annotator.AnnotatorInitializationException;
import com.ibm.uima.analysis_engine.annotator.AnnotatorProcessException;
import com.ibm.uima.analysis_engine.annotator.Annotator_ImplBase;
import com.ibm.uima.analysis_engine.annotator.TextAnnotator;
import com.ibm.uima.cas.Type;
import com.ibm.uima.cas.TypeSystem;
import com.ibm.uima.cas.text.AnnotationFS;
import com.ibm.uima.cas.text.TCAS;

/**
 * TODO: add javadoc comment here
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version
 */
public class SentenceDetector extends Annotator_ImplBase implements
    TextAnnotator {

  /**
   * OpenNLP sentence detector.
   */
  private SentenceDetectorME mSentenceDetector;

  /**
   * The type of the created sentencs.
   */
  private Type mSentenceType;

  private AnnotatorContext mContext;

  @Override
  public void initialize(AnnotatorContext context)
      throws AnnotatorInitializationException, AnnotatorConfigurationException {
    mContext = context;

    String modelName = UIMAUtil.getRequiredParameter(mContext,
        UIMAUtil.MODEL_PARAMETER);

    try {
      // TODO: check if this is done correct here
      InputStream inModel = context.getResourceAsStream(modelName);

      if (inModel == null) {
        throw new AnnotatorConfigurationException(
            AnnotatorConfigurationException.RESOURCE_NOT_FOUND,
            new Object[] { "Unable to load model!" });
      }

      mSentenceDetector = new SentenceDetectorME(new BinaryGISModelReader(
          new DataInputStream(inModel)).getModel());
    } catch (AnnotatorContextException e) {
      throw new AnnotatorConfigurationException(
          AnnotatorConfigurationException.STANDARD_MESSAGE_CATALOG,
          new Object[] {
              "There is an internal error in the UIMA SDK," + "sorry", e });
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
    String sentenceTypeName = UIMAUtil.getRequiredParameter(mContext,
        UIMAUtil.SENTENCE_TYPE_PARAMETER);

    mSentenceType = typeSystem.getType(sentenceTypeName);

    if (mSentenceType == null) {
      throw new AnnotatorConfigurationException(
          AnnotatorConfigurationException.STANDARD_MESSAGE_CATALOG,
          new Object[] { "Unable to retrive sentence type!" });
    }
  }

  public void process(TCAS tcas, ResultSpecification specification)
      throws AnnotatorProcessException {
    String documentText = tcas.getDocumentText();

    int[] sentPositions = mSentenceDetector.sentPosDetect(documentText);

    int endOfLastSent = 0;

    for (int sentPosition : sentPositions) {
      AnnotationFS sent = tcas.createAnnotation(mSentenceType, endOfLastSent,
          sentPosition);

      tcas.getIndexRepository().addFS(sent);

      endOfLastSent = sentPosition;
    }
  }

  @Override
  public void destroy() {
    // dereference model to allow garbage collection 
    mSentenceDetector = null;
  }
}