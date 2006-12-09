package opennlp.tools.namefind;

public class Annotation {
  private int mBegin;
  private int mEnd;
  
  Annotation(int begin, int end) {
    mBegin = begin;
    mEnd = end;
  }
  
  int getBegin() {
    return mBegin;
  }
  
  int getEnd() {
    return mEnd;
  }
}