package opennlp.tools.namefind;

import java.util.List;
import java.util.Map;

import opennlp.tools.util.BeamSearchContextGenerator;

public interface NameContextGenerator extends BeamSearchContextGenerator {
  public abstract String[] getContext(int i, List toks,List preds, Map prevTags);
}