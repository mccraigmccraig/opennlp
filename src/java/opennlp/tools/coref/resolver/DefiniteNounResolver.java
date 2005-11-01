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
import opennlp.tools.coref.mention.MentionContext;
import opennlp.tools.coref.mention.Parse;

/**
 * Resolves coreference between definite noun-phrases. 
 */
public class DefiniteNounResolver extends MaxentResolver {

  public DefiniteNounResolver(String projectName, ResolverMode m) throws IOException {
    super(projectName, "defmodel", m, 80);
    //preferFirstReferent = true;
  }
  
  public DefiniteNounResolver(String projectName, ResolverMode m, NonReferentialResolver nrr) throws IOException {
    super(projectName, "defmodel", m, 80,nrr);
    //preferFirstReferent = true;
  }


  public boolean canResolve(MentionContext mention) {
    Object[] mtokens = mention.getTokens();

    String firstTok = mention.getFirstTokenText().toLowerCase();
    boolean rv = mtokens.length > 1 && !mention.getHeadTokenTag().startsWith("NNP") && definiteArticle(firstTok, mention.getFirstTokenTag());
    //if (rv) {
    //  System.err.println("defNp "+ec);
    //}
    return (rv);
  }

  protected List getFeatures(MentionContext mention, DiscourseEntity entity) {
    List features = new ArrayList();
    features.addAll(super.getFeatures(mention, entity));
    if (entity != null) {
      features.addAll(getContextFeatures(mention));
      features.addAll(getStringMatchFeatures(mention,entity));
      features.addAll(getDistanceFeatures(mention,entity));
    }
    return (features);
  }
}
