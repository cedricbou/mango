package com.emo.mango.config.internal.paths;

public class WhichOS {

	public static enum OS {
		win, mac, unix, solaris, unknown;
	}
	
	private static String osName = System.getProperty("os.name").toLowerCase();
	
	public static final OS os = os();

	private static OS os() {
		if(isWindows()) {
			return OS.win;
		}
		else if(isMac()) {
			return OS.mac;
		}
		else if(isSolaris()) {
			return OS.solaris;
		}
		else if(isUnix()) {
			return OS.unix;
		}
		else {
			return OS.unknown;
		}
	}
	
	public static boolean isWindows() {
		return osName.contains("win");
	}

	public static boolean isMac() {
		return osName.contains("mac");
	}

	public static boolean isUnix() {
		return osName.contains("unix") || osName.contains("linux")
				|| osName.contains("aix");
	}

	public static boolean isSolaris() {
		return osName.contains("sunos");
	}
}
