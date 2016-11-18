package io.github.phantamanta44.sonarous.command;

import io.github.phantamanta44.c4a4d4j.CmdCtx;
import io.github.phantamanta44.commands4a.annot.Alias;
import io.github.phantamanta44.commands4a.annot.Command;
import io.github.phantamanta44.commands4a.annot.Desc;
import io.github.phantamanta44.commands4a.annot.Prereq;
import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.bot.ServerData;
import io.github.phantamanta44.sonarous.player.MusicPlayer;
import io.github.phantamanta44.sonarous.player.search.SearchResolver;
import io.github.phantamanta44.sonarous.player.song.SongResolver;
import io.github.phantamanta44.sonarous.util.Embed;
import io.github.phantamanta44.sonarous.util.Maths;
import io.github.phantamanta44.sonarous.util.RBU;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlayerCommands {

    private static final SongResolver resolver = new SongResolver("io.github.phantamanta44.sonarous.player.song.impl");
    private static final SearchResolver search = new SearchResolver("io.github.phantamanta44.sonarous.player.search.impl", resolver);

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
            player.incrementOperations();
            resolver.resolve(url).done(song -> {
                RBU.reply(ctx.getMessage(), "Queued `%s`.", song.getName());
                player.queue(song);
            }).fail(e -> RBU.reply(ctx.getMessage(), "Failed to resolve audio: %s", e.getMessage()))
            .always(ignored -> player.decrementOperations());
        } catch (UnsupportedOperationException e) {
            player.decrementOperations();
            RBU.reply(ctx.getMessage(), "This audio source isn't supported!");
        }
    }

    @Command(name = "skip")
    @Desc("Votes to skip the current song. A majority must be exceeded for the skip to succeed.")
    @Alias("voteskip")
    @Prereq("guild:true")
    public static void skip(CmdCtx ctx) {
        MusicPlayer player = ServerData.forServer(ctx.getGuild().getID()).getPlayer();
        if (!player.isBound())
            RBU.reply(ctx.getMessage(), "The bot isn't bound to a voice channel!");
        else if (player.getPlaying() == null)
            RBU.reply(ctx.getMessage(), "Nothing is playing.");
        else if (testSkipVotes(ctx, player)) {
            if (player.skipVotes.contains(ctx.getAuthor().getID()))
                RBU.reply(ctx.getMessage(), "You have already voted to skip this song!");
            else {
                player.skipVotes.add(ctx.getAuthor().getID());
                if (testSkipVotes(ctx, player)) {
                    RBU.reply(ctx.getMessage(), "Voted to skip the song (%d / %d needed).",
                            player.skipVotes.size(), Maths.majority(player.getChannel().getConnectedUsers().size() - 1));
                }
            }
        }
    }

    private static boolean testSkipVotes(CmdCtx ctx, MusicPlayer player) {
        if (player.skipVotes.size() >= Maths.majority(player.getChannel().getConnectedUsers().size() - 1)) {
            forceSkip(ctx);
            return false;
        }
        return true;
    }

    @Command(name = "forceskip")
    @Desc("Forcefully skips the current song.")
    @Alias("fs")
    @Prereq("perm:manage_messages") @Prereq("guild:true")
    public static void forceSkip(CmdCtx ctx) {
        MusicPlayer player = ServerData.forServer(ctx.getGuild().getID()).getPlayer();
        if (!player.isBound()) {
            RBU.reply(ctx.getMessage(), "The bot isn't bound to a voice channel!");
            return;
        }
        if (player.getPlaying() != null) {
            RBU.reply(ctx.getMessage(), "Skipped `%s`.", player.getPlaying().getName());
            player.nextSong();
        } else {
            RBU.reply(ctx.getMessage(), "Nothing is playing.");
        }
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

    @Command(name = "volume", usage = "[decibels]")
    @Desc("Sets the playback volume, in decibels.")
    @Alias("vol")
    @Prereq("perm:manage_messages") @Prereq("guild:true")
    public static void volume(CmdCtx ctx, Integer volume) {
        MusicPlayer player = ServerData.forServer(ctx.getGuild().getID()).getPlayer();
        if (!player.isBound())
            RBU.reply(ctx.getMessage(), "The bot isn't bound to a voice channel!");
        else {
            volume = Math.min(volume, 6);
            player.getVolControl().setAmplitude(0.5F * (float)Math.pow(10F, volume / 20F));
            RBU.reply(ctx.getMessage(), "Set volume to %d dB.", volume.intValue());
        }
    }

    @Command(name = "playing")
    @Desc("Gets the currently playing song.")
    @Alias("nowplaying") @Alias("np")
    @Prereq("guild:true")
    public static void playing(CmdCtx ctx) {
        MusicPlayer player = ServerData.forServer(ctx.getGuild().getID()).getPlayer();
        if (!player.isBound())
            RBU.reply(ctx.getMessage(), "The bot isn't bound to a voice channel!");
        else if (player.getPlaying() == null)
            RBU.reply(ctx.getMessage(), "Nothing is playing.");
        else
            RBU.send(ctx.getChannel(), player.getPlayingMessage());
    }

    @Command(name = "lsqueue")
    @Desc("Gets the currently queued songs.")
    @Alias("lsque") @Alias("lsq") @Alias("playlist")
    @Prereq("guild:true")
    public static void lsqueue(CmdCtx ctx) {
        MusicPlayer player = ServerData.forServer(ctx.getGuild().getID()).getPlayer();
        if (!player.isBound())
            RBU.reply(ctx.getMessage(), "The bot isn't bound to a voice channel!");
        else if (player.isQueueEmpty())
            RBU.reply(ctx.getMessage(), "Nothing queued.");
        else {
            StringBuilder msg = new StringBuilder("__**Queued Songs**__\n");
            AtomicInteger index = new AtomicInteger(1);
            player.queue()
                    .map(s -> String.format("`%d |` %s\n", index.getAndIncrement(), s.getName()))
                    .forEach(msg::append);
            RBU.send(ctx.getChannel(), msg.toString().trim());
        }
    }

    @Command(name = "unqueue", usage = "index")
    @Desc("Removes a song from the queue.")
    @Alias("remove") @Alias("pop")
    @Prereq("perm:manage_messages") @Prereq("guild:true")
    public static void unqueue(CmdCtx ctx, Integer index) {
        MusicPlayer player = ServerData.forServer(ctx.getGuild().getID()).getPlayer();
        if (!player.isBound())
            RBU.reply(ctx.getMessage(), "The bot isn't bound to a voice channel!");
        else if (player.isQueueEmpty())
            RBU.reply(ctx.getMessage(), "Nothing queued.");
        else if (!Maths.bounds(index, 1, player.queue().count() + 1))
            RBU.reply(ctx.getMessage(), "Your selected song number is out of bounds!");
        else {
            RBU.reply(ctx.getMessage(), "Removed `%s` from the queue.", player.queue().skip(index).findFirst().get().getName());
            player.drop(index);
        }
    }

    @Command(name = "search", usage = "provider query...")
    @Desc("Searches for a song from a given search provider.")
    @Prereq("guild:true")
    public static void search(CmdCtx ctx, String provider, String query, String[] args) {
        MusicPlayer player = ServerData.forServer(ctx.getGuild().getID()).getPlayer();
        if (!player.isBound()) {
            RBU.reply(ctx.getMessage(), "The bot isn't bound to a voice channel!");
            return;
        }
        try {
            player.incrementOperations();
            search.resolve(provider, Arrays.stream(args).skip(1).collect(Collectors.joining(" "))).done(r -> {
                player.search.clear();
                player.search.addAll(r);
                if (r.isEmpty())
                    RBU.reply(ctx.getMessage(), "No results!");
                else {
                    Embed msg = new Embed()
                            .withAuthor("Search Results", null, BotMain.EMBED_ICON)
                            .withFooter("Use \"result [songNumber]\" to select a song.")
                            .withColour(BotMain.EMBED_COL);
                    IntStream.range(0, r.size())
                            .forEach(i -> msg.withField("Result " + Integer.toString(i + 1), r.get(i).getName()));
                    RBU.send(ctx.getChannel(), msg);
                }
            }).fail(e -> RBU.reply(ctx.getMessage(), "Failed to resolve search results: %s", e.getMessage()))
            .always(ignored -> player.decrementOperations());
        } catch (UnsupportedOperationException e) {
            player.decrementOperations();
            RBU.reply(ctx.getMessage(), "This search provider isn't supported!");
        }
    }

    @Command(name = "result", usage = "index")
    @Desc("Queues a song from the previous search results.")
    @Alias("pick")
    @Prereq("guild:true")
    public static void result(CmdCtx ctx, Integer index) {
        MusicPlayer player = ServerData.forServer(ctx.getGuild().getID()).getPlayer();
        if (!player.isBound())
            RBU.reply(ctx.getMessage(), "The bot isn't bound to a voice channel!");
        else if (player.search.size() == 0)
            RBU.reply(ctx.getMessage(), "There are no search results to select from!");
        else if (!Maths.bounds(index, 1, player.search.size() + 1))
            RBU.reply(ctx.getMessage(), "Your selected result number is out of bounds!");
        else {
            player.incrementOperations();
            player.search.get(index - 1).resolve().done(song -> {
                RBU.reply(ctx.getMessage(), "Queued `%s`.", song.getName());
                player.queue(song);
                player.search.clear();
            }).fail(e -> RBU.reply(ctx.getMessage(), "Failed to resolve audio: %s", e.getMessage()))
            .always(ignored -> player.decrementOperations());
        }
    }

    @Command(name = "lssongp")
    @Desc("Lists available song providers.")
    @Alias("songproviders") @Alias("lssop")
    public static void lssongp(CmdCtx ctx) {
        StringBuilder msg = new StringBuilder("__**Song Providers**__\n");
        resolver.providers()
                .map(s -> String.format("`%s`\n", s.getName()))
                .forEach(msg::append);
        RBU.send(ctx.getChannel(), msg.toString().trim());
    }

    @Command(name = "lssearchp")
    @Desc("Lists available search providers.")
    @Alias("searchproviders") @Alias("lssep")
    public static void lssearchp(CmdCtx ctx) {
        StringBuilder msg = new StringBuilder("__**Search Providers**__\n");
        search.providers()
                .map(s -> String.format("`%s`\n", s.getIdentifier()))
                .forEach(msg::append);
        RBU.send(ctx.getChannel(), msg.toString().trim());
    }

}
