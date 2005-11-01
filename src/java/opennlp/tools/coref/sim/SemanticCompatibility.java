package opennlp.tools.coref.sim;

public class SemanticCompatibility {

  private SemanticEnum type;
  private double confidence;
  
  public SemanticCompatibility(SemanticEnum type,double confidence) {
    this.type = type;
    this.confidence = confidence;
  }
  
  public SemanticEnum getType() {
    return type;
  }
  
  public double getConfidence() {
    return confidence;
  }
}
