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

public class JpegDateReader {

	String fileName;

	public JpegDateReader(String fileName) {
		this.fileName = fileName;
	}

	public String getDateTime() throws IOException {
		String retval = null;
		RandomAccess ra = RandomAccessFactory.create(new File(fileName));
		JpegReader reader = new JpegReader(ra);
		JpegMetadataReader jmr = reader.getJpegMetadataReader();
		if (jmr.hasExif()) {
			ExifApp1 exif = jmr.getExif()[0];
			// exif.getExifParser().debug();

			ImageFileDirectory ifd = exif.getExifParser().getExifIFD();
			if (ifd != null) {
				IFDEntry entry = ifd.getEntry(IFDEntryMeta.DATE_TIME_ORIGINAL);
				if (entry != null) {
					retval = entry.getValue().toString().trim();
				}
			}
		}
		ra.close();
		return retval;
	}
}