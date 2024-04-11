package electron.networking.packets;

import org.json.simple.JSONObject;

import electron.networking.Connection;

public class ErrorPacket {
	private JSONObject main = new JSONObject();

	public ErrorPacket(String message) {
		main.put("message", message);
		main.put("packettype", "3");
	}

	public String get() {
		return main.toJSONString();
	}
	public static void sendError(String msg) {
		Connection.send(new ErrorPacket(msg).get());
	}
}
