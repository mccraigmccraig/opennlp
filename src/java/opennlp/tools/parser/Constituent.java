package opennlp.tools.parser;

import opennlp.tools.util.Span;

/**
 * Class used to hold constituents when reading parses.
 */
public class Constituent {

  private String label;
  private Span span;
  
  public Constituent(String label, Span span) {
    this.label = label;
    this.span = span;
  }


  /**
   * Returns the label of the constituent.
   * @return the label of the constituent.
   */
  public String getLabel() {
    return label;
  }


  /**
   * Assigns the label to the constituent.
   * @param label The label to set.
   */
  public void setLabel(String label) {
    this.label = label;
  }


  /**
   * Returns the span of the constituent.
   * @return the span of the constituent.
   */
  public Span getSpan() {
    return span;
  }
}
