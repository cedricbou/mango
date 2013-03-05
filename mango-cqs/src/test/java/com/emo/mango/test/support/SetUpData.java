package com.emo.mango.test.support;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.logging.PrintStreamLog;

public class SetUpData {
	public static void setUpPeople(DBI dbi) {
		dbi.setSQLLog(new PrintStreamLog(System.out));
		final Handle handle = dbi.open();

		handle.execute("drop table if exists people");
		handle.execute("create table people( id integer primary key, name varchar(100), foo varchar(100), bar varchar(100) )");
	
		final String insertQuery = "insert into people (id, name, foo, bar) values (?, ?, ?, ?)";
		
		int i = 0;
		
		handle.execute(insertQuery, ++i, "gregory", "winner", "bar");
		handle.execute(insertQuery, ++i, "stephan", "nothing", "bar");
		handle.execute(insertQuery, ++i, "george greg", "winner", "bar");
		handle.execute(insertQuery, ++i, "greg", "winner", "bar");
		handle.execute(insertQuery, ++i, "greg stephan", "winner", "bar");
		handle.execute(insertQuery, ++i, "vanceslas", "nothing", "bar");
		handle.execute(insertQuery, ++i, "john greg", "winner", "bar");
		handle.execute(insertQuery, ++i, "paul greg", "winner", "bar");
		handle.execute(insertQuery, ++i, "gregoire", "winner", "bar");
		handle.execute(insertQuery, ++i, "grzegusz", "cool", "bar");
		handle.execute(insertQuery, ++i, "grzegusz", "cool", "bar");
		handle.execute(insertQuery, ++i, "grzegusz", "cool", "bar");
		handle.execute(insertQuery, ++i, "grzegusz", "cool", "bar");
		
		handle.close();
	}
}
