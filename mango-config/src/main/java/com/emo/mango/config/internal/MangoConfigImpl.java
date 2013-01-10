package com.emo.mango.config.internal;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.emo.mango.config.AvailableExtensions;
import com.emo.mango.config.ConfigDirectoryStrategy;
import com.emo.mango.config.DefaultExtensions;
import com.emo.mango.config.Extension;
import com.emo.mango.config.MangoConfig;
import com.emo.mango.config.PathFinder;
import com.emo.mango.config.RootDirectoryStrategy;
import com.emo.mango.config.TrackingStrategy;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class MangoConfigImpl implements MangoConfig {

	private Config config = ConfigFactory.empty();

	private final PathFinder pathFinder;
		
	private final List<Extension> notifiers = new LinkedList<Extension>();

	private final DefaultExtensionsImpl ext = new DefaultExtensionsImpl();

	public MangoConfigImpl(final String name, final RootDirectoryStrategy rootDirectoryStrategy, final ConfigDirectoryStrategy configDirectoryStrategy, final TrackingStrategy trackingStrategy, final AvailableExtensions... extensions) {
		pathFinder = rootDirectoryStrategy.strategy().factory().get(name, configDirectoryStrategy.strategy().naming());
		loadConfiguration();
		
		for (final AvailableExtensions extension : extensions) {
			newExtension(extension.factory().extension(this.ext));
		}
		
		if(trackingStrategy == TrackingStrategy.ModificationTracking) {
			ModificationTracker.get.subscribe(this, pathFinder.paths());
		}
	}

	public Config config() {
		return config;
	}

	public void override(final Config config) {
		this.config = config.withFallback(this.config);
		notifyExtensions();
	}
	
	protected void loadConfiguration() {
		System.out.println("will load configuration");
		
		final File[] configDirs = pathFinder.paths();

		Config newConfig = ConfigFactory.empty();

		for (final File configDir : configDirs) {
			if (!configDir.exists()) {
				configDir.mkdirs();
			}
			
			File[] externalFiles = new File[ConfigurationFiles.names.length];
			for(int i = 0; i < externalFiles.length; ++i) {
				externalFiles[i] = new File(configDir, ConfigurationFiles.names[i]);
			}
			
			for (final File file : externalFiles) {
				try {
					if (!file.exists() && configDir.canWrite()) {
						file.createNewFile();
					}
				} catch (IOException e) {
					// TODO : better logging with warn or info level.
					System.err
							.println("could not auto-create empty configuration file for "
									+ file);
				}
				
				if (file.exists() && file.canRead()) {
					newConfig = newConfig.withFallback(ConfigFactory
							.parseFile(file));
				} else {
					// FIXME : add proper log warning if file does not exist or
					// cannot be read.
					System.err.println("could not read file " + file);
				}
			}

			newConfig = newConfig.withFallback(ConfigFactory.load());

			this.config = newConfig;

			notifyExtensions();
		}
	}
	
	public void notifyExtensions() {
		for (final Extension notifier : notifiers) {
			notifier.onConfigurationChanged(this.config);
		}
	}

	public void newExtension(Extension notifier) {
		notifiers.add(notifier);
		notifier.onConfigurationChanged(config);
	}
	
	public DefaultExtensions ext() {
		return ext;
	}
}
