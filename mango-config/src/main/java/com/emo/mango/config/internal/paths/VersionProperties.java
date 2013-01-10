package com.emo.mango.config.internal.paths;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VersionProperties {
	public final static VersionProperties get = new VersionProperties();
	
	public final String group;
	public final String artifact;
	public final String version;
	public final String release;
	
	private final static String GROUP_KEY = "app.group";
	private final static String ARTIFACT_KEY = "app.artifact";
	private final static String VERSION_KEY = "app.version";
	private final static String RELEASE_KEY = "app.release";
		
	private final static String VERSION_PROPERTY_FILE = "version.properties";

	private final static Properties versionProps() {
		final Properties props = new Properties();
		try {
			final InputStream inProps = ClassLoader.getSystemResourceAsStream(VERSION_PROPERTY_FILE);
			
			if(inProps == null) {
				throw new RuntimeException("no version.properties file found in classpath");
			}
			
			props.load(inProps);
		}
		catch(IOException e) {
			throw new RuntimeException("failed to load " + VERSION_PROPERTY_FILE, e);
		}
		return props;
	}

	public VersionProperties() {
		this(versionProps());
	}
	
	public VersionProperties(final Properties props) {
		assertValid(GROUP_KEY, props);
		group = props.getProperty(GROUP_KEY);
		
		assertValid(ARTIFACT_KEY, props);
		artifact = props.getProperty(ARTIFACT_KEY);
		
		assertValid(VERSION_KEY, props);
		version = props.getProperty(VERSION_KEY);
	
		assertValid(RELEASE_KEY, props);
		release = props.getProperty(RELEASE_KEY);
	}
	
	public VersionProperties(final String group, final String artifact, final String version, final String release) {
		this.group = group;
		this.artifact = artifact;
		this.version = version;
		this.release = release;
	}
	
	private void assertValid(final String key, final Properties props) {
		final String value = props.getProperty(key);
		if(null == value) {
			throw new IllegalArgumentException(key + " cannot be found in the version properties");
		}
	}
	
	@Override
	public String toString() {
		return group + "-" +  artifact + "-" + release + "[" + version + "]";
	}
}
