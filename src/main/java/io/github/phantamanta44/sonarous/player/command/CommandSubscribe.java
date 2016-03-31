package io.github.phantamanta44.sonarous.player.command;

import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.core.command.ICommand;
import io.github.phantamanta44.sonarous.core.context.IEventContext;
import io.github.phantamanta44.sonarous.player.SubscriptionHandler;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collections;
import java.util.List;

public class CommandSubscribe implements ICommand {

	@Override
	public String getName() {
		return "subscribe";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Subscribes the text channel to now-playing alerts.";
	}

	@Override
	public String getUsage() {
		return "subscribe";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (SubscriptionHandler.subscribe(ctx.getChannel()))
			ctx.sendMessage("Successfully subscribed channel.");
		else
			ctx.sendMessage("Channel was already subscribed!");
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
