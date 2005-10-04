package opennlp.tools.ngram;

import java.util.Arrays;
import java.util.List;

import opennlp.tools.util.NumberedSet;

/** This class is used to create NGrams with a particular mapping of words to integers.
 * @author tsmorton
 *
 */
public class NGramFactory {

  private NumberedSet wordMap;
  
  public NGramFactory(NumberedSet wordMap) {
    super();
    this.wordMap = wordMap;
  }
  
  public NGram createNGram(String[] words) {
    return createNGram(Arrays.asList(words));
  }
  
  /** 
   * Creates a new n-gram for the specified list of words.
   * @param words The words which are part of this n-gram.
   * @return the newly created object or null if the n-gram contains an unknown word.
   */
  public NGram createNGram(List words) {
    int[] nums = new int[words.size()]; //ngram needs it own copy of this array so keep making a unique one for each instance.
    for (int wi=0;wi<nums.length;wi++) {
      nums[wi] = wordMap.getIndex(words.get(wi));
      if (nums[wi] == -1) {
        return null;
      }
    }
    return new NGram(nums);
  }

}
