package com.emo.mango.config.internal.paths;

import java.io.File;

import com.emo.mango.config.PathFinder;
import com.emo.mango.config.internal.paths.WhichOS.OS;

public class ConfigPathFinder implements PathFinder {
	
	private final PathProvider[] providers;
	
	private final ConfigPathFinderStrategy strategy;
	
	private final ConfigTreeNaming naming;
	
	protected ConfigPathFinder(final ConfigPathFinderStrategy strategy, final ConfigTreeNaming naming, final String configContainerName) {
		this.providers = new PathProvider[] {
			new WinPath(configContainerName),
			new UnixPath(configContainerName)
		};
		
		this.strategy = strategy;
		
		this.naming = naming;
	}

	public File[] paths() {
		PathProvider detectedProvider = null;
		
		for(PathProvider provider : providers) {
			if(WhichOS.os.equals(provider.os())) {
				detectedProvider = provider;
			}
		}
		
		if(null == detectedProvider) {
			throw new IllegalStateException("no support for configuration on this OS : " + WhichOS.os);
		}
		
		final File[] files = detectedProvider.paths(strategy);
		
		if(files.length == 0) {
			throw new IllegalStateException("no configuration path found for this OS : " + WhichOS.os);
		}

		final File[] filesWithVersion = new File[files.length];
		
		for(int i = 0; i < files.length; ++i) {
			filesWithVersion[i] = new File(files[i], naming.tree(VersionProperties.get));
		}
		
		return filesWithVersion;
	}
	
	private static interface PathProvider {
		public File[] paths(final ConfigPathFinderStrategy strategy);

		public WhichOS.OS os();
	}

	private static class WinPath implements PathProvider {
		private final File[] filesWithAppData;
		private final File[] filesWithUserHome;

		public WinPath(final String configContainerName) {
			filesWithAppData = new File[] { new File(appDataFile,
					configContainerName) };
			filesWithUserHome = new File[] { new File(userHomeFile,
					configContainerName) };
		}

		private static final String appData = System.getenv("APPDATA");

		private static final File appDataFile = new File(appData);
		private static final File userHomeFile = new File(
				System.getProperty("user.home"));

		private static final File[] emptyFileArray = new File[] {};

		@Override
		public File[] paths(final ConfigPathFinderStrategy strategy) {
			if(strategy == ConfigPathFinderStrategy.SystemDirectory) {
				return emptyFileArray;
			}
			
			if (appDataFile.exists()) {
				return filesWithAppData;
			} else if (userHomeFile.exists()) {
				return filesWithUserHome;
			} else {
				return emptyFileArray;
			}
		}

		@Override
		public OS os() {
			return OS.win;
		}
	}

	private static class UnixPath implements PathProvider {
		private static final File etc = new File("/etc");
		private static final File home = new File(
				System.getProperty("user.home"));
		
		private final File[] userOnly;
		private final File[] systemOnly;
		private final File[] all;
		
		private static final File[] empty = new File[] {};
		
		public UnixPath(final String configContainerName) {
			if (etc.exists() && home.exists()) {
				all = new File[] { new File(home, "." + configContainerName),
						new File(etc, configContainerName) };
				userOnly = new File[] { new File(home, "." + configContainerName) };
				systemOnly = new File[] { new File(etc, configContainerName) };
			}
			else if(home.exists()) {
				all = new File[] { new File(home, "." + configContainerName) };
				userOnly = new File[] { new File(home, "." + configContainerName) };
				systemOnly = empty;
			}
			else if(etc.exists()) {
				all = new File[] { new File(etc, configContainerName) };
				userOnly = empty;
				systemOnly = new File[] { new File(etc, configContainerName) };
			}
			else {
				all = empty;
				userOnly = empty;
				systemOnly = empty;
			}
		}

		@Override
		public File[] paths(final ConfigPathFinderStrategy strategy) {
			if(strategy == ConfigPathFinderStrategy.SystemAndUserDirectories) {
				return all;
			}
			else if(strategy == ConfigPathFinderStrategy.SystemDirectory) {
				return systemOnly;
			}
			else if(strategy == ConfigPathFinderStrategy.UserDirectory) {
				return userOnly;
			}
			
			return empty;
		}
		
		@Override
		public OS os() {
			return OS.unix;
		}
	}
}
