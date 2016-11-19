package io.github.phantamanta44.sonarous.player;

import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.player.audio.AmplificationProcessor;
import io.github.phantamanta44.sonarous.player.audio.SonarousAudioProvider;
import io.github.phantamanta44.sonarous.player.search.ISearchResult;
import io.github.phantamanta44.sonarous.player.song.ISong;
import io.github.phantamanta44.sonarous.util.D4JUtils;
import io.github.phantamanta44.sonarous.util.RBU;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;
import sx.blah.discord.api.internal.DiscordUtils;
import sx.blah.discord.handle.audio.IAudioManager;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class MusicPlayer {

    public Set<String> skipVotes = new HashSet<>();
    public List<ISearchResult> search = new CopyOnWriteArrayList<>();
    private int operations = 0;

    private SonarousAudioProvider provider;
    private AmplificationProcessor volControl = new AmplificationProcessor();
    private IAudioManager audioManager;

    private IChannel notChan = null;
    private IVoiceChannel channel = null;
    private ISong playing = null;
    private List<ISong> queue = new CopyOnWriteArrayList<>();

    public void setNotificationChan(IChannel notChan) {
        this.notChan = notChan;
    }

    public synchronized void incrementOperations() {
        operations++;
        notChan.setTypingStatus(true);
    }

    public synchronized void decrementOperations() {
        operations = Math.max(operations - 1, 0);
        if (operations == 0 && notChan != null)
            notChan.setTypingStatus(false);
    }

    public IPromise<Void> bind(IVoiceChannel channel) {
        unbind();
        this.channel = channel;
        return RBU.join(channel).done(ignored -> am());
    }

    public void unbind() {
        clearQueue();
        if (channel != null) {
            channel.leave();
            channel = null;
        }
    }

    public void queue(ISong song) {
        if (isBound()) {
            queue.add(song);
            if (playing == null)
                nextSong();
        }
    }

    public void nextSong() {
        if (hasNext()) {
            skipVotes.clear();
            playing = queue.remove(0);
            provider.put(playing.getAudio(), playing.getFrameSize());
            RBU.send(notChan, getPlayingMessage()).fail(e -> {
                if (e instanceof MissingPermissionsException)
                    RBU.send(notChan, D4JUtils.stringify(getPlayingMessage().build()));
                else
                    BotMain.log().error("Failed to send embed!", e);
            });
        } else {
            RBU.send(notChan, "__**The queue is empty! Stopping player...**__");
            clearQueue();
        }
    }

    public boolean hasNext() {
        return !queue.isEmpty();
    }

    public void pause() {
        provider.setEnabled(false);
    }

    public void unpause() {
        provider.setEnabled(true);
    }

    public void drop(int index) {
        queue.remove(index);
    }

    public void clearQueue() {
        playing = null;
        queue.clear();
        skipVotes.clear();
        search.clear();
        operations = 0;
        if (provider != null)
            provider.clear();
    }

    public ISong getPlaying() {
        return playing;
    }

    public EmbedBuilder getPlayingMessage() {
        EmbedBuilder msg = new EmbedBuilder()
                .withAuthorName("Now Playing")
                .withAuthorIcon(BotMain.EMBED_ICON)
                .withTitle(playing.getName())
                .withFooterText("Via " + playing.getProvider().getName())
                .withFooterIcon(playing.getProvider().getIconUrl())
                .withColor(BotMain.EMBED_COL);
        if (playing.getAuthor() != null && !playing.getAuthor().isEmpty())
            msg.appendField("Artist", playing.getAuthor(), false);
        if (playing.getAlbum() != null && !playing.getAlbum().isEmpty())
            msg.appendField("Album", playing.getAlbum(), false);
        msg.appendField("Length", formatDuration(playing.getLength()), false);
        if (playing.getUrl() != null && !playing.getUrl().isEmpty())
            msg.withAuthorUrl(playing.getUrl());
        if (playing.getArtUrl() != null && !playing.getArtUrl().isEmpty())
            msg.withThumbnail(playing.getArtUrl());
        return msg;
    }

    public Stream<ISong> queue() {
        return queue.stream();
    }

    public AmplificationProcessor getVolControl() {
        return volControl;
    }

    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    public boolean isBound() {
        return channel != null;
    }

    private IAudioManager am() {
        if (audioManager == null) {
            provider = new SonarousAudioProvider();
            provider.addProcessor(volControl);
            audioManager = channel.getGuild().getAudioManager();
            audioManager.setAudioProvider(provider);
            audioManager.setAudioProcessor(provider.getProcessor());
            provider.onEOF(this::nextSong);
        }
        return audioManager;
    }

    public IVoiceChannel getChannel() {
        return channel;
    }

    private static String formatDuration(float secondFloat) {
        int ttlSeconds = (int)Math.ceil(secondFloat);
        if (ttlSeconds < 60)
            return String.format("0:%02d", ttlSeconds);
        int seconds = ttlSeconds % 60;
        int ttlMinutes = (ttlSeconds - seconds) / 60;
        if (ttlMinutes < 60)
            return String.format("%d:%02d", ttlMinutes, seconds);
        int minutes = ttlMinutes % 60;
        int ttlHours = (ttlMinutes - minutes) / 60;
        return String.format("%d:%02d:%02d", ttlHours, minutes, seconds);
    }

}
