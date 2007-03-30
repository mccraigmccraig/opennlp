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
//GNU Lesser General Public License for more details.
// 
//You should have received a copy of the GNU Lesser General Public
//License along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////

package opennlp.tools.dictionary.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import opennlp.tools.ngram.Token;
import opennlp.tools.ngram.TokenList;
import opennlp.tools.util.InvalidFormatException;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLReaderFactory;

/**
  * 
  * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
  * @version $Revision: 1.3 $, $Date: 2007/03/30 09:46:33 $
  */
public class DictionarySerializer {
  
  // TODO: should check for invalid format, make it save
  private static class DictionaryContenthandler implements ContentHandler {
    
    private EntryInserter mInserter;
    
//    private boolean mIsInsideDictionaryElement;
//    private boolean mIsInsideEntryElement;
    private boolean mIsInsideTokenElement;
    
    private List mTokenList = new LinkedList();

    private Attributes mAttributes;
    
    private DictionaryContenthandler(EntryInserter inserter) {
      mInserter = inserter;
    }
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

     public void startElement(String uri, String localName, String qName, 
         org.xml.sax.Attributes atts) throws SAXException {
       if (ENTRY_ELEMENT.equals(localName)) {
         
         mAttributes = new Attributes();
         
         for (int i = 0; i < atts.getLength(); i++) {
           mAttributes.setValue(atts.getLocalName(i), atts.getValue(i));
         }
       }
       else if (TOKEN_ELEMENT.equals(localName)) {
         mIsInsideTokenElement = true;
       }
     }
     
     public void characters(char[] ch, int start, int length) 
         throws SAXException {
       if (mIsInsideTokenElement) {
         mTokenList.add(Token.create(new String(ch, start, length)));
       }
     }

     /**
      * Creates the Profile object after processing is complete
      * and switches mIsInsideNgramElement flag. 
      */
     public void endElement(String uri, String localName, String qName) 
         throws SAXException {
       
       if (ENTRY_ELEMENT.equals(localName)) {
         
         Token[] tokens = (Token[]) mTokenList.toArray(
             new Token[mTokenList.size()]);
         
         Entry entry = new Entry(new TokenList(tokens), mAttributes);
         
         try {
           mInserter.insert(entry);
         } catch (InvalidFormatException e) {
           throw new SAXException("Invalid dictionary format!");
         }
         
         mTokenList.clear();
         mAttributes = null;
       } 
       else if (TOKEN_ELEMENT.equals(localName)) {
         mIsInsideTokenElement = false;
       }
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
  
  private static final String DICTIONARY_ELEMENT = "dictionary";
  private static final String ENTRY_ELEMENT = "entry";
  private static final String TOKEN_ELEMENT = "token";
  
  /**
   * Creates {@link Entry}s form the given {@link InputStream} and
   * forwards these {@link Entry}s to the {@link EntryInserter}.
   * 
   * @param in
   * @param inserter
   * @throws IOException
   * @throws InvalidFormatException 
   */
  public static void create(InputStream in, EntryInserter inserter) 
      throws IOException, InvalidFormatException {
    
    DictionaryContenthandler profileContentHandler = 
        new DictionaryContenthandler(inserter);
    
    XMLReader xmlReader;
    try {
      xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(profileContentHandler);
      xmlReader.parse(new InputSource(new GZIPInputStream(in)));
    } 
    catch (SAXException e) {
      throw new InvalidFormatException("The profile data stream has" +
            "an invalid format!", e);
    } 
  }
  
  /**
   * Serializes the given entries to the given {@link OutputStream}.
   * 
   * @param out 
   * @param entries 
   * 
   * @throws IOException If an I/O error occurs
   */
  public static void serialize(OutputStream out, Iterator entries) 
      throws IOException {
    GZIPOutputStream gzipOut = new GZIPOutputStream(out);
    StreamResult streamResult = new StreamResult(gzipOut);
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

    
      hd.startElement("", "", DICTIONARY_ELEMENT, new AttributesImpl());
    
      while (entries.hasNext()) {
        Entry entry = (Entry) entries.next();
        
        serializeEntry(hd, entry);
      }
      
      hd.endElement("", "", DICTIONARY_ELEMENT);
      
      hd.endDocument();
    }
    catch (SAXException e) {
      throw new IOException("There was an error during serialization!");
    }
    
    gzipOut.finish();
  }
  
  private static void serializeEntry(TransformerHandler hd, Entry entry) 
      throws SAXException{
    
    AttributesImpl entryAttributes = new AttributesImpl();
    
    for (Iterator it = entry.getAttributes().iterator(); it.hasNext();) {
      String key = (String) it.next();
      
      entryAttributes.addAttribute("", "", key,
              "", entry.getAttributes().getValue(key));
    }
    
    hd.startElement("", "", ENTRY_ELEMENT, entryAttributes); 
    
    TokenList tokens = entry.getTokens();
    
    for (Iterator it = tokens.iterator(); it.hasNext(); ) {
      
      hd.startElement("", "", TOKEN_ELEMENT, new AttributesImpl()); 

      Token token = (Token) it.next();
      
      hd.characters(token.getToken().toCharArray(), 
          0, token.getToken().length());
      
      hd.endElement("", "", TOKEN_ELEMENT);
    }
    
    hd.endElement("", "", ENTRY_ELEMENT);
  }
}