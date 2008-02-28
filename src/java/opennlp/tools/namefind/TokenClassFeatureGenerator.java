package opennlp.tools.namefind;

import java.util.List;


/**
 * Generates features for different for the class of the token.
 */
public class TokenClassFeatureGenerator extends FeatureGeneratorAdapter {

  private static final String TOKEN_CLASS_PREFIX = "wc";
  private static final String TOKEN_AND_CLASS_PREFIX = "w&c";

  private boolean generateWordAndClassFeature;
  
  public TokenClassFeatureGenerator() {
    this(false);
  }
  
  public TokenClassFeatureGenerator(boolean genearteWordAndClassFeature) {
    this.generateWordAndClassFeature = genearteWordAndClassFeature;
  }
  
  public void createFeatures(List features, String[] tokens, int index, String[] preds) {
    String wordClass = FeatureGeneratorUtil.tokenFeature(tokens[index]);
    features.add(TOKEN_CLASS_PREFIX + "=" + wordClass);
    if (generateWordAndClassFeature) {
      features.add(TOKEN_AND_CLASS_PREFIX + "=" + tokens[index].toLowerCase()+","+wordClass);
    }
  }
}
