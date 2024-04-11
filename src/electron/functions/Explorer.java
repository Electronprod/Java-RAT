package electron.functions;

import java.io.File;
import org.json.simple.JSONArray;

public class Explorer {
	private static String path = System.getProperty("user.home");

	public static String getPath() {
		return path;
	}

	public static boolean execCommand(String command) {
		if (command == "unset") {
			return false;
		}
		return false;
	}

	public static void setPath(String path1) {
		if (path1 == "unset") {
			return;
		}
		path = path1;
	}

	public static JSONArray getFiles() {
		JSONArray files = new JSONArray();
		File folder = new File(path);
		File[] filesArr = folder.listFiles();
		if (filesArr == null) {
			return files;
		}
		for (File file : filesArr) {
			String result = file.getName();
			files.add(result);
		}
		return files;
	}
}
