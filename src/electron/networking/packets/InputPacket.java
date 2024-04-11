package electron.networking.packets;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import electron.console.logger;
import electron.tools.FileOptions;

public class InputPacket {
	private String command;

	public InputPacket(String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}

	public static InputPacket parse(String data) {
		try {
			JSONObject input = (JSONObject) FileOptions.ParseJsThrought(data);
			String command = String.valueOf(input.get("command"));
			return new InputPacket(command);
		} catch (ParseException e) {
			logger.error("[networking.InputPacket.parse]: incorrect JSON format.");
			return null;
		}
	}

}
