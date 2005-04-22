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

import java.util.Set;

import opennlp.tools.coref.mention.MentionContext;
import opennlp.tools.coref.sim.GenderEnum;
import opennlp.tools.coref.sim.NumberEnum;

/**
 * Represents an entity in a discourse model. 
 */
public class DiscourseEntity extends DiscourseElement {

  private String category = null;
  private Set synsets;
  private GenderEnum gender;
  private double genderProb;
  private NumberEnum number;
  private double numberProb;

  /**
   * Creates a new entity based on the specified mention and its specified gender and number properties.
   * @param mention The first mention of this entity.
   * @param gender The gender of this entity.
   * @param genderProb The probability that the specified gender is correct.
   * @param number The number for this entity.
   * @param numberProb The probability that the specified number is correct.
   */
  public DiscourseEntity(MentionContext mention, GenderEnum gender, double genderProb, NumberEnum number, double numberProb) {
    super(mention);
    this.gender = gender;
    this.genderProb = genderProb;
    this.number = number;
    this.numberProb = numberProb;
  }

  /**
   * Creates a new entity based on the specified mention.
   * @param mention The first mention of this entity.
   */
  public DiscourseEntity(MentionContext mention) {
    super(mention);
    gender = GenderEnum.UNKNOWN;
    number = NumberEnum.UNKNOWN;
  }

  /**
   * Returns the semantic category of this entity.  This field is used to associated named-entity categories with an entity.
   * @return the semantic category of this entity.
   */
  public String getCategory() {
    return (category);
  }

  /**
   * Specifies the semantic category of this entity.
   * @param cat The semantic category of ths entity.
   */
  public void setCategory(String cat) {
    category = cat;
  }

  /** 
   * Returns the set of synsets associated with this entity. 
   * @return the set of synsets associated with this entity.
   */
  public Set getSynsets() {
    return (synsets);
  }

  /**
   * Returns the gender associated with this entity.
   * @return the gender associated with this entity.
   */
  public GenderEnum getGender() {
    return gender;
  }
  
  /**
   * Returns the probability for the gender associated with this entity.
   * @return the probability for the gender associated with this entity.
   */
  public double getGenderProbability() {
    return genderProb;
  }
  
  /**
   * Returns the number associated with this entity.
   * @return the number associated with this entity.
   */
  public NumberEnum getNumber() {
    return number;
  }
  
  /**
   * Returns the probability for the number associated with this entity.
   * @return the probability for the number associated with this entity.
   */
  public double getNumberProbability() {
    return numberProb;
  }

  /** 
   * Specifies the gender of this entity.
   * @param gender The gender.
   */
  public void setGender(GenderEnum gender) {
    this.gender = gender;
  }
  
  /**
   * Specifies the probability of the gender of this entity.
   * @param p the probability of the gender of this entity.
   */
  public void setGenderProbability(double p) {
    genderProb = p;
  }

  /**
   * Specifies the number of this entity.
   * @param number
   */
  public void setNumber(NumberEnum number) {
    this.number = number;
  }
  
  /**
   * Specifies the probability of the number of this entity.
   * @param p the probability of the number of this entity.
   */
  public void setNumberProbability(double p) {
    numberProb = p;
  }
}
