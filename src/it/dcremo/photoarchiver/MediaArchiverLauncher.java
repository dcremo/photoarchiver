package it.dcremo.photoarchiver;


import java.io.File;
import java.io.IOException;

import com.imagero.reader.ImageReader;
import com.imagero.reader.ReaderFactory;
import com.imagero.reader.exif.ExifParser;
import com.imagero.reader.jfif.JpegMetadataReader;
import com.imagero.reader.jpeg.ExifApp1;
import com.imagero.reader.jpeg.JpegReader;
import com.imagero.reader.tiff.IFDEntry;
import com.imagero.reader.tiff.IFDEntryMeta;
import com.imagero.reader.tiff.ImageFileDirectory;
import com.imagero.uio.RandomAccess;
import com.imagero.uio.RandomAccessFactory;
import com.imagero.uio.io.IOutils;

public class MediaArchiverLauncher {

	public MediaArchiverLauncher() {
	}

	public static void main(String[] args) throws Exception {
		/*
		 * ImageReaderMain imageReaderMain1 = new ImageReaderMain(); try {
		 * imageReaderMain1.editEXIF(); } catch (IOException ex) {
		 * ex.printStackTrace(); }
		 * 
		 * try { imageReaderMain1.readEXIF(); } catch (IOException ex) {
		 * ex.printStackTrace(); }
		 */
		String sourceDir;
		String imageDestinationDir;
		String otherDestinationDir;
		if (args.length==3) {
			sourceDir = args[0];
			imageDestinationDir = args[1];
			otherDestinationDir = args[2];
		} else {
			System.err.println("Specificare le cartelle di lavoro.");
			System.err.println("1- Cartella contenente le immagini da archiviare");
			System.err.println("2- Cartella in cui archiviare le immagini rinominate");
			System.err.println("3- Cartella in cui archiviare i video rinominati");
			
			throw new Exception("Numero di parametri insufficienti.");
		}
		
		if (!esisteDir(sourceDir)) {
			System.err.println("La cartella specificata contenente le immagini da archiviare ["+sourceDir+"] non esiste o non è accessibile.");
			return;
		}
		if (!esisteDir(imageDestinationDir)) {
			System.err.println("La cartella destinazione per le immagini ordinate ["+imageDestinationDir+"] non esiste o non è accessibile.");
			return;
		}
		if (!esisteDir(otherDestinationDir)) {
			System.err.println("La cartella destinazione per i video ordinati ["+otherDestinationDir+"] non esiste o non è accessibile.");
			return;
		}
		
		MediaArchiverWorker cp = new MediaArchiverWorker(sourceDir, imageDestinationDir,
				otherDestinationDir);
		cp.doArchive(null);
		// ShiftImageDate.processDir(null);
	}

	private static boolean esisteDir(String sourceDir) {
		File f = new File(sourceDir);
		return f.exists()&&f.isDirectory();
	}
}
