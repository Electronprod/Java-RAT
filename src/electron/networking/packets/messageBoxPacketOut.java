package electron.networking.packets;

import org.json.simple.JSONObject;

public class messageBoxPacketOut {
	private JSONObject main = new JSONObject();

	public messageBoxPacketOut(String title, String message) {
		main.put("title", title);
		main.put("message", message);
		main.put("packettype", "7");
	}

	public JSONObject get() {
		return main;
	}
}
