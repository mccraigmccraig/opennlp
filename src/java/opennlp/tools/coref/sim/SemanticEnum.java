package opennlp.tools.coref.sim;

public class SemanticEnum {

  private String compatibility;
  
  /** Semantically compatible. */
  public static final SemanticEnum COMPATIBLE = new SemanticEnum("compatible");
  /** Semantically incompatible. */
  public static final SemanticEnum INCOMPATIBLE = new SemanticEnum("incompatible");
  /** Semantic compatibility Unknown. */
  public static final SemanticEnum UNKNOWN = new SemanticEnum("unknown");
  
  private SemanticEnum(String g) {
    compatibility = g;
  }
  
  public String toString() {
    return compatibility;
  }
}
