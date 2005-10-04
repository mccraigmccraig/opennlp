package opennlp.tools.ngram;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import opennlp.tools.util.CountedNumberedSet;
import opennlp.tools.util.CountedSet;

/**
 * This class allows for the creation and saving of n-gram dictionaries.
 * @author Tom Morton
 *
 */
public class MutableDictionary extends Dictionary {

  List wordCounts;
  
  public MutableDictionary(int cutoff) {
    super();
    this.cutoff = cutoff;
    wordMap = new CountedNumberedSet();
    nGramFactory = new NGramFactory(wordMap);
    gramSet = new CountedSet();
    wordCounts = new ArrayList();
  }
  
  public MutableDictionary(File dictionaryFile, int cutoff) throws IOException {
    super(dictionaryFile);
    this.cutoff = cutoff;
  }
  
  protected void loadGrams(DataInputStream input) throws IOException {
    int numGrams = input.readInt();
    CountedSet cgramSet = new CountedSet(numGrams);
    for (int gi=0;gi<numGrams;gi++) {
      int gramLength=input.readInt();
      int[] words = new int[gramLength];
      for (int wi=0;wi<gramLength;wi++) {
        words[wi]=input.readInt();
      }
      cgramSet.setCount(new NGram(words),cutoff);
    }
    gramSet = cgramSet;
  }
  
  public void add(String[] words,int size) {
    List gram = new ArrayList(size);
    //create uni-grams so n-grams can be created.
    for (int wi=0,wn=words.length;wi<wn;wi++) {
      wordMap.add(words[wi]);
    }
    for (int wi=0,wn=words.length;wi<wn;wi++) {
      //create all n-gram which start with wi
      gram.clear();
      gram.add(words[wi]);
      for (int gi=2;gi<size;gi++) {
        if (wi+gi-1 < words.length) {
          gram.add(words[wi+gi-1]);
          gramSet.add(nGramFactory.createNGram(gram));
        }
      }
    }
  }

  public void persist(File file) throws IOException {
    //System.err.println("Writting "+wordMap.size()+" words and "+gramSet.size()+" n-grams");
    DataOutputStream output = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
    output.writeUTF(FILE_TYPE);
    //System.err.println("pruning from "+wordMap.size());
    for (Iterator ki=wordMap.iterator();ki.hasNext();) {
      String key = (String) ki.next();
      if (((CountedNumberedSet) wordMap).getCount(key) < cutoff) {
        ki.remove();
      }
    }
    //System.err.println("pruning to "+wordMap.size());
    output.writeInt(wordMap.size());
    for (Iterator ki=wordMap.iterator();ki.hasNext();) {
      String key = (String) ki.next();
      output.writeUTF(key);
      output.writeInt(wordMap.getIndex(key));
    }
    CountedSet cset = (CountedSet) gramSet;
    for (Iterator gi = gramSet.iterator();gi.hasNext();) {
      NGram ngram = (NGram) gi.next();
      if (cset.getCount(ngram) >= cutoff) {
        int[] words = ngram.getWords();
        output.writeInt(words.length);
        for (int wi=0;wi<words.length;wi++) {
          output.writeInt(words[wi]);
        }
      }
    }
    output.close();
  }

}
