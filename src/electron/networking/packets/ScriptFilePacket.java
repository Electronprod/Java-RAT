package electron.networking.packets;

import org.json.simple.JSONObject;

public class ScriptFilePacket {
	public static final int EXECUTOR_CMD = 1;
	public static final int EXECUTOR_POWERSHELL = 2;
	public static final int EXECUTOR_VBS = 3;
	public static final int EXECUTOR_POWERSHELL_CONSOLE = 4;
	public static final int EXECUTOR_BAT = 5;
	public static final int EXECUTOR_JS = 6;
	private String content;
	private int executor;

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @return the executor
	 */
	public int getExecutor() {
		return executor;
	}

	public ScriptFilePacket(JSONObject input) {
		this.content = String.valueOf(input.get("content"));
		this.executor = Integer.parseInt(String.valueOf(input.get("action")));
	}

}
