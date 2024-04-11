package electron.functions;

import java.awt.Robot;
import java.awt.event.KeyEvent;

public class Keyboard {
	private static Robot r;

	public static void setRobot(Robot ra) {
		r = ra;
	}

	public static void press(int key) {
		if (key == 0) {
			return;
		}
		r.keyPress(key);
	}

	public static void release(int key) {
		if (key == 0) {
			return;
		}
		r.keyRelease(key);
	}

	/**
	 * Converts String keycode to Integer keycode
	 * 
	 * @param input - String to convert
	 * @return int keycode
	 */
	public static int getKeyCode(String input) {
		if (input.length() == 1) {
			// one symbol key code
			return KeyEvent.getExtendedKeyCodeForChar((int) input.charAt(0));
		} else {
			// Multiple key code
			input = input.toLowerCase();
			if (input.equals("win")) {
				return KeyEvent.VK_WINDOWS;
			} else if (input.equals("space")) {
				return KeyEvent.VK_SPACE;
			} else if (input.equals("backspace")) {
				return KeyEvent.VK_BACK_SPACE;
			} else if (input.equals("+")) {
				return KeyEvent.VK_PLUS;
			} else if (input.equals("-")) {
				return KeyEvent.VK_MINUS;
			} else if (input.equals("esc")) {
				return KeyEvent.VK_ESCAPE;
			} else if (input.equals("tab")) {
				return KeyEvent.VK_TAB;
			} else if (input.equals("alt")) {
				return KeyEvent.VK_ALT;
			} else if (input.equals("shift")) {
				return KeyEvent.VK_SHIFT;
			} else if (input.equals("pageup")) {
				return KeyEvent.VK_PAGE_UP;
			} else if (input.equals("pagedown")) {
				return KeyEvent.VK_PAGE_DOWN;
			} else if (input.equals("delete")) {
				return KeyEvent.VK_DELETE;
			} else if (input.equals("up")) {
				return KeyEvent.VK_UP;
			} else if (input.equals("down")) {
				return KeyEvent.VK_DOWN;
			} else if (input.equals("right")) {
				return KeyEvent.VK_RIGHT;
			} else if (input.equals("left")) {
				return KeyEvent.VK_LEFT;
			} else if (input.equals("enter")) {
				return KeyEvent.VK_ENTER;
			} else if (input.equals("f1")) {
				return KeyEvent.VK_F1;
			} else if (input.equals("f2")) {
				return KeyEvent.VK_F2;
			} else if (input.equals("f3")) {
				return KeyEvent.VK_F3;
			} else if (input.equals("f4")) {
				return KeyEvent.VK_F4;
			} else if (input.equals("f5")) {
				return KeyEvent.VK_F5;
			} else if (input.equals("f6")) {
				return KeyEvent.VK_F6;
			} else if (input.equals("f7")) {
				return KeyEvent.VK_F7;
			} else if (input.equals("f8")) {
				return KeyEvent.VK_F8;
			} else if (input.equals("f9")) {
				return KeyEvent.VK_F9;
			} else if (input.equals("f10")) {
				return KeyEvent.VK_F10;
			} else if (input.equals("f11")) {
				return KeyEvent.VK_F11;
			} else if (input.equals("f12")) {
				return KeyEvent.VK_F12;
			} else if (input.equals("capslock")) {
				return KeyEvent.VK_CAPS_LOCK;
			} else if (input.equals("ctrl")) {
				return KeyEvent.CTRL_DOWN_MASK;
			} else {
				return KeyEvent.VK_UNDEFINED;
			}
		}
	}
}
