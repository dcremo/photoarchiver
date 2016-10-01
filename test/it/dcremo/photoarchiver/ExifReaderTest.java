package it.dcremo.photoarchiver;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.imagero.reader.ImageReader;
import com.imagero.reader.ReaderFactory;
import com.imagero.reader.exif.ExifParser;
import com.imagero.reader.jfif.JpegMetadataReader;
import com.imagero.reader.jpeg.ExifApp1;
import com.imagero.reader.jpeg.JpegReader;
import com.imagero.reader.tiff.IFDEntry;
import com.imagero.reader.tiff.IFDEntryMeta;
import com.imagero.reader.tiff.ImageFileDirectory;
/**
 * @author admin
 *
 */
public class ExifReaderTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void readEXIF() throws IOException {
		// read EXIF from JPEG files
		String filename = "test/resources/DSC_0734.jpg";
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

	@Test
	public void editExif() throws IOException {
		String filename = "test/resources/DSC_0734.jpg";
		String outputFilename = "test/output/DSC_0734_EDIT.jpg";
		File outf = new File(outputFilename);
		if (outf.exists()) {
			outf.delete();
		}
		// TODO: completa
	}
}
