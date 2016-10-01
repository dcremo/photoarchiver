package imagereader;

import java.io.*;
import java.text.*;
import java.util.*;

import org.apache.log4j.*;

import it.dcremo.photoarchiver.EstrattoreDataJpeg;

public class ShiftImageDate
{
  public static final Logger logger = Logger.getLogger(ShiftImageDate.class);
  public static String sourceDir = "C:/Documents and Settings/dcremonesi/Documenti/Immagini/FotoDaAllineare";

  public static void processDir(File topDir) throws IOException {

    //apri la cartella
    File src=null;
    if (topDir==null) {
      src = new File(sourceDir);
    } else {
      src = topDir;
    }

    // apri il file di correzione
    long fromDate = 0;
    long toDate   = 0;
    try {
      logger.debug("File di correzione ["+src.getAbsolutePath()+"/correzione.txt");
      File corr = new File(src.getAbsolutePath()+"/correzione.txt");
      BufferedReader correction = new BufferedReader(new FileReader(corr));
      fromDate = parse(correction.readLine()).getTime();
      toDate   = parse(correction.readLine()).getTime();
      logger.debug("Correzione della data exif da : "+new Date(fromDate));
      logger.debug("Correzione della data exif a  : "+new Date(toDate));
      corr.renameTo(new File(src.getAbsolutePath()+"/correzioneDone.txt"));
    } catch (Exception e) {
      // non fare nulla su questa dir
    }
    File[] files = src.listFiles();
    for (int i=0; i<files.length; i++) {
      if (files[i].isDirectory()) {
        processDir(files[i]);
      } else if (files[i].getName().toLowerCase().endsWith(".jpg")) {
        if (fromDate>0 && toDate>0) {
          logger.debug("Processing file: " + files[i].getAbsolutePath());
          ShiftImageDate(files[i], fromDate, toDate);
        } else {
          logger.debug("No processing of file: " + files[i].getAbsolutePath()+" since no correction file is found.");
        }
      } else {
        logger.warn("File con estensione sconosciuta:"+files[i].getName());
        if (files[i].getName().equals("Thumbs.db")) {
          files[i].delete();
        }
      }
    }
  }

  /**
   * CopyImage
   *
   * @param file File
   */
  private static boolean ShiftImageDate(File fileorig, long fromDate, long toDate) throws IOException {
    boolean retval = false;
    Date imageDate = null;
    String exif_datetimestr = new EstrattoreDataJpeg(fileorig.getAbsolutePath()).getDateTime();
    if (exif_datetimestr == null) {
      logger.fatal("Non riesco a leggere la data del file [" + fileorig.getAbsolutePath() + "]");
      return false;
    }
    imageDate = parse(exif_datetimestr);
    if (imageDate!=null) {
      System.out.println(fileorig.getName() + ":" + imageDate);
      long deltaTime = toDate-fromDate;
      long finalTime = imageDate.getTime()+deltaTime;
      logger.debug("Vecchia data: "+imageDate);
      logger.debug("Nuova   data: "+new Date(finalTime));
      String newExifDate = formatDateToExif(new Date(finalTime));
      String newpath = sourceDir+"/"+formatDateToFileName(new Date(finalTime))+".jpg";
      File newFile = new File(newpath);
      //boolean esito = fileorig.renameTo(newFile);

      if (correggiExifData(fileorig, newFile, exif_datetimestr, newExifDate)) {
        fileorig.delete();
      }
      int a=0;
    } else {
      logger.fatal("Non riesco a decifrare la data del file [" + fileorig.getAbsolutePath() + "]");
      return false;
    }
    return retval;
  }

  /**
   * correggiExitData
   *
   * @param newFile File
   */
  private static boolean correggiExifData(File fileorig, File filedest, String oldDate, String newDate) throws FileNotFoundException, IOException {
    if (oldDate==newDate) {
      logger.debug("Niente da fare, la data e' gia' OK");
      return false;
    }
    FileInputStream fis = new FileInputStream(fileorig);
    // leggi tutto il file in memoria
    int contLen = (int)fileorig.length();
    byte[] fileContent = new byte[contLen];
    byte[] buffer = new byte[1024];
    int bytesRead = 0;
    int position = 0;
    try {
      while ( (bytesRead = fis.read(buffer)) != -1) {
        System.arraycopy(buffer, 0, fileContent, position, bytesRead);
        position+=bytesRead;
      }
    } catch (IOException ex) {
    }
    fis.close();
    // ora cerca la corrispondenza copiando tranche di content
    int len = oldDate.length();
    int limit = 1000;
    int[] startPos = new int[3];
    int pp = 0;
    for (int start=0; start<limit; start++) {
      byte[] olddatebuffer = new byte[len];
      System.arraycopy(fileContent, start, olddatebuffer, 0, len);
      boolean compare = Arrays.equals(olddatebuffer, oldDate.getBytes());
      if (compare) {
        logger.fatal("Found @ position ["+start+"]");
        startPos[pp++]=start;
      }
    }
    if (pp!=3) {
      logger.fatal("Non ho trovato 3 posizioni ma pp=["+pp+"]");
    } else {
      byte[] newdatebuffer = newDate.getBytes();
      for (int p = 0; p < 3; p++) {
        System.arraycopy(newdatebuffer, 0, fileContent, startPos[p], newdatebuffer.length);
      }
    }

    // e adesso riscrivi il file
    FileOutputStream fos = new FileOutputStream(filedest);
    fos.write(fileContent);
    fos.close();
    return true;
  };

  public static String getMyDateTimeFullStringPrefix(Date date) {
    return getMyDateTime_YearString(date)+getMyDateTime_MonthString(date)+formatString(date.getDate(),2)+"_"+
        formatString(date.getHours(),2)+formatString(date.getMinutes(),2)+formatString(date.getSeconds(),2)+"_";
  }

  public static String getMyDateTime_YearString(Date date) {
    return ""+(1900+date.getYear());
  }

  public static String getMyDateTime_MonthString(Date date) {
    return formatString(date.getMonth()+1,2);
  }

  public static String formatString(long value, int digits) {
    String retval = ""+value;
    while (retval.length()<digits) retval = "0"+retval;
    return retval;
  }

  public static Date parse(String datestr) {
    Date retval = null;
    DateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    try {
      retval = (Date) formatter.parse(datestr);
    } catch (ParseException ex) {
      retval = null;
    }
    return retval;
  }

  public static String formatDateToFileName(Date date) {
    String retval = null;
    DateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    retval = formatter.format(date);
    return retval;
  }

  public static String formatDateToExif(Date date) {
    String retval = null;
    DateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    retval = formatter.format(date);
    return retval;
  }
}
