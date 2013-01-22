package com.emo.mango.config.internal.ds;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

public class DataSourceWrapper implements DataSource {

	private BasicDataSource ds;

	private final Set<Connection> connections = new HashSet<Connection>();

	public DataSourceWrapper(final BasicDataSource ds) {
		this.ds = ds;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return ds.getLogWriter();
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return ds.getLoginTimeout();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		ds.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		ds.setLoginTimeout(seconds);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return ds.isWrapperFor(iface);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return ds.unwrap(iface);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return wrapConnection(ds.getConnection());
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		return wrapConnection(ds.getConnection(username, password));
	}

	private Connection wrapConnection(final Connection con) {
		connections.add(con);
		return con;
	}

	public void switchTo(final BasicDataSource ds) throws SQLException {
		try {
			this.ds.close();
			for (final Connection con : connections) {
				con.close();
			}
			connections.clear();
			this.ds = ds;
		} catch (SQLException e) {
			throw new SQLException("failed to switch datasource, problem during closing previous data source", e);
		}
	}
	
	public BasicDataSource underlying() {
		return ds;
	}
	
	public static DataSourceWrapper wrap(final BasicDataSource ds) {
		return new DataSourceWrapper(ds);
	}
}
