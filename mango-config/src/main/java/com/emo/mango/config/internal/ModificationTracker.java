package com.emo.mango.config.internal;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

class ModificationTracker extends Thread {

	private final List<SubscriberEntry> subscribers = new LinkedList<SubscriberEntry>(); 
		
	private long checkIntervalMillis;
	
	public final static ModificationTracker get = new ModificationTracker(2000);
	
	static {
		get.start();
	}
		
	private ModificationTracker(final long checkIntervalMillis) {
		this.checkIntervalMillis = checkIntervalMillis;
	}
	
	public void subscribe(final MangoConfigImpl subscriber, final File[] paths) {
		final TrackDirectory[] directories = new TrackDirectory[paths.length];
		
		int i = 0;
		for(final File path : paths) {
			directories[i++] = new TrackDirectory(path);
		}
		
		this.subscribers.add(new SubscriberEntry(subscriber, directories));
	}
	
	@Override
	public void run() {
		super.run();
		
		while(true) {
			for(final SubscriberEntry entry : subscribers) {
				for(final TrackDirectory directory : entry.directories) {
					if(directory.modified()) {
						entry.subscriber.loadConfiguration();
						break;
					}
				}
				
				for(final TrackDirectory directory : entry.directories) {
					directory.track();
				}
			}
			
			try {
				Thread.sleep(checkIntervalMillis);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static class SubscriberEntry {
		public final MangoConfigImpl subscriber;
		public final TrackDirectory[] directories;
		
		public SubscriberEntry(final MangoConfigImpl subscriber, final TrackDirectory[] directories) {
			this.subscriber = subscriber;
			this.directories = directories;
		}
	}
}