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

import opennlp.tools.coref.sim.GenderEnum;
import opennlp.tools.coref.sim.NumberEnum;

public class DiscourseEntity extends DiscourseElement {

  private String category = null;
  private Set synsets;
  private GenderEnum gender;
  private double genderProb;
  private NumberEnum number;
  private double numberProb;

  public DiscourseEntity(MentionContext ec, GenderEnum g, double gp, NumberEnum n, double np) {
    super(ec);
    gender = g;
    genderProb = gp;
    number = n;
    numberProb = np;
  }

  public DiscourseEntity(MentionContext ec) {
    super(ec);
    gender = GenderEnum.UNKNOWN;
    number = NumberEnum.UNKNOWN;
  }

  public String getCategory() {
    return (category);
  }

  public void setCategory(String cat) {
    category = cat;
  }

  public Set getSynsets() {
    return (synsets);
  }

  public GenderEnum getGender() {
    return gender;
  }
  
  public double getGenderProbability() {
    return genderProb;
  }
  
  public NumberEnum getNumber() {
    return number;
  }
  
  public double getNumberProbability() {
    return numberProb;
  }

  public void setGender(GenderEnum gender) {
    this.gender = gender;
  }
  
  public void setGenderProbability(double p) {
    genderProb = p;
  }

  public void setNumber(NumberEnum number) {
    this.number = number;
  }
  
  public void setNumberProbability(double p) {
    numberProb = p;
  }
}
