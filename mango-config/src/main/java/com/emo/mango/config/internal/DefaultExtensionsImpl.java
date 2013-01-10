package com.emo.mango.config.internal;

import com.emo.mango.config.DataSourceExtension;
import com.emo.mango.config.DefaultExtensions;

class DefaultExtensionsImpl implements DefaultExtensions {
	private DataSourceExtension datasource = null;

	public DataSourceExtension datasource() {
		if (null == datasource) {
			datasource = new DataSourceExtension();
		}

		return datasource;
	}
}