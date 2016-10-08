package it.dcremo.photoarchiver.tools;

import java.io.File;
import java.io.FileReader;

public class PurgeDuplicateFiles {
	public static void main(String[] args) {
		PurgeDuplicateFiles f = new PurgeDuplicateFiles(new File("H:/Immagini/FotoOrdinate"));
		f.purge();
	};
		
	File rootDir;
	PurgeDuplicateFiles(File rootDir) {
		this.rootDir = rootDir;
	};	
	
	public void purge() {
		doPurge(rootDir);
	}
	
	public static void doPurge(File f) {
		if (f.isDirectory()) {
			File[] c = f.listFiles();
			for (File file : c) {
				doPurge(file);
			}
		} else {
			int prefixLen = "2011_03_06_17_36_47_".length(); // 20
			String name = f.getName().toUpperCase();
			// 2011_03_06_17_36_47_IMG_1648.JPG
			// esiste il doppione?
			// 2011_03_06_17_36_47_2011_03_06_17_36_47_IMG_1648.JPG
			if (name.length()>prefixLen) {
				String root = name.substring(0, prefixLen);
				File ghostfile = new File(f.getParentFile().getAbsolutePath()+"/"+root+name);
				if (ghostfile.exists()) {
					if (ghostfile.length()==f.length()) {
						System.out.println("DELETE:"+ghostfile.getAbsolutePath());
						ghostfile.delete();
					} else {
						System.out.println("KEEP FALSE GHOST:"+ghostfile.getAbsolutePath());
					}	
				}
			}
		}
	}
}
