package com.emo.mango.config.internal;

import com.typesafe.config.Config;

public interface Extension {
	public void onConfigurationChanged(final Config config);
}