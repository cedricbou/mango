package com.emo.mango.config.internal.paths;

import com.emo.mango.config.PathFinder;

public interface ConfigPathFinderFactory {
	
	public PathFinder get(final String name, final ConfigTreeNaming naming);
}
