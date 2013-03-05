package com.emo.mango.test.support;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class FooBarMapper implements ResultSetMapper<FooBar> {

	@Override
	public FooBar map(int arg0, ResultSet arg1, StatementContext arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
}
