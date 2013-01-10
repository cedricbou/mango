package com.emo.mango.config.internal.paths.tree;

import java.io.File;

import com.emo.mango.config.internal.paths.ConfigTreeNaming;
import com.emo.mango.config.internal.paths.VersionProperties;

public class GroupArtifactRelease implements ConfigTreeNaming {

	@Override
	public String tree(final VersionProperties version) {
		return 
			version.group 
			+ File.separator + version.artifact 
			+ File.separator + version.release;
	}
}
