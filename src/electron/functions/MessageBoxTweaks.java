package electron.functions;

import java.util.ArrayList;
import java.util.List;

import electron.networking.CommandExecutor;
import electron.networking.Connection;

public class MessageBoxTweaks {
	private static List<Thread> boxes = new ArrayList<Thread>();

	public static void callMessageBox(String text, String title, String type, String msgtype) {
		if (CommandExecutor.isNumber(msgtype) && CommandExecutor.isNumber(type)) {
			Thread boxthread = new MessageBoxThread(text, Integer.parseInt(type), Integer.parseInt(msgtype), title,
					boxes);
			boxthread.start();
		} else {
			electron.console.logger.error("Incorrect numbers for message received!");
			Connection.sendMessage("[MESSAGES]: error: Incorrect numbers for message received!");
		}
	}

	public static String close() {
		boxes = checkAliveThreads(boxes);
		boxes.get(boxes.size() - 1).stop();
		boxes.remove(boxes.size() - 1);
		return "[MESSAGES]: Done.";
	}

	public static String stopAll() {
		boxes = checkAliveThreads(boxes);
		for (int i = 0; i < boxes.size(); i++) {
			boxes.get(i).stop();
		}
		boxes.clear();
		return "[MESSAGES]: closed all messageboxes.";
	}

	public static String show() {
		boxes = checkAliveThreads(boxes);
		String message = "[MESSAGES]: list of threads: \n";
		for (int i = 0; i < boxes.size(); i++) {
			message = message + (boxes.get(i).toString() + "\n");
		}
		message = message + ("[MESSAGES]: Done.");
		return message;
	}

	private static List<Thread> checkAliveThreads(List<Thread> threads) {
		List<Thread> alivethreads = new ArrayList<Thread>();
		for (int i = 0; i < threads.size(); i++) {
			if (threads.get(i).isAlive()) {
				alivethreads.add(threads.get(i));
			}
		}
		return alivethreads;
	}
}
