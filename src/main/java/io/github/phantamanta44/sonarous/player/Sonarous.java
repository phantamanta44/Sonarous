package io.github.phantamanta44.sonarous.player;

import io.github.phantamanta44.sonarous.core.EventDispatcher;
import io.github.phantamanta44.sonarous.core.ICTListener;
import io.github.phantamanta44.sonarous.core.command.CommandDispatcher;
import io.github.phantamanta44.sonarous.player.command.*;
import io.github.phantamanta44.sonarous.player.queue.Song;
import sx.blah.discord.handle.AudioChannel;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class Sonarous implements ICTListener {
	
	private static final List<Song> queue = new CopyOnWriteArrayList<>();
	
	private static IVoiceChannel channel;
	private static Song currentSong;
	
	public static void registerListeners() {
		CommandDispatcher.registerCommand(new CommandJoin());
		CommandDispatcher.registerCommand(new CommandLeave());
		CommandDispatcher.registerCommand(new CommandPause());
		CommandDispatcher.registerCommand(new CommandPlaying());
		CommandDispatcher.registerCommand(new CommandPlaylist());
		CommandDispatcher.registerCommand(new CommandQueue());
		CommandDispatcher.registerCommand(new CommandReplay());
		CommandDispatcher.registerCommand(new CommandSkip());
		CommandDispatcher.registerCommand(new CommandSubscribe());
		CommandDispatcher.registerCommand(new CommandUnpause());
		CommandDispatcher.registerCommand(new CommandUnqueue());
		CommandDispatcher.registerCommand(new CommandUnsubscribe());
		EventDispatcher.registerHandler(new SongEndListener());
		SubscriptionHandler.load();
	}

	public static AudioChannel getAudioChannel() {
		if (channel == null)
			return null;
		try {
			return initAudioChannel(channel.getAudioChannel());
		} catch (DiscordException e) {
			throw new RuntimeException(e);
		}
	}

	private static AudioChannel initAudioChannel(AudioChannel ac) {
		ac.setVolume(0.8F);
		return ac;
	}

	public static void join(IVoiceChannel target) {
		try {
			leave();
		} catch (IllegalStateException e) { }
		target.join();
		channel = target;
	}

	public static void leave() {
		AudioChannel ac;
		if ((ac = getAudioChannel()) == null)
			throw new IllegalStateException("Channel is not set!");
		ac.clearQueue();
		queue.clear();
		channel = null;
	}
	
	public static void enqueue(Song song) {
		if (channel == null)
			throw new IllegalStateException("Channel is not set!");
		queue.add(song);
	}

	public static Song drop(int index) {
		if (channel == null)
			throw new IllegalStateException("Channel is not set!");
		return queue.remove(index);
	}
	
	public static void next() {
		if (!hasNext())
			throw new IndexOutOfBoundsException("Queue is empty!");
		AudioChannel ac;
		if ((ac = getAudioChannel()) == null)
			throw new IllegalStateException("Channel is not set!");
		currentSong = queue.remove(0);
		ac.clearQueue();
		ac.queue(currentSong.getAudioStream());
	}

	public static boolean hasNext() {
		return !queue.isEmpty();
	}

	public static void pause() {
		AudioChannel ac;
		if ((ac = getAudioChannel()) == null)
			throw new IllegalStateException("Channel is not set!");
		ac.pause();
	}

	public static void resume() {
		AudioChannel ac;
		if ((ac = getAudioChannel()) == null)
			throw new IllegalStateException("Channel is not set!");
		ac.resume();
	}

	public static boolean isPaused() {
		AudioChannel ac;
		if ((ac = getAudioChannel()) == null)
			throw new IllegalStateException("Channel is not set!");
		return ac.isPaused();
	}

	public static void stop() {
		AudioChannel ac;
		if ((ac = getAudioChannel()) == null)
			throw new IllegalStateException("Channel is not set!");
		ac.clearQueue();
		queue.add(0, currentSong);
		currentSong = null;
	}

	public static boolean isPlaying() {
		return currentSong != null;
	}
	
	public static Stream<Song> streamQueue() {
		return queue.stream();
	}

	public static Song getCurrentSong() {
		return currentSong;
	}

	public static String nowPlaying() {
		if (!isPlaying())
			return "There is no song playing at the moment.";
		return String.format("__**Now Playing: %s \u2013 %s**__\nURL: %s\nCover Art: %s\n*Playing via %s*",
				currentSong.getArtist(), currentSong.getName(), currentSong.getUrl(),
				currentSong.getCoverArt(), currentSong.getSourceName());
	}

}
