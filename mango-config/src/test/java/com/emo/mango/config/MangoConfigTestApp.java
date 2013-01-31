package com.emo.mango.config;

import java.sql.SQLException;


public class MangoConfigTestApp {

	public static void main(String[] args) throws SQLException, InterruptedException {
		System.out.println(MangoConfigs.singleton().get().config().getConfig("good"));
		System.out.println(MangoConfigs.singleton().get().config().getString("good.bad.toto"));
	}
	


}
