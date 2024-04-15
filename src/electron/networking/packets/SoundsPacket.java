package electron.networking.packets;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SoundsPacket {
	private JSONObject main = new JSONObject();

	public SoundsPacket(List<Thread> sounds) {
		JSONArray arr = new JSONArray();
		if (!sounds.isEmpty()) {
			for (Thread sound : sounds) {
				arr.add(sound.getName());
			}
		} else {
			arr.add("-");
		}
		main.put("list", arr);
		main.put("packettype", "9");
	}

	public String get() {
		return main.toJSONString();
	}
}
