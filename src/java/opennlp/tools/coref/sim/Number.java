package opennlp.tools.coref.sim;

public class Number {
  private NumberEnum type;
  private double confidence;
  
  public Number(NumberEnum type,double confidence) {
    this.type = type;
    this.confidence = confidence;
  }
  
  public NumberEnum getType() {
    return type;
  }
  
  public double getConfidence() {
    return confidence;
  }
}
