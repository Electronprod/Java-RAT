package electron.networking;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;

import electron.console.logger;
import electron.networking.packets.ErrorPacket;

public class FileReceiver extends Thread {
	private String ip;
	private int port;
	private String path;

	public FileReceiver(String ip, int port, String path) {
		this.ip = ip;
		this.port = port;
		this.path = path;
	}

	public void run() {
		try {
			Socket socket = new Socket(ip, port);
			Connection.sendMessage("[FTP]: uploading file to: " + path);
			InputStream is = socket.getInputStream();
			FileOutputStream fos = new FileOutputStream(path);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			byte[] byteArray = new byte[1024];
			int bytesRead;
			while ((bytesRead = is.read(byteArray)) != -1) {
				bos.write(byteArray, 0, bytesRead);
				Connection.sendMessage("[FTP]: writing: " + byteArray.length + "/" + bytesRead);
			}
			bos.close();
			is.close();
			socket.close();
			Connection.sendMessage("[FTP]: file uploaded: " + path);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("[electron.networking.FileReceiver]: error connecting to " + ip + ":" + port + ": "
					+ e.getMessage());
			ErrorPacket.sendError(
					"[electron.networking.FTP]: error connecting to " + ip + ":" + port + ": " + e.getMessage());
		}
	}
}
