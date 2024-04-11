package electron.console;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import electron.networking.Connection;

public class logger {
	private static Date date = new Date();
	private static List<String> log = new ArrayList<String>();

	public static void log(Object msg) {
		System.out.println(getTime() + " INFO: " + msg);
		saveToLog(getTime() + " INFO: " + msg);
	}

	public static void warn(Object msg) {
		System.err.println(getTime() + " WARN: " + msg);
		saveToLog(getTime() + " WARN: " + msg);
	}

	public static void error(Object msg) {
		System.err.println(getTime() + " ERROR: " + msg);
		saveToLog(getTime() + " ERROR: " + msg);
	}

	@SuppressWarnings("deprecation")
	public static String getTime() {
		date.setTime(System.currentTimeMillis());
		return date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
	}

	private static void saveToLog(String message) {
		log.add(message);
		if (log.size() > 15) {
			log.remove(0);
		}
	}

	public static void sendLog() {
		Connection.sendMessage("[CLIENT_LOG]: ------------------------------");
		for (String message : log) {
			Connection.sendMessage("[CLIENT_LOG]: " + message);
		}
		Connection.sendMessage("[CLIENT_LOG]: ------------------------------");
	}
}
