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

package opennlp.tools.ngram;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.util.InvalidFormatException;

import junit.framework.TestCase;

/**
 * Tests the {@link ProfileSerializer} class.
 * 
 *
 * @author <a href="mailto:kottmann@gmail.com">Joern Kottmann</a>
 * @version $Revision: 1.2 $, $Date: 2006/11/09 15:34:45 $
 */
public class ProfileSerializerTest extends TestCase {
 
  /**
   * Tests if serialization and creation produces the same object again.
   * 
   * @throws IOException
   * @throws InvalidFormatException
   */
  public void testSerializeAndCreate()
      throws IOException, InvalidFormatException {
    
    String text = "When is a deleted file really deleted";
    
    Profile referenceNgrams = Ngram.create("test", text, 2, 5);
    
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ProfileSerializer.serialize(referenceNgrams, out);
    
    InputStream in = new ByteArrayInputStream(out.toByteArray());
    
    Profile recreatedProfile = ProfileSerializer.create(in);
    
    assertEquals(referenceNgrams, recreatedProfile);
  }
}