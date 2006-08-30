package opennlp.tools.parser;

public class Cons {

  String cons;
  String consbo;
  int index;
  boolean unigram;
  
  public Cons(String cons, String consbo, int index, boolean unigram) {
    this.cons = cons;
    this.consbo = consbo;
    this.index = index;
    this.unigram = unigram;
  }
}
