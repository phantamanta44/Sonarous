package io.github.phantamanta44.sonarous.player.command;

import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.core.command.ICommand;
import io.github.phantamanta44.sonarous.core.context.IEventContext;
import io.github.phantamanta44.sonarous.player.Sonarous;
import io.github.phantamanta44.sonarous.player.queue.Song;
import sx.blah.discord.handle.obj.IUser;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

public class CommandQueue implements ICommand {

	@Override
	public String getName() {
		return "queue";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Queues a song from a media source.";
	}

	@Override
	public String getUsage() {
		return "queue <url>";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (Sonarous.getAudioChannel() == null) {
			ctx.sendMessage("Not in any voice channels!");
			return;
		}
		if (args.length < 1) {
			ctx.sendMessage("You must supply a URL for a song!");
			return;
		}
		try {
			Song song = Song.getSong(args[0]);
			Sonarous.enqueue(song);
			ctx.sendMessage("Queued song: **%s \u2013 %s**", song.getArtist(), song.getName());
			if (!Sonarous.isPlaying())
				Sonarous.next();
		} catch (URISyntaxException e) {
			ctx.sendMessage("Not a valid URL!");
		} catch (ClassNotFoundException e) {
			ctx.sendMessage("This media source isn't supported at the moment!");
		} catch (IllegalArgumentException e) {
			ctx.sendMessage("Not a valid media link!");
		} catch (Exception e) {
			ctx.sendMessage("General exception retrieving media!");
			e.printStackTrace();
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
