package opennlp.tools.coref.sim;

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
