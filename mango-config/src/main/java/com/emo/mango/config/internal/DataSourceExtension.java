package com.emo.mango.config.internal;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.emo.mango.config.internal.ds.DataSourceWrapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class DataSourceExtension {

	private Config config = ConfigFactory.empty();

	private final Map<String, DataSourceWrapper> datasources = new HashMap<String, DataSourceWrapper>();

	public DataSourceExtension() {
		super();
	}

	public void onConfigurationChanged(Config config) {
		final Config formerConfig = this.config;

		if (config.hasPath("datasources")) {
			final Config newConfig = config.getConfig("datasources");
			this.config = newConfig;

			for (final String name : datasources.keySet()) {
				if (!configEquals(formerConfig.getConfig(name),
						newConfig.getConfig(name))) {
					reconfigureDataSource(name);
				}
			}
		}
	}

	private synchronized void createDataSourceIfRequired(final String name) {
		final DataSourceWrapper ds = datasources.get(name);
		final Config config = this.config.getConfig(name);

		if (ds == null) {
			final BasicDataSource newDs = new BasicDataSource();
			configureDataSource(newDs, config);

			datasources.put(name, DataSourceWrapper.wrap(newDs));
		}
	}

	private synchronized void reconfigureDataSource(final String name) {
		System.out.println("reconfigure datasource " + name);

		final DataSourceWrapper ds = datasources.get(name);
		final Config config = this.config.getConfig(name);

		final BasicDataSource replacementDs = new BasicDataSource();
		configureDataSource(replacementDs, config);

		try {
			ds.switchTo(replacementDs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public DataSource datasource() {
		return datasource("default");
	}

	public DataSource datasource(final String name) {
		createDataSourceIfRequired(name);
		return datasources.get(name);
	}

	private static void configureDataSource(final BasicDataSource ds,
			final Config config) {
		ds.setUrl(config.getString("url"));
		// FIXME : it is possible to only set an URL with JDBC.
		ds.setUsername(config.getString("user"));
		ds.setPassword(config.getString("password"));
		ds.setDriverClassName(com.mysql.jdbc.Driver.class.getName());
		ds.setMaxActive(1);
		ds.setMaxWait(30L);
		ds.setMinIdle(1);
		ds.setValidationQuery("select 1;");

		if (config.hasPath("driver")) {
			ds.setDriverClassName(config.getString("driver"));
		}

		if (config.hasPath("pool.max")) {
			ds.setMaxActive(config.getInt("pool.max"));
		}

		if (config.hasPath("pool.min")) {
			ds.setMinIdle(config.getInt("pool.max"));
		}

		if (config.hasPath("pool.wait")) {
			ds.setMaxWait(config.getInt("pool.wait"));
		}
	}

	private static boolean configEquals(final Config c1, final Config c2) {
		return configPathEquals("url", c1, c2)
				&& configPathEquals("user", c1, c2)
				&& configPathEquals("password", c1, c2)
				&& configPathEquals("driver", c1, c2)
				&& configPathEquals("pool.max", c1, c2)
				&& configPathEquals("pool.min", c1, c2)
				&& configPathEquals("pool.wait", c1, c2);
	}

	private final static boolean configPathEquals(final String path,
			final Config c1, final Config c2) {
		return (!c1.hasPath(path) && !c2.hasPath(path))
				|| ((c1.hasPath(path) && c2.hasPath(path)) && c1
						.getAnyRef(path).equals(c2.getAnyRef(path)));
	}
}
