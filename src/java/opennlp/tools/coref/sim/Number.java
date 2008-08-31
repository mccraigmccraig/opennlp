package opennlp.tools.coref.sim;
/**
 * Class which models the number of an enity and the confidence of that association.
 */
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
