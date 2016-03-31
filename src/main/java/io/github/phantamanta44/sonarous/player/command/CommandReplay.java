package io.github.phantamanta44.sonarous.player.command;

import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.core.command.ICommand;
import io.github.phantamanta44.sonarous.core.context.IEventContext;
import io.github.phantamanta44.sonarous.player.Sonarous;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collections;
import java.util.List;

public class CommandReplay implements ICommand {

	@Override
	public String getName() {
		return "replay";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Starts the current song over.";
	}

	@Override
	public String getUsage() {
		return "replay";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		try {
			if (Sonarous.isPlaying()) {
				Sonarous.stop();
				Sonarous.next();
				ctx.sendMessage("Replaying **%s!**", Sonarous.getCurrentSong());
			}
			else
				ctx.sendMessage("Nothing is playing right now!");
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
