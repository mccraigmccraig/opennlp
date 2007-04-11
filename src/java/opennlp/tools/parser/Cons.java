package opennlp.tools.parser;
/**
 * Class to hold feature information about a specific parse node.
 * @author tsmorton
 *
 */
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
