package opennlp.dictionary.wordnet;


/** Structure for index file entry.  */

public class Index {
    public long idxoffset;		// byte offset of entry in index file
    public String wd;			// word string
    public String pos;			// part of speech
    public int sense_cnt;		// sense (collins) count
    public int tagged_cnt;		// number senses that are tagged
    public int[] offset;		// offsets of synsets containing word
    public int[] ptruse;		// pointers used
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(idxoffset).append(",").append(wd).append(",").append(pos).append(",");
        sb.append(sense_cnt).append(",").append(tagged_cnt).append(",[");
        for (int i=0; i<offset.length-1; i++) {
            sb.append(offset[i]).append(",");
        }
        if (offset.length>0) {
            sb.append(offset[offset.length-1]).append("],[");
        }
        for (int i=0; i<ptruse.length-1; i++) {
            sb.append(ptruse[i]).append(",");
        }
        if (ptruse.length>0) {
            sb.append(ptruse[ptruse.length-1]).append("]");
        }
        return sb.toString();
    }
            
}


