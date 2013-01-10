package com.emo.mango.config.internal.paths;

import com.emo.mango.config.internal.paths.tree.GroupArtifactRelease;
import com.emo.mango.config.internal.paths.tree.GroupArtifactVersion;
import com.emo.mango.config.internal.paths.tree.Nothing;

public enum ConfigTreeStrategy {
	GroupArtifactRelease(new GroupArtifactRelease()),
	GroupArtifactVersion(new GroupArtifactVersion()),
	Nothing(new Nothing());
	
	private ConfigTreeNaming naming;
	
	private ConfigTreeStrategy(final ConfigTreeNaming naming) {
		this.naming = naming;
	}
	
	public ConfigTreeNaming naming() {
		return naming;
	}
}
