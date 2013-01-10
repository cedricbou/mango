package com.emo.mango.config;

import com.typesafe.config.Config;

public interface Extension {
	public void onConfigurationChanged(final Config config);
}