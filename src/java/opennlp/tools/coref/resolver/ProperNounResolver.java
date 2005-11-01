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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import opennlp.tools.coref.DiscourseEntity;
import opennlp.tools.coref.mention.MentionContext;

/**
 * Resolves coreference between proper nouns.
 */
public class ProperNounResolver extends MaxentResolver {

  private static final Pattern initialCaps = Pattern.compile("^[A-Z]");
  private static Map acroMap;
  private static boolean acroMapLoaded = false;

  public ProperNounResolver(String projectName, ResolverMode m) throws IOException {
    super(projectName,"pnmodel", m, 500);
    if (!acroMapLoaded) {
      initAcronyms(projectName + "/acronyms");
      acroMapLoaded = true;
    }
    showExclusions = false;
  }
  
  public ProperNounResolver(String projectName, ResolverMode m,NonReferentialResolver nonRefResolver) throws IOException {
    super(projectName,"pnmodel", m, 500,nonRefResolver);
    if (!acroMapLoaded) {
      initAcronyms(projectName + "/acronyms");
      acroMapLoaded = true;
    }
    showExclusions = false;
  }

  public boolean canResolve(MentionContext mention) {
    return (mention.getHeadTokenTag().startsWith("NNP") || mention.getHeadTokenTag().startsWith("CD"));
  }

  private void initAcronyms(String name) {
    acroMap = new HashMap(15000);
    try {
      BufferedReader str;
      if (MaxentResolver.loadAsResource()) {
        str = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(name)));
      }
      else {
        str = new BufferedReader(new FileReader(name));
      }
      //System.err.println("Reading acronyms database: " + file + " ");
      String line;
      while (null != (line = str.readLine())) {
        StringTokenizer st = new StringTokenizer(line, "\t");
        String acro = st.nextToken();
        String full = st.nextToken();
        Set exSet = (Set) acroMap.get(acro);
        if (exSet == null) {
          exSet = new HashSet();
          acroMap.put(acro, exSet);
        }
        exSet.add(full);
        exSet = (Set) acroMap.get(full);
        if (exSet == null) {
          exSet = new HashSet();
          acroMap.put(full, exSet);
        }
        exSet.add(acro);
      }
    }
    catch (IOException e) {
      System.err.println("ProperNounResolver.initAcronyms: Acronym Database not found: " + e);
    }
  }

  private MentionContext getProperNounExtent(DiscourseEntity de) {
    for (Iterator ei = de.getMentions(); ei.hasNext();) { //use first extent which is propername
      MentionContext xec = (MentionContext) ei.next();
      String xecHeadTag = xec.getHeadTokenTag();
      if (xecHeadTag.startsWith("NNP") || initialCaps.matcher(xec.getHeadTokenText()).find()) {
        return xec;
      }
    }
    return null;
  }


  private boolean isAcronym(String ecStrip, String xecStrip) {
    Set exSet = (Set) acroMap.get(ecStrip);
    if (exSet != null && exSet.contains(xecStrip)) {
      return true;
    }
    return false;
  }

  protected List getAcronymFeatures(MentionContext mention, DiscourseEntity entity) {
    MentionContext xec = getProperNounExtent(entity);
    String ecStrip = stripNp(mention);
    String xecStrip = stripNp(xec);
    if (ecStrip != null && xecStrip != null) {
      if (isAcronym(ecStrip, xecStrip)) {
        List features = new ArrayList(1);        
        features.add("knownAcronym");
        return features;
      }
    }
    return Collections.EMPTY_LIST;
  }

  protected List getFeatures(MentionContext mention, DiscourseEntity entity) {
    //System.err.println("ProperNounResolver.getFeatures: "+mention.toText()+" -> "+entity);
    List features = new ArrayList();
    features.addAll(super.getFeatures(mention, entity));
    if (entity != null) {
      features.addAll(getStringMatchFeatures(mention, entity));
      features.addAll(getAcronymFeatures(mention, entity));
    }
    return features;
  }

  public boolean excluded(MentionContext mention, DiscourseEntity entity) {
    if (super.excluded(mention, entity)) {
      return (true);
    }
    for (Iterator ei = entity.getMentions(); ei.hasNext();) {
      MentionContext xec = (MentionContext) ei.next();
      if (xec.getHeadTokenTag().startsWith("NNP")) { // || initialCaps.matcher(xec.headToken.toString()).find()) {
        //System.err.println("MaxentProperNounResolver.exclude: kept "+xec.toText()+" with "+xec.headTag);
        return (false);
      }
    }
    return (true);
  }
}
