package electron.networking;

import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;

import org.json.simple.JSONObject;

import electron.console.logger;
import electron.networking.packets.ErrorPacket;

public class ScreenSender extends Thread {
	private static int timeout = 1;
	private static double imageQuality = 0.1F;

	public static void setTimeout(int timeout1) {
		timeout = timeout1;
	}

	public static int getTimeout() {
		return timeout;
	}

	public static void setImageQuality(double imageQuality1) {
		imageQuality = imageQuality1;
	}

	public static double getImageQuality() {
		return timeout;
	}

	public void run() {
		try {
			while (true) {
				if (!Connection.isConnected()) {
					return;
				}
				Thread.currentThread().sleep(timeout);
				// Sending data
				if (!sendScreen()) {
					logger.error("[electron.networking.ScreenSender]: error sending screen.");
					ErrorPacket.sendError("[electron.networking.ScreenSender]: error sending screen.");
					return;
				}
			}
		} catch (InterruptedException e) {
			logger.log("[electron.networking.ScreenSender]: interrupted thread.");
		}
	}

	private boolean sendScreen() {
		if (Connection.r == null) {
			return true;
		}
		if (CommandExecutor.isScreenEnabled() == false) {
			return true;
		}
		try {
			Robot robot = Connection.r;
			Rectangle rech = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage image = robot.createScreenCapture(rech);
			String img = imageToBase64String(reduceImageQuality(image), "jpeg");
			int chunkSize = 65535 - 10000; // adjust the chunk size as needed
			int length = img.length();
			// Sending image
			for (int i = 0; i < length; i += chunkSize) {
				int end = Math.min(length, i + chunkSize);
				String chunk = img.substring(i, end);
				JSONObject packet = new JSONObject();
				packet.put("packettype", "4");
				packet.put("counter", String.valueOf(i));
				packet.put("content", chunk);
				Connection.send(packet.toJSONString());
			}
			// Sending end packet
			JSONObject packet = new JSONObject();
			packet.put("packettype", "4");
			packet.put("counter", "0");
			packet.put("content", "0");
			Connection.send(packet.toJSONString());
			return true;
		} catch (HeadlessException e) {
			e.printStackTrace();
			logger.error("[networking.ScreenSender.sendScreen]: error sending screen: " + e.getMessage());
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("[networking.ScreenSender.sendScreen]: error sending screen: " + e.getMessage());
			return false;
		}
	}

	private static String imageToBase64String(BufferedImage image, String type) {
		String imageString = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			ImageIO.write(image, type, bos);
			byte[] imageBytes = bos.toByteArray();
			imageString = Base64.getEncoder().encodeToString(imageBytes);
			bos.close();
		} catch (IOException e) {
			logger.error("[networking.ScreenSender.imageToBase64String]: error encoding image: " + e.getMessage());
		}
		return imageString;
	}

	private static BufferedImage reduceImageQuality(BufferedImage image) throws IOException {
		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
		ImageWriter writer = writers.next();
		ImageWriteParam param = new JPEGImageWriteParam(null);
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(Float.parseFloat(imageQuality + ""));
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		writer.setOutput(ImageIO.createImageOutputStream(outputStream));
		writer.write(null, new IIOImage(image, null, null), param);
		writer.dispose();
		return ImageIO.read(new java.io.ByteArrayInputStream(outputStream.toByteArray()));
	}
//	private static BufferedImage downscaleImage(BufferedImage image, int targetWidth, int targetHeight) {
//		BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
//		Graphics2D graphics = scaledImage.createGraphics();
//
//		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//		graphics.drawImage(image, 0, 0, targetWidth, targetHeight, null);
//		graphics.dispose();
//
//		return scaledImage;
//	}
}
