package com.emo.mango.log.params;

public class ParamDumpFactory {

	private static final ParamDumper simple = new SimpleParamDumper();
	private static final ParamDumper detailed = new DetailedParamDumper();
	private static final ParamDumper json = new JsonParamDumper();
	
	public static ParamDumper dumper(final ParamDumpType type) {
		switch(type) {
		case Simple:
			return simple;
		case Detailed:
			return detailed;
		case Json:
			return json;
		default:
			return simple;
		}
	}
}
