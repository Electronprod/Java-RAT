package electron.networking.packets;

import org.json.simple.JSONObject;

import electron.console.logger;

public class ProcessPacket {
	private String pid = "-";
	private String name = "-";
	private String user = "-";
	private String state = "-";
	private String memory = "-";
	private String cpu_time = "-";
	private String session = "-";
	private String title = "-";
	private String last = "no";
	private String time = "0";

	/**
	 * @param pid
	 * @param name
	 * @param user
	 * @param state
	 * @param memory
	 * @param cpu_time
	 * @param session
	 * @param title
	 */
	public ProcessPacket(String pid, String name, String user, String state, String memory, String cpu_time,
			String session, String title) {
		this.pid = pid;
		this.name = name;
		this.user = user;
		this.state = state;
		this.memory = memory;
		this.cpu_time = cpu_time;
		this.session = session;
		this.title = title;
	}

	/**
	 * Last packet to end sending
	 */
	public ProcessPacket() {
		this.last = "yes";
		this.time = logger.getTime();
	}

	@SuppressWarnings("unchecked")
	public JSONObject generate() {
		JSONObject p = new JSONObject();
		p.put("pid", pid);
		p.put("name", name);
		p.put("user", user);
		p.put("state", state);
		p.put("memory", memory);
		p.put("cpu_time", cpu_time);
		p.put("session", session);
		p.put("title", title);
		p.put("packettype", "5");
		p.put("last", last);
		p.put("time", time);
		return p;
	}
}
