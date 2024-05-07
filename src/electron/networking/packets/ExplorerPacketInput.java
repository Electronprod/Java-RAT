package electron.networking.packets;

import org.json.simple.JSONObject;

public class ExplorerPacketInput {
	private JSONObject main = new JSONObject();

	public ExplorerPacketInput(JSONObject main) {
		this.main = main;
	}

	public String getCommand() {
		return String.valueOf(main.get("command"));
	}
	public String getPath() {
		return String.valueOf(main.get("path"));
	}

}
