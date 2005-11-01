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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import opennlp.maxent.Event;
import opennlp.maxent.GIS;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.maxent.io.SuffixSensitiveGISModelWriter;
import opennlp.tools.coref.Linker;
import opennlp.tools.util.CollectionEventStream;
import opennlp.tools.util.HashList;

/**
 * Class which models the number of particular mentions and the entities made up of mentions. 
 */
public class NumberModel implements TestNumberModel, TrainSimilarityModel {

  private String modelName;
  private String modelExtension = ".bin.gz";
  private MaxentModel testModel;
  private List events;

  private int singularIndex;
  private int pluralIndex;

  public static TestNumberModel testModel(String name) throws IOException {
    NumberModel nm = new NumberModel(name, false);
    return nm;
  }

  public static TrainSimilarityModel trainModel(String modelName) throws IOException {
    NumberModel gm = new NumberModel(modelName, true);
    return gm;
  }

  private NumberModel(String modelName, boolean train) throws IOException {
    this.modelName = modelName;
    if (train) {
      events = new ArrayList();
    }
    else {
      //if (MaxentResolver.loadAsResource()) {
      //  testModel = (new PlainTextGISModelReader(new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(modelName))))).getModel();
      //}
      testModel = (new SuffixSensitiveGISModelReader(new File(modelName+modelExtension))).getModel();
      singularIndex = testModel.getIndex(NumberEnum.SINGULAR.toString());
      pluralIndex = testModel.getIndex(NumberEnum.PLURAL.toString());
    }
  }

  private List getFeatures(Context np1) {
    List features = new ArrayList();
    features.add("default");
    Object[] npTokens = np1.getTokens();
    for (int ti = 0, tl = npTokens.length - 1; ti < tl; ti++) {
      features.add("mw=" + npTokens[ti].toString());
    }
    features.add("hw=" + np1.getHeadTokenText().toLowerCase());
    features.add("ht=" + np1.getHeadTokenTag());
    return features;
  }

  private void addEvent(String outcome, Context np1) {
    List feats = getFeatures(np1);
    events.add(new Event(outcome, (String[]) feats.toArray(new String[feats.size()])));
  }

  public NumberEnum getNumber(Context ec) {
    if (Linker.singularPronounPattern.matcher(ec.getHeadTokenText()).matches()) {
      return NumberEnum.SINGULAR;
    }
    else if (Linker.pluralPronounPattern.matcher(ec.getHeadTokenText()).matches()) {
      return NumberEnum.PLURAL;
    }
    else {
      return NumberEnum.UNKNOWN;
    }
  }

  private NumberEnum getNumber(List entity) {
    for (Iterator ci = entity.iterator(); ci.hasNext();) {
      Context ec = (Context) ci.next();
      NumberEnum ne = getNumber(ec);
      if (ne != NumberEnum.UNKNOWN) {
        return ne;
      }
    }
    return NumberEnum.UNKNOWN;
  }

  public void setExtents(Context[] extentContexts) {
    HashList entities = new HashList();
    List singletons = new ArrayList();
    for (int ei = 0, el = extentContexts.length; ei < el; ei++) {
      Context ec = extentContexts[ei];
      //System.err.println("NumberModel.setExtents: ec("+ec.getId()+") "+ec.toText());
      if (ec.getId() != -1) {
        entities.put(new Integer(ec.getId()), ec);
      }
      else {
        singletons.add(ec);
      }
    }
    List singles = new ArrayList();
    List plurals = new ArrayList();
    // coref entities
    for (Iterator ei = entities.keySet().iterator(); ei.hasNext();) {
      Integer key = (Integer) ei.next();
      List entityContexts = (List) entities.get(key);
      NumberEnum number = getNumber(entityContexts);
      if (number == NumberEnum.SINGULAR) {
        singles.addAll(entityContexts);
      }
      else if (number == NumberEnum.PLURAL) {
        plurals.addAll(entityContexts);
      }
    }
    // non-coref entities.
    for (Iterator ei = singletons.iterator(); ei.hasNext();) {
      Context ec = (Context) ei.next();
      NumberEnum number = getNumber(ec);
      if (number == NumberEnum.SINGULAR) {
        singles.add(ec);
      }
      else if (number == NumberEnum.PLURAL) {
        plurals.add(ec);
      }
    }

    for (Iterator si = singles.iterator(); si.hasNext();) {
      Context ec = (Context) si.next();
      addEvent(NumberEnum.SINGULAR.toString(), ec);
    }
    for (Iterator fi = plurals.iterator(); fi.hasNext();) {
      Context ec = (Context) fi.next();
      addEvent(NumberEnum.PLURAL.toString(),ec);
    }
  }

  public double[] numberDist(Context c) {
    List feats = getFeatures(c);
    return testModel.eval((String[]) feats.toArray(new String[feats.size()]));
  }

  public int getSingularIndex() {
    return singularIndex;
  }

  public int getPluralIndex() {
    return pluralIndex;
  }

  public void trainModel() throws IOException {
    (new SuffixSensitiveGISModelWriter(GIS.trainModel(new CollectionEventStream(events),100,10),new File(modelName+modelExtension))).persist();    
  }

}
