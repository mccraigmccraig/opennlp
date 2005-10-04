package opennlp.tools.ngram;

import java.io.BufferedReader;
import java.io.InputStreamReader;


/** Class for storing n-grams into a hash-like structure.  
 * You should use the #NGramFactory to create these.
 * Only n-grams of size 32 or smaller are handled.  */
public class NGram {

  private int[] words;
  
  protected NGram(int[] words) {
    super();
    if (words.length <=32) {
      this.words = words;
    }
    else {
      throw(new IllegalArgumentException("Only n-grams of size 32 or smaller can be created: n-gram length = "+words.length));
    }
  }
  
  public int size() {
    return words.length;
  }
  
  protected int[] getWords() {
    return words;
  }

  public boolean equals(NGram ngram) {
    int wn = this.words.length;
    if (wn == ngram.words.length) {
      for (int wi=0;wi<wn;wi++) {
        if (words[wi] != ngram.words[wi]) {
          return false;
        }
      }
      return true;
    }
    else {
      return false;
    }
  }

  public int hashCode() {
    int numBitsRegular = 32 / words.length;
    int numExtra = 32 % words.length;
    int maskExtra = 0xFFFFFFFF >>> (32-(numBitsRegular+1));
    int maskRegular = 0xFFFFFFFF >>> 32-numBitsRegular;    
    int code = 0x000000000;
    int leftMostBit = 0;
    for (int wi=0;wi<words.length;wi++) {
      int word;
      int mask;
      int numBits;
      if (wi < numExtra){
        mask = maskExtra;
        numBits = numBitsRegular+1;
      }
      else {
        mask = maskRegular;
        numBits = numBitsRegular;
      }
      word = words[wi] & mask; // mask off top bits
      word <<= 32-leftMostBit-numBits; // move to correct position
      leftMostBit+=numBits; //set for next interation
      code |= word;
    }
    return code;
  }
  
  public static void main(String[] args) throws java.io.IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    for (String line=in.readLine(); null != line; line=in.readLine()) {
      String[] snums = line.split(" ");
      int[] nums = new int[snums.length];
      for (int ni=0;ni<nums.length;ni++){
        nums[ni] = Integer.parseInt(snums[ni]);
      }
      NGram ngram = new NGram(nums);
      int code = ngram.hashCode();
      System.out.println("hashcode="+code+" size="+ngram.size());
      for (int sh = 31; sh >= 0; sh--) {
        System.out.print((code >> sh) & 1);
      }
      System.out.println();
    }
  }
  
}
