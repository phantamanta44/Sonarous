package io.github.phantamanta44.sonarous.player;

import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.Discord;
import io.github.phantamanta44.sonarous.core.rate.RateLimitedChannel;
import io.github.phantamanta44.sonarous.util.MessageUtils;
import sx.blah.discord.handle.obj.IChannel;

import java.io.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SubscriptionHandler {

	private static final File dataFile = new File("sonarsubs.txt");
	private static final List<IChannel> subs = new CopyOnWriteArrayList<>();

	public static void load() {
		try (BufferedReader strIn = new BufferedReader(new FileReader(dataFile))) {
			subs.clear();
			String line;
			while ((line = strIn.readLine()) != null) {
				try {
					subs.add(new RateLimitedChannel(Discord.getInstance().getChannelById(line)));
				} catch (Exception e) {
					BotMain.logger.warn("Line read error: %s", line);
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			BotMain.logger.severe("Failed to load subscription list!");
			e.printStackTrace();
		}
	}

	public static void save() {
		try (PrintStream strOut = new PrintStream(new FileOutputStream(dataFile))) {
			subs.forEach(c -> strOut.print(c.getID()));
		} catch (Exception e) {
			BotMain.logger.severe("Failed to save subscription list!");
			e.printStackTrace();
		}
	}

	public static boolean subscribe(IChannel chan) {
		boolean success = subs.add(chan);
		save();
		return success;
	}

	public static boolean unsubscribe(IChannel chan) {
		boolean success = subs.remove(chan);
		save();
		return success;
	}

	public static void dispatch() {
		String msg = Sonarous.nowPlaying();
		subs.forEach(c -> MessageUtils.sendMessage(c, msg));
	}

}
