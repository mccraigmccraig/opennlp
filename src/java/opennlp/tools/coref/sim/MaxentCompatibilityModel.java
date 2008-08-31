package opennlp.tools.coref.sim;

import java.io.IOException;

/**
 * Model of mention compatibiltiy using a maxent model.
 */
public class MaxentCompatibilityModel {
  
  private final double minGenderProb = 0.66;
  private final double minNumberProb = 0.66;
  
  private static TestGenderModel genModel;
  private static TestNumberModel numModel;
  
  private boolean debugOn = false;
  
  public MaxentCompatibilityModel(String corefProject) throws IOException {
    genModel = GenderModel.testModel(corefProject + "/gen");
    numModel = NumberModel.testModel(corefProject + "/num");
  }

  public Gender computeGender(Context c) {
    Gender gender;
    double[] gdist = genModel.genderDistribution(c);
    if (debugOn) {
      System.err.println("MaxentCompatibilityModel.computeGender: "+c.toString()+" m="+gdist[genModel.getMaleIndex()]+" f="+gdist[genModel.getFemaleIndex()]+" n="+gdist[genModel.getNeuterIndex()]);
    }
    if (genModel.getMaleIndex() >= 0 && gdist[genModel.getMaleIndex()] > minGenderProb) {
      gender = new Gender(GenderEnum.MALE,gdist[genModel.getMaleIndex()]);
    }
    else if (genModel.getFemaleIndex() >= 0 && gdist[genModel.getFemaleIndex()] > minGenderProb) {
      gender = new Gender(GenderEnum.FEMALE,gdist[genModel.getFemaleIndex()]);
    }
    else if (genModel.getNeuterIndex() >= 0 && gdist[genModel.getNeuterIndex()] > minGenderProb) {
      gender = new Gender(GenderEnum.NEUTER,gdist[genModel.getNeuterIndex()]);
    }
    else {
      gender = new Gender(GenderEnum.UNKNOWN,minGenderProb);
    }
    return gender;
  }

  public Number computeNumber(Context c) {
    double[] dist = numModel.numberDist(c);
    Number number;
    //System.err.println("MaxentCompatibiltyResolver.computeNumber: "+c+" sing="+dist[numModel.getSingularIndex()]+" plural="+dist[numModel.getPluralIndex()]);
    if (dist[numModel.getSingularIndex()] > minNumberProb) {
      number = new Number(NumberEnum.SINGULAR,dist[numModel.getSingularIndex()]);
    }
    else if (dist[numModel.getPluralIndex()] > minNumberProb) {
      number = new Number(NumberEnum.PLURAL,dist[numModel.getPluralIndex()]);
    }
    else {
      number = new Number(NumberEnum.UNKNOWN,minNumberProb);
    }
    return number;
  }
}
