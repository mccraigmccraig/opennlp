package opennlp.tools.coref.mention;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides default implemenation of many of the methods in the {@link Parse} interface.  
 */
public abstract class AbstractParse implements Parse {

  public boolean isCoordinatedNounPhrase() {
    List parts = getSyntacticChildren();
    if (parts.size() >= 2) {
      for (int pi = 1; pi < parts.size(); pi++) {
        Parse child = (Parse) parts.get(pi);
        String ctype = child.getSyntacticType();
        if (ctype != null && ctype.equals("CC") && !child.toString().equals("&")) {
          return true;
        }
      }
    }
    return false;
  }

  public List getNounPhrases() {
    List parts = getSyntacticChildren();
    List nps = new ArrayList();
    while (parts.size() > 0) {
      List newParts = new ArrayList();
      for (int pi=0,pn=parts.size();pi<pn;pi++) {
        //System.err.println("AbstractParse.getNounPhrases "+parts.get(pi).getClass());
        Parse cp = (Parse) parts.get(pi);
        if (cp.isNounPhrase()) {
          nps.add(cp);
        }
        if (!cp.isToken()) {
          newParts.addAll(cp.getSyntacticChildren());
        }
      }
      parts = newParts;
    }
    return nps;
  }
  
  

}
