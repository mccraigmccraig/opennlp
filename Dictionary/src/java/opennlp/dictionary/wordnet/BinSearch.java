package opennlp.dictionary.wordnet;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;

/**
 *  Binary search - looks for the key passed at the start of a line in the file
 *  associated with open file descriptor fp, and returns a buffer containing the
 *  line in the file. <p>
 *
 *  This class was created by heavily modifying the WordNet 1.7 code src/lib/binsearch.c.
 *
 * @author     Mike Atkinson
 * @since      0.1.0
 * @created    20 March 2002
 * @version    $Id: BinSearch.java,v 1.3 2002/03/26 19:07:50 mratkinson Exp $
 */
public class BinSearch {

    private long lastBinSearchOffset = 0;
    private byte[] lineBuf = new byte[LINE_LEN];

    private Map binSearchCache = new WordNetCache(10000);
    private String NULL_DATA = "NULL_DATA";

    private long offset;
    private static String Id = "$Id: BinSearch.java,v 1.3 2002/03/26 19:07:50 mratkinson Exp $";
    private final static int LINE_LEN = 1024;


    /**
     *  Constructor for the BinSearch object
     *
     * @since    0.1.0
     */
    public BinSearch() { }


    /**
     *  Gets the Last Binary Search Offset into the file.
     *
     * @return    The LastBinSearchOffset value
     * @since     0.2.0
     */
    public long getLastBinSearchOffset() {
        return lastBinSearchOffset;
    }


    /**
     *  General purpose binary search function to get the line starting at offset
     *  from the random access file fp.
     *
     * @param  offset  The offset to read the line from.
     * @param  fp      The file to read the line from.
     * @return         The line read stating from offset in file fp.
     * @since          0.1.0
     */
    public String read_index(long offset, RandomAccessFile fp) {
        try {
            fp.seek(offset);
            int size = Search.fgets(fp, lineBuf);
            return new String(lineBuf, 0, 0, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     *  General purpose binary search function to search for key as first item on
     *  line in open file. Item is delimited by space.<p>
     *
     *  This caches search results so that they can be found much faster the second
     *  time.
     *
     * @param  searchKey  A key (without spaces) which will be looked for at start
     *      of lines in the file.
     * @param  fp         The file to look for the searchKey in.
     * @return            The line (stating with searchKey) found in the file ,
     *      or null if not found.
     * @since             0.1.0
     */
    public String binSearch(String searchKey, RandomAccessFile fp) {
        if (searchKey == null) {
            throw new IllegalArgumentException("searchKey should not be null");
        }
        if (fp == null) {
            throw new IllegalArgumentException("fp should not be null");
        }
        try {
            BinSearchData r = (BinSearchData)binSearchCache.get(new BinSearchKey(searchKey, fp));
            if (r != null) {
                fp.seek(r.pos);
                lastBinSearchOffset = r.pos;
                if (r.data == NULL_DATA) {
                    return null;
                }
                return r.data;
            }
            byte[] buffer = new byte[1024];
            String key = "";
            String line = "";
            long diff = 666;
            long top = 0;
            long bot = fp.length();
            long mid = (bot - top) >> 1;

            do {
                int size;
                if (mid != 1) {
                    r = (BinSearchData)binSearchCache.get(new BinSearchKey2(mid, fp));
                    if (r==null) {
                        fp.seek(mid - 1);
                        size = readBufferTillNewLine(fp, buffer);
                        lastBinSearchOffset = mid + size;
                        fp.seek(lastBinSearchOffset);
                        size = Search.fgets(fp, lineBuf);
                        if (size>0) {
                            line = new String(lineBuf, 0, 0, size);
                            int length = line.indexOf(' ');
                            key = line.substring(0, length);
                        } else {
                            line = "";
                            key = "";
                        }
                        //System.out.println(line);
                        binSearchCache.put(new BinSearchKey2(mid, fp), new BinSearchData(line, key, lastBinSearchOffset));
                    } else {
                        line = r.data;
                        size = line.length();
                        lastBinSearchOffset = r.pos;
                        fp.seek(lastBinSearchOffset);
                        key = r.key;
                    }
                } else {
                    fp.seek(mid - 1);
                    //lastBinSearchOffset = fp.getFilePointer();
                    lastBinSearchOffset = mid - 1;
                    size = Search.fgets(fp, lineBuf);
                    line = new String(lineBuf, 0, 0, size);
                    int length = line.indexOf(' ');
                    key = line.substring(0, length);
                }
                if (size > 0) {
                    //line = new String(lineBuf, 0, 0, size);
                    //int length = line.indexOf(' ');
                    //key = line.substring(0, length);
                    if (key.compareTo(searchKey) < 0) {
                        top = mid;
                        diff = (bot - top) >> 1;
                        mid = top + diff;
                    }
                    if (key.compareTo(searchKey) > 0) {
                        bot = mid;
                        diff = (bot - top) >> 1;
                        mid = top + diff;
                    }
                } else {
                    binSearchCache.put(new BinSearchKey(searchKey, fp), new BinSearchData(NULL_DATA, "", fp.getFilePointer()));
                    return null;
                }
            } while (!key.equals(searchKey) && (diff != 0));

            if (searchKey.equals(key)) {
                binSearchCache.put(new BinSearchKey(searchKey, fp), new BinSearchData(line, key, fp.getFilePointer()));
                return line;
            } else {
                binSearchCache.put(new BinSearchKey(searchKey, fp), new BinSearchData(NULL_DATA, "", fp.getFilePointer()));
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    private int readBufferTillNewLine(RandomAccessFile fp, byte[] buffer) {
        try {
            int start =0;
            int len = buffer.length>>2;
            for (int x=0; x<4; x++) {
                int size = fp.read(buffer, start, len);
                for (int i = 0; i < size; i++) {
                    if (buffer[start+i] == '\n') {
                        return start+i;
                    }
                }
                start += len;
            }
            return buffer.length;
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    
    /**
     *  Function to replace a line in a file. Returns the original line, or null
     *  in case of error.
     *
     * @param  newLine    The line to replace
     * @param  searchKey  A key (without spaces) which will be looked for at start
     *      of lines in the file.
     * @param  fp         The file to look for the searchKey in.
     * @return            The original line if the replacement took place (null
     *      otherwise).
     * @since             0.1.0
     */

    public String replaceLine(String newLine, String searchKey, RandomAccessFile fp) {
        try {
            if (!binSearchKey(searchKey, fp)) {
                return null;// line with key not found.
            }

            File tmp = File.createTempFile("wordnet", null);
            RandomAccessFile tfp = new RandomAccessFile(tmp, "rw");// temporary file pointer.
            fp.seek(offset);
            int size = Search.fgets(fp, lineBuf);// read original.
            String line = new String(lineBuf, 0, 0, size);
            copyFile(fp, tfp);
            fp.seek(offset);
            fp.writeBytes(newLine);// write line.
            tfp.seek(0);
            copyFile(tfp, fp);

            tfp.close();

            return line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     *  Find location to insert line at in file. If line with this key is already
     *  in file, return null.
     *
     * @param  newLine    The line to insert
     * @param  searchKey  A key (without spaces) which will be looked for at start
     *      of lines in the file.
     * @param  fp         The file to look for the searchKey in.
     * @return            The newLine if the insertion took place (null otherwise).
     * @since             0.1.0
     */

    public String insertLine(String newLine, String searchKey, RandomAccessFile fp) {
        try {
            if (binSearchKey(searchKey, fp)) {
                return null;
            }

            File tmp = File.createTempFile("wordnet", null);
            RandomAccessFile tfp = new RandomAccessFile(tmp, "rw");// temporary file pointer.

            fp.seek(offset);
            copyFile(fp, tfp);
            fp.seek(offset);
            fp.writeBytes(newLine);// write line.
            tfp.seek(0);
            copyFile(tfp, fp);

            tfp.close();

            return newLine;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     *  General purpose binary search function to search for key as first item on
     *  line in open file. Item is delimited by space.<p>
     *
     *  This is used by the insert and replace methods.
     *
     * @param  searchKey  A key (without spaces) which will be looked for at start
     *      of lines in the file.
     * @param  fp         The file to look for the searchKey in.
     * @return            <tt>true</tt> if the searchKey is found in the file.
     * @since             0.1.0
     */
    private boolean binSearchKey(String searchKey, RandomAccessFile fp) {
        if (searchKey == null) {
            throw new IllegalArgumentException("searchKey should not be null");
        }
        try {
            // do binary search to find correct place in file to insert line

            long offset1;
            // do binary search to find correct place in file to insert line

            long offset2;
            int c;
            String key = "";
            String line = "";
            long diff = 666;
            long top = 0;
            long bot = fp.length();
            if (bot == 0) {
                offset = 0;
                return false;// empty file
            }
            long mid = (bot - top) >> 1;

            // If only one line in file, don't work through loop.

            int length = 0;
            fp.seek(0);
            while ((c = fp.read()) != '\n' && c != -1) {
                lineBuf[length++] = (byte)c;
            }
            if (fp.read() == -1) {// only 1 line in file
                line = new String(lineBuf, 0, length);
                length = line.lastIndexOf(' ');
                key = new String(lineBuf, 0, length);
                if (key.compareTo(searchKey) > 0) {
                    offset = 0;
                    return false;// line with key is not found.
                } else if (key.compareTo(searchKey) < 0) {
                    offset = fp.getFilePointer();
                    return false;// line with key is not found.
                } else {
                    offset = 0;
                    return true;// line with key is found.
                }
            }

            do {
                fp.seek(mid - 1);
                if (mid != 1) {
                    while ((c = fp.read()) != '\n' && c != -1) {
                    }
                }
                offset1 = fp.getFilePointer();// offset at start of line.
                lastBinSearchOffset = fp.getFilePointer();
                int size = Search.fgets(fp, lineBuf);
                if (size > 0) {
                    offset2 = fp.getFilePointer();// offset at start of next line.
                    line = new String(lineBuf, 0, 0, size);
                    length = line.indexOf(' ');
                    key = line.substring(0, length);
                    if (key.compareTo(searchKey) < 0) {
                        top = mid;
                        diff = (bot - top) >> 1;
                        mid = top + diff;
                        offset = offset2;
                    }
                    if (key.compareTo(searchKey) > 0) {
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
            } while (!key.equals(searchKey) && (diff != 0));

            if (key.equals(searchKey)) {
                offset = offset1;// get to start of current line.
                return true;// line with key is found.
            } else {
                return false;// line with key is not found.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     *  Copy contents from one file to another.
     *
     * @param  fromfp  Copy from this file.
     * @param  tofp    To this file.
     * @since          0.1.0
     */
    public static void copyFile(RandomAccessFile fromfp, RandomAccessFile tofp) {
        try {
            int c;

            while ((c = fromfp.read()) != -1) {
                tofp.write((byte)c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *  This class is used to hold a key for the Binary Search cache of recent search
     *  results.
     *
     * @author     Mike Atkinson (mratkinson)
     * @since      0.1.0
     * @created    20 March 2002
     */
    private final static class BinSearchKey {
        private String searchKey;
        private RandomAccessFile fp;


        /**
         *  Constructor for the Binary Search cache Key.
         *
         * @param  searchKey  The string which was searched for.
         * @param  fp         The file it was searched for in.
         * @since             0.1.0
         */
        public BinSearchKey(String searchKey, RandomAccessFile fp) {
            this.searchKey = searchKey;
            this.fp = fp;
        }


        /**
         *  Compute the hash for this object so that it can be used within Maps.
         *
         * @return    The hash value of this object.
         * @since     0.1.0
         */
        public int hashCode() {
            return searchKey.hashCode() + fp.hashCode();
        }


        /**
         *  Tests whether this object is equal to the parameter so that it can be
         *  used within Maps.
         *
         * @param  o  The object to compare to.
         * @return    <tt>true</tt> if the object equals this one.
         * @since     0.1.0
         */
        public boolean equals(Object o) {
            if (o instanceof BinSearchKey) {
                BinSearchKey rhs = (BinSearchKey)o;
                return fp == rhs.fp && searchKey.equals(rhs.searchKey);
            }
            return false;
        }
    }
    /**
     *  This class is used to hold a key for the Binary Search cache of recent search
     *  results.
     *
     * @author     Mike Atkinson (mratkinson)
     * @since      0.1.0
     * @created    20 March 2002
     */
    private final static class BinSearchKey2 {
        private long searchKey;
        private RandomAccessFile fp;


        /**
         *  Constructor for the Binary Search cache Key.
         *
         * @param  searchKey  The string which was searched for.
         * @param  fp         The file it was searched for in.
         * @since             0.1.0
         */
        public BinSearchKey2(long searchKey, RandomAccessFile fp) {
            this.searchKey = searchKey;
            this.fp = fp;
        }


        /**
         *  Compute the hash for this object so that it can be used within Maps.
         *
         * @return    The hash value of this object.
         * @since     0.1.0
         */
        public int hashCode() {
            return (int)searchKey + fp.hashCode();
        }


        /**
         *  Tests whether this object is equal to the parameter so that it can be
         *  used within Maps.
         *
         * @param  o  The object to compare to.
         * @return    <tt>true</tt> if the object equals this one.
         * @since     0.1.0
         */
        public boolean equals(Object o) {
            if (o instanceof BinSearchKey2) {
                BinSearchKey2 rhs = (BinSearchKey2)o;
                return (fp == rhs.fp) && (searchKey==rhs.searchKey);
            }
            return false;
        }
    }


    /**
     *  This class holds the data which was found by the Binary Search and which
     *  needs to be stored in the Binary Search cache.
     *
     * @author     Mike Atkinson (mratkinson)
     * @since      0.1.0
     * @created    20 March 2002
     */
    private final static class BinSearchData {
        /**
         *  The data (line) which was found in the file.
         *
         * @since    0.1.0
         */
        public String data;
        /**
         *  The key (line up to the first space).
         *
         * @since    0.1.0
         */
        public String key;
        /**
         *  The position the data was found in the file.
         *
         * @since    0.1.0
         */
        public long pos;


        /**
         *  Constructor for the data which was found as a result of a Binary Search.
         *
         * @param  data  The data (line) which was found in the file.
         * @param  pos   The position the data was found in the file.
         * @since        0.1.0
         */
        public BinSearchData(String data, String key, long pos) {
            this.data = data;
            this.key = key;
            this.pos = pos;
        }
    }
}

