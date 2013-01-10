package com.emo.mango.config.internal;

import java.io.File;

import org.joda.time.DateTime;

class TrackDirectory {
	private DateTime lastModified = DateTime.now();
	
	private final File[] files;
	
	public TrackDirectory(final File confDirectory) {
		files = new File[ConfigurationFiles.names.length];
		for(int i = 0; i < files.length; ++i) {
			files[i] = new File(confDirectory, ConfigurationFiles.names[i]);
		}
	}
	
	public boolean modified() {
		for(File file : files) {
			if(file.exists() && file.canRead() && new DateTime(file.lastModified()).isAfter(lastModified)) {
				return true;
			}
		}
		return false;
	}
	
	public void track() {
		if(modified()) {
			lastModified = DateTime.now();
		}
	}
}