package io.github.phantamanta44.sonarous.player.command;

import io.github.phantamanta44.sonarous.core.command.ICommand;
import io.github.phantamanta44.sonarous.core.context.IEventContext;
import io.github.phantamanta44.sonarous.player.Sonarous;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collections;
import java.util.List;

public class CommandPlaying implements ICommand {

	private static final List<String> ALIASES = Collections.singletonList("nowplaying");

	@Override
	public String getName() {
		return "playing";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Displays information about the currently playing song.";
	}

	@Override
	public String getUsage() {
		return "playing";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		try {
			ctx.sendMessage(Sonarous.nowPlaying());
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
