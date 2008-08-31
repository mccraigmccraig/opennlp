package opennlp.tools.coref.sim;

/**
 * Class which models the semantic compatibility of an enity and the confidence of that association.
 */
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
