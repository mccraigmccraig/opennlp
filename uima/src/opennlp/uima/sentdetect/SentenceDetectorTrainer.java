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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import opennlp.maxent.DataStream;
import opennlp.maxent.GISModel;
import opennlp.maxent.io.BinaryGISModelWriter;
import opennlp.tools.sentdetect.SDEventStream;
import opennlp.tools.sentdetect.SentenceDetectorME;
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
 * TODO: add javadoc here
 * 
 *  Required parametes:
 *    opennlp.uima.ModelName - the name of the model file
 *    opennlp.uima.SentenceType - the full name of the sentence type
 *    
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.1 $, $Date: 2005/12/02 16:47:04 $
 */
public final class SentenceDetectorTrainer extends CasConsumer_ImplBase {
  
  /**
   *
   * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
   */
  private class DataStreamBuffer implements DataStream {
    private LinkedList<DataStream> mDataStreams = new LinkedList<DataStream>();

    public void add(DataStream stream) {
      mDataStreams.add(stream);
    }

    public Object nextToken() {
      DataStream first = mDataStreams.getFirst();

      if (first == null) {
        return null;
      }

      if (first.hasNext()) {
        return first.nextToken();
      } else {
        moveToNextStream();
        return nextToken();
      }
    }

    public boolean hasNext() {
      if (mDataStreams.isEmpty()) {
        return false;
      }

      DataStream first = mDataStreams.getFirst();

      if (first.hasNext()) {
        return true;
      } else {
        moveToNextStream();
        return hasNext();
      }
    }

    void moveToNextStream() {
      mDataStreams.removeFirst();
    }
  }

  /**
   *
   * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
   */
  private class ArrayDataStream implements DataStream {
    private int mCounter = 0;

    private Object[] mArray;

    ArrayDataStream(Object object) {
      this(new Object[] { object });
    }

    ArrayDataStream(Object[] array) {
      mArray = array;
    }

    public Object nextToken() {
      return mArray[mCounter++];
    }

    public boolean hasNext() {
      return mArray.length > mCounter;
    }
  }

  private DataStreamBuffer mDataStreams = new DataStreamBuffer();

  private Type mSentenceType;

  private String mModelName;

  @Override
  public void initialize() throws ResourceInitializationException {
    UimaContext context = getUimaContext();
    
    mModelName = CasConsumerUtil.getRequiredParameter(context, 
        UIMAUtil.MODEL_PARAMETER);
  }
  
  @Override
  public void typeSystemInit(TypeSystem typeSystem)
      throws ResourceInitializationException {
    String sentenceTypeName = UIMAUtil.SENTENCE_TYPE_PARAMETER;

    mSentenceType = CasConsumerUtil.getType(typeSystem, sentenceTypeName);
  }

  public void processCas(CAS cas) throws ResourceProcessException {
    TCAS tcas = (TCAS) cas;

    FSIndex sentenceIndex = tcas.getAnnotationIndex(mSentenceType);

    String[] sentArray = new String[sentenceIndex.size()];

    int i = 0;
    Iterator sentenceIterator = sentenceIndex.iterator();
    while (sentenceIterator.hasNext()) {
      AnnotationFS sentenceAnnotation = (AnnotationFS) sentenceIterator.next();

      sentArray[i++] = sentenceAnnotation.getCoveredText();
    }

    mDataStreams.add(new ArrayDataStream(sentArray));
  }

  @Override
  public void collectionProcessComplete(ProcessTrace arg0)
      throws ResourceProcessException, IOException {
    GISModel sentenceModel = SentenceDetectorME.train(new SDEventStream(
        mDataStreams), 100, 5);

    File modelFile = new File(getUimaContextAdmin().getResourceManager()
        .getDataPath() + "/" + mModelName);

    // TODO: write as .gz file ?
    BinaryGISModelWriter writer = new BinaryGISModelWriter(sentenceModel,
        new DataOutputStream(new FileOutputStream(modelFile)));

    writer.persist();

    mDataStreams = null;
  }

  @Override
  public void destroy() {
    mDataStreams = null;
  }

  @Override
  public boolean isStateless() {
    return false;
  }
}