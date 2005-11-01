///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2003 Thomas Morton
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
package opennlp.tools.coref.mention;

/** Interface to provide dictionary information to the coreference modulue assuming a
 * heirarachly structured dictionary (such as WordNet) is avaliable. 
 */ 
public interface Dictionary {
  
  /** 
   * Returns the lemmas of the specified word with the specified part-of-speech.  
   * @param word The word whose lemmas are desired.
   * @param pos The part-of-speech of the specified word.
   * @return The lemmas of the specified word given the specified part-of-speech.
   */
  public String[] getLemmas(String word, String pos);
  
  /**
   * Returns a key indicating the specified sense number of the specified 
   * lemma with the specified part-of-speech.  
   * @param lemma The lemmas for which the key is desired.
   * @param pos The pos for which the key is desired.
   * @param senseNumber The sense number for which the key is desired.
   * @return a key indicating the specified sense number of the specified 
   * lemma with the specified part-of-speech.
   */
 public  String getSenseKey(String lemma, String pos, int senseNumber);
  
  /**
   * Returns the number of senses in the dictionry for the specified lemma.
   * @param lemma A lemmatized form of the word to look up.
   * @param pos The part-of-sppech for the lemma.
   * @return the number of senses in the dictionry for the specified lemma.
   */
  public int getNumSenses(String lemma, String pos);
  
  /**
   * Returns an array of keys for each parent of the specified sense nuber of the specified lemma with the specified part-of-speech.
   * @param lemma A lemmatized form of the word to look up.
   * @param pos The part-of-sppech for the lemma.
   * @param senseNumber The sense number for which the parent keys are desired.
   * @return an array of keys for each parent of the specified sense nuber of the specified lemma with the specified part-of-speech.
   */
  public String[] getParentSenseKeys(String lemma, String pos, int senseNumber);
}
