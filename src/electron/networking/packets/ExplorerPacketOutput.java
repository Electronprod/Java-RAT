package electron.networking.packets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ExplorerPacketOutput {
	private JSONObject main = new JSONObject();

	public ExplorerPacketOutput(String path, JSONArray files) {
		main.put("files", files);
		main.put("path", path);
		main.put("packettype", "2");
	}

	public String get() {
		return main.toJSONString();
	}

	public static JSONArray getFilesDir(String path) {
		// Getting files
		List<File> fls = new ArrayList();
		File folder = new File(path);
		File[] files = folder.listFiles();
		if (files==null) {
			JSONArray a = new JSONArray();
			a.add("Unknown path");
			return a;
		}
		for (File file : files) {
			fls.add(file);
		}
		// Adding all files to JSONArray
		JSONArray arr = new JSONArray();
		for (File f : fls) {
			arr.add(f.getName());
		}
		return arr;
	}
}
