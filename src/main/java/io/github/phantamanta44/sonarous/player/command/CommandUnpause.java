package io.github.phantamanta44.sonarous.player.command;

import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.core.command.ICommand;
import io.github.phantamanta44.sonarous.core.context.IEventContext;
import io.github.phantamanta44.sonarous.player.Sonarous;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collections;
import java.util.List;

public class CommandUnpause implements ICommand {

	private static final List<String> ALIASES = Collections.singletonList("resume");

	@Override
	public String getName() {
		return "unpause";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Unpauses the music if it's paused.";
	}

	@Override
	public String getUsage() {
		return "unpause";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		try {
			if (Sonarous.isPaused()) {
				Sonarous.resume();
				ctx.sendMessage("Unpaused music.");
			}
			else
				ctx.sendMessage("Music wasn't paused!");
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
