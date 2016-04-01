package io.github.phantamanta44.sonarous.player.command;

import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.core.command.ICommand;
import io.github.phantamanta44.sonarous.core.context.IEventContext;
import io.github.phantamanta44.sonarous.player.Sonarous;
import sx.blah.discord.handle.AudioChannel;
import sx.blah.discord.handle.obj.IUser;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class CommandTest implements ICommand {
	@Override
	public String getName() {
		return "test";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Play the test audio file.";
	}

	@Override
	public String getUsage() {
		return "test";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		AudioChannel ac = Sonarous.getAudioChannel();
		if (ac == null) {
			ctx.sendMessage("No audio channel!");
			return;
		}
		ac.clearQueue();
		ac.queueFile(new File("test.mp3"));
		ctx.sendMessage("Playing test audio...");
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
