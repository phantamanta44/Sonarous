package io.github.phantamanta44.sonarous.player.command;

import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.core.command.ICommand;
import io.github.phantamanta44.sonarous.core.context.IEventContext;
import io.github.phantamanta44.sonarous.player.Sonarous;
import io.github.phantamanta44.sonarous.player.queue.Song;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collections;
import java.util.List;

public class CommandUnqueue implements ICommand {

	@Override
	public String getName() {
		return "unqueue";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Removes a song from the queue.";
	}

	@Override
	public String getUsage() {
		return "unqueue <#index>";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 1) {
			ctx.sendMessage("You must specify a queue index to drop!");
			return;
		}
		try {
			Song song = Sonarous.drop(Integer.parseInt(args[0]) - 1);
			ctx.sendMessage("Removed **%s** from the queue.", song);
		} catch (NumberFormatException e) {
			ctx.sendMessage("Index must be a valid integer!");
		} catch (IndexOutOfBoundsException e) {
			ctx.sendMessage("No such queue item!");
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
