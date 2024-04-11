package electron.networking;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

import electron.console.logger;
import electron.networking.packets.ErrorPacket;

public class FileSender extends Thread {
	private File file;
	private String ip;
	private int port;

	public FileSender(File file, String ip, int port) {
		this.file = file;
		this.ip = ip;
		this.port = port;
	}

	public void run() {
		try {
			Socket socket = new Socket(ip, port);
			byte[] byteArray = new byte[(int) file.length()];

			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			bis.read(byteArray, 0, byteArray.length);

			OutputStream os = socket.getOutputStream();
			os.write(byteArray, 0, byteArray.length);
			os.flush();

			socket.close();
			bis.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("[electron.networking.FileSender]: error" + e.getMessage());
			ErrorPacket.sendError("[electron.networking.FileSender]: error" + e.getMessage());
		}
	}
}