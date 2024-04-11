package electron.networking;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.locks.LockSupport;

import javax.imageio.ImageIO;

import electron.console.logger;
import electron.networking.packets.ErrorPacket;

public class ScreenV2Sender extends Thread {
	private Robot robot;
	private static boolean isEnabled = false;
	private int port;
	private String ip;

	public static boolean isEnabled() {
		return isEnabled;
	}

	public static void setEnabled(boolean state) {
		isEnabled = state;
	}

	public ScreenV2Sender(Robot r, String address) {
		robot = r;
		String[] address1 = address.split(":");
		this.ip = address1[0];
		this.port = Integer.parseInt(address1[1]) + 2;
	}

	public void run() {
		while (true) {
			if (!isEnabled) {
				LockSupport.parkNanos(10000);
				continue;
			}
			Rectangle rech = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage image = robot.createScreenCapture(rech);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ImageIO.write(image, "jpeg", baos);
				baos.flush();
				byte[] imageBytes = baos.toByteArray();
				Socket socket = new Socket(ip, port);
				OutputStream os = socket.getOutputStream();
				os.write(imageBytes);
				os.flush();
				os.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("[ScreenV2]: error: " + e.getMessage());
				ErrorPacket.sendError("[ScreenV2]: error: " + e.getMessage());
			}
		}
	}
}
