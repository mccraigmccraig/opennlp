///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2006 Calcucare GmbH
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

package opennlp.tools.ngram;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import opennlp.tools.util.InvalidFormatException;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This class serializes and deserialies an {@link Profile} object to an or from
 * an xml byte stream.
 *
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.3 $, $Date: 2006/11/08 19:25:38 $
 */
public final class ProfileSerializer { 
  
  private static class ProfileContentHandler  implements ContentHandler {

    private List mNgrams;

    private boolean mIsInsideNgramElement = false;
    
    private int mCurrentNgramOccurenceCount = -1;

    private Profile mProfile;
    
    private String mProfileName;
    
   /**
    * Not implemendted.
    */
    public void processingInstruction(String target, String data) 
      throws SAXException {
    }

    /**
     * Not implemendted.
     */
    public void startDocument() throws SAXException {
    }

    /**
     * Cretes the ngram list for the ngrams element. 
     * 
     * Sets for each ngram mCurrentNgramOccurenceCount and switches the
     * mIsInsideNgramElement flag.
     */
    public void startElement(String uri, String localName, String qName, 
        Attributes atts) throws SAXException {
    
      if (localName.equals(NGRAMS_ELEMENT)) {
        mNgrams = new LinkedList();
        
        mProfileName = atts.getValue("", NAME_ATTRIBUTE);
        
      }
      else if (localName.equals(NGRAM_ELEMENT)) {
        
        mIsInsideNgramElement = true;
        
          mCurrentNgramOccurenceCount = Integer.parseInt(
              atts.getValue("", OCCURENCE_ATTRIBUTE));
      }
      else {
        throw new SAXException("Received unexpected element: "  + localName);
      }
    }
    
    /**
     * Creates the nrgam object with the previous set occurence count
     * and given chars. Then adds the object to the ngram list.
     */
    public void characters(char[] ch, int start, int length) 
        throws SAXException {
      if (mIsInsideNgramElement) {
        mNgrams.add(new Ngram(new String(ch, start, length), 
            mCurrentNgramOccurenceCount));
      }
    }

    /**
     * Creates the Profile object after processing is complete
     * and switches mIsInsideNgramElement flag. 
     */
    public void endElement(String uri, String localName, String qName) 
        throws SAXException {
      if (localName.equals(NGRAMS_ELEMENT)) {
        
        Map ngramMap = new HashMap();
        
        for (Iterator it = mNgrams.iterator(); it.hasNext(); ) {
          
          Ngram ngram = (Ngram) it.next();
          
          ngramMap.put(ngram.getGramText(), ngram);
        }
        
        mProfile = new Profile(mProfileName, ngramMap);
      }
      else if (localName.equals(NGRAM_ELEMENT)) {
        mIsInsideNgramElement = false;
      }
      else {
        throw new SAXException("Received unexpected element: "  + localName);
      }
    }
    
    /**
     * Retrives the profile or null if something went wrong
     * during parsing.
     */
    Profile getProfile() {
      return mProfile;
    }
    
    /**
     * Not implemendted.
     */
    public void endDocument() throws SAXException {
    }

    /**
     * Not implemendted.
     */
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    /**
     * Not implemendted.
     */
    public void ignorableWhitespace(char[] ch, int start, int length) 
        throws SAXException {
    }

    /**
     * Not implemendted.
     */
    public void setDocumentLocator(Locator locator) {
    }

    /**
     * Not implemendted.
     */
    public void skippedEntity(String name) throws SAXException {
    }

    /**
     * Not implemendted.
     */
    public void startPrefixMapping(String prefix, String uri) 
        throws SAXException {
    }
  }
  
  private static final String CHARSET = "UTF-8";
  
  private static final String NGRAMS_ELEMENT = "ngrams";
  private static final String NAME_ATTRIBUTE = "name";
  private static final String NGRAM_ELEMENT = "ngram";
  private static final String OCCURENCE_ATTRIBUTE = "occurence";
  
  
  private ProfileSerializer() {
  }
  
  /**
   * Parses an profle data stream and creates a {@link Profile} object.
   * 
   * @param in
   * @return the {@link Profile} object.
   * @throws InvalidFormatException
   * @throws IOException
   */
  public static Profile create(InputStream in) throws InvalidFormatException, 
      IOException {
    
    ProfileContentHandler profileContentHandler = new ProfileContentHandler();
    
    XMLReader xmlReader;
    try {
      xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(profileContentHandler);
      xmlReader.parse(new InputSource(in));
      
    } 
    catch (SAXException e) {
      throw new InvalidFormatException("The profile data stream has" +
            "an invalid format!", e);
    } 
    
    return profileContentHandler.getProfile();
  }
  
  /**
   * Wrties the given {@link Profile} to the given {@link OutputStream}.
   * 
   * @param profile
   * @param out
   * @throws IOException 
   */
  public static void serialize(Profile profile, OutputStream out) 
      throws IOException {
    
    StreamResult streamResult = new StreamResult(out);
    SAXTransformerFactory tf = (SAXTransformerFactory) 
        SAXTransformerFactory.newInstance();
    
    TransformerHandler hd;
    try {
      hd = tf.newTransformerHandler();
    } catch (TransformerConfigurationException e1) {
      throw new AssertionError("The Tranformer configuration must be valid!");
    }
   
    Transformer serializer = hd.getTransformer();
    serializer.setOutputProperty(OutputKeys.ENCODING, CHARSET);
    serializer.setOutputProperty(OutputKeys.INDENT, "yes");
    
    hd.setResult(streamResult);
    
    try {
        hd.startDocument();
        
        AttributesImpl ngramsAttributes = new AttributesImpl();
        ngramsAttributes.addAttribute("", "", NAME_ATTRIBUTE,
                "", profile.getName());
        
        hd.startElement("", "", NGRAMS_ELEMENT, ngramsAttributes);
        
        
        for (Iterator it = profile.iterator(); it.hasNext();) {
          
          Ngram ngram = (Ngram) it.next();
          
          AttributesImpl ngramAttributes = new AttributesImpl();
          ngramAttributes.addAttribute("", "", OCCURENCE_ATTRIBUTE,
                  "", Integer.toString(ngram.getOccurenceCount()));

          
          hd.startElement("", "", NGRAM_ELEMENT, ngramAttributes); 
          hd.characters(ngram.getGramText().toCharArray(), 
              0, ngram.length());
          hd.endElement("", "", NGRAM_ELEMENT);
        }
        
        hd.endElement("", "", NGRAMS_ELEMENT);
        
        hd.endDocument();
    }
    catch (SAXException e) {
      throw new IOException("There was an error during serialization!");
    }
  }
}