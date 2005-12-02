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

package opennlp.uima.tokenize;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import opennlp.maxent.io.BinaryGISModelReader;
import opennlp.tools.tokenize.TokenizerME;
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
import com.ibm.uima.cas.Type;
import com.ibm.uima.cas.TypeSystem;
import com.ibm.uima.cas.text.AnnotationFS;
import com.ibm.uima.cas.text.TCAS;

/**
 * TODO: add javadoc comment here
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.2 $, $Date: 2005/12/02 16:03:56 $
 */
public final class Tokenizer extends Annotator_ImplBase 
    implements TextAnnotator {
  private AnnotatorContext mContext;

  private TokenizerME mTokenizer;

  private Type mSentenceType;

  private Type mTokenType;

  @Override
  public void initialize(AnnotatorContext context)
      throws AnnotatorInitializationException, AnnotatorConfigurationException {
    mContext = context;

    String modelName = AnnotatorUtil.getRequiredParameter(mContext,
        UIMAUtil.MODEL_PARAMETER);

    InputStream inModel = AnnotatorUtil.getResourceAsStream(
        mContext, modelName);
    
    try {
      mTokenizer = new TokenizerME(new BinaryGISModelReader(
          new DataInputStream(inModel)).getModel());
    } catch (IOException e) {
      throw new AnnotatorInitializationException(
          AnnotatorInitializationException.STANDARD_MESSAGE_CATALOG,
          new Object[] { "IOException during model reading: " + e.getMessage(),
          e });
    }

    // TODO: add a configuration parameter for this
    mTokenizer.setAlphaNumericOptimization(true);
  }

  @Override
  public void typeSystemInit(TypeSystem typeSystem)
      throws AnnotatorInitializationException, AnnotatorConfigurationException {
    String containgTypeName = AnnotatorUtil.getRequiredParameter(mContext,
        UIMAUtil.SENTENCE_TYPE_PARAMETER);

    mSentenceType = typeSystem.getType(containgTypeName);

    if (mSentenceType == null) {
      throw new AnnotatorConfigurationException(
          AnnotatorConfigurationException.STANDARD_MESSAGE_CATALOG,
          new Object[] { "Unable to retrive containing type!" });
    }

    String tokenTypeName = AnnotatorUtil.getRequiredParameter(mContext,
        UIMAUtil.TOKEN_TYPE_PARAMETER);

    mTokenType = typeSystem.getType(tokenTypeName);

    if (mTokenType == null) {
      throw new AnnotatorConfigurationException(
          AnnotatorConfigurationException.STANDARD_MESSAGE_CATALOG,
          new Object[] { "Unable to retrive token type!" });
    }
  }

  public void process(TCAS tcas, ResultSpecification specification)
      throws AnnotatorProcessException {
    String documentText = tcas.getDocumentText();

    // TODO: can this here return null ?
    // do i need here a return ?: if (containgAnnotations == null) return;
    FSIndex containgAnnotations = tcas.getAnnotationIndex(mSentenceType);

    Iterator containgAnnotationsIterator = containgAnnotations.iterator();

    while (containgAnnotationsIterator.hasNext()) {
      AnnotationFS containgAnnotation = (AnnotationFS) 
          containgAnnotationsIterator.next();

      opennlp.tools.util.Span[] spans = mTokenizer
          .tokenizePos(documentText.substring(containgAnnotation.getBegin(),
          containgAnnotation.getEnd()));

      int containingAnnotationOffset = containgAnnotation.getBegin();

      for (int i = 0; i < spans.length; i++) {
        AnnotationFS token = tcas.createAnnotation(mTokenType,
            containingAnnotationOffset + spans[i].getStart(),
            containingAnnotationOffset + spans[i].getEnd());

        tcas.getIndexRepository().addFS(token);
      }
    }
  }

  @Override
  public void destroy() {
    // dereference model to allow garbage collection 
    mTokenizer = null;
  }
}