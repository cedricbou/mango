package com.emo.mango.config;

import com.emo.mango.config.internal.paths.ConfigPathFinderStrategy;

public enum RootDirectoryStrategy {
	SystemAndUserDirectories(ConfigPathFinderStrategy.SystemAndUserDirectories),
	SystemDirectory(ConfigPathFinderStrategy.SystemDirectory),
	UserDirectory(ConfigPathFinderStrategy.UserDirectory);
	
	private ConfigPathFinderStrategy strategy;
	
	private RootDirectoryStrategy(final ConfigPathFinderStrategy strategy) {
		this.strategy = strategy;
	}
	
	public ConfigPathFinderStrategy strategy() {
		return strategy;
	}
}
