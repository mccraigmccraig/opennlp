package opennlp.tools.coref.sim;

/**
 * Class which models the gender of an enity and the confidence of that association.
 */
public class Gender {

  private GenderEnum type;
  private double confidence;
  
  public Gender(GenderEnum type,double confidence) {
    this.type = type;
    this.confidence = confidence;
  }
  
  public GenderEnum getType() {
    return type;
  }
  
  public double getConfidence() {
    return confidence;
  }
}
