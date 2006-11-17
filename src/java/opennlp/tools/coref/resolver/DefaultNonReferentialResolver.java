package opennlp.tools.coref.resolver;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import opennlp.maxent.Event;
import opennlp.maxent.GIS;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.BinaryGISModelReader;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.maxent.io.SuffixSensitiveGISModelWriter;
import opennlp.tools.coref.mention.MentionContext;
import opennlp.tools.coref.mention.Parse;
import opennlp.tools.util.CollectionEventStream;

/**
 * Default implementation of the {@link NonReferentialResolver} interface.
 */
public class DefaultNonReferentialResolver implements NonReferentialResolver {

  private MaxentModel model;
  private List events;
  private boolean loadAsResource;
  private boolean debugOn = false;
  private ResolverMode mode;
  private String modelName;
  private String modelExtension = ".bin.gz";
  private int nonRefIndex;
  
  public DefaultNonReferentialResolver(String projectName, String name, ResolverMode mode) throws IOException {
    this.mode = mode;
    this.modelName = projectName+"/"+name+".nr";
    if (mode == ResolverMode.TRAIN) {
      events = new ArrayList();
    }
    else if (mode == ResolverMode.TEST) {
      if (loadAsResource) {
        model = (new BinaryGISModelReader(new DataInputStream(this.getClass().getResourceAsStream(modelName)))).getModel();
      }
      else {
        model = (new SuffixSensitiveGISModelReader(new File(modelName+modelExtension))).getModel();
      }
      nonRefIndex = model.getIndex(MaxentResolver.SAME);
    }
    else {
      throw new RuntimeException("unexpected mode "+mode);
    }
  }
  
  public double getNonReferentialProbability(MentionContext mention) {
    List features = getFeatures(mention);
    double r = model.eval((String[]) features.toArray(new String[features.size()]))[nonRefIndex];
    if (debugOn) System.err.println(this +" " + mention.toText() + " ->  null " + r + " " + features);
    return r;
  }
  
  public void addEvent(MentionContext ec) {
    List features = getFeatures(ec);
    if (-1 == ec.getId()) {
      events.add(new Event(MaxentResolver.SAME, (String[]) features.toArray(new String[features.size()])));
    }
    else {
      events.add(new Event(MaxentResolver.DIFF, (String[]) features.toArray(new String[features.size()])));
    } 
  }
  
  protected List getFeatures(MentionContext mention) {
    List features = new ArrayList();
    features.add(MaxentResolver.DEFAULT);
    features.addAll(getNonReferentialFeatures(mention));
    return features;
  }
  
  /**
   * Returns a list of featues used to predict whether the sepcified mention is non-referential.
   * @param mention The mention under considereation.
   * @return a list of featues used to predict whether the sepcified mention is non-referential.
   */
  protected List getNonReferentialFeatures(MentionContext mention) {
    List features = new ArrayList();
    Parse[] mtokens = mention.getTokenParses();
    //System.err.println("getNonReferentialFeatures: mention has "+mtokens.length+" tokens");
    for (int ti = 0; ti <= mention.getHeadTokenIndex(); ti++) {
      Parse tok = mtokens[ti];
      List wfs = MaxentResolver.getWordFeatures(tok);
      for (int wfi = 0; wfi < wfs.size(); wfi++) {
        features.add("nr" + (String) wfs.get(wfi));
      }
    }
    features.addAll(MaxentResolver.getContextFeatures(mention));
    return features;
  }
  
  public void train() throws IOException {
    if (ResolverMode.TRAIN == mode) {
      System.err.println(this +" referential");
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
  }
}
