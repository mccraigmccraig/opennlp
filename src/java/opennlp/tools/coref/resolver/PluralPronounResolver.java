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
package opennlp.tools.coref.resolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.coref.DiscourseEntity;
import opennlp.tools.coref.Linker;
import opennlp.tools.coref.mention.MentionContext;

/**
 * Resolves coreference between plural pronouns and their referents.
 */
public class PluralPronounResolver extends MaxentResolver {

  int NUM_SENTS_BACK_PRONOUNS = 2;
  
  public PluralPronounResolver(String projectName, ResolverMode m) throws IOException {
    super(projectName, "tmodel", m, 30);
  }
  
  public PluralPronounResolver(String projectName, ResolverMode m,NonReferentialResolver nrr) throws IOException {
    super(projectName, "tmodel", m, 30,nrr);
  }

  protected List getFeatures(MentionContext mention, DiscourseEntity entity) {
    List features = new ArrayList();
    features.addAll(super.getFeatures(mention,entity));
    //features.add("eid="+pc.id);
    if (entity != null) { //generate pronoun w/ referent features
      features.addAll(getPronounMatchFeatures(mention,entity));
      MentionContext cec = entity.getLastExtent();      
      features.addAll(getDistanceFeatures(mention,entity));
      features.addAll(getContextFeatures(cec));
      features.add(getMentionCountFeature(entity));
      /*
      //lexical features
      Set featureSet = new HashSet();
      for (Iterator ei = entity.getExtents(); ei.hasNext();) {
        MentionContext ec = (MentionContext) ei.next();
        int headIndex = PTBHeadFinder.getInstance().getHeadIndex(ec.tokens);
        Parse tok = (Parse) ec.tokens.get(headIndex);
        featureSet.add("hw=" + tok.toString().toLowerCase());
        if (ec.parse.isCoordinatedNounPhrase()) {
          featureSet.add("ht=CC");
        }
        else {
          featureSet.add("ht=" + tok.getSyntacticType());
        }
        if (ec.neType != null){
          featureSet.add("ne="+ec.neType);
        }
      }
      Iterator fset = featureSet.iterator();
      while (fset.hasNext()) {
        String f = (String) fset.next();
        features.add(f);
      }
      */
    }
    return (features);
  }
  
  protected boolean outOfRange(MentionContext mention, DiscourseEntity entity) {
    MentionContext cec = entity.getLastExtent();
    //System.err.println("MaxentPluralPronounResolver.outOfRange: ["+ec.toText()+" ("+ec.id+")] ["+cec.toText()+" ("+cec.id+")] ec.sentenceNumber=("+ec.sentenceNumber+")-cec.sentenceNumber=("+cec.sentenceNumber+") > "+NUM_SENTS_BACK_PRONOUNS);    
    return (mention.getSentenceNumber() - cec.getSentenceNumber() > NUM_SENTS_BACK_PRONOUNS);
  }

  public boolean canResolve(MentionContext mention) {
    String tag = mention.getHeadTokenTag();
    return (tag != null && tag.startsWith("PRP") && Linker.pluralThirdPersonPronounPattern.matcher(mention.getHeadTokenText()).matches());
  }
}
