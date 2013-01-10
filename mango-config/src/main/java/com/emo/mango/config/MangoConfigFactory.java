package com.emo.mango.config;

import com.emo.mango.config.internal.MangoConfigImpl;

public class MangoConfigFactory {
	
	public static MangoConfig load() {
		return new MangoConfigImpl("mango",
			RootDirectoryStrategy.SystemAndUserDirectories,
			ConfigDirectoryStrategy.GroupArtifactRelease,
			TrackingStrategy.ModificationTracking,
			AvailableExtensions.DataSource);
	}
	
	public MangoConfig config() {
		return MangoConfigFactory.load();
	}

}
