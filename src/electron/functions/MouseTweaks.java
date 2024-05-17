package electron.functions;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.Toolkit;
import java.util.concurrent.locks.LockSupport;

public class MouseTweaks {
	private static boolean mouseFixed = false;
	private static Thread mousefixer = new MouseFixer();
	private static Robot r;

	public static void setRobot(Robot ra) {
		r = ra;
	}

	public static void setMouse(int x, int y) {
		r.mouseMove(x, y);
	}

	public static void clickMouse(int btn) {
		r.mousePress(btn);
		r.mouseRelease(btn);
	}

	public static void setMouse(double x, double y, double maxX, double maxY) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;
		x = map(x, 0, maxX, 0, screenWidth);
		y = map(y, 0, maxY, 0, screenHeight);
		setMouse((int) Math.round(x), (int) Math.round(y));
	}

	/**
	 * Toggle mouse fixer state
	 * 
	 * @return message to send to Server
	 */
	public static String toggleMouseFixer() {
		mouseFixed = !mouseFixed;
		if (mouseFixed == true) {
			mousefixer = new MouseFixer();
			mousefixer.start();
			return "[MOUSE_FIXER]: fixer enabled.";
		} else {
			mousefixer.stop();
			return "[MOUSE_FIXER]: fixer disabled.";
		}
	}

	private static double map(double value, double fromLow, double fromHigh, double toLow, double toHigh) {
		return Math.round((value - fromLow) * (toHigh - toLow) / (fromHigh - fromLow) + toLow);
	}
}

class MouseFixer extends Thread {
	public void run() {
		while (true) {
			MouseTweaks.setMouse(0, 0);
			LockSupport.parkNanos(10);
		}
	}
}
