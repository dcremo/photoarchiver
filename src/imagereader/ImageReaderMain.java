package imagereader;

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

public class ImageReaderMain {
	// public static String sourceDir =
	// "C:/Documents and Settings/dcremonesi/Documenti/CopiaDiscoUSB/Davide";
	// public static String sourceDir =
	// "C:/Documents and Settings/dcremonesi/Documenti/Immagini/DumpCD";
	// public static String sourceDir = "H:/";
	// public static String sourceDir =
	// "C:/Documents and Settings/dcremonesi/Documenti/Immagini/FotoDaAllineare";
	// public static String sourceDir =
	// "C:/Documents and Settings/dcremonesi/Documenti/Video/daDell";
	// public static String sourceDir =
	// "C:\\Documents and Settings\\dcremonesi\\Documenti\\Immagini\\FotoOrdinate\\Nuova cartella";
	public static String sourceDir = "D:\\updateImg";
	public static String imageDestinationDir = "D:/Users/davide/Pictures/FotoOrdinate";
	public static String otherDestinationDir = "D:/Users/davide/Videos/FilmatiOrdinati";

	public ImageReaderMain() {
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
		CopyImages cp = new CopyImages(sourceDir, imageDestinationDir,
				otherDestinationDir);
		cp.doArchive(null);
		// ShiftImageDate.processDir(null);
	}

	public void readEXIF() throws IOException {
		// read EXIF from JPEG files
		String filename = "testimg/test.jpg";
		ImageReader reader = ReaderFactory.createReader(filename);
		if (reader instanceof JpegReader) {
			JpegReader jpegReader = ((JpegReader) reader);
			JpegMetadataReader jmr = jpegReader.getJpegMetadataReader();
			if (jmr.hasExif()) {
				ExifApp1 exif = jmr.getExif()[0];
				ExifParser parser = exif.getExifParser();
				int count = parser.getIfdCount();
				for (int i = 0; i < count; i++) {
					System.out.println("\nImageFileDirectory:" + i);
					System.out.println("************************");
					ImageFileDirectory ifd = parser.getIFD(i);
					int entryCount = ifd.getEntryCount();
					for (int k = 0; k < entryCount; k++) {
						IFDEntry entry = ifd.getEntryAt(k);
						IFDEntryMeta meta = entry.getEntryMeta();
						String name = meta.getName();
						System.out.println(name + ": " + entry);
					}
				}
			}
		}
	}

	/**
	 * editEXIF
	 */
	public void editEXIF() throws IOException {
		// edit EXIF in JPEG files
		final File f = new File("testimg/test.jpg");
		// create writeable stream
		RandomAccess ra = RandomAccessFactory.create(f);
		JpegReader reader = new JpegReader(ra);
		JpegMetadataReader jmr = reader.getJpegMetadataReader();
		if (jmr.hasExif()) {
			ExifApp1 exif = jmr.getExif()[0];
			exif.getExifParser().debug();
			/*
			 * int count = exif.getExifParser().getIfdCount(); //iterate through
			 * IFDs for (int i = 0; i < count; i++) { ImageFileDirectory ifd =
			 * exif.getExifParser().getIFD(i); //get UserComment IFDEntry entry
			 * = ifd.getEntry(IFDEntryMeta.SOFTWARE); if (entry != null) { //get
			 * IFDEntry data as byte array byte[] data = entry.getRawValue();
			 * int j = 0; //search first 0 for (; j < data.length; j++) { if
			 * (data[j] == 0) { break; } } String s = "pippo"; byte[] b0 =
			 * s.getBytes(); //write String for (int k = 0; k < b0.length; k++)
			 * { final int index = j + k; if (index >= data.length) { break; }
			 * data[index] = b0[k]; } //save changes entry.writeRawValue(); } }
			 */

			ImageFileDirectory ifd = exif.getExifParser().getExifIFD();
			IFDEntry entry = ifd.getEntry(IFDEntryMeta.DATE_TIME_ORIGINAL);
			if (entry != null) {
				// get IFDEntry data as byte array
				byte[] data = entry.getRawValue();
				int j = 0;
				// search first 0
				for (; j < data.length; j++) {
					if (data[j] == 0) {
						break;
					}
				}
				String s = "pippo";
				byte[] b0 = s.getBytes();
				// write String
				for (int k = 0; k < b0.length; k++) {
					final int index = j + k;
					if (index >= data.length) {
						break;
					}
					data[index] = b0[k];
				}
				// save changes
				entry.writeRawValue();
			}
		}
		// close stream
		IOutils.closeStream(ra);

	}

	public static String getDateTime(File f) throws IOException {
		String retval = null;
		// create writeable stream
		RandomAccess ra = RandomAccessFactory.create(f);
		JpegReader reader = new JpegReader(ra);
		JpegMetadataReader jmr = reader.getJpegMetadataReader();
		if (jmr.hasExif()) {
			ExifApp1 exif = jmr.getExif()[0];
			// exif.getExifParser().debug();

			ImageFileDirectory ifd = exif.getExifParser().getExifIFD();
			if (ifd != null) {
				IFDEntry entry = ifd.getEntry(IFDEntryMeta.DATE_TIME_ORIGINAL);
				if (entry != null) {
					retval = entry.getValue().toString();
				}
			}
		}
		// close stream
		IOutils.closeStream(ra);
		return retval != null ? retval.trim() : null;
	}
}
