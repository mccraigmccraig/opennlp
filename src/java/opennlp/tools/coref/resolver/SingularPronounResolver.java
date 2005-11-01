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
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import opennlp.tools.coref.DiscourseEntity;
import opennlp.tools.coref.Linker;
import opennlp.tools.coref.mention.MentionContext;

/**
 * This class resolver singlular pronouns such as "he", "she", "it" and their various forms. 
 */
public class SingularPronounResolver extends MaxentResolver {

  int mode;

  Pattern PronounPattern;

  public SingularPronounResolver(String projectName, ResolverMode m) throws IOException {
    super(projectName, "pmodel", m, 30);
    this.numSentencesBack = 2;
  }
  
  public SingularPronounResolver(String projectName, ResolverMode m, NonReferentialResolver nonReferentialResolver) throws IOException {
    super(projectName, "pmodel", m, 30,nonReferentialResolver);
    this.numSentencesBack = 2;
  }

  public boolean canResolve(MentionContext mention) {
    //System.err.println("MaxentSingularPronounResolver.canResolve: ec= ("+mention.id+") "+ mention.toText());
    String tag = mention.getHeadTokenTag();
    return (tag != null && tag.startsWith("PRP") && Linker.singularThirdPersonPronounPattern.matcher(mention.getHeadTokenText()).matches());
  }

  protected List getFeatures(MentionContext mention, DiscourseEntity entity) {
    List features = new ArrayList();
    features.addAll(super.getFeatures(mention, entity));
    if (entity != null) { //generate pronoun w/ referent features
      MentionContext cec = entity.getLastExtent();
      //String gen = getPronounGender(pronoun);
      features.addAll(getPronounMatchFeatures(mention,entity));
      features.addAll(getContextFeatures(cec));      
      features.addAll(getDistanceFeatures(mention,entity));      
      features.add(getMentionCountFeature(entity));
      /*
      //lexical features
      Set featureSet = new HashSet();
      for (Iterator ei = entity.getExtents(); ei.hasNext();) {
        MentionContext ec = (MentionContext) ei.next();
        List toks = ec.tokens;
        Parse tok;
        int headIndex = PTBHeadFinder.getInstance().getHeadIndex(toks);
        for (int ti = 0; ti < headIndex; ti++) {
          tok = (Parse) toks.get(ti);
          featureSet.add(gen + "mw=" + tok.toString().toLowerCase());
          featureSet.add(gen + "mt=" + tok.getSyntacticType());
        }
        tok = (Parse) toks.get(headIndex);
        featureSet.add(gen + "hw=" + tok.toString().toLowerCase());
        featureSet.add(gen + "ht=" + tok.getSyntacticType());
        //semantic features
        if (ec.neType != null) {
          featureSet.add(gen + "," + ec.neType);
        }
        else {
          for (Iterator si = ec.synsets.iterator(); si.hasNext();) {
            Integer synset = (Integer) si.next();
            featureSet.add(gen + "," + synset);
          }
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

  public boolean excluded(MentionContext mention, DiscourseEntity entity) {
    if (super.excluded(mention, entity)) {
      return (true);
    }
    String mentionGender = null;

    for (Iterator ei = entity.getMentions(); ei.hasNext();) {
      MentionContext entityMention = (MentionContext) ei.next();
      String tag = entityMention.getHeadTokenTag();
      if (tag != null && tag.startsWith("PRP") && Linker.singularThirdPersonPronounPattern.matcher(mention.getHeadTokenText()).matches()) {
        if (mentionGender == null) { //lazy initilization
          mentionGender = getPronounGender(mention.getHeadTokenText());
        }
        String entityGender = getPronounGender(entityMention.getHeadTokenText());
        if (!entityGender.equals("u") && !mentionGender.equals(entityGender)) {
          return (true);
        }
      }
    }
    return (false);
  }

  protected boolean outOfRange(MentionContext mention, DiscourseEntity entity) {
    MentionContext cec = entity.getLastExtent();
    //System.err.println("MaxentSingularPronounresolve.outOfRange: ["+entity.getLastExtent().toText()+" ("+entity.getId()+")] ["+mention.toText()+" ("+mention.getId()+")] entity.sentenceNumber=("+entity.getLastExtent().getSentenceNumber()+")-mention.sentenceNumber=("+mention.getSentenceNumber()+") > "+numSentencesBack);    
    return (mention.getSentenceNumber() - cec.getSentenceNumber() > numSentencesBack);
  }

  /*
  public boolean definiteArticle(String tok, String tag) {
    tok = tok.toLowerCase();
    if (tok.equals("the") || tok.equals("these")) {
      //|| tok.equals("these") || tag.equals("PRP$")) {
      return (true);
    }
    return (false);
  }
  */
}
