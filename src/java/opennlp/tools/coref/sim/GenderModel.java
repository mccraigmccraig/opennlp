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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import opennlp.maxent.Event;
import opennlp.maxent.GIS;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.maxent.io.SuffixSensitiveGISModelWriter;
import opennlp.tools.coref.Linker;
import opennlp.tools.util.CollectionEventStream;
import opennlp.tools.util.HashList;

/**
 * Class which models the gender of a particular mentions and entities made up of mentions. 
 * @author Tom Morton
 *
 */
public class GenderModel implements TestGenderModel, TrainSimilarityModel {

  private int maleIndex;
  private int femaleIndex;
  private int neuterIndex;
  
  private String modelName;
  private String modelExtension = ".bin.gz";
  private MaxentModel testModel;
  private List events;
  private boolean debugOn = true;
  
  private Set maleNames;
  private Set femaleNames;

  public static TestGenderModel testModel(String name) throws IOException {
    GenderModel gm = new GenderModel(name, false);
    return gm;
  }

  public static TrainSimilarityModel trainModel(String name) throws IOException {
    GenderModel gm = new GenderModel(name, true);
    return gm;
  }
  
  private Set readNames(String nameFile) throws IOException {
    Set names = new HashSet();
    BufferedReader nameReader = new BufferedReader(new FileReader(nameFile));
    for (String line = nameReader.readLine(); line != null; line = nameReader.readLine()) {
      names.add(line);
    }
    return names;
  }
  
  private GenderModel(String modelName, boolean train) throws IOException {
    this.modelName = modelName;
    maleNames = readNames(modelName+".mas");
    femaleNames = readNames(modelName+".fem");
    if (train) {
      events = new ArrayList();
    }
    else {
      //if (MaxentResolver.loadAsResource()) {
      //  testModel = (new BinaryGISModelReader(new DataInputStream(this.getClass().getResourceAsStream(modelName)))).getModel();
      //}
      testModel = (new SuffixSensitiveGISModelReader(new File(modelName+modelExtension))).getModel();
      maleIndex = testModel.getIndex(GenderEnum.MALE.toString());
      femaleIndex = testModel.getIndex(GenderEnum.FEMALE.toString());
      neuterIndex = testModel.getIndex(GenderEnum.NEUTER.toString());
    }
  }

  private List getFeatures(Context np1) {
    List features = new ArrayList();
    features.add("default");
    for (int ti = 0, tl = np1.getHeadTokenIndex(); ti < tl; ti++) {
      features.add("mw=" + np1.getTokens()[ti].toString());
    }
    features.add("hw=" + np1.getHeadTokenText());
    features.add("n="+np1.getNameType());
    if (np1.getNameType() != null && np1.getNameType().equals("person")) {
      Object[] tokens = np1.getTokens();
      //System.err.println("GenderModel.getFeatures: person name="+np1);
      for (int ti=0;ti<np1.getHeadTokenIndex() || ti==0;ti++) {
        String name = tokens[ti].toString().toLowerCase();
        if (femaleNames.contains(name)) {
          features.add("fem");
          //System.err.println("GenderModel.getFeatures: person (fem) "+np1);
        }
        if (maleNames.contains(name)) {
          features.add("mas");
          //System.err.println("GenderModel.getFeatures: person (mas) "+np1);
        }
      }
    }
    for (Iterator si = np1.getSynsets().iterator(); si.hasNext();) {
      features.add("ss=" + si.next().toString());
    }
    return features;
  }

  private void addEvent(String outcome, Context np1) {
    List feats = getFeatures(np1);
    events.add(new Event(outcome, (String[]) feats.toArray(new String[feats.size()])));
  }

  /**
   * Hueristic computation of gender for a mention context using pronouns and honorifics. 
   * @param mention The mention whose gender is to be computed.
   * @return The hueristically determined gender or unknown.
   */
  private GenderEnum getGender(Context mention) {
    if (Linker.malePronounPattern.matcher(mention.getHeadTokenText()).matches()) {
      return GenderEnum.MALE;
    }
    else if (Linker.femalePronounPattern.matcher(mention.getHeadTokenText()).matches()) {
      return GenderEnum.FEMALE;
    }
    else if (Linker.neuterPronounPattern.matcher(mention.getHeadTokenText()).matches()) {
      return GenderEnum.NEUTER;
    }
    Object[] mtokens = mention.getTokens();
    for (int ti = 0, tl = mtokens.length - 1; ti < tl; ti++) {
      String token = mtokens[ti].toString();
      if (token.equals("Mr.") || token.equals("Mr")) {
        return GenderEnum.MALE;
      }
      else if (token.equals("Mrs.") || token.equals("Mrs") || token.equals("Ms.") || token.equals("Ms")) {
        return GenderEnum.FEMALE;
      }
    }
    return GenderEnum.UNKNOWN;
  }

  private GenderEnum getGender(List entity) {
    for (Iterator ci = entity.iterator(); ci.hasNext();) {
      Context ec = (Context) ci.next();
      GenderEnum ge = getGender(ec);
      if (ge != GenderEnum.UNKNOWN) {
        return ge;
      }
    }
    return GenderEnum.UNKNOWN;
  }

  public void setExtents(Context[] extentContexts) {
    HashList entities = new HashList();
    List singletons = new ArrayList();
    for (int ei = 0, el = extentContexts.length; ei < el; ei++) {
      Context ec = extentContexts[ei];
      //System.err.println("GenderModel.setExtents: ec("+ec.getId()+") "+ec.toText());
      if (ec.getId() != -1) {
        entities.put(new Integer(ec.getId()), ec);
      }
      else {
        singletons.add(ec);
      }
    }
    List males = new ArrayList();
    List females = new ArrayList();
    List eunuches = new ArrayList();
    //coref entities
    for (Iterator ei = entities.keySet().iterator(); ei.hasNext();) {
      Integer key = (Integer) ei.next();
      List entityContexts = (List) entities.get(key);
      GenderEnum gender = getGender(entityContexts);
      if (gender != null) {
        if (gender == GenderEnum.MALE) {
          males.addAll(entityContexts);
        }
        else if (gender == GenderEnum.FEMALE) {
          females.addAll(entityContexts);
        }
        else if (gender == GenderEnum.NEUTER) {
          eunuches.addAll(entityContexts);
        }
      }
    }
    //non-coref entities
    for (Iterator ei = singletons.iterator(); ei.hasNext();) {
      Context ec = (Context) ei.next();
      GenderEnum gender = getGender(ec);
      if (gender == GenderEnum.MALE) {
        males.add(ec);
      }
      else if (gender == GenderEnum.FEMALE) {
        females.add(ec);
      }
      else if (gender == GenderEnum.NEUTER) {
        eunuches.add(ec);
      }
    }
    for (Iterator mi = males.iterator(); mi.hasNext();) {
      Context ec = (Context) mi.next();
      addEvent(GenderEnum.MALE.toString(), ec);
    }
    for (Iterator fi = females.iterator(); fi.hasNext();) {
      Context ec = (Context) fi.next();
      addEvent(GenderEnum.FEMALE.toString(), ec);
    }
    for (Iterator ei = eunuches.iterator(); ei.hasNext();) {
      Context ec = (Context) ei.next();
      addEvent(GenderEnum.NEUTER.toString(), ec);
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.err.println("Usage: GenderModel modelName < tiger/NN bear/NN");
      System.exit(1);
    }
    String modelName = args[0];
    GenderModel model = new GenderModel(modelName, false);
    //Context.wn = new WordNet(System.getProperty("WNHOME"), true);
    //Context.morphy = new Morphy(Context.wn);
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    for (String line = in.readLine(); line != null; line = in.readLine()) {
      String[] words = line.split(" ");
      double[] dist = model.genderDistribution(Context.parseContext(words[0]));
      System.out.println("m="+dist[model.getMaleIndex()] + " f=" +dist[model.getFemaleIndex()]+" n="+dist[model.getNeuterIndex()]+" "+model.getFeatures(Context.parseContext(words[0])));
    }
  }

  public double[] genderDistribution(Context np1) {
    List features = getFeatures(np1);
    if (debugOn) {
      //System.err.println("GenderModel.genderDistribution: "+features);
    }
    return testModel.eval((String[]) features.toArray(new String[features.size()]));
  }

  public void trainModel() throws IOException {
    if (debugOn) {
      FileWriter writer = new FileWriter(modelName+".events");
      for (Iterator ei=events.iterator();ei.hasNext();) {
        Event e = (Event) ei.next();
        writer.write(e.toString()+"\n");
      }
      writer.close();
    }
    (new SuffixSensitiveGISModelWriter(GIS.trainModel(new CollectionEventStream(events),true),new File(modelName+modelExtension))).persist();
  }

  public int getFemaleIndex() {
    return femaleIndex;
  }

  public int getMaleIndex() {
    return maleIndex;
  }

  public int getNeuterIndex() {
    return neuterIndex;
  }

}
