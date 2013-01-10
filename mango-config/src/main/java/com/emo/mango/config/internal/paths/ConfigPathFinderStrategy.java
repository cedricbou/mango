package com.emo.mango.config.internal.paths;

import com.emo.mango.config.PathFinder;

public enum ConfigPathFinderStrategy {
	SystemAndUserDirectories(new ConfigPathFinderFactory() {
		@Override
		public PathFinder get(final String name, final ConfigTreeNaming naming) {
			return new ConfigPathFinder(ConfigPathFinderStrategy.SystemAndUserDirectories, naming, name);
		}
	}),
	SystemDirectory(new ConfigPathFinderFactory() {
		@Override
		public PathFinder get(final String name, final ConfigTreeNaming naming) {
			return new ConfigPathFinder(ConfigPathFinderStrategy.SystemDirectory, naming, name);
		}
	}),
	UserDirectory(new ConfigPathFinderFactory() {
		@Override
		public PathFinder get(final String name, final ConfigTreeNaming naming) {
			return new ConfigPathFinder(ConfigPathFinderStrategy.UserDirectory, naming, name);
		}
	});

	private ConfigPathFinderFactory factory;
	
	private ConfigPathFinderStrategy(ConfigPathFinderFactory factory) {
		this.factory = factory;
	}
	
	public ConfigPathFinderFactory factory() {
		return this.factory;
	}
}
