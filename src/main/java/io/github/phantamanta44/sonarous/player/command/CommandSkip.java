package io.github.phantamanta44.sonarous.player.command;

import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.core.command.ICommand;
import io.github.phantamanta44.sonarous.core.context.IEventContext;
import io.github.phantamanta44.sonarous.player.Sonarous;
import sx.blah.discord.handle.obj.IUser;

import java.util.Arrays;
import java.util.List;

public class CommandSkip implements ICommand {

	private static final List<String> ALIASES = Arrays.asList("next", "ff");

	@Override
	public String getName() {
		return "skip";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Skips the currently playing song.";
	}

	@Override
	public String getUsage() {
		return "skip";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		try {
			if (Sonarous.hasNext())
				Sonarous.next();
			else
				Sonarous.stop();
			ctx.sendMessage("Skipped song.");
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
