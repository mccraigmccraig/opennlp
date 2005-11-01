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
package opennlp.tools.coref.sim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import opennlp.maxent.Event;
import opennlp.maxent.GIS;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.PlainTextGISModelReader;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.maxent.io.SuffixSensitiveGISModelWriter;
import opennlp.tools.coref.resolver.AbstractResolver;
import opennlp.tools.coref.resolver.MaxentResolver;
import opennlp.tools.util.CollectionEventStream;
import opennlp.tools.util.HashList;

/**
 * Models semantic similarity between two mentions and returns a score based on 
 * how semantically comparible the mentions are with one another.  
 */
public class SimilarityModel implements TestSimilarityModel, TrainSimilarityModel {

  private String modelName;
  private String modelExtension = ".bin.gz";
  private MaxentModel testModel;
  private List events;
  private int SAME_INDEX;
  private static final String SAME = "same";
  private static final String DIFF = "diff";
  private boolean debugOn = false;

  public static TestSimilarityModel testModel(String name) throws IOException {
    return new SimilarityModel(name, false);
  }

  public static TrainSimilarityModel trainModel(String name) throws IOException {
    SimilarityModel sm = new SimilarityModel(name, true);
    return sm;
  }

  private SimilarityModel(String modelName, boolean train) throws IOException {
    this.modelName = modelName; 
    if (train) {
      events = new ArrayList();
    }
    else {
      if (MaxentResolver.loadAsResource()) {
        testModel = (new PlainTextGISModelReader(new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(modelName))))).getModel();
      }
      else {
        testModel = (new SuffixSensitiveGISModelReader(new File(modelName+modelExtension))).getModel();
      }
      SAME_INDEX = testModel.getIndex(SAME);
    }
  }

  private void addEvent(boolean same, Context np1, Context np2) {
    if (same) {
      List feats = getFeatures(np1, np2);
      //System.err.println(SAME+" "+np1.headTokenText+" ("+np1.id+") -> "+np2.headTokenText+" ("+np2.id+") "+feats);
      events.add(new Event(SAME, (String[]) feats.toArray(new String[feats.size()])));
    }
    else {
      List feats = getFeatures(np1, np2);
      //System.err.println(DIFF+" "+np1.headTokenText+" ("+np1.id+") -> "+np2.headTokenText+" ("+np2.id+") "+feats);
      events.add(new Event(DIFF, (String[]) feats.toArray(new String[feats.size()])));
    }
  }

  /**
   * Produces a set of head words for the specified list of mentions.
   * @param mentions The mentions to use to construct the 
   * @return A set containing the head words of the sepecified mentions.
   */
  private Set constructHeadSet(List mentions) {
    Set headSet = new HashSet();
    for (Iterator ei = mentions.iterator(); ei.hasNext();) {
      Context ec = (Context) ei.next();
      headSet.add(ec.getHeadTokenText().toLowerCase());
    }
    return headSet;
  }

  private boolean hasSameHead(Set entityHeadSet, Set candidateHeadSet) {
    for (Iterator hi = entityHeadSet.iterator(); hi.hasNext();) {
      if (candidateHeadSet.contains(hi.next())) {
        return true;
      }
    }
    return false;
  }

  private boolean hasSameNameType(Set entityNameSet, Set candidateNameSet) {
    for (Iterator hi = entityNameSet.iterator(); hi.hasNext();) {
      if (candidateNameSet.contains(hi.next())) {
        return true;
      }
    }
    return false;
  }

  private boolean hasSuperClass(List entityContexts, List candidateContexts) {
    for (Iterator ei = entityContexts.iterator(); ei.hasNext();) {
      Context ec = (Context) ei.next();
      for (Iterator cei = candidateContexts.iterator(); cei.hasNext();) {
        if (inSuperClass(ec, (Context) cei.next())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Constructs a set of entities which may be semantically compatible with the entity indicated by the specified entityKey.
   * @param entityKey The key of the entity for which the set is being constructed. 
   * @param entities A mapping between entity keys and their meantions. 
   * @param headSets A mapping between entity keys and their head sets.
   * @param nameSets A mapping between entity keys and their name sets.
   * @param singletons A list of all entities which consists of a single mentions.
   * @return A set of mentions for all the entities which might be semantically compatible 
   * with entity indicated by the specified key. 
   */
  private Set constructExclusionSet(Integer entityKey, HashList entities, Map headSets, Map nameSets, List singletons) {
    Set exclusionSet = new HashSet();
    Set entityHeadSet = (Set) headSets.get(entityKey);
    Set entityNameSet = (Set) nameSets.get(entityKey);
    List entityContexts = (List) entities.get(entityKey);
    //entities
    for (Iterator ei = entities.keySet().iterator(); ei.hasNext();) {
      Integer key = (Integer) ei.next();
      List candidateContexts = (List) entities.get(key);
      if (key.equals(entityKey)) {
        exclusionSet.addAll(candidateContexts);
      }
      else if (((Set) nameSets.get(key)).isEmpty()) {
        exclusionSet.addAll(candidateContexts);
      }
      else if (hasSameHead(entityHeadSet, (Set) headSets.get(key))) {
        exclusionSet.addAll(candidateContexts);
      }
      else if (hasSameNameType(entityNameSet, (Set) nameSets.get(key))) {
        exclusionSet.addAll(candidateContexts);
      }
      else if (hasSuperClass(entityContexts, candidateContexts)) {
        exclusionSet.addAll(candidateContexts);
      }
    }
    //singles
    List singles = new ArrayList(1);
    for (Iterator si = singletons.iterator(); si.hasNext();) {
      Context sc = (Context) si.next();
      singles.clear();
      singles.add(sc);
      if (entityHeadSet.contains(sc.getHeadTokenText().toLowerCase())) {
        exclusionSet.add(sc);
      }
      else if (sc.getNameType() == null) {
        exclusionSet.add(sc);
      }
      else if (entityNameSet.contains(sc.getNameType())) {
        exclusionSet.add(sc);
      }
      else if (hasSuperClass(entityContexts, singles)) {
        exclusionSet.add(sc);
      }
    }
    return exclusionSet;
  }

  /**
   * Constructs a mapping between the specified entities and their head set.
   * @param entities Mapping between a key and a list of meanions which compose an entity.
   * @return a mapping between the keys of the secified entity mapping and the head set 
   * generatated from the mentions associated with that key.
   */
  private Map constructHeadSets(HashList entities) {
    Map headSets = new HashMap();
    for (Iterator ei = entities.keySet().iterator(); ei.hasNext();) {
      Integer key = (Integer) ei.next();
      List entityContexts = (List) entities.get(key);
      headSets.put(key, constructHeadSet(entityContexts));
    }
    return headSets;
  }

  /**
   * Produces the set of name types associated with each of the specified mentions.
   * @param mentions A list of mentions.
   * @return A set set of name types assigned to the specified mentions.
   */
  private Set constructNameSet(List mentions) {
    Set nameSet = new HashSet();
    for (Iterator ei = mentions.iterator(); ei.hasNext();) {
      Context ec = (Context) ei.next();
      if (ec.getNameType() != null) {
        nameSet.add(ec.getNameType());
      }
    }
    return nameSet;
  }

  /**
   * Constructs a mappng between the specified entities and the names associated with these entities.
   * @param entities A mapping between a key and a list of mentions.
   * @return a mapping between each key in the specified entity map and the name types associated with the each mention of that entity.
   */
  private Map constructNameSets(HashList entities) {
    Map nameSets = new HashMap();
    for (Iterator ei = entities.keySet().iterator(); ei.hasNext();) {
      Integer key = (Integer) ei.next();
      List entityContexts = (List) entities.get(key);
      nameSets.put(key, constructNameSet(entityContexts));
    }
    return nameSets;
  }

  private boolean inSuperClass(Context ec, Context cec) {
    if (ec.getSynsets().size() == 0 || cec.getSynsets().size() == 0) {
      return false;
    }
    else {
      int numCommonSynsets = 0;
      for (Iterator si = ec.getSynsets().iterator(); si.hasNext();) {
        Object synset = si.next();
        if (cec.getSynsets().contains(synset)) {
          numCommonSynsets++;
        }
      }
      if (numCommonSynsets == 0) {
        return false;
      }
      else if (numCommonSynsets == ec.getSynsets().size() || numCommonSynsets == cec.getSynsets().size()) {
        return true;
      }
      else {
        return false;
      }
    }
  }

  /*
  private boolean isPronoun(MentionContext mention) {
    return mention.getHeadTokenTag().startsWith("PRP");
  }
  */
  
  
  public void setExtents(Context[] extentContexts) {
    HashList entities = new HashList();
    /** Extents which are not in a coreference chain. */
    List singletons = new ArrayList();
    List allExtents = new ArrayList();
    //populate data structures
    for (int ei = 0, el = extentContexts.length; ei < el; ei++) {
      Context ec = extentContexts[ei];
      //System.err.println("SimilarityModel: setExtents: ec("+ec.getId()+") "+ec.getNameType()+" "+ec);
      if (ec.getId() == -1) {
        singletons.add(ec);
      }
      else {
        entities.put(new Integer(ec.getId()), ec);
      }
      allExtents.add(ec);
    }
    
    int axi = 0;
    Map headSets = constructHeadSets(entities);
    Map nameSets = constructNameSets(entities);
    
    for (Iterator ei = entities.keySet().iterator(); ei.hasNext();) {
      Integer key = (Integer) ei.next();
      Set entityNameSet = (Set) nameSets.get(key);
      if (entityNameSet.isEmpty()) {
        continue;
      }
      List entityContexts = (List) entities.get(key);      
      Set exclusionSet = constructExclusionSet(key, entities, headSets, nameSets, singletons);
      if (entityContexts.size() == 1) {
      }
      for (int xi1 = 0, xl = entityContexts.size(); xi1 < xl; xi1++) {
        Context ec1 = (Context) entityContexts.get(xi1);
        //if (isPronoun(ec1)) {
        //  continue;
        //}
        for (int xi2 = xi1 + 1; xi2 < xl; xi2++) {
          Context ec2 = (Context) entityContexts.get(xi2);
          //if (isPronoun(ec2)) {
          //  continue;
          //}
          addEvent(true, ec1, ec2);
          int startIndex = axi;
          do {
            Context sec1 = (Context) allExtents.get(axi);
            axi = (axi + 1) % allExtents.size();
            if (!exclusionSet.contains(sec1)) {
              if (debugOn) System.err.println(ec1.toString()+" "+entityNameSet+" "+sec1.toString()+" "+nameSets.get(new Integer(sec1.getId())));
              addEvent(false, ec1, sec1);
              break;
            }
          }
          while (axi != startIndex);
        }
      }
    }
  }

  /**
   * Returns a number between 0 and 1 which represents the models belief that the specified mentions are compatible.
   * Value closer to 1 are more compatible, while values closer to 0 are less compatible.
   * @param mention1 The first mention to be considered.
   * @param mention2 The second mention to be considered.
   * @return a number between 0 and 1 which represents the models belief that the specified mentions are compatible.
   */
  public double compatible(Context mention1, Context mention2) {
    List feats = getFeatures(mention1, mention2);
    if (debugOn) System.err.println("SimilarityModel.compatible: feats="+feats);
    return (testModel.eval((String[]) feats.toArray(new String[feats.size()]))[SAME_INDEX]);
  }

  /**
   * Train a model based on the previously supplied evidence.
   * @see #setExtents(Context[])
   */
  public void trainModel() throws IOException {
    if (debugOn) {
      FileWriter writer = new FileWriter(modelName+".events");
      for (Iterator ei=events.iterator();ei.hasNext();) {
        Event e = (Event) ei.next();
        writer.write(e.toString()+"\n");
      }
      writer.close();
    }
    (new SuffixSensitiveGISModelWriter(GIS.trainModel(new CollectionEventStream(events),100,10),new File(modelName+modelExtension))).persist();
  }

  private boolean isName(Context np) {
    return np.getHeadTokenTag().startsWith("NNP");
  }

  private boolean isCommonNoun(Context np) {
    return !np.getHeadTokenTag().startsWith("NNP") && np.getHeadTokenTag().startsWith("NN");
  }

  private boolean isPronoun(Context np) {
    return np.getHeadTokenTag().startsWith("PRP");
  }

  private boolean isNumber(Context np) {
    return np.getHeadTokenTag().equals("CD");
  }

  private List getNameCommonFeatures(Context name, Context common) {
    Set synsets = common.getSynsets();
    List features = new ArrayList(2 + synsets.size());
    features.add("nn=" + name.getNameType() + "," + common.getNameType());
    features.add("nw=" + name.getNameType() + "," + common.getHeadTokenText().toLowerCase());
    for (Iterator si = synsets.iterator(); si.hasNext();) {
      features.add("ns=" + name.getNameType() + "," + si.next());
    }
    if (name.getNameType() == null) {
      //features.addAll(getCommonCommonFeatures(name,common));
    }
    return features;
  }

  private List getNameNumberFeatures(Context name, Context number) {
    List features = new ArrayList(2);
    features.add("nt=" + name.getNameType() + "," + number.getHeadTokenTag());
    features.add("nn=" + name.getNameType() + "," + number.getNameType());
    return features;
  }

  private List getNamePronounFeatures(Context name, Context pronoun) {
    List features = new ArrayList(2);
    features.add("nw=" + name.getNameType() + "," + pronoun.getHeadTokenText().toLowerCase());
    features.add("ng=" + name.getNameType() + "," + AbstractResolver.getPronounGender(pronoun.getHeadTokenText().toLowerCase()));
    return features;
  }

  private List getCommonPronounFeatures(Context common, Context pronoun) {
    List features = new ArrayList();
    Set synsets1 = common.getSynsets();
    String p = pronoun.getHeadTokenText().toLowerCase();
    String gen = AbstractResolver.getPronounGender(p);
    features.add("wn=" + p + "," + common.getNameType());
    for (Iterator si = synsets1.iterator(); si.hasNext();) {
      Object synset = si.next();
      features.add("ws=" + p + "," + synset);
      features.add("gs=" + gen + "," + synset);
    }
    return features;
  }

  private List getCommonNumberFeatures(Context common, Context number) {
    List features = new ArrayList();
    Set synsets1 = common.getSynsets();
    for (Iterator si = synsets1.iterator(); si.hasNext();) {
      Object synset = si.next();
      features.add("ts=" + number.getHeadTokenTag() + "," + synset);
      features.add("ns=" + number.getNameType() + "," + synset);
    }
    features.add("nn=" + number.getNameType() + "," + common.getNameType());
    return features;
  }

  private List getNumberPronounFeatures(Context number, Context pronoun) {
    List features = new ArrayList();
    String p = pronoun.getHeadTokenText().toLowerCase();
    String gen = AbstractResolver.getPronounGender(p);
    features.add("wt=" + p + "," + number.getHeadTokenTag());
    features.add("wn=" + p + "," + number.getNameType());
    features.add("wt=" + gen + "," + number.getHeadTokenTag());
    features.add("wn=" + gen + "," + number.getNameType());
    return features;
  }

  private List getNameNameFeatures(Context name1, Context name2) {
    List features = new ArrayList(1);
    if (name1.getNameType() == null && name2.getNameType() == null) {
      features.add("nn=" + name1.getNameType() + "," + name2.getNameType());
      //features.addAll(getCommonCommonFeatures(name1,name2));
    }
    else if (name1.getNameType() == null) {
      features.add("nn=" + name1.getNameType() + "," + name2.getNameType());
      //features.addAll(getNameCommonFeatures(name2,name1));
    }
    else if (name2.getNameType() == null) {
      features.add("nn=" + name2.getNameType() + "," + name1.getNameType());
      //features.addAll(getNameCommonFeatures(name1,name2));
    }
    else {
      if (name1.getNameType().compareTo(name2.getNameType()) < 0) {
        features.add("nn=" + name1.getNameType() + "," + name2.getNameType());
      }
      else {
        features.add("nn=" + name2.getNameType() + "," + name1.getNameType());
      }
      if (name1.getNameType().equals(name2.getNameType())) {
        features.add("sameNameType");
      }
    }
    return features;
  }

  private List getCommonCommonFeatures(Context common1, Context common2) {
    List features = new ArrayList();
    Set synsets1 = common1.getSynsets();
    Set synsets2 = common2.getSynsets();

    if (synsets1.size() == 0) {
      //features.add("missing_"+common1.headToken);
      return features;
    }
    if (synsets2.size() == 0) {
      //features.add("missing_"+common2.headToken);
      return features;
    }
    int numCommonSynsets = 0;
    boolean same = false;
    if (numCommonSynsets == 0) {
      features.add("ncss");
    }
    else if (numCommonSynsets == synsets1.size() && numCommonSynsets == synsets2.size()) {
      same = true;
      features.add("samess");
    }
    else if (numCommonSynsets == synsets1.size()) {
      features.add("2isa1");
      //features.add("2isa1-"+(synsets2.size() - numCommonSynsets));
    }
    else if (numCommonSynsets == synsets2.size()) {
      features.add("1isa2");
      //features.add("1isa2-"+(synsets1.size() - numCommonSynsets));
    }
    if (!same) {
      for (Iterator si = synsets1.iterator(); si.hasNext();) {
        Object synset = si.next();
        if (synsets2.contains(synset)) {
          features.add("ss=" + synset);
          numCommonSynsets++;
        }
      }
    }
    if (numCommonSynsets == 0) {
      features.add("ncss");
    }
    else if (numCommonSynsets == synsets1.size() && numCommonSynsets == synsets2.size()) {
      features.add("samess");
    }
    else if (numCommonSynsets == synsets1.size()) {
      features.add("2isa1");
      //features.add("2isa1-"+(synsets2.size() - numCommonSynsets));
    }
    else if (numCommonSynsets == synsets2.size()) {
      features.add("1isa2");
      //features.add("1isa2-"+(synsets1.size() - numCommonSynsets));
    }
    return features;
  }

  private List getPronounPronounFeatures(Context pronoun1, Context pronoun2) {
    List features = new ArrayList();
    String g1 = AbstractResolver.getPronounGender(pronoun1.getHeadTokenText());
    String g2 = AbstractResolver.getPronounGender(pronoun2.getHeadTokenText());
    if (g1.equals(g2)) {
      features.add("sameGender");
    }
    else {
      features.add("diffGender");
    }
    return features;
  }

  private List getFeatures(Context np1, Context np2) {
    List features = new ArrayList();
    features.add("default");
    //  semantic categories
    String w1 = np1.getHeadTokenText().toLowerCase();
    String w2 = np2.getHeadTokenText().toLowerCase();
    if (w1.compareTo(w2) < 0) {
      features.add("ww=" + w1 + "," + w2);
    }
    else {
      features.add("ww=" + w2 + "," + w1);
    }
    if (w1.equals(w2)) {
      features.add("sameHead");
    }
    //features.add("tt="+np1.headTag+","+np2.headTag);
    if (isName(np1)) {
      if (isName(np2)) {
        features.addAll(getNameNameFeatures(np1, np2));
      }
      else if (isCommonNoun(np2)) {
        features.addAll(getNameCommonFeatures(np1, np2));
      }
      else if (isPronoun(np2)) {
        features.addAll(getNamePronounFeatures(np1, np2));
      }
      else if (isNumber(np2)) {
        features.addAll(getNameNumberFeatures(np1, np2));
      }
    }
    else if (isCommonNoun(np1)) {
      if (isName(np2)) {
        features.addAll(getNameCommonFeatures(np2, np1));
      }
      else if (isCommonNoun(np2)) {
        features.addAll(getCommonCommonFeatures(np1, np2));
      }
      else if (isPronoun(np2)) {
        features.addAll(getCommonPronounFeatures(np1, np2));
      }
      else if (isNumber(np2)) {
        features.addAll(getCommonNumberFeatures(np1, np2));
      }
      else {
        //System.err.println("unknown group for " + np1.headTokenText + " -> " + np2.headTokenText);
      }
    }
    else if (isPronoun(np1)) {
      if (isName(np2)) {
        features.addAll(getNamePronounFeatures(np2, np1));
      }
      else if (isCommonNoun(np2)) {
        features.addAll(getCommonPronounFeatures(np2, np1));
      }
      else if (isPronoun(np2)) {
        features.addAll(getPronounPronounFeatures(np1, np2));
      }
      else if (isNumber(np2)) {
        features.addAll(getNumberPronounFeatures(np2, np1));
      }
      else {
        //System.err.println("unknown group for " + np1.headTokenText + " -> " + np2.headTokenText);
      }
    }
    else if (isNumber(np1)) {
      if (isName(np2)) {
        features.addAll(getNameNumberFeatures(np2, np1));
      }
      else if (isCommonNoun(np2)) {
        features.addAll(getCommonNumberFeatures(np2, np1));
      }
      else if (isPronoun(np2)) {
        features.addAll(getNumberPronounFeatures(np1, np2));
      }
      else if (isNumber(np2)) {}
      else {
        //System.err.println("unknown group for " + np1.headTokenText + " -> " + np2.headTokenText);
      }
    }
    else {
      //System.err.println("unknown group for " + np1.headToken);
    }
    return (features);
  }

  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.err.println("Usage: SimilarityModel modelName < tiger/NN bear/NN");
      System.exit(1);
    }
    String modelName = args[0];
    SimilarityModel model = new SimilarityModel(modelName, false);
    //Context.wn = new WordNet(System.getProperty("WNHOME"), true);
    //Context.morphy = new Morphy(Context.wn);
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    for (String line = in.readLine(); line != null; line = in.readLine()) {
      String[] words = line.split(" ");
      double p = model.compatible(Context.parseContext(words[0]), Context.parseContext(words[1]));
      System.out.println(p + " " + model.getFeatures(Context.parseContext(words[0]), Context.parseContext(words[1])));
    }
  }
}
