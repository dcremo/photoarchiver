package it.dcremo.photoarchiver;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EstrattoreDataJpegTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetDateTime() throws IOException {
		EstrattoreDataJpeg jpegReader = new EstrattoreDataJpeg("test/resources/DSC_0734.jpg");		
		assertEquals("2013:08:01 00:45:01", jpegReader.getDateTime());
	}

}
