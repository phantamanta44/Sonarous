package io.github.phantamanta44.sonarous.command;

import io.github.phantamanta44.c4a4d4j.CmdCtx;
import io.github.phantamanta44.commands4a.annot.Alias;
import io.github.phantamanta44.commands4a.annot.Command;
import io.github.phantamanta44.commands4a.annot.Desc;
import io.github.phantamanta44.commands4a.annot.Prereq;
import io.github.phantamanta44.sonarous.bot.ServerData;
import io.github.phantamanta44.sonarous.player.MusicPlayer;
import io.github.phantamanta44.sonarous.player.SongResolver;
import io.github.phantamanta44.sonarous.util.Maths;
import io.github.phantamanta44.sonarous.util.RBU;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.Optional;

public class PlayerCommands {

    private static final SongResolver resolver = new SongResolver("io.github.phantamanta44.sonarous.player.impl");

    @Command(name = "bind")
    @Desc("Binds the bot to a voice channel.")
    @Alias("join") @Alias("summon")
    @Prereq("perm:manage_messages") @Prereq("guild:true")
    public static void bind(CmdCtx ctx) {
        Optional<IVoiceChannel> channel = ctx.getGuild().getVoiceChannels().stream()
                .filter(c -> c.getConnectedUsers().stream().anyMatch(u -> u.getID().equalsIgnoreCase(ctx.getAuthor().getID())))
                .findAny();
        if (!channel.isPresent())
            RBU.reply(ctx.getMessage(), "You are not in a voice channel in this guild!");
        else {
            MusicPlayer player = ServerData.forServer(ctx.getGuild().getID()).getPlayer();
            if (player.isBound() && player.getChannel().getID().equalsIgnoreCase(channel.get().getID()))
                RBU.reply(ctx.getMessage(), "Already bound to this channel!");
            else {
                player.bind(channel.get());
                player.setNotificationChan(ctx.getChannel());
                RBU.reply(ctx.getMessage(), "Bound to voice channel %s!", channel.get().getName());
            }
        }
    }

    @Command(name = "unbind")
    @Desc("Unbinds the bot from a voice channel.")
    @Alias("leave")
    @Prereq("perm:manage_messages") @Prereq("guild:true")
    public static void unbind(CmdCtx ctx) {
        MusicPlayer player = ServerData.forServer(ctx.getGuild().getID()).getPlayer();
        player.clearQueue();
        player.unbind();
        RBU.reply(ctx.getMessage(), "Bot unbound!");
    }

    @Command(name = "play", usage = "songUrl")
    @Desc("Adds a song to the queue.")
    @Alias("queue")
    @Prereq("guild:true")
    public static void play(CmdCtx ctx, String url) {
        MusicPlayer player = ServerData.forServer(ctx.getGuild().getID()).getPlayer();
        if (!player.isBound()) {
            RBU.reply(ctx.getMessage(), "The bot isn't bound to a voice channel!");
            return;
        }
        try {
            resolver.resolve(url).done(song -> {
                player.queue(song);
                RBU.reply(ctx.getMessage(), "Queued `%s`.", song.getName());
            }).fail(e -> RBU.reply(ctx.getMessage(), "Failed to resolve audio: %s", e.getMessage()));
        } catch (UnsupportedOperationException e) {
            RBU.reply(ctx.getMessage(), "This audio source isn't supported!");
        }
    }

    @Command(name = "skip")
    @Desc("Votes to skip the current song. A majority must be exceeded for the skip to succeed.")
    @Alias("voteskip")
    @Prereq("guild:true")
    public static void skip(CmdCtx ctx) {
        MusicPlayer player = ServerData.forServer(ctx.getGuild().getID()).getPlayer();
        if (!player.isBound()) {
            RBU.reply(ctx.getMessage(), "The bot isn't bound to a voice channel!");
            return;
        }
        if (testSkipVotes(ctx, player)) {
            if (player.skipVotes.contains(ctx.getAuthor().getID()))
                RBU.reply(ctx.getMessage(), "You have already voted to skip this song!");
            else {
                player.skipVotes.add(ctx.getAuthor().getID());
                if (testSkipVotes(ctx, player))
                    RBU.reply(ctx.getMessage(), "Voted to skip the song (%d / %d needed).",
                            player.skipVotes.size(), Maths.majority(player.getChannel().getConnectedUsers().size() - 1));
            }
        }
    }

    private static boolean testSkipVotes(CmdCtx ctx, MusicPlayer player) {
        if (player.skipVotes.size() >= Maths.majority(player.getChannel().getConnectedUsers().size())) {
            forceSkip(ctx);
            return false;
        }
        return true;
    }

    @Command(name = "forceskip")
    @Desc("Forcefully skips the current song.")
    @Prereq("perm:manage_messages") @Prereq("guild:true")
    public static void forceSkip(CmdCtx ctx) {
        MusicPlayer player = ServerData.forServer(ctx.getGuild().getID()).getPlayer();
        if (!player.isBound()) {
            RBU.reply(ctx.getMessage(), "The bot isn't bound to a voice channel!");
            return;
        }
        RBU.reply(ctx.getMessage(), "Skipped `%s`.", player.getPlaying().getName());
        player.nextSong();
    }

    @Command(name = "pause")
    @Desc("Pauses the audio playback.")
    @Prereq("perm:manage_messages") @Prereq("guild:true")
    public static void pause(CmdCtx ctx) {
        MusicPlayer player = ServerData.forServer(ctx.getGuild().getID()).getPlayer();
        if (!player.isBound()) {
            RBU.reply(ctx.getMessage(), "The bot isn't bound to a voice channel!");
            return;
        }
        player.pause();
        RBU.reply(ctx.getMessage(), "Paused playback.");
    }

    @Command(name = "unpause")
    @Desc("Unpauses the audio playback.")
    @Alias("resume")
    @Prereq("perm:manage_messages") @Prereq("guild:true")
    public static void unpause(CmdCtx ctx) {
        MusicPlayer player = ServerData.forServer(ctx.getGuild().getID()).getPlayer();
        if (!player.isBound()) {
            RBU.reply(ctx.getMessage(), "The bot isn't bound to a voice channel!");
            return;
        }
        player.unpause();
        RBU.reply(ctx.getMessage(), "Resuming playback.");
    }

}
