package electron.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Web {

	/**
	 * Get data from URI
	 * 
	 * @param url1 - URI
	 * @return String data
	 * @throws IOException - connection error
	 */
	public static String get(String url1) throws IOException {
		URL url = new URL(url1);
		URLConnection urlConnection = url.openConnection();
		HttpURLConnection connection = null;
		if (urlConnection instanceof HttpURLConnection) {
			connection = (HttpURLConnection) urlConnection;
		} else {
			return null;
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String urlString = "";
		String current;

		while ((current = in.readLine()) != null) {
			urlString += current;
		}
		return urlString;
	}

}
