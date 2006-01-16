package opennlp.tools.sentdetect;

import java.util.ArrayList;
import java.util.List;

import opennlp.maxent.IntegerPool;

public abstract class AbstractEndOfSentenceScanner implements EndOfSentenceScanner {

  protected static final IntegerPool INT_POOL = new IntegerPool(500);
  
  public List getPositions(String s) {
    return getPositions(s.toCharArray());
  }

  public List getPositions(StringBuffer buf) {
    return getPositions(buf.toString().toCharArray());
  }

  public List getPositions(char[] cbuf) {
    List l = new ArrayList();
    char[] eosCharacters = getEndOfSentenceCharacters();
    for (int i = 0; i < cbuf.length; i++) {
      for (int ci=0;ci<eosCharacters.length;ci++) {
        if (cbuf[i] == eosCharacters[ci]) {
          //System.err.println("getPositions: adding "+i+" for "+ci);
            l.add(INT_POOL.get(i));
            break;
        }
      }
    }
    return l;
  }
}
