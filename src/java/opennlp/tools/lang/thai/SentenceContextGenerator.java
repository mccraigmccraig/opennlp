///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2006 Tom Morton
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

package opennlp.tools.lang.thai;

import opennlp.tools.sentdetect.DefaultSDContextGenerator;

/**
 * Creates contexts/features for end-of-sentence detection in Thai text. 
 */
public class SentenceContextGenerator extends DefaultSDContextGenerator {

  public SentenceContextGenerator() {
    super(EndOfSentenceScanner.eosCharacters);
  }

  protected void collectFeatures(String prefix, String suffix, String previous, String next) {
    buf.append("p=");
    buf.append(prefix);
    collectFeats.add(buf.toString());
    buf.setLength(0);
    
    buf.append("s=");
    buf.append(suffix);
    collectFeats.add(buf.toString());
    buf.setLength(0);

    collectFeats.add("p1="+prefix.substring(Math.max(prefix.length()-1,0)));
    collectFeats.add("p2="+prefix.substring(Math.max(prefix.length()-2,0)));
    collectFeats.add("p3="+prefix.substring(Math.max(prefix.length()-3,0)));
    collectFeats.add("p4="+prefix.substring(Math.max(prefix.length()-4,0)));
    collectFeats.add("p5="+prefix.substring(Math.max(prefix.length()-5,0)));
    collectFeats.add("p6="+prefix.substring(Math.max(prefix.length()-6,0)));
    collectFeats.add("p7="+prefix.substring(Math.max(prefix.length()-7,0)));
    
    collectFeats.add("n1="+suffix.substring(0,Math.min(1,suffix.length())));
    collectFeats.add("n2="+suffix.substring(0,Math.min(2,suffix.length())));
    collectFeats.add("n3="+suffix.substring(0,Math.min(3,suffix.length())));
    collectFeats.add("n4="+suffix.substring(0,Math.min(4,suffix.length())));
    collectFeats.add("n5="+suffix.substring(0,Math.min(5,suffix.length())));
    collectFeats.add("n6="+suffix.substring(0,Math.min(6,suffix.length())));
    collectFeats.add("n7="+suffix.substring(0,Math.min(7,suffix.length())));
  }
}
