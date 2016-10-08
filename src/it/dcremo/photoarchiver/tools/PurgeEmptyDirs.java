package it.dcremo.photoarchiver.tools;

import java.io.File;

public class PurgeEmptyDirs {
	public static void main(String[] args) {
		PurgeEmptyDirs f = new PurgeEmptyDirs(new File("C:/FotoOrdinateDaVecchioDisco"));
		f.purge();
	};
		
	File rootDir;
	PurgeEmptyDirs(File rootDir) {
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
			c = f.listFiles();
			if (c.length==0) {
				System.out.println("DELETE:"+f.getAbsolutePath());
				f.delete();
			}
		} else {
			if (f.getName().equalsIgnoreCase("Picasa.ini") ||
				f.getName().equalsIgnoreCase(".Picasa.ini") ||
				f.getName().equalsIgnoreCase("ehthumbs_vista.db")) {
				System.out.println("DELETE:"+f.getAbsolutePath());
				f.delete();
			}
		}
	}
}
