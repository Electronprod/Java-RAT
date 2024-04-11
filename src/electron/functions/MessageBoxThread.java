package electron.functions;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import electron.networking.Connection;

public class MessageBoxThread extends Thread {
	private String text;
	private int type;
	private int msgtype;
	private String title;
	private List<Thread> boxes;

	/**
	 * @param text    - text to show
	 * @param type    - type to show
	 * @param msgtype - type of messageBox
	 * @param title   - title of message
	 * @param s       - Socket to send answer
	 */
	public MessageBoxThread(String text, int type, int msgtype, String title, List<Thread> boxes) {
		Thread.currentThread().setPriority(MIN_PRIORITY);
		this.type = type;
		this.text = text;
		this.msgtype = msgtype;
		this.title = title;
		this.boxes = boxes;
		boxes.add(Thread.currentThread());
	}

	// Thread body
	public void run() {
		JFrame fr = new JFrame();
		fr.setAlwaysOnTop(true);
		if (msgtype == 0) {
			// Show message
			sendData("[MESSAGES]: showed message dialog.");
			JOptionPane.showMessageDialog(fr, text, title, type);
			sendData("[MESSAGES]: message dialog closed.");
		} else if (msgtype == 1) {
			// Show dialog with Yes/no/cancel buttons
			sendData("[MESSAGES]: showed message dialog.");
			sendData("[MESSAGES]: messagebox(YES/NO/CANCEL): selected: " + JOptionPane.showConfirmDialog(fr, text));
		} else if (msgtype == 2) {
			// Show input dialog
			sendData("[MESSAGES]: showed message dialog.");
			sendData("[MESSAGES]: messagebox(INPUT): " + JOptionPane.showInputDialog(fr, text, title, type));
		} else {
			sendData("[MESSAGES][ERROR]: incorrect type of message.");
		}
		boxes.remove(Thread.currentThread());
	}

	private void sendData(String msg) {
		Connection.sendMessage(msg);
	}
}
