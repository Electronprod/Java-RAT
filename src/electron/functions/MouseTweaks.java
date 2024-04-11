package electron.functions;

import java.awt.Robot;
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
}

class MouseFixer extends Thread {
	public void run() {
		while (true) {
			MouseTweaks.setMouse(0, 0);
			LockSupport.parkNanos(10);
		}
	}
}
