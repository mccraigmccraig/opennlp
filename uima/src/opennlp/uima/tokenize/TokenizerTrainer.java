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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import opennlp.maxent.GISModel;
import opennlp.maxent.TwoPassDataIndexer;
import opennlp.maxent.io.BinaryGISModelWriter;
import opennlp.tools.tokenize.TokSpanEventStream;
import opennlp.tools.util.Span;
import opennlp.uima.util.ContainingConstraint;
import opennlp.uima.util.CasConsumerUtil;
import opennlp.uima.util.UIMAUtil;

import com.ibm.uima.UimaContext;
import com.ibm.uima.cas.CAS;
import com.ibm.uima.cas.FSIndex;
import com.ibm.uima.cas.Type;
import com.ibm.uima.cas.TypeSystem;
import com.ibm.uima.cas.text.AnnotationFS;
import com.ibm.uima.cas.text.TCAS;
import com.ibm.uima.collection.CasConsumer_ImplBase;
import com.ibm.uima.resource.ResourceInitializationException;
import com.ibm.uima.resource.ResourceProcessException;
import com.ibm.uima.util.ProcessTrace;

/**
 * TODO: add javadoc comment
 * 
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.1 $, $Date: 2005/12/02 16:04:09 $
 */
public final class TokenizerTrainer extends CasConsumer_ImplBase {
  private TokSpanEventStream mEventStream;

  private UimaContext mContext;

  private Type mSentenceType;

  private Type mTokenType;

  private String mModelName;

  @Override
  public void initialize() throws ResourceInitializationException {
    mContext = getUimaContext();

    mModelName = CasConsumerUtil.getRequiredParameter(mContext,
        UIMAUtil.MODEL_PARAMETER);
    
    // TODO: this is an optional parameter
    String isSkipAlphaNumericsString = CasConsumerUtil.getRequiredParameter(
        mContext, "opennlp.uima.tokenizer.IsSkipAlphaNumerics");
    
    boolean isSkipAlphaNumerics = Boolean.valueOf(isSkipAlphaNumericsString);
    
    mEventStream = new TokSpanEventStream(isSkipAlphaNumerics);
  }

  public void typeSystemInit(TypeSystem typeSystem)
      throws ResourceInitializationException {

    String sentenceTypeName = CasConsumerUtil.getRequiredParameter(mContext,
        UIMAUtil.SENTENCE_TYPE_PARAMETER);

    mSentenceType = CasConsumerUtil.getType(typeSystem, sentenceTypeName);


    String tokenTypeName = CasConsumerUtil.getRequiredParameter(mContext,
        UIMAUtil.TOKEN_TYPE_PARAMETER);
    
    mTokenType = CasConsumerUtil.getType(typeSystem, tokenTypeName);
  }

  public void processCas(CAS cas) throws ResourceProcessException {
    TCAS tcas = (TCAS) cas;

    FSIndex sentenceAnnotations = tcas.getAnnotationIndex(mSentenceType);

    Iterator sentenceAnnotationsIterator = sentenceAnnotations.iterator();

    while (sentenceAnnotationsIterator.hasNext()) {
      // sentence
      AnnotationFS sentenceAnnotation = 
        (AnnotationFS) sentenceAnnotationsIterator.next();

      FSIndex tokenAnnotations = tcas.getAnnotationIndex(mTokenType);

      ContainingConstraint containingConstraint = 
          new ContainingConstraint(sentenceAnnotation);

      Iterator containingTokensIterator = tcas.createFilteredIterator(
          tokenAnnotations.iterator(), containingConstraint);

      List<Span> openNLPSpans = new LinkedList<Span>();

      while (containingTokensIterator.hasNext()) {
        AnnotationFS tokenAnnotation = 
          (AnnotationFS) containingTokensIterator .next();

        openNLPSpans.add(new Span(tokenAnnotation.getBegin()
            - sentenceAnnotation.getBegin(), tokenAnnotation.getEnd()
            - sentenceAnnotation.getBegin()));
      }

      Collections.sort(openNLPSpans);
      
      Span[] spans = openNLPSpans.toArray(new Span[openNLPSpans.size()]);

      mEventStream.addEvents(spans, sentenceAnnotation.getCoveredText());
    }
  }

  @Override
  public void collectionProcessComplete(ProcessTrace arg0)
      throws ResourceProcessException, IOException {
    
    // TODO: add configparameter for smoothing
    // GIS.SMOOTHING_OBSERVATION = 0.1;

    GISModel tokenModel = opennlp.maxent.GIS.trainModel(100,
        new TwoPassDataIndexer(mEventStream, 2));

    File modelFile = new File(getUimaContextAdmin().getResourceManager()
        .getDataPath() + "/" + mModelName);

    // TODO: write as .gz file ?
    BinaryGISModelWriter writer = new BinaryGISModelWriter(tokenModel,
        new DataOutputStream(new FileOutputStream(modelFile)));

   writer.persist();
   
   // dereference to allow garbage collection
   mEventStream = null;
  }
  
  @Override
  public boolean isStateless() {
    return false;
  }
  
  @Override
  public void destroy() {
    // dereference to allow garbage collection
    mEventStream = null;
  }
}