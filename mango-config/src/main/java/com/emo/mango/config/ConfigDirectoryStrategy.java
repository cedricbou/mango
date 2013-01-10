package com.emo.mango.config;

import com.emo.mango.config.internal.paths.ConfigTreeStrategy;

public enum ConfigDirectoryStrategy {
	GroupArtifactRelease(ConfigTreeStrategy.GroupArtifactRelease),
	GroupArtifactVersion(ConfigTreeStrategy.GroupArtifactVersion),
	Nothing(ConfigTreeStrategy.Nothing);
	
	private ConfigTreeStrategy strategy;
	
	private ConfigDirectoryStrategy(final ConfigTreeStrategy strategy) {
		this.strategy = strategy;
	}
	
	public ConfigTreeStrategy strategy() {
		return strategy;
	}
}
