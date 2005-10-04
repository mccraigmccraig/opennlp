package opennlp.tools.ngram;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import opennlp.tools.util.NumberedSet;

/**
 * This class allows for the the loading of n-gram dictionaries to facilitate feature generation 
 * for n-gram based models.
 * @see MutableDictionary
 * @author Tom Morton
 *
 */
public class Dictionary {

  public static String FILE_TYPE = "dict";
  /** Mapping between words and a unique integer assigned to each words. **/
  protected NumberedSet wordMap;
  /** Set which stores all n-grams. */
  protected Set gramSet;
  //protected int size;
  protected int cutoff;
  protected NGramFactory nGramFactory;
  
  protected Dictionary() {}

  /** Constructor used to load a previously created dictionary for the specifed dictionary file.
   * @param dictionaryFile A file storing a dictionary.
   */
  public Dictionary(File dictionaryFile) throws IOException {
    DataInputStream input = new DataInputStream(new GZIPInputStream(new FileInputStream(dictionaryFile)));
    //System.err.println("Reading: "+input.readUTF());
    int numWords = input.readInt();
    //System.err.println("Reading: "+numWords+" words");
    wordMap = new NumberedSet(numWords);
    for (int wi=0;wi<numWords;wi++) {
      String word = input.readUTF();
      int index = input.readInt();
      wordMap.setIndex(word,index);
    }
    loadGrams(input);
    nGramFactory = new NGramFactory(wordMap);
  }
  
  protected void loadGrams(DataInputStream input) throws IOException {
    gramSet = new HashSet();
    try {
      while(true) {
        int gramLength=input.readInt();
      
        int[] words = new int[gramLength];
        for (int wi=0;wi<gramLength;wi++) {
          words[wi]=input.readInt();
        }
        gramSet.add(new NGram(words));
      }
    }
    catch(EOFException e) {
      
    }
  }
  
  public boolean get(String[] words) {
    if (words.length == 1) {
      return wordMap.contains(words[0]);
    }
    else {
      NGram ngram = nGramFactory.createNGram(words);
      if (ngram == null) {
        return false;
      }
      else {
        return gramSet.contains(ngram);
      }
    }
  }
}
