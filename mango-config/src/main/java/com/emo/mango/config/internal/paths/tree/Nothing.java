package com.emo.mango.config.internal.paths.tree;

import com.emo.mango.config.internal.paths.ConfigTreeNaming;
import com.emo.mango.config.internal.paths.VersionProperties;

public class Nothing implements ConfigTreeNaming {

	@Override
	public String tree(final VersionProperties version) {
		return "";
	}
}
