package electron.functions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Overlay {
	private static JFrame frame;
	public static boolean isCreated = false;

	public static String toggle() {
		if (Overlay.isCreated == false) {
			create();
			return ("[OVERLAY]: created.");
		} else {
			delete();
			return ("[OVERLAY]: closed.");
		}
	}

	private static void create() {
		isCreated = true;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		// Создаем окно на весь экран
		frame = new JFrame(gd.getDefaultConfiguration());
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setBackground(Color.BLACK);
		// Устанавливаем окно на весь экран
		gd.setFullScreenWindow(frame);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int) screenSize.getWidth();
		int screenHeight = (int) screenSize.getHeight();
		// Проверяем, поддерживает ли экран режим отображения с определенным разрешением
		// и глубиной цвета
		DisplayMode dm = new DisplayMode(screenWidth, screenHeight, 32, DisplayMode.REFRESH_RATE_UNKNOWN);
		if (gd.isDisplayChangeSupported()) {
			gd.setDisplayMode(dm);
		}
		// Задаем размер окна на весь экран
		Dimension screenSize1 = frame.getSize();
		frame.setSize(screenSize1);
		// Отображаем окно
		frame.setVisible(true);
	}

	private static void delete() {
		isCreated = false;
		frame.dispose();
	}
}
