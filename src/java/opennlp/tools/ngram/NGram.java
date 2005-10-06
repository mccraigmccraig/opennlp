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

  public boolean equals(Object o) {
    NGram ngram = (NGram) o;
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
    /*
    NGram g1 = new NGram(new int[]{1,2,3});
    NGram g2 = new NGram(new int[]{2,3,4});
    NGram g3 = new NGram(new int[]{1,2,3});
    assert(g1.equals(g3));
    assert(g3.equals(g1));
    assert(!g1.equals(g2));
    assert(!g2.equals(g1));
    assert(g1.hashCode() == g3.hashCode());
    System.err.println("g1 == g3 "+g1.equals(g3));
    opennlp.tools.util.CountedSet cset = new opennlp.tools.util.CountedSet();
    cset.add(g1);
    cset.add(g2);
    cset.add(g3);
    
    for (java.util.Iterator ci = cset.iterator();ci.hasNext();) {
      Object ng = ci.next();
      System.err.println(ng+" "+cset.getCount(ng));
    }
    */
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
