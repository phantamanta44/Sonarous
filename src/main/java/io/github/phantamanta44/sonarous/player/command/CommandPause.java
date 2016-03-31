package io.github.phantamanta44.sonarous.player.command;

import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.core.command.ICommand;
import io.github.phantamanta44.sonarous.core.context.IEventContext;
import io.github.phantamanta44.sonarous.player.Sonarous;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collections;
import java.util.List;

public class CommandPause implements ICommand {

	@Override
	public String getName() {
		return "pause";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Pauses the currently playing music.";
	}

	@Override
	public String getUsage() {
		return "pause";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		try {
			if (!Sonarous.isPaused()) {
				Sonarous.pause();
				ctx.sendMessage("Paused music.");
			}
			else
				ctx.sendMessage("Music was already paused!");
		} catch (IllegalStateException e) {
			ctx.sendMessage("Not in any voice channels!");
		}
	}

	@Override
	public boolean canUseCommand(IUser sender, IEventContext ctx) {
		return BotMain.isAdmin(sender);
	}

	@Override
	public String getPermissionMessage(IUser sender, IEventContext ctx) {
		return "No permission!";
	}

	@Override
	public String getEnglishInvocation() {
		return null;
	}

}
