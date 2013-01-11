package com.emo.mango.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.typesafe.config.ConfigFactory;


public class MangoConfigTestApp {

	public static void main(String[] args) throws SQLException, InterruptedException {
		final MangoConfigTestApp ta = new MangoConfigTestApp();
		ta.testMangoConfig();
		ta.testMangoConfig2();
	}
	

	public void testMangoConfig() throws SQLException {
		final MangoConfig config = MangoConfigFactory.load();

		System.out.println(config.config());
				
		final String[] urls = new String[] {
			"jdbc:mysql://localhost:3306/test",
			"jdbc:mysql://localhost:3306/test2"
		};
		
		Properties props = new Properties();
		
		for(int i = 0; i < 10; ++i) {
			props.setProperty("datasources.default.url", urls[i % urls.length]);
			config.override(ConfigFactory.parseProperties(props));

			final Connection con = config.datasource().getConnection();
			final PreparedStatement stmt = con.prepareStatement("select * from test");

			stmt.execute();
			final ResultSet rs = stmt.getResultSet();
			while(rs.next()) {
				System.out.println(rs.getString("col1") + rs.getString("col2"));
			}
			rs.close();
			
			stmt.close();
			con.close();
		}
		
	}

	public void testMangoConfig2() throws SQLException, InterruptedException {
		final MangoConfig config = MangoConfigFactory.load();

		System.out.println(config.config());
						
		for(int i = 0; i < 24; ++i) {
			final Connection con = config.datasource().getConnection();
			final PreparedStatement stmt = con.prepareStatement("select * from test");

			stmt.execute();
			final ResultSet rs = stmt.getResultSet();
			while(rs.next()) {
				System.out.println(rs.getString("col1") + rs.getString("col2"));
			}
			rs.close();
			
			stmt.close();
			con.close();
			
			Thread.sleep(5000);
		}
		
	}

}
