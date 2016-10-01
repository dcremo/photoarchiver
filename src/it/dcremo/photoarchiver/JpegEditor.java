package it.dcremo.photoarchiver;

import java.io.File;
import java.io.IOException;

import com.imagero.reader.jfif.JpegMetadataReader;
import com.imagero.reader.jpeg.ExifApp1;
import com.imagero.reader.jpeg.JpegReader;
import com.imagero.reader.tiff.IFDEntry;
import com.imagero.reader.tiff.IFDEntryMeta;
import com.imagero.reader.tiff.ImageFileDirectory;
import com.imagero.uio.RandomAccess;
import com.imagero.uio.RandomAccessFactory;
import com.imagero.uio.io.IOutils;

public class JpegEditor {
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

}
