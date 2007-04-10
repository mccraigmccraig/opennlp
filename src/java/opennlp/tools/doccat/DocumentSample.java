package opennlp.tools.doccat;

import opennlp.tools.tokenize.SimpleTokenizer;

public class DocumentSample {
  
  private String category;
  private String text[];
  
  public DocumentSample(String category, String text) {
    this(category, new SimpleTokenizer().tokenize(text));
  }
  
  public DocumentSample(String category, String text[]) {
    if (category == null || text == null) {
      throw new IllegalArgumentException();
    }
    
    this.category = category;
    this.text = text;
  }
  
  String getCategory() {
    return category;
  }
  
  String[] getText() {
    return text;
  }
}
