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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PTBMentionFinder extends AbstractMentionFinder {

  static MentionFinder ef = null;

  private PTBMentionFinder(HeadFinder hf) { 
    collectPrenominalNamedEntities = false;
    headFinder = hf;
  }

  public static MentionFinder getInstance(HeadFinder hf) {
    if (ef == null) {
      ef = new PTBMentionFinder(hf);
    }
    return (ef);
  }
  
  private boolean isTraceNp(Parse np){
    List sc = np.getSyntacticChildren();
    return (sc.size() == 0);
  }

  protected List getNounPhrases(Parse p) {
    List nps = new ArrayList(p.getNounPhrases());
    for (int npi = 0; npi < nps.size(); npi++) {
      Parse np = (Parse) nps.get(npi);
      if (!isTraceNp(np)) {
        if (np.getChildren().size()!=0) {
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

  protected List getNamedEntities(Parse p) {
    List nes = new ArrayList(p.getNamedEntities());
    for (int nei = 0; nei < nes.size(); nei++) {
      Parse ne = (Parse) nes.get(nei);
      nes.addAll(ne.getNamedEntities());
    }
    return (nes);
  }

  /** Moves entity ids assigned to basal nps and possesives to their
   * maximaly containing np.  Also assign head information of basal
   * noun phase to the maximally containing np.
   * @deprecated No on uses this any more.
   */
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
}
