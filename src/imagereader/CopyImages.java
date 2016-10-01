package imagereader;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;

import it.dcremo.photoarchiver.JpegDateReader;

public class CopyImages {

	static boolean compareDate = false;
	public static final Logger logger = Logger.getLogger(CopyImages.class);
	private String sourceDir;
	private String imageDestinationDir;
	private String otherDestinationDir;

	public CopyImages(String sourceDir, String imageDestinationDir,
			String otherDestinationDir) {
		this.sourceDir = sourceDir;
		this.imageDestinationDir = imageDestinationDir;
		this.otherDestinationDir = otherDestinationDir;
	}

	public void doArchive(File topDir) throws IOException {
		// apri la cartella
		File src = null;
		if (topDir == null) {
			src = new File(sourceDir);
		} else {
			src = topDir;
		}
		if (!src.exists()) throw new IOException("Cartella ["+sourceDir+"] non esistente.");
		File[] files = src.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				doArchive(files[i]);
			} else if (files[i].getName().toLowerCase().endsWith(".jpg")) {
				logger.debug(files[i].getAbsolutePath());
				CopyImage(files[i], 0);
			} else if (files[i].getName().toLowerCase().endsWith(".mov")
					|| files[i].getName().toLowerCase().endsWith(".avi")
					|| files[i].getName().toLowerCase().endsWith(".mp4")
					|| files[i].getName().toLowerCase().endsWith(".thm")) {
				CopyGenericFile(files[i]);
			} else {
				logger.warn("File con estensione sconosciuta:"
						+ files[i].getName());
				if (files[i].getName().equals("Thumbs.db")) {
					files[i].delete();
				}
			}
		}
	}

	/**
	 * CopyImage
	 * 
	 * @param file
	 *            File
	 */
	private boolean CopyImage(File file, int progr) throws IOException {
		boolean retval = false;
		try {
			String datetime = new JpegDateReader(file.getAbsolutePath()).getDateTime();
			if (datetime == null) {
				logger.fatal("Non riesco a leggere la data del file ["
						+ file.getAbsolutePath() + "]");
				return false;
			}
			;

			datetime = datetime.trim();
			datetime = datetime.replace(':', '_');
			datetime = datetime.replace(' ', '_');
			String anno = datetime.substring(0, 4);
			String mese = datetime.substring(5, 7);
			if (anno.equals("0000") || mese.equals("00")) {
				logger.fatal("La data del file [" + file.getAbsolutePath()
						+ "] non � valida (" + datetime + ")");
				return false;
			}
			if (progr > 0)
				datetime += "_" + progr;
			System.out.println(file.getName() + ":" + datetime);
			File destDir = new File(imageDestinationDir + File.separator + anno
					+ File.separator + mese);
			destDir.mkdirs();
			System.out.println(destDir);
			String destName = destDir + File.separator + datetime + "_"
					+ file.getName();// .substring(file.getName().lastIndexOf("."),
										// file.getName().length());
			logger.info("Rename to:" + destName);
			File destFile = new File(destName);
			retval = file.renameTo(destFile);
			if (retval == false) {
				boolean identical = false;
				// non � stata copiata, guarda se esiste gi� e se � identica
				identical = true;
				if (compareDate) {
					identical = destFile.lastModified() == file.lastModified();
				}
				identical = identical && destFile.length() == file.length();
				if (identical) {
					System.out.println("I files sono identici ("
							+ file.getName() + "), elimino l'origine.");
					// retval = file.renameTo(new
					// File(file.getAbsolutePath()+".bak"));
					file.delete();
				} else {
					System.out.println("I files NON sono identici, errore.");
					logger.fatal("Errore, incompatibilit� dei file origine:"
							+ file.getName());
					logger.fatal("lastModified:"
							+ new Date(file.lastModified()));
					logger.fatal("length:" + file.length());
					logger.fatal("Errore, incompatibilit� dei file destinazione:"
							+ destFile.getName());
					logger.fatal("lastModified:"
							+ new Date(destFile.lastModified()));
					logger.fatal("length:" + destFile.length());
					CopyImage(file, progr + 1);
				}
			}
		} catch (Exception e) {
			logger.fatal(e);
			retval = false;
		}
		return retval;
	}

	private boolean CopyGenericFile(File file) throws IOException {
		boolean retval = false;
		Date datetime = new Date(file.lastModified());
		System.out.println(file.getAbsolutePath() + ": " + datetime);
		File destDir = new File(otherDestinationDir + File.separator
				+ getMyDateTime_YearString(datetime) + File.separator
				+ getMyDateTime_MonthString(datetime));
		destDir.mkdirs();
		System.out.println(destDir);
		File destFile = new File(destDir + File.separator + file.getName());
		retval = file.renameTo(destFile);
		if (retval == false) {
			boolean identical = destFile.length() == file.length();
			if (identical) {
				System.out.println("I files sono identici (" + file.getName()
						+ "), elimino l'origine.");
				file.delete();
			} else {
				System.out.println("I files NON sono identici, errore.");
				System.exit(0);
			}
		}
		return retval;
	}

	public static String getMyDateTimeFullStringPrefix(Date date) {
		return getMyDateTime_YearString(date) + getMyDateTime_MonthString(date)
				+ formatString(date.getDate(), 2) + "_"
				+ formatString(date.getHours(), 2)
				+ formatString(date.getMinutes(), 2)
				+ formatString(date.getSeconds(), 2) + "_";
	}

	public static String getMyDateTime_YearString(Date date) {
		return "" + (1900 + date.getYear());
	}

	public static String getMyDateTime_MonthString(Date date) {
		return formatString(date.getMonth() + 1, 2);
	}

	public static String formatString(long value, int digits) {
		String retval = "" + value;
		while (retval.length() < digits)
			retval = "0" + retval;
		return retval;
	}
}
