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
package opennlp.tools.coref;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import opennlp.tools.coref.mention.HeadFinder;

import opennlp.tools.coref.mention.Extent;
import opennlp.tools.coref.mention.Parse;

/** A linker provides an interface for finding mentions <code>getMentions</code>, 
 * and creating entities out of those mentions <code>getEntities</code>.  This interface also allows
 * for the training of a resolver with the method <code>setEntities</code> which is used to give the
 * resolver mentions whose entityId fields indicate which mentions refer to the same entity and the 
 * <code>train</code> method which compiles all the inormation provided via calls to 
 * <code>setEntities</code> into a model.
 * @author Tom Morton
 *
 */
public interface Linker {
  

  public static final String DESCRIPTOR = "desc";
  public static final String ISA = "isa";

  public static final String COMBINED_NPS = "cmbnd";
  public static final String NP = "np";
  public static final String PROPER_NOUN_MODIFIER = "pnmod";
  public static final String PRONOUN_MODIFIER = "np";
  
  public static final Pattern singularThirdPersonPronounPattern = Pattern.compile("^(he|she|it|him|her|his|hers|its|himself|herself|itself)$",Pattern.CASE_INSENSITIVE);
  public static final Pattern pluralThirdPersonPronounPattern = Pattern.compile("^(they|their|theirs|them|themselves)$",Pattern.CASE_INSENSITIVE);
  public static final Pattern speechPronounPattern = Pattern.compile("^(I|me|my|you|your|you|we|us|our|ours)$",Pattern.CASE_INSENSITIVE);
  public static final Pattern malePronounPattern = Pattern.compile("^(he|him|his|himself)$",Pattern.CASE_INSENSITIVE);
  public static final Pattern femalePronounPattern = Pattern.compile("^(she|her|hers|herself)$",Pattern.CASE_INSENSITIVE);
  public static final Pattern neuterPronounPattern = Pattern.compile("^(it|its|itself)$",Pattern.CASE_INSENSITIVE);
  public static final Pattern firstPersonPronounPattern = Pattern.compile("^(I|me|my|we|our|us|ours)$",Pattern.CASE_INSENSITIVE);  
  public static final Pattern secondPersonPronounPattern = Pattern.compile("^(you|your|yours)$",Pattern.CASE_INSENSITIVE);  
  public static final Pattern thirdPersonPronounPattern = Pattern.compile("^(he|she|it|him|her|his|hers|its|himself|herself|itself|they|their|theirs|them|themselves)$",Pattern.CASE_INSENSITIVE);
  public static final Pattern singularPronounPattern = Pattern.compile("^(I|me|my|he|she|it|him|her|his|hers|its|himself|herself|itself)$",Pattern.CASE_INSENSITIVE);
  public static final Pattern pluralPronounPattern = Pattern.compile("^(we|us|our|ours|they|their|theirs|them|themselves)$",Pattern.CASE_INSENSITIVE);
  public static final Pattern honorificsPattern = Pattern.compile("[A-Z][a-z]+\\.$|^[A-Z][b-df-hj-np-tv-xz]+$");
  public static final Pattern designatorsPattern = Pattern.compile("[a-z]\\.$|^[A-Z][b-df-hj-np-tv-xz]+$|^Co(rp)?$");
  
  public void setEntities(MentionContext[] mentions);
  
  /** Returns a list of entities which group the mentions into entity classes. 
   * @param mentions A array of mentions. 
   * return A list of entities with elements of type <code>DiscourseEntity</code>.
   */
  public List getEntities(MentionContext[] mentions);
  
  /** Trains the linker based on the data specified via calls to #setEntities */ 
  public void train() throws IOException;
    
  /** Returns a array of mentions contained in the specified parse for coreference resolution.
   * @param p A top-level arse from which mentions are gathered.  Typically this represents a document.
   * @return Array of mentions contained in the specified parse.
   */
  public Extent[] getMentions(Parse parse);
 
  /** 
   * Returns the head finder associated with this linker.
   * @return The head finder associated with this linker.
   */
  public HeadFinder getHeadFinder();
}
