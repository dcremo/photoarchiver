package it.dcremo.photoarchiver;

import java.io.File;
import java.io.IOException;

import com.imagero.reader.ImageReader;
import com.imagero.reader.ReaderFactory;
import com.imagero.reader.jfif.JpegMetadataReader;
import com.imagero.reader.jpeg.ExifApp1;
import com.imagero.reader.jpeg.JpegReader;
import com.imagero.reader.tiff.IFDEntry;
import com.imagero.reader.tiff.IFDEntryMeta;
import com.imagero.reader.tiff.ImageFileDirectory;
import com.imagero.uio.RandomAccess;
import com.imagero.uio.RandomAccessFactory;

public class EstrattoreDataJpeg {

	String fileName;

	public EstrattoreDataJpeg(String fileName) {
		this.fileName = fileName;
	}

	public String getDateTime() throws IOException {
		String retval = null;
		ImageReader reader = ReaderFactory.createReader(fileName);
		JpegReader jpegReader = ((JpegReader) reader);
		JpegMetadataReader jmr = jpegReader.getJpegMetadataReader();
		if (jmr.hasExif()) {
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
		}
		return retval;
	}
}