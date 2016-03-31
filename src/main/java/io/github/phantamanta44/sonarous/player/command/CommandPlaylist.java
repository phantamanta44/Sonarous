package io.github.phantamanta44.sonarous.player.command;

import io.github.phantamanta44.sonarous.core.command.ICommand;
import io.github.phantamanta44.sonarous.core.context.IEventContext;
import io.github.phantamanta44.sonarous.player.Sonarous;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandPlaylist implements ICommand {

	private static final List<String> ALIASES = Collections.singletonList("songqueue");

	@Override
	public String getName() {
		return "playlist";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Displays the song queue.";
	}

	@Override
	public String getUsage() {
		return "playlist";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		try {
			AtomicInteger ind = new AtomicInteger(1);
			String playlist = Sonarous.streamQueue().map(s -> String.format("%s | %s", ind.getAndIncrement(), s)).reduce((a, b) -> a.concat("\n").concat(b)).orElse("Nothing else is queued.");
			ctx.sendMessage("__**Playlist:**__\nNow Playing: **%s**\nPlaylist: %s", Sonarous.getCurrentSong(), playlist);
		} catch (IllegalStateException e) {
			ctx.sendMessage("Not in any voice channels!");
		}
	}

	@Override
	public boolean canUseCommand(IUser sender, IEventContext ctx) {
		return true;
	}

	@Override
	public String getPermissionMessage(IUser sender, IEventContext ctx) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getEnglishInvocation() {
		return null;
	}

}
