package electron.networking;

import electron.console.logger;
import electron.networking.packets.ErrorPacket;
import electron.networking.packets.ProcessPacket;
import electron.tools.cmd;

public class ProcessSender extends Thread {
	// Only one TASKLIST.exe can be running
	private static boolean isBusy = false;
	private static boolean mode;
	private static int ErrCounter = 0;

	/**
	 * If true - mode enabled
	 * 
	 * @param mode
	 */
	public ProcessSender(boolean mode) {
		this.mode = mode;
	}

	public void run() {
		if (isBusy) {
			return;
		}
		isBusy = true;
		// Sending start point
		ProcessPacket packet = new ProcessPacket();
		Connection.send(packet.generate().toJSONString());
		if (cmd.isWindows) {
			// If Microsoft Windows
			String input;
			// Fastmode disabled
			if (!mode) {
				input = cmd.executeWhileReceiving("TASKLIST.exe /FO CSV /v /NH");
				String[] inp = input.split("\n");
				for (String proc : inp) {
					proc.replaceAll("\"", "");
					String[] procinf = proc.split(",");
					generateAndSendWindows(procinf);
				}
			} else {
				// Fastmode enabled
				input = cmd.executeWhileReceiving("TASKLIST.exe /FO CSV /NH");
				String[] inp = input.split("\n");
				for (String proc : inp) {
					proc.replaceAll("\"", "");
					String[] procinf = proc.split(",");
					generateAndSendWindowsFast(procinf);
				}
			}
			Connection.send(packet.generate().toJSONString());
			logger.log("[networking.ProcessSender]: sent TaskManager. (" + logger.getTime() + ")");
		} else {
			// If other systems (Linux)
			String input = cmd.executeWhileProcAlive(
					"ps -e -o pid,user,%cpu,%mem,cmd,state,command --no-headers | awk '{print $1\"(splitter)\",\"(splitter)\"$2,\"(splitter)\"$3,\"(splitter)\"$4,\"(splitter)\"$5,\"(splitter)\"$6,\"(splitter)\"$7 \"(end)\"}'");
			try {
				String[] inp = input.split("(end)");
				for (String proc : inp) {
					String[] procinf = proc.split("(splitter)");
					generateAndSendLinux(procinf);
				}
			} catch (java.lang.ArrayIndexOutOfBoundsException | java.lang.NullPointerException e) {
				isBusy = false;
				logger.error("[networking.ProcessSender]: Exception: " + e.getMessage());
				if (ErrCounter <= 3) {
					ErrCounter++;
					Connection.sendMessage("[networking.ProcessSender]: error №" + ErrCounter);
					ErrorPacket p = new ErrorPacket("[networking.ProcessSender]: error №" + ErrCounter);
					Connection.send(p.get());
				} else {
					return;
				}
			}
			Connection.send(packet.generate().toJSONString());
			logger.log("[networking.ProcessSender]: sent Top. (" + logger.getTime() + ")");
		}
		isBusy = false;
	}

	private static void generateAndSendLinux(String[] procinf) {
		String pid = procinf[0];
		String user = procinf[1];
		String cpu_time = procinf[2];
		String memory = procinf[3];
		String session = procinf[4];
		String state = procinf[5];
		String name = procinf[6];
		ProcessPacket packet = new ProcessPacket(pid, name, user, state, memory, cpu_time, session,
				"Unsupported on Linux");
		Connection.send(packet.generate().toJSONString());
	}

	private static void generateAndSendWindows(String[] procinf) {
		String name = procinf[0];
		String pid = procinf[1];
		String session = procinf[2];
		// номер сеанса
		String memory = procinf[4];
		String state = procinf[5];
		String user = procinf[6];
		String cpu_time = procinf[7];
		String title = procinf[8];
		ProcessPacket packet = new ProcessPacket(pid, name, user, state, memory, cpu_time, session, title);
		Connection.send(packet.generate().toJSONString());
	}

	private static void generateAndSendWindowsFast(String[] procinf) {
		String name = procinf[0];
		String pid = procinf[1];
		String session = procinf[2];
		// номер сеанса
		String memory = procinf[4];
		ProcessPacket packet = new ProcessPacket(pid, name, "-", "-", memory, "-", session, "-");
		Connection.send(packet.generate().toJSONString());
	}
}
