package electron.functions.player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicManager {
	private static List<Thread> threadsallsessions = new ArrayList<Thread>();
	private static List<Thread> threads = new ArrayList<Thread>();

	/**
	 * Play sound file in .wav format
	 * 
	 * @param fname - file to play
	 * @return String message to send to Server
	 */
	public static String play(String fname) {
		Thread player = new MusicPlayer(new File(fname));
		player.setName(fname);
		threads.add(player);
		threadsallsessions.add(player);
		player.start();
		return "[PLAYER]: playing " + fname;
	}

	/**
	 * Stop playing sound file by name
	 * 
	 * @param name - name of file to stop
	 * @return String message to send to Server
	 */
	public static String stopName(String name) {
		threads = checkAliveThreads(threads);
		for (int i = 0; i < threads.size(); i++) {
			if (threads.get(i).getName().equalsIgnoreCase(name)) {
				threads.get(i).interrupt();
				threads.remove(i);
				return "[PLAYER]: Done.";
			}
		}
		return "[PLAYER]: Music not found.";
	}

	/**
	 * Stop last started sound file
	 * 
	 * @return String message to send to Server
	 */
	public static String stopLast() {
		threads = checkAliveThreads(threads);
		threads.get(threads.size() - 1).interrupt();
		threads.remove(threads.size() - 1);
		return "[PLAYER]: Done.";
	}

	/**
	 * Stop all playing sound files
	 * 
	 * @return String message to send to Server
	 */
	public static String stopAll() {
		threadsallsessions = checkAliveThreads(threadsallsessions);
		for (int i = 0; i < threadsallsessions.size(); i++) {
			threadsallsessions.get(i).interrupt();
		}
		threadsallsessions.clear();
		return "[PLAYER]: stopped all sounds played.";
	}

	/**
	 * Show playing sound files list
	 * 
	 * @return String message to send to Server
	 */
	public static String showPlayers() {
		threads = checkAliveThreads(threads);
		String result;
		result = ("[PLAYER]: list of players:\n");
		for (int i = 0; i < threads.size(); i++) {
			result = result + (threads.get(i).getName()) + "\n";
		}
		result = result + ("[PLAYER]: Done.");
		return result;
	}

	/**
	 * Removes dead threads from List<Thread>
	 * 
	 * @param threads - List<Thread> to check
	 * @return List<Thread> checked
	 */
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
