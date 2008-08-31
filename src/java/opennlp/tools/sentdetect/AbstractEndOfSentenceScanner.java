///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2002
// 
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////

package opennlp.tools.sentdetect;

import java.util.ArrayList;
import java.util.List;

import opennlp.maxent.IntegerPool;

/**
 * Abstract class for common methods related to identifying potential ends of sentences.
 */
public abstract class AbstractEndOfSentenceScanner implements EndOfSentenceScanner {

  protected static final IntegerPool INT_POOL = new IntegerPool(500);
  
  public List<Integer> getPositions(String s) {
    return getPositions(s.toCharArray());
  }

  public List<Integer> getPositions(StringBuffer buf) {
    return getPositions(buf.toString().toCharArray());
  }

  public List<Integer> getPositions(char[] cbuf) {
    List<Integer> l = new ArrayList<Integer>();
    char[] eosCharacters = getEndOfSentenceCharacters();
    for (int i = 0; i < cbuf.length; i++) {
      for (int ci=0;ci<eosCharacters.length;ci++) {
        if (cbuf[i] == eosCharacters[ci]) {
          //System.err.println("getPositions: adding "+i+" for "+ci);
            l.add(INT_POOL.get(i));
            break;
        }
      }
    }
    return l;
  }
}