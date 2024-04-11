package electron.networking.packets;

import org.json.simple.JSONObject;

import electron.tools.FileOptions;

public class EditPacket {
	private JSONObject main = new JSONObject();
	private String path;

	public EditPacket(String path) {
		this.path = path;
	}

	public JSONObject get() {
		main.put("packettype", "6");
		main.put("path", path);
		main.put("content", getFileContent());
		return main;
	}

	private String getFileContent() {
		return FileOptions.getFileLineWithSeparator(FileOptions.getFileLines(path), "\n");
	}
}
