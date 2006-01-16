///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2002, Eric D. Friedman All Rights Reserved.
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
///////////////////////////////////////////////////////////////////////////////


package opennlp.tools.lang.english;

import opennlp.tools.sentdetect.AbstractEndOfSentenceScanner;

/**
 * The default end of sentence scanner implements all of the
 * EndOfSentenceScanner methods in terms of the getPositions(char[])
 * method.  It scans for <tt>. ? ! ) and "</tt>.
 *
 * Created: Sat Oct 27 11:46:46 2001
 *
 * @author Eric D. Friedman
 * @version $Id: EndOfSentenceScanner.java,v 1.1 2006/01/16 17:38:42 tsmorton Exp $
 */

public class EndOfSentenceScanner extends AbstractEndOfSentenceScanner {

    public static final char[] eosCharacters =  {'.','?','!'};
    
    /**
     * Creates a new <code>DefaultEndOfSentenceScanner</code> instance.
     *
     */
    public EndOfSentenceScanner() {
        super();
    }
    
    public char[] getEndOfSentenceCharacters() {
      return eosCharacters;
    }
}
