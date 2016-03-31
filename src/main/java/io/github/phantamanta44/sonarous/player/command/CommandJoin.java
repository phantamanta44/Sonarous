package io.github.phantamanta44.sonarous.player.command;

import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.core.command.ICommand;
import io.github.phantamanta44.sonarous.core.context.IEventContext;
import io.github.phantamanta44.sonarous.player.Sonarous;
import io.github.phantamanta44.sonarous.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.Collections;
import java.util.List;

public class CommandJoin implements ICommand {

	@Override
	public String getName() {
		return "join";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Joins a voice channel.";
	}

	@Override
	public String getUsage() {
		return "join [name]";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		IVoiceChannel target;
		if (args.length < 1)
			target = sender.getVoiceChannel().orElse(null);
		else {
			if (ctx.getChannel().isPrivate()) {
				ctx.sendMessage("You can't do this in a private channel!");
				return;
			}
			String chanName = MessageUtils.concat(args);
			target = ctx.getGuild().getVoiceChannels().stream()
					.filter(c -> MessageUtils.lenientMatch(c.getName(), chanName))
					.findAny().orElse(null);
		}
		if (target == null) {
			ctx.sendMessage("Channel not found!");
			return;
		}
		Sonarous.join(target);
		ctx.sendMessage("Joined channel %s.", target.getName());
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
