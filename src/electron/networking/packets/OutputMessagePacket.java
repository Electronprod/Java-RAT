package electron.networking.packets;
import org.json.simple.JSONObject;

public class OutputMessagePacket {
	private String message;

	public OutputMessagePacket(String message) {
		this.message = message;
	}

	public JSONObject get() {
		JSONObject out = new JSONObject();
		out.put("packettype", "1");
		out.put("message", message);
		return out;
	}

}
