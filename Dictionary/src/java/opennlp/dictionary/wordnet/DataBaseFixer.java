package opennlp.dictionary.wordnet;

import java.io.*;

public class DataBaseFixer {

   private boolean needToFix;
   
   public DataBaseFixer(File input, File output) {
      try {
         needToFix = false;
         FileInputStream is = new FileInputStream(input);
         byte[] buffer = new byte[4096];
         int size = is.read(buffer);
         for (int i=0; i<size; i++) {
            if (buffer[i] == '\r') {
               needToFix=true;
               break;
            }
         }
         is.close();
         if (needToFix) {
            is = new FileInputStream(input);
            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(output));
            while ( (size= is.read(buffer))==buffer.length) {
               for (int i=0; i<size; i++) {
                  if (buffer[i] == '\r') {
                     // do nothing
                  } else {
                     os.write(buffer[i]);
                  }
               }
            }
            for (int i=0; i<size; i++) {
               if (buffer[i] == '\r') {
                  // do nothing
               } else {
                  os.write(buffer[i]);
               }
            }
            os.close();
            is.close();
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   public static void main(String[] argv) {
      DataBaseFixer dbFix = new DataBaseFixer(new File("D:\\WN17\\dict_x\\data.adj"),
                                              new File("D:\\WN17\\dict_x\\data_x.adj"));
      System.out.println("needToFix data.adj="+dbFix.needToFix);
      dbFix = new DataBaseFixer(new File("D:\\WN17\\dict_x\\data.adv"),
                                new File("D:\\WN17\\dict_x\\data_x.adv"));
      System.out.println("needToFix data.adv="+dbFix.needToFix);
      dbFix = new DataBaseFixer(new File("D:\\WN17\\dict_x\\data.verb"),
                                new File("D:\\WN17\\dict_x\\data_x.verb"));
      System.out.println("needToFix data.verb="+dbFix.needToFix);
      dbFix = new DataBaseFixer(new File("D:\\WN17\\dict_x\\data.noun"),
                                new File("D:\\WN17\\dict_x\\data_x.noun"));
      System.out.println("needToFix data.noun="+dbFix.needToFix);
   }
}
