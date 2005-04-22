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
 * Finds mentions from shallow np-chunking based parses. 
 */
public class ShallowParseMentionFinder extends AbstractMentionFinder {
  
  private static ShallowParseMentionFinder instance;

  private ShallowParseMentionFinder(HeadFinder hf) {
    headFinder = hf;
    collectPrenominalNamedEntities=true;
    collectCoordinatedNounPhrases=true;
  }
  
  public static ShallowParseMentionFinder getInstance(HeadFinder hf) {
    if (instance == null) {
      instance = new ShallowParseMentionFinder(hf);
    }
    else if (instance.headFinder != hf) {
      instance = new ShallowParseMentionFinder(hf);
    }
    return(instance);
  }

  /*
  protected final List getNounPhrases(Parse p) {
    List nps = p.getNounPhrases();
    List basals = new ArrayList();
    for (int ni=0,ns=nps.size();ni<ns;ni++) {
      Parse np = (Parse) nps.get(ni);
      //System.err.println("getNounPhrases: np="+np);
      if (isBasalNounPhrase(np)) {
        //System.err.println("basal");
        basals.add(np);
      }
      else if (isPossessive(np)) {
        //System.err.println("pos np");
        basals.add(np);
        basals.addAll(getNounPhrases(np));
      }
      else if (isOfPrepPhrase(np)) {
        //System.err.println("of np");
        basals.add(np);
        basals.addAll(getNounPhrases(np));
      }
      else {
        //System.err.println("big np");
        basals.addAll(getNounPhrases(np));
      }
    }
    return(basals);
  }
  */
}
