package opennlp.tools.namefind;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.ngram.Token;
import opennlp.tools.util.Span;

public class NameSample {

  public static final String START_TAG = "<START>";

  public static final String END_TAG = "<END>";

  private final Token sentence[];

  private final Span names[];

  private final String additionalContext[][];

  private final boolean isClearAdaptiveData;

  /**
   * Initializes the current instance. 
   * 
   * @param sentence training sentence
   * @param names 
   * @param additionalContext
   * @param clearAdaptiveData if true the adaptive data of the feature generators is cleared
   */
  public NameSample(Token sentence[], Span names[],
      String additionalContext[][], boolean clearAdaptiveData) {

    if (sentence == null) {
      throw new IllegalArgumentException();
    }

    if (names == null) {
      names = new Span[0];
    }

    this.sentence = sentence;
    this.names = names;
    this.additionalContext = additionalContext;
    isClearAdaptiveData = clearAdaptiveData;
  }

  /**
   * Initializes the current instance.
   * 
   * @param sentence
   * @param names
   * @param clearAdaptiveData
   */
  public NameSample(Token sentence[], Span[] names, boolean clearAdaptiveData) {
    this(sentence, names, null, clearAdaptiveData);
  }

  /**
   * Initializes the current instance.
   * 
   * @param taggedTokens
   * @param clearAdaptiveData
   */
  public NameSample(String taggedTokens, boolean clearAdaptiveData) {

    String[] parts = taggedTokens.split(" ");

    List tokenList = new ArrayList(parts.length);
    List nameList = new ArrayList();

    int startIndex = -1;

    for (int pi = 0; pi < parts.length; pi++) {
      if (parts[pi].equals(START_TAG)) {
        startIndex = pi;
      } else if (parts[pi].equals(END_TAG)) {
        // create name
        nameList.add(new Span(startIndex, pi));
      } else {
        tokenList.add(Token.create(parts[pi]));
      }
    }

    sentence = (Token[]) tokenList.toArray(new Token[tokenList.size()]);

    names = (Span[]) nameList.toArray(new Span[nameList.size()]);

    this.additionalContext = null;
    
    isClearAdaptiveData = false;
  }

  public Token[] sentence() {
    return sentence;
  }

  public Span[] names() {
    return names;
  }

  public String[][] additionalContext() {
    return additionalContext;
  }
  
  public boolean isClearAdaptiveDataSet() {
    return isClearAdaptiveData;
  }

  public String toString() {

    Token sentence[] = sentence();

    StringBuilder result = new StringBuilder();

    for (int tokenIndex = 0; tokenIndex < sentence.length; tokenIndex++) {
      // token

      for (int nameIndex = 0; nameIndex < names.length; nameIndex++) {
        if (names[nameIndex].getStart() == tokenIndex) {
          result.append(START_TAG + ' ');
        }

        if (names[nameIndex].getEnd() == tokenIndex) {
          result.append(END_TAG + ' ');
        }
      }

      result.append(sentence[tokenIndex].getToken() + ' ');
    }

    for (int nameIndex = 0; nameIndex < names.length; nameIndex++) {
      if (names[nameIndex].getEnd() == sentence.length) {
        result.append(END_TAG + ' ');
      }
    }

    result.setLength(result.length() - 1);

    return result.toString();
  }
}