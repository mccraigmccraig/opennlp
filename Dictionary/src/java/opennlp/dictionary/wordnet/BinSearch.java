/**

  binsearch.c - general binary search functions

*/

package opennlp.dictionary.wordnet;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.WeakHashMap;

/** Binary search - looks for the key passed at the start of a line
 *  in the file associated with open file descriptor fp, and returns
 *  a buffer containing the line in the file.
 */
public class BinSearch {
    private static final int LINE_LEN = 1024;
    
    public BinSearch() {}
    private   byte[] line_buf = new byte[LINE_LEN]; 
    protected long last_bin_search_offset = 0;
    
    /** General purpose binary search function to search for key as first
     *  item on line in open file.  Item is delimited by space.
     */
    public String read_index(long offset, RandomAccessFile fp) {
        try {
            fp.seek(offset);
            int size = Search.fgets(fp, line_buf);
            return new String(line_buf, 0, 0, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static final class BinSearchKey {
       private String searchkey;
       private RandomAccessFile fp;
       public BinSearchKey(String searchkey, RandomAccessFile fp) {
          this.searchkey=searchkey;
          this.fp=fp;
       }
       public int hashCode() {
          return searchkey.hashCode() + fp.hashCode();
       }
       public boolean equals(Object o) {
          if (o instanceof BinSearchKey) {
             BinSearchKey rhs = (BinSearchKey)o;
             return fp==rhs.fp && searchkey.equals(rhs.searchkey);
          }
          return false;
       }
    }
    private static final class BinSearchData {
       public String data;
       public long pos;
       public BinSearchData(String data, long pos) {
          this.data=data;
          this.pos=pos;
       }
    }

    private Map binSearchCache = new WeakHashMap();
    private String NULL_DATA = "NULL_DATA";
    public String bin_search(String searchkey, RandomAccessFile fp) {
        if (searchkey==null) {
            throw new IllegalArgumentException("searchkey should not be null");
        }
        if (fp==null) {
            throw new IllegalArgumentException("fp should not be null");
        }
        try {
            BinSearchData r = (BinSearchData)binSearchCache.get(new BinSearchKey(searchkey, fp));
            if (r != null) {
               fp.seek(r.pos);
               if (r.data==NULL_DATA) {
                  return null;
               }
               return r.data;
            }
            byte[] buffer = new byte[1024];
            String key="";
            String line="";
            long diff=666;
            long top = 0;
            long bot = fp.length();
            long mid = (bot - top) >> 1;
        
            do {
                fp.seek(mid - 1);
                int c;
                if (mid != 1) {
                    int size = fp.read(buffer);
                    int i;
                    for (i=0; i<size; i++) {
                       if (buffer[i]=='\n') {
                          break;
                       }
                    }
                    fp.seek(mid+i);
                }
                last_bin_search_offset = fp.getFilePointer();
                int size = Search.fgets(fp, line_buf);
                if (size>0) {
                    line = new String(line_buf, 0, 0, size);
                    int length = line.indexOf(' ');
                    key = line.substring(0,length);
                    if (key.compareTo(searchkey) < 0) {
                        top = mid;
                        diff = (bot - top) >> 1;
                        mid = top + diff;
                    }
                    if (key.compareTo(searchkey) > 0) {
                        bot = mid;
                        diff = (bot - top) >> 1;
                        mid = top + diff;
                    }
                } else {
                    binSearchCache.put(new BinSearchKey(searchkey, fp), new BinSearchData(NULL_DATA,fp.getFilePointer()));
                    return null;
                }
            } while  (!key.equals(searchkey) && (diff != 0));
            
            if (searchkey.equals(key)) {
                binSearchCache.put(new BinSearchKey(searchkey, fp), new BinSearchData(line,fp.getFilePointer()) );
                return line;
            } else {
                binSearchCache.put(new BinSearchKey(searchkey, fp), new BinSearchData(NULL_DATA,fp.getFilePointer()));
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private long offset;
    
    public int bin_search_key(String searchkey, RandomAccessFile fp) {
        if (searchkey==null) {
            throw new IllegalArgumentException("searchkey should not be null");
        }
        try {
            // do binary search to find correct place in file to insert line

            long offset1, offset2;
            int c;
            String key="";
            String line="";
            long diff=666;
            long top = 0;
            long bot = fp.length();
            if (bot == 0) {
                offset = 0;
                return 0;		// empty file
            }
            long mid = (bot - top) >> 1;
        
            // If only one line in file, don't work through loop.
        
            int length = 0;
            fp.seek(0);
            while((c = fp.read()) != '\n' && c != -1) {
                line_buf[length++] =  (byte)c;
            }
            if (fp.read() == -1) {	// only 1 line in file
                line = new String(line_buf, 0, length);
                length = line.lastIndexOf(' ');
                key= new String(line_buf, 0, length);
                if (key.compareTo(searchkey) > 0) {
                    offset = 0;
                    return 0;		// line with key is not found.
                } else if (key.compareTo(searchkey) < 0) {
                    offset = fp.getFilePointer();
                    return 0;		// line with key is not found.
                } else {
                    offset = 0;
                    return 1;		// line with key is found.
                }
            }
        
            do {
                fp.seek(mid - 1);
                if (mid != 1) {
                    while((c = fp.read()) != '\n' && c != -1) {}
                }
                offset1 = fp.getFilePointer();	// offset at start of line.
                last_bin_search_offset = fp.getFilePointer();
                int size = Search.fgets(fp, line_buf);
                if (size>0) {
                    offset2 = fp.getFilePointer(); // offset at start of next line.
                    line = new String(line_buf, 0, 0, size);
                    length = line.indexOf(' ');
                    key = line.substring(0,length);
                    if (key.compareTo(searchkey) < 0) {
                        top = mid;
                        diff = (bot - top) >> 1;
                        mid = top + diff;
                        offset = offset2;
                    }
                    if (key.compareTo(searchkey) > 0) {
                        bot = mid;
                        diff = (bot - top) >> 1;
                        mid = top + diff;
                        offset = offset1;
                    }
                } else {
                    bot = mid;
                    diff = (bot - top) / 2;
                    mid = top + diff;
                }
            } while  (!key.equals(searchkey) && (diff != 0));
    
            if (key.equals(searchkey)) {
                offset = offset1;	// get to start of current line.
                return 1;		// line with key is found.
            } else {
                return 0;		// line with key is not found.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    

    /** Copy contents from one file to another.
    */
    public static void copyfile(RandomAccessFile fromfp, RandomAccessFile tofp) {
        try {
            int c;
        
            while ((c = fromfp.read()) != -1) {
                    tofp.write((byte)c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /** Function to replace a line in a file.  Returns the original line,
     *  or null in case of error.
     */
    
    public String replace_line(String new_line, String searchkey, RandomAccessFile fp) {
        try {
            if (bin_search_key(searchkey, fp)==0) {
                return null;		// line with key not found.
            }
        
            File tmp  = File.createTempFile("wordnet",null);
            RandomAccessFile tfp = new RandomAccessFile(tmp, "rw"); // temporary file pointer.
            fp.seek(offset);
            int size = Search.fgets(fp, line_buf);	// read original.
            String line = new String(line_buf, 0, 0, size);
            copyfile(fp, tfp);
            fp.seek(offset);
            fp.writeBytes(new_line);	// write line.
            tfp.seek(0);
            copyfile(tfp, fp);
        
            tfp.close();
        
            return line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    /** Find location to insert line at in file.  If line with this
     *  key is already in file, return null.
     */
    
    public String insert_line(String new_line, String searchkey, RandomAccessFile fp) {
        try {
            if (bin_search_key(searchkey, fp)!=0) {
                return null;
            }
            
            File tmp  = File.createTempFile("wordnet",null);
            RandomAccessFile tfp = new RandomAccessFile(tmp, "rw"); // temporary file pointer.
    
            fp.seek(offset);
            copyfile(fp, tfp);
            fp.seek(offset);
            fp.writeBytes(new_line);	// write line.
            tfp.seek(0);
            copyfile(tfp, fp);
        
            tfp.close();
        
            return new_line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
