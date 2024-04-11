package electron.tools;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.LockSupport;

import electron.console.logger;
import electron.networking.Connection;
import electron.networking.packets.OutputMessagePacket;

public class cmd {
	public static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

	public static boolean execute(String data, boolean directCommand) {
		try {
			Process proc;
			if (!directCommand) {
				if (isWindows) {
					proc = Runtime.getRuntime().exec("cmd  /c " + data);
				} else {
					proc = Runtime.getRuntime().exec("sh -c " + data);
				}
			} else {
				proc = Runtime.getRuntime().exec(data);
			}
			while (proc.isAlive()) {
				loadoutput2(proc);
				loadoutput2Error(proc);
				LockSupport.parkNanos(100);
			}
			proc.destroy();
			return true;
		} catch (IOException e) {
			logger.error("[cmd.execute]: I/O: " + e.getMessage());
			logger.error("[cmd.execute]: command was: " + data);
			logger.error("[cmd.execute]: directCommand =" + directCommand);
			return false;
		}
	}

	/**
	 * Execute command in system
	 * 
	 * @param data          - command to execute
	 * @param path          - path where command would be executed
	 * @param directCommand - if it equal true command executes not in Shell
	 * @param loadmode      - if it equal true answer from process will send
	 *                      immediately
	 * @return answer from process if loadmode equal loadmode. In other it returns
	 *         null.
	 */
	public static String executeV2(String data, String path, boolean directCommand, boolean loadmode) {
		try {
			ProcessBuilder builder = new ProcessBuilder();
			// is it direct command?
			if (!directCommand) {
				// It's not direct command
				// Checking operation system
				if (isWindows) {
					// Microsoft Windows
					builder.command("cmd.exe", "/c", data);
				} else {
					// Linux
					builder.command("sh", "-c", data);
				}
			} else {
				// It's direct command
				builder.command(data);
			}
			if (path == null || path == "") {
				// Using default directory
				builder.directory(new File(System.getProperty("user.home")));
			} else {
				builder.directory(new File(path));
			}
			// Starting process
			Process proc = builder.start();
			while (proc.isAlive()) {
				// Send data immediately
				if (loadmode) {
					loadoutput2(proc);
					loadoutput2Error(proc);
				}
				LockSupport.parkNanos(100);
			}
			proc.destroy();
			if (!loadmode) {
				return loadoutput(proc);
			} else {
				return null;
			}
		} catch (IOException e) {
			logger.error("[cmd.executeV2]: I/O: " + e.getMessage());
			logger.error("[cmd.executeV2]: command was: " + data);
			logger.error("[cmd.executeV2]: directCommand =" + directCommand);
			return null;
		}
	}

	public static boolean run(String data) {
		try {
			Runtime.getRuntime().exec(data);
			return true;
		} catch (IOException e) {
			logger.error("[cmd.run]: " + e.getMessage());
			logger.error("[cmd.run]: command was: " + data);
			return false;
		}
	}

	public static String executeWhileReceiving(String command) {
		try {
			Process proc = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = "";
			String inf = "";
			while ((line = reader.readLine()) != null) {
				inf = inf + line + "\n";
			}
			return inf;
		} catch (IOException e) {
			logger.error("[cmd.executeWhileReceiving]: I/O: " + e.getMessage());
			return null;
		}
	}

	public static String executeWhileProcAlive(String command) {
		try {
			Process proc;
			if (isWindows) {
				proc = Runtime.getRuntime().exec(command);
			} else {
				proc = Runtime.getRuntime().exec(command);
			}
			while (proc.isAlive()) {
				LockSupport.parkNanos(100);
			}
			return loadoutput(proc);
		} catch (IOException e) {
			logger.error("[cmd.executeWhileProcAlive]: I/O: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Load output from process when it already closed
	 * 
	 * @param proc - Process to handle
	 * @return String output
	 * @throws IOException
	 */
	public static String loadoutput(Process proc) throws IOException {
		String inf = "";
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8));
		String line = "";
		while ((line = reader.readLine()) != null) {
			inf = String.valueOf(inf) + line + "\n";
		}
		if (inf != "")
			return inf;
		return null;
	}

	/**
	 * Load output from process and send immediately
	 * 
	 * @param proc   - Process to handle
	 * @param packet - packet to use for sending
	 * @throws IOException
	 */
	private static void loadoutput2(Process proc) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		String line = "";
		while ((line = reader.readLine()) != null) {
			// Sending message
			OutputMessagePacket packet = new OutputMessagePacket("[CMD]: " + line + "\n");
			Connection.send(packet.get().toJSONString());
		}
	}

	private static void loadoutput2Error(Process proc) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
		String line = "";
		while ((line = reader.readLine()) != null) {
			// Sending message
			OutputMessagePacket packet = new OutputMessagePacket("[CMD][ERROR_STREAM]: " + line + "\n");
			Connection.send(packet.get().toJSONString());
		}
	}
}
