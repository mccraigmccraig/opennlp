///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2003 Thomas Morton
//
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////
package opennlp.tools.coref.mention;

/**
 * Finds mentions from Penn Treebank style parses. 
 */
public class PTBMentionFinder extends AbstractMentionFinder {

  private static PTBMentionFinder instance = null;

  /**
   * Creates a new mention finder with the specified head finder.
   * @param hf The head finder.
   */
  private PTBMentionFinder(HeadFinder hf) { 
    collectPrenominalNamedEntities = false;
    collectCoordinatedNounPhrases = true;
    headFinder = hf;
  }
  
  /**
   * Retrives the one and only existing instance.
   * 
   * @param hf
   * @return the one and only existing instance
   */
  public static PTBMentionFinder getInstance(HeadFinder hf) {
    if (instance == null) {
      instance = new PTBMentionFinder(hf);
    }
    else if (instance.headFinder != hf) {
      instance = new PTBMentionFinder(hf);
    }
    return instance;
  }
  
  
  
  
  /*
  private boolean isTraceNp(Parse np){
    List sc = np.getSyntacticChildren();
    return (sc.size() == 0);
  }

  protected List getNounPhrases(Parse p) {
    List nps = new ArrayList(p.getNounPhrases());
    for (int npi = 0; npi < nps.size(); npi++) {
      Parse np = (Parse) nps.get(npi);
      if (!isTraceNp(np)) {
        if (np.getSyntacticChildren().size()!=0) {
          List snps = np.getNounPhrases();
          for (int snpi=0,snpl=snps.size();snpi<snpl;snpi++) {
            Parse snp = (Parse) snps.get(snpi);
            if (!snp.isParentNAC() && !isTraceNp(snp)) {
              nps.add(snp);
            }
          }
        }
      }
      else {
        nps.remove(npi);
        npi--;
      }
    }
    return (nps);
  }
  */

  /** Moves entity ids assigned to basal nps and possesives to their
   * maximaly containing np.  Also assign head information of basal
   * noun phase to the maximally containing np.
   * @deprecated No on uses this any more.
   *
  private void propigateEntityIds(Map headMap) {
    for (Iterator ki = headMap.keySet().iterator(); ki.hasNext();) {
      Parse np = (Parse) ki.next();
      if (isBasalNounPhrase(np) || isPossessive(np)) {
        int ei = np.getEntityId();
        if (ei != -1) {
          Parse curHead = np;
          Parse newHead = null;
          while ((newHead = (Parse) headMap.get(curHead)) != null) {
            curHead.removeEntityId();
            curHead = newHead;
          }
          curHead.setEntityId(ei);
          curHead.setProperty("head", np.getSpan().toString());
        }
      }
    }
  }
  */
}
