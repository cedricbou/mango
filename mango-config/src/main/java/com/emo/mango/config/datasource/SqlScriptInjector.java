package com.emo.mango.config.datasource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.emo.mango.config.internal.ds.ScriptRunner;

public class SqlScriptInjector {

	public SqlScriptInjector(final DataSource datasource,
			final InputStream scriptIs) throws IOException, SQLException {
		
		final Connection con = datasource.getConnection();
		
		try {
			final ScriptRunner runner = new ScriptRunner(con, false, true);
			runner.runScript(new InputStreamReader(scriptIs));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			scriptIs.close();
			con.close();
		}
	}
}
