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
import java.util.regex.Pattern;

import opennlp.maxent.Event;
import opennlp.maxent.GIS;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.PlainTextGISModelReader;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.maxent.io.SuffixSensitiveGISModelWriter;
import opennlp.tools.coref.DiscourseEntity;
import opennlp.tools.coref.DiscourseModel;
import opennlp.tools.coref.Linker;
import opennlp.tools.coref.mention.MentionContext;
import opennlp.tools.coref.mention.Parse;
import opennlp.tools.coref.sim.Context;
import opennlp.tools.coref.sim.GenderEnum;
import opennlp.tools.coref.sim.NumberEnum;
import opennlp.tools.coref.sim.TestGenderModel;
import opennlp.tools.coref.sim.TestNumberModel;
import opennlp.tools.coref.sim.TestSimilarityModel;
import opennlp.tools.util.CollectionEventStream;

/**
 *  Provides common functionality used by classes which implement the {@link Resolver} class and use maximum entropy models to make resolution decisions. 
 */
public abstract class MaxentResolver extends AbstractResolver {

  public static final String SAME = "same";
  public static final String DIFF = "diff";

  public static final String DEFAULT = "default";

  private static final Pattern endsWithPeriod = Pattern.compile("\\.$");

  private final double minGenderProb = 0.66;
  private final Double minGenderProbObject = new Double(minGenderProb);
  private final double minNumberProb = 0.66;
  private final Double minNumberProbObject = new Double(minNumberProb);
  private final double minSimProb = 0.60;

  private final String SIM_COMPATIBLE = "sim.compatible";
  private final String SIM_INCOMPATIBLE = "sim.incompatible";
  private final String SIM_UNKNOWN = "sim.unknown";

  private final String NUM_COMPATIBLE = "num.compatible";
  private final String NUM_INCOMPATIBLE = "num.incompatible";
  private final String NUM_UNKNOWN = "num.unknown";

  private final String GEN_COMPATIBLE = "gen.compatible";
  private final String GEN_INCOMPATIBLE = "gen.incompatible";
  private final String GEN_UNKNOWN = "gen.unknown";

  private static boolean debugOn=false;
  
  private static boolean loadAsResource=false;

  private String modelName;
  private MaxentModel model;
  private double[] candProbs;
  private int sameIndex;
  private ResolverMode mode;
  private List events;

  /** When true, this designates that the resolver should use the first referent encountered which it
   * more preferable than non-reference.  When false all non-excluded referents within this resolvers range
   * are considered. 
   */
  protected boolean preferFirstReferent;
  /** When true, this designates that training should consist of a single positive and a single negitive example
   * (when possible) for each mention.  When false all possible pairs are used for training. */
  protected boolean sampleSelection;
  /** When true, this designates that the same maximum entropy model should be used non-reference
   * events (the pairing of a mention and the "null" reference) as is used for potentially 
   * referential pairs.  When false a seperate model is created for these events.  
   */ 
  protected boolean useSameModelForNonRef;
  
  private static TestSimilarityModel simModel = null;
  private static TestGenderModel genModel = null;
  private static TestNumberModel numModel = null;
  /** The model for computing non-referential probabilities. */
  protected NonReferentialResolver nonReferentialResolver;
  
  private static final String modelExtension = ".bin.gz";

  /**
   * Creates a maximum-entropy-based resolver which will look the specified number of entities back for a referent.
   * This constructor is only used for unit testing.
   * @param numberOfEntitiesBack
   * @param preferFirstReferent
   */
  protected MaxentResolver(int numberOfEntitiesBack, boolean preferFirstReferent) {
    super(numberOfEntitiesBack);
    this.preferFirstReferent = preferFirstReferent;
  }
  

  /**
   * Creates a maximum-entropy-based resolver with the specified model name, using the 
   * specified mode, which will look the specified number of entities back for a referent and
   * prefer the first referent if specified.
   * @param project The name of the file where this model will be read or written.
   * @param mode The mode this resolver is being using in (training, testing).
   * @param numberOfEntitiesBack The number of entities back in the text that this resolver will look
   * for a referent.
   * @param preferFirstReferent Set to true if the resolver should prefer the first referent which is more
   * likly than non-reference.  This only affects testing.
   * @param nonReferentialResolver Determines how likly it is that this entity is non-referential.
   * @throws IOException If the model file is not found or can not be written to.
   */
  public MaxentResolver(String project, String name, ResolverMode mode, int numberOfEntitiesBack, boolean preferFirstReferent, NonReferentialResolver nonReferentialResolver) throws IOException {
    super(numberOfEntitiesBack);
    this.preferFirstReferent = preferFirstReferent;
    this.nonReferentialResolver = nonReferentialResolver;
    this.mode = mode;
    this.modelName = project+"/"+name;
    if (ResolverMode.TEST == this.mode) {
      if (loadAsResource) {
        model = (new PlainTextGISModelReader(new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(modelName+modelExtension))))).getModel();
      }
      else {
        model = (new SuffixSensitiveGISModelReader(new File(modelName+modelExtension))).getModel();
      }
      sameIndex = model.getIndex(SAME);
    }
    else if (ResolverMode.TRAIN == this.mode) {
      events = new ArrayList();
    }
    else {
      System.err.println("Unknown mode: " + this.mode);
    }
    //add one for non-referent possibility
    candProbs = new double[getNumEntities() + 1];
  }

  /**
   * Creates a maximum-entropy-based resolver with the specified model name, using the 
   * specified mode, which will look the specified number of entities back for a referent.
   * @param modelName The name of the file where this model will be read or written.
   * @param mode The mode this resolver is being using in (training, testing).
   * @param numberEntitiesBack The number of entities back in the text that this resolver will look
   * for a referent.
   * @throws IOException If the model file is not found or can not be written to.
   */
  public MaxentResolver(String projectName, String modelName, ResolverMode mode, int numberEntitiesBack) throws IOException {
    this(projectName, modelName, mode, numberEntitiesBack, false);
  }
  
  public MaxentResolver(String projectName, String modelName, ResolverMode mode, int numberEntitiesBack, NonReferentialResolver nonReferentialResolver) throws IOException {
    this(projectName, modelName, mode, numberEntitiesBack, false,nonReferentialResolver);
  }
  
  public MaxentResolver(String projectName, String modelName, ResolverMode mode, int numberEntitiesBack, boolean preferFirstReferent) throws IOException {
    //this(projectName, modelName, mode, numberEntitiesBack, preferFirstReferent, SingletonNonReferentialResolver.getInstance(projectName,mode));
    this(projectName, modelName, mode, numberEntitiesBack, preferFirstReferent, new DefaultNonReferentialResolver(projectName, modelName, mode));
  }
  
  public MaxentResolver(String projectName, String modelName, ResolverMode mode, int numberEntitiesBack, boolean preferFirstReferent, double nonReferentialProbability) throws IOException {
    //this(projectName, modelName, mode, numberEntitiesBack, preferFirstReferent, SingletonNonReferentialResolver.getInstance(projectName,mode));
    this(projectName, modelName, mode, numberEntitiesBack, preferFirstReferent, new FixedNonReferentialResolver(nonReferentialProbability));
  }
  
  public static void loadAsResource(boolean las) {
    loadAsResource = las;
  }
  
  public static boolean loadAsResource() {
    return loadAsResource;
  }

  public DiscourseEntity resolve(MentionContext ec, DiscourseModel dm) {
    DiscourseEntity de;
    int ei = 0;
    Context c = Context.getContext(ec);
    Object[] pair = computeGender(c);
    ec.setGender((GenderEnum) pair[0],((Double) pair[1]).doubleValue());
    pair = computeNumber(c);
    ec.setNumber((NumberEnum) pair[0],((Double) pair[1]).doubleValue());
    double nonReferentialProbability = nonReferentialResolver.getNonReferentialProbability(ec);
    for (; ei < getNumEntities(dm); ei++) {
      de = (DiscourseEntity) dm.getEntity(ei);
      if (outOfRange(ec, de)) {
        break;
      }
      if (excluded(ec, de)) {
        candProbs[ei] = 0;
        if (debugOn) {
          System.err.println("excluded "+this +".resolve: " + ec.toText() + " -> " + de + " " + candProbs[ei]);
        }
      }
      else {

        List lfeatures = getFeatures(ec, de);
        String[] features = (String[]) lfeatures.toArray(new String[lfeatures.size()]);
        try {
          candProbs[ei] = model.eval(features)[sameIndex];
        }
        catch (ArrayIndexOutOfBoundsException e) {
          candProbs[ei] = 0;
        }
        if (debugOn) {
          System.err.println(this +".resolve: " + ec.toText() + " -> " + de + " " + candProbs[ei] + " " + lfeatures);
        }
      }
      if (preferFirstReferent && candProbs[ei] > nonReferentialProbability) {
        ei++; //update for nonRef assignment
        break;
      }
    }
    candProbs[ei] = nonReferentialProbability;

    // find max
    int maxCandIndex = 0;
    for (int k = 1; k <= ei; k++) {
      if (candProbs[k] > candProbs[maxCandIndex]) {
        maxCandIndex = k;
      }
    }
    if (maxCandIndex == ei) { // no referent
      return (null);
    }
    else {
      de = (DiscourseEntity) dm.getEntity(maxCandIndex);
      return (de);
    }
  }

  /*
  protected double getNonReferentialProbability(MentionContext ec) {
    if (useFixedNonReferentialProbability) {
      if (debugOn) {
        System.err.println(this +".resolve: " + ec.toText() + " -> " + null +" " + fixedNonReferentialProbability);
        System.err.println();
      }
      return fixedNonReferentialProbability;
    }
    List lfeatures = getFeatures(ec, null);
    String[] features = (String[]) lfeatures.toArray(new String[lfeatures.size()]);

    if (features == null) {
      System.err.println("features=null in " + this);
    }
    if (model == null) {
      System.err.println("model=null in " + this);
    }
    double[] dist = nrModel.eval(features);

    if (dist == null) {
      System.err.println("dist=null in " + this);
    }
    if (debugOn) {
      System.err.println(this +".resolve: " + ec.toText() + " -> " + null +" " + dist[nrSameIndex] + " " + lfeatures);
      System.err.println();
    }
    return (dist[nrSameIndex]);
  }
  */

  protected boolean diffCriteria(DiscourseEntity de) {
    MentionContext ec = de.getLastExtent();
    if (ec.getNounPhraseSentenceIndex() == 0) {
      return (true);
    }
    return (false);
  }

  public DiscourseEntity retain(MentionContext mention, DiscourseModel dm) {
    //System.err.println(this+".retain("+ec+") "+mode);
    if (ResolverMode.TRAIN == mode) {
      Context c = Context.getContext(mention);
      Object[] pair = computeGender(c);
      mention.setGender((GenderEnum) pair[0],((Double) pair[1]).doubleValue());
      pair = computeNumber(c);
      mention.setNumber((NumberEnum) pair[0],((Double) pair[1]).doubleValue());
      DiscourseEntity de = null;
      boolean referentFound = false;
      boolean hasReferentialCandidate = false;
      boolean nonReferentFound = false;
      for (int ei = 0; ei < getNumEntities(dm); ei++) {
        DiscourseEntity cde = (DiscourseEntity) dm.getEntity(ei);
        MentionContext entityMention = cde.getLastExtent();
        if (outOfRange(mention, cde)) {
          if (mention.getId() != -1 && !referentFound) {
            //System.err.println("retain: Referent out of range: "+ec.toText()+" "+ec.parse.getSpan());
          }
          break;
        }
        if (excluded(mention, cde)) {
          if (showExclusions) {
            if (mention.getId() != -1 && entityMention.getId() == mention.getId()) {
              System.err.println(this +".retain: Referent excluded: (" + mention.getId() + ") " + mention.toText() + " " + mention.getSpan() + " -> (" + entityMention.getId() + ") " + entityMention.toText() + " " + entityMention.getSpan() + " " + this);
            }
          }
        }
        else {
          hasReferentialCandidate = true;
          boolean useAsDifferentExample = diffCriteria(cde);
          if (!sampleSelection || (mention.getId() != -1 && entityMention.getId() == mention.getId()) || (!nonReferentFound && useAsDifferentExample)) {
            List features = getFeatures(mention, cde);

            //add Event to Model
            if (debugOn) {
              System.err.println(this +".retain: " + mention.getId() + " " + mention.toText() + " -> " + entityMention.getId() + " " + cde);
            }
            if (mention.getId() != -1 && entityMention.getId() == mention.getId()) {
              referentFound = true;
              events.add(new Event(SAME, (String[]) features.toArray(new String[features.size()])));
              de = cde;
              //System.err.println("MaxentResolver.retain: resolved at "+ei);
              distances.add(new Integer(ei));
            }
            else if (!sampleSelection || (!nonReferentFound && useAsDifferentExample)) {
              nonReferentFound = true;
              events.add(new Event(DIFF, (String[]) features.toArray(new String[features.size()])));
            }
          }
        }
        if (sampleSelection && referentFound && nonReferentFound) {
          break;
        }
        if (preferFirstReferent && referentFound) {
          break;
        }
      }
      // doesn't refer to anything
      if (hasReferentialCandidate) {
        nonReferentialResolver.addEvent(mention);
      }
      return (de);
    }
    else {
      return (super.retain(mention, dm));
    }
  }

  protected String getMentionCountFeature(DiscourseEntity de) {
    if (de.getNumExtents() >= 5) {
      return ("mc=5+");
    }
    else {
      return ("mc=" + de.getNumExtents());
    }
  }

  

  /** 
   * Returns a list of features for deciding whether the specificed mention refers to the specified discourse entity.
   * @param mention the mention being considers as possibly referential. 
   * @param entity The disource entity with which the mention is being considered referential.  
   * @return a list of features used to predict reference between the specified mention and entity.
   */
  protected List getFeatures(MentionContext mention, DiscourseEntity entity) {
    List features = new ArrayList();
    features.add(DEFAULT);
    features.addAll(getCompatibilityFeatures(mention, entity));
    return features;
  }

  public void train() throws IOException {
    if (ResolverMode.TRAIN == mode) {
      if (debugOn) {
        System.err.println(this +" referential");
        FileWriter writer = new FileWriter(modelName+".events");
        for (Iterator ei=events.iterator();ei.hasNext();) {
          Event e = (Event) ei.next();
          writer.write(e.toString()+"\n");
        }
        writer.close();
      }
      (new SuffixSensitiveGISModelWriter(GIS.trainModel(new CollectionEventStream(events),100,10),new File(modelName+modelExtension))).persist();
      nonReferentialResolver.train();
    }
  }

  public static void setSimilarityModel(TestSimilarityModel sm, TestGenderModel gm, TestNumberModel nm) {
    simModel = sm;
    genModel = gm;
    numModel = nm;
  }

  private Object[] computeGender(Context c) {
    Object[] rv = new Object[2];
    double[] gdist = genModel.genderDistribution(c);
    if (debugOn) {
      System.err.println("MaxentResolver.computeGender: "+c.toString()+" m="+gdist[genModel.getMaleIndex()]+" f="+gdist[genModel.getFemaleIndex()]+" n="+gdist[genModel.getNeuterIndex()]);
    }
    if (genModel.getMaleIndex() >= 0 && gdist[genModel.getMaleIndex()] > minGenderProb) {
      rv[0] = GenderEnum.MALE;
      rv[1] = new Double(gdist[genModel.getMaleIndex()]);
    }
    else if (genModel.getFemaleIndex() >= 0 && gdist[genModel.getFemaleIndex()] > minGenderProb) {
      rv[0] = GenderEnum.FEMALE;
      rv[1] = new Double(gdist[genModel.getFemaleIndex()]);
    }
    else if (genModel.getNeuterIndex() >= 0 && gdist[genModel.getNeuterIndex()] > minGenderProb) {
      rv[0] = GenderEnum.NEUTER;
      rv[1] = new Double(gdist[genModel.getNeuterIndex()]);
    }
    else {
      rv[0] = GenderEnum.UNKNOWN;
      rv[1] = minGenderProbObject;
    }
    return rv;
  }

  public Object[] computeNumber(Context c) {
    double[] dist = numModel.numberDist(c);
    Object[] rv = new Object[2];
    //System.err.println("computeNumber: "+c+" sing="+dist[numModel.getSingularIndex()]+" plural="+dist[numModel.getPluralIndex()]);
    if (dist[numModel.getSingularIndex()] > minNumberProb) {
      rv[0] = NumberEnum.SINGULAR;
      rv[1] = new Double(dist[numModel.getSingularIndex()]);
    }
    else if (dist[numModel.getPluralIndex()] > minNumberProb) {
      rv[0] = NumberEnum.PLURAL;
      rv[1] = new Double(dist[numModel.getPluralIndex()]);
    }
    else {
      rv[0] = NumberEnum.UNKNOWN;
      rv[1] = minNumberProbObject;
    }
    return rv;
  }

  protected String getSemanticCompatibilityFeature(MentionContext ec, DiscourseEntity de) {
    if (simModel != null) {
      double best = 0;
      for (Iterator xi = de.getExtents(); xi.hasNext();) {
        MentionContext ec2 = (MentionContext) xi.next();
        double sim = simModel.compatible(ec, ec2);
        if (debugOn) {
          System.err.println("MaxentResolver,getSemanticCompatibilityFeature: sem-compat " + sim + " " + ec.toText() + " " + ec2.toText());
        }
        if (sim > best) {
          best = sim;
        }
      }
      if (best > minSimProb) {
        return SIM_COMPATIBLE;
      }
      else if (best > 1 - minSimProb) {
        return SIM_UNKNOWN;
      }
      else {
        return SIM_INCOMPATIBLE;
      }
    }
    else {
      System.err.println("MaxentResolver: Uninitialized Semantic Model");
      return SIM_UNKNOWN;
    }
  }

  protected String getGenderCompatibilityFeature(MentionContext ec, DiscourseEntity de) {
    GenderEnum eg = de.getGender();
    if (eg == GenderEnum.UNKNOWN || ec.getGender() == GenderEnum.UNKNOWN) {
      return GEN_UNKNOWN;
    }
    else if (ec.getGender() == eg) {
      return GEN_COMPATIBLE;
    }
    else {
      return GEN_INCOMPATIBLE;
    }
  }

  protected String getNumberCompatibilityFeature(MentionContext ec, DiscourseEntity de) {
    NumberEnum en = de.getNumber();
    if (en == NumberEnum.UNKNOWN || ec.getNumber() == NumberEnum.UNKNOWN) {
      return NUM_UNKNOWN;
    }
    else if (ec.getNumber() == en) {
      return NUM_COMPATIBLE;
    }
    else {
      return NUM_INCOMPATIBLE;
    }
  }

  protected List getCompatibilityFeatures(MentionContext ec, DiscourseEntity de) {
    List compatFeatures = new ArrayList();
    String semCompatible = getSemanticCompatibilityFeature(ec, de);
    compatFeatures.add(semCompatible);
    String genCompatible = getGenderCompatibilityFeature(ec, de);
    compatFeatures.add(genCompatible);
    String numCompatible = getNumberCompatibilityFeature(ec, de);
    compatFeatures.add(numCompatible);
    if (semCompatible.equals(SIM_COMPATIBLE) && genCompatible.equals(GEN_COMPATIBLE) && numCompatible.equals(NUM_COMPATIBLE)) {
      compatFeatures.add("all.compatible");
    }
    else if (semCompatible.equals(SIM_INCOMPATIBLE) || genCompatible.equals(GEN_INCOMPATIBLE) || numCompatible.equals(NUM_INCOMPATIBLE)) {
      compatFeatures.add("some.incompatible");
    }
    return compatFeatures;
  }
  /**
   * Returns a list of features based on the surrounding context of the specified mention.
   * @param mention he mention whose surround context the features model. 
   * @return a list of features based on the surrounding context of the specified mention
   */
  public static List getContextFeatures(MentionContext mention) {
    List features = new ArrayList();
    if (mention.getPreviousToken() != null) {
      features.add("pt=" + mention.getPreviousToken().getSyntacticType());
      features.add("pw=" + mention.getPreviousToken().toString());
    }
    else {
      features.add("pt=BOS");
      features.add("pw=BOS");
    }
    if (mention.getNextToken() != null) {
      features.add("nt=" + mention.getNextToken().getSyntacticType());
      features.add("nw=" + mention.getNextToken().toString());
    }
    else {
      features.add("nt=EOS");
      features.add("nw=EOS");
    }
    if (mention.getNextTokenBasal() != null) {
      features.add("bnt=" + mention.getNextTokenBasal().getSyntacticType());
      features.add("bnw=" + mention.getNextTokenBasal().toString());
    }
    else {
      features.add("bnt=EOS");
      features.add("bnw=EOS");
    }
    return (features);
  }

  public static boolean isDebugOn() {
    return debugOn;
  }

  public static void setDebug(boolean b) {
    debugOn = b;
  }

  protected Set constructModifierSet(Parse[] tokens, int headIndex) {
    Set modSet = new HashSet();
    for (int ti = 0; ti < headIndex; ti++) {
      Parse tok = tokens[ti];
      modSet.add(tok.toString().toLowerCase());
    }
    return (modSet);
  }

  public boolean definiteArticle(String tok, String tag) {
    tok = tok.toLowerCase();
    if (tok.equals("the") || tok.equals("these") || tok.equals("these") || tag.equals("PRP$")) {
      return (true);
    }
    return (false);
  }

  private boolean isSubstring(String ecStrip, String xecStrip) {
    //System.err.println("MaxentResolver.isSubstring: ec="+ecStrip+" xec="+xecStrip);
    int io = xecStrip.indexOf(ecStrip);
    if (io != -1) {
      //check boundries
      if (io != 0 && xecStrip.charAt(io - 1) != ' ') {
        return false;
      }
      int end = io + ecStrip.length();
      if (end != xecStrip.length() && xecStrip.charAt(end) != ' ') {
        return false;
      }
      return true;
    }
    return false;
  }

  protected boolean excluded(MentionContext ec, DiscourseEntity de) {
    if (super.excluded(ec, de)) {
      return true;
    }
    return false;
    /*
    else {
      if (GEN_INCOMPATIBLE == getGenderCompatibilityFeature(ec,de)) {
        return true; 
      }
      else if (NUM_INCOMPATIBLE == getNumberCompatibilityFeature(ec,de)) {
        return true;
      }
      else if (SIM_INCOMPATIBLE == getSemanticCompatibilityFeature(ec,de)) {
        return true;
      }
      return false;
    }
    */
  }

  protected List getDistanceFeatures(MentionContext mention, DiscourseEntity entity) {
    List features = new ArrayList();
    MentionContext cec = entity.getLastExtent();
    int edist = mention.getNounPhraseDocumentIndex()- cec.getNounPhraseDocumentIndex();
    int sdist = mention.getSentenceNumber() - cec.getSentenceNumber();
    int hdist;
    if (sdist == 0) {
      hdist = cec.getNounPhraseSentenceIndex();
    }
    else {
      //hdist = edist + (2 * cec.nounLocation) - cec.maxNounLocation;
      hdist = edist + cec.getNounPhraseSentenceIndex() - cec.getMaxNounPhraseSentenceIndex();
    }
    features.add("hd=" + hdist);
    features.add("de=" + edist);
    features.add("ds=" + sdist);
    //features.add("ds=" + sdist + pronoun);
    //features.add("dn=" + cec.sentenceNumber);
    //features.add("ep=" + cec.nounLocation);
    return (features);
  }
  
  private Map getPronounFeatureMap(String pronoun) {
    Map pronounMap = new HashMap();
    if (Linker.malePronounPattern.matcher(pronoun).matches()) {
      pronounMap.put("gender","male");
    }
    else if (Linker.femalePronounPattern.matcher(pronoun).matches()) {
      pronounMap.put("gender","female");
    }
    else if (Linker.neuterPronounPattern.matcher(pronoun).matches()) {
      pronounMap.put("gender","neuter");
    }
    if (Linker.singularPronounPattern.matcher(pronoun).matches()) {
      pronounMap.put("number","singular");
    }
    else if (Linker.pluralPronounPattern.matcher(pronoun).matches()) {
      pronounMap.put("number","plural");
    }
    /*
    if (Linker.firstPersonPronounPattern.matcher(pronoun).matches()) {
      pronounMap.put("person","first");
    }
    else if (Linker.secondPersonPronounPattern.matcher(pronoun).matches()) {
      pronounMap.put("person","second");
    }
    else if (Linker.thirdPersonPronounPattern.matcher(pronoun).matches()) {
      pronounMap.put("person","third");
    }
    */
    return pronounMap;
  }
  
  protected List getPronounMatchFeatures(MentionContext mention, DiscourseEntity entity) {
    boolean foundCompatiblePronoun = false;
    boolean foundIncompatiblePronoun = false;
    if (mention.getHeadTokenTag().startsWith("PRP")) {
      Map pronounMap = getPronounFeatureMap(mention.getHeadTokenText());
      //System.err.println("getPronounMatchFeatures.pronounMap:"+pronounMap);
      for (Iterator mi=entity.getExtents();mi.hasNext();) {
        MentionContext candidateMention = (MentionContext) mi.next();
        if (candidateMention.getHeadTokenTag().startsWith("PRP")) {
          if (mention.getHeadTokenText().equalsIgnoreCase(candidateMention.getHeadTokenText())) {
            foundCompatiblePronoun = true;
            break;
          }
          else {
            Map candidatePronounMap = getPronounFeatureMap(candidateMention.getHeadTokenText());
            //System.err.println("getPronounMatchFeatures.candidatePronounMap:"+candidatePronounMap);
            boolean allKeysMatch = true;
            for (Iterator ki = pronounMap.keySet().iterator(); ki.hasNext();) {
              Object key = ki.next();
              Object cfv = candidatePronounMap.get(key);
              if (cfv != null) {
                if (!pronounMap.get(key).equals(cfv)) {
                  foundIncompatiblePronoun = true;
                  allKeysMatch = false;
                }
              }
              else {
                allKeysMatch = false;
              }
            }
            if (allKeysMatch) {
              foundCompatiblePronoun = true;
            }
          }
        }
      }
    }
    List pronounFeatures = new ArrayList();
    if (foundCompatiblePronoun) {
      pronounFeatures.add("compatiblePronoun");
    }
    if (foundIncompatiblePronoun) {
      pronounFeatures.add("incompatiblePronoun");
    }
    return pronounFeatures;
  }

  protected List getStringMatchFeatures(MentionContext mention, DiscourseEntity entity) {
    boolean sameHead = false;
    boolean modsMatch = false;
    boolean titleMatch = false;
    boolean nonTheModsMatch = false;
    List features = new ArrayList();
    Parse[] mtokens = mention.getTokens();
    Set ecModSet = constructModifierSet(mtokens, mention.getHeadTokenIndex());
    String mentionHeadString = mention.getHeadTokenText().toLowerCase();
    Set featureSet = new HashSet();
    for (Iterator ei = entity.getExtents(); ei.hasNext();) {
      MentionContext entityMention = (MentionContext) ei.next();
      String exactMatchFeature = getExactMatchFeature(mention, entityMention);
      if (exactMatchFeature != null) {
        featureSet.add(exactMatchFeature);
      }
      else if (entityMention.getParse().isCoordinatedNounPhrase() && !mention.getParse().isCoordinatedNounPhrase()) {
        featureSet.add("cmix");
      }
      else {
        String mentionStrip = stripNp(mention);
        String entityMentionStrip = stripNp(entityMention);
        if (mentionStrip != null && entityMentionStrip != null) {
          if (isSubstring(mentionStrip, entityMentionStrip)) {
            featureSet.add("substring");
          }
        }
      }
      Parse[] xtoks = entityMention.getTokens();
      int headIndex = entityMention.getHeadTokenIndex();
      //if (!mention.getHeadTokenTag().equals(entityMention.getHeadTokenTag())) {
      //  //System.err.println("skipping "+mention.headTokenText+" with "+xec.headTokenText+" because "+mention.headTokenTag+" != "+xec.headTokenTag);
      //  continue;
      //}  want to match NN NNP
      String entityMentionHeadString = entityMention.getHeadTokenText().toLowerCase();
      // model lexical similarity
      if (mentionHeadString.equals(entityMentionHeadString)) {
        sameHead = true;
        featureSet.add("hds=" + mentionHeadString);
        if (!modsMatch || !nonTheModsMatch) { //only check if we haven't already found one which is the same
          modsMatch = true;
          nonTheModsMatch = true;
          Set entityMentionModifierSet = constructModifierSet(xtoks, headIndex);
          for (Iterator mi = ecModSet.iterator(); mi.hasNext();) {
            String mw = (String) mi.next();
            if (!entityMentionModifierSet.contains(mw)) {
              modsMatch = false;
              if (!mw.equals("the")) {
                nonTheModsMatch = false;
                featureSet.add("mmw=" + mw);
              }
            }
          }
        }
      }
      Set descModSet = constructModifierSet(xtoks, entityMention.getNonDescriptorStart());
      if (descModSet.contains(mentionHeadString)) {
        titleMatch = true;
      }
    }
    if (!featureSet.isEmpty()) {
      features.addAll(featureSet);
    }
    if (sameHead) {
      features.add("sameHead");
      if (modsMatch) {
        features.add("modsMatch");
      }
      else if (nonTheModsMatch) {
        features.add("nonTheModsMatch");
      }
      else {
        features.add("modsMisMatch");
      }
    }
    if (titleMatch) {
      features.add("titleMatch");
    }
    return features;
  }

  protected String extentString(MentionContext ec) {
    StringBuffer sb = new StringBuffer();
    Parse[] mtokens = ec.getTokens();
    sb.append(mtokens[0].toString());
    for (int ti = 1, tl = mtokens.length; ti < tl; ti++) {
      String token = mtokens[ti].toString();
      sb.append(" ").append(token);
    }
    return sb.toString();
  }

  protected String excludeTheExtentString(MentionContext ec) {
    StringBuffer sb = new StringBuffer();
    boolean first = true;
    Parse[] mtokens = ec.getTokens();
    for (int ti = 0, tl = mtokens.length; ti < tl; ti++) {
      String token = mtokens[ti].toString();
      if (!token.equals("the") && !token.equals("The") && !token.equals("THE")) {
        if (!first) {
          sb.append(" ");
        }
        sb.append(token);
        first = false;
      }
    }
    return sb.toString();
  }

  protected String excludeHonorificExtentString(MentionContext ec) {
    StringBuffer sb = new StringBuffer();
    boolean first = true;
    Parse[] mtokens = ec.getTokens();
    for (int ti = 0, tl = mtokens.length; ti < tl; ti++) {
      String token = mtokens[ti].toString();
      if (!Linker.honorificsPattern.matcher(token).matches()) {
        if (!first) {
          sb.append(" ");
        }
        sb.append(token);
        first = false;
      }
    }
    return sb.toString();
  }

  protected String excludeDeterminerExtentString(MentionContext ec) {
    StringBuffer sb = new StringBuffer();
    boolean first = true;
    Parse[] mtokens = ec.getTokens();
    for (int ti = 0, tl = mtokens.length; ti < tl; ti++) {
      Parse token = mtokens[ti];
      String tag = token.getSyntacticType();
      if (!tag.equals("DT")) {
        if (!first) {
          sb.append(" ");
        }
        sb.append(token.toString());
        first = false;
      }
    }
    return sb.toString();
  }

  protected String getExactMatchFeature(MentionContext ec, MentionContext xec) {
    if (extentString(ec).equals(extentString(xec))) {
      return "exactMatch";
    }
    else if (excludeHonorificExtentString(ec).equals(excludeHonorificExtentString(xec))) {
      return "exactMatchNoHonor";
    }
    else if (excludeTheExtentString(ec).equals(excludeTheExtentString(xec))) {
      return "exactMatchNoThe";
    }
    else if (excludeDeterminerExtentString(ec).equals(excludeDeterminerExtentString(xec))) {
      return "exactMatchNoDT";
    }
    return null;
  }

  public static List getWordFeatures(Parse tok) {
    List wordFeatures = new ArrayList();
    String word = tok.toString().toLowerCase();
    String wf = "";
    if (endsWithPeriod.matcher(word).find()) {
      wf = ",endWithPeriod";
    }
    String tokTag = tok.getSyntacticType();
    wordFeatures.add("w=" + word + ",t=" + tokTag + wf);
    wordFeatures.add("t=" + tokTag + wf);
    return (wordFeatures);
  }
}
