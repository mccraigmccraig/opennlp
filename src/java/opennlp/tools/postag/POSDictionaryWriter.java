///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2004 Thomas Morton
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
package opennlp.tools.postag;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/** 
 * Class for writting a pos-tag-dictionary to a file.
 */
public class POSDictionaryWriter {

  private Writer dictFile;
  private Map dictionary;
  private Map wordCounts;
  private Integer ONE = new Integer(1);
  private String newline = System.getProperty("line.seperator");
  
  public POSDictionaryWriter(String file) throws IOException {
    dictFile = new FileWriter(file);
    dictionary = new HashMap();
    wordCounts = new HashMap();
  }
  
  public void addEntry(String word, String tag) {
    Set tags = (Set) dictionary.get(word);
    if (tags == null) {
      tags = new HashSet();
      dictionary.put(word,tags);
    }
    tags.add(tag);
    Integer c = (Integer) wordCounts.get(word);
    if (c == null) {
      wordCounts.put(word,ONE);
    }
    else {
      wordCounts.put(word,new Integer(c.intValue()+1));
    }
  }
  
  public void write() throws IOException {
    write(5);
  }
  
  public void write(int cutoff) throws IOException {
    for (Iterator wi=wordCounts.keySet().iterator();wi.hasNext();) {
      String word = (String) wi.next();
      if (((Integer) wordCounts.get(word)).intValue() > cutoff) {
        dictFile.write(word);
        Set tags = (Set) dictionary.get(word);
        for (Iterator ti=tags.iterator();ti.hasNext();) {
          dictFile.write(" ");
          dictFile.write((String) ti.next());
        }
        dictFile.write(newline);
      }
    }
  }

}
