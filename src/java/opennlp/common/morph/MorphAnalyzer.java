///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2002 Jason Baldridge and Gann Bierner
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
package opennlp.common.morph;


/**
 * The interface for morphological analyzers, which return morphological
 * information for word forms.
 *
 * @author      Jason Baldridge
 * @version     $Revision: 1.1 $, $Date: 2002/04/25 14:44:33 $
 */
public interface MorphAnalyzer {

    /**
     * Returns the morphological information for a word.
     *
     * @param word  The string representation of the word to be analyzed.
     * @return A String with the morph info, such as root, tense, person,
     *         etc.  Eventually, this should be a class instead of a String.
     */
    public String analyze (String word);

    
    /**
     * Returns the prefixes of a word.
     *
     * @param word  The string representation of the word to be analyzed.
     * @return A String[] containing all the suffixes of the word.
     */
    public String[] getPrefixes (String word);

    
    /**
     * Returns the suffixes of a word.
     *
     * @param word  The string representation of the word to be analyzed.
     * @return A String[] containing all the suffixes of the word.
     */
    public String[] getSuffixes (String word);
    
}
