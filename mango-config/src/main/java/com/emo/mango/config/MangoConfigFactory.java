package com.emo.mango.config;

import com.emo.mango.config.internal.MangoConfigImpl;

public class MangoConfigFactory {

	public static MangoConfig load() {
		return new MangoConfigImpl(System.getenv("mango.profile"), "mango",
				RootDirectoryStrategy.SystemAndUserDirectories,
				ConfigDirectoryStrategy.GroupArtifactRelease,
				TrackingStrategy.ModificationTracking);
	}

	public MangoConfig config() {
		return MangoConfigFactory.load();
	}

}
