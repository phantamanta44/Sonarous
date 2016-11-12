package io.github.phantamanta44.sonarous.player;

import io.github.phantamanta44.sonarous.player.audio.AmplificationProcessor;
import io.github.phantamanta44.sonarous.player.audio.SonarousAudioProvider;
import io.github.phantamanta44.sonarous.player.search.ISearchResult;
import io.github.phantamanta44.sonarous.player.song.ISong;
import io.github.phantamanta44.sonarous.util.RBU;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;
import sx.blah.discord.handle.audio.IAudioManager;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class MusicPlayer {

    public Set<String> skipVotes = new HashSet<>();
    public List<ISearchResult> search = new CopyOnWriteArrayList<>();

    private SonarousAudioProvider provider;
    private AmplificationProcessor volControl = new AmplificationProcessor(0.5F);
    private IAudioManager audioManager;

    private IChannel notChan = null;
    private IVoiceChannel channel = null;
    private ISong playing = null;
    private List<ISong> queue = new CopyOnWriteArrayList<>();

    public void setNotificationChan(IChannel notChan) {
        this.notChan = notChan;
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
        queue.add(song);
        if (playing == null)
            nextSong();
    }

    public void nextSong() {
        if (hasNext()) {
            skipVotes.clear();
            playing = queue.remove(0);
            provider.put(playing.getAudio(), playing.getFrameSize());
            RBU.send(notChan, getPlayingMessage());
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
        queue.clear();
        skipVotes.clear();
        search.clear();
        if (provider != null)
            provider.clear();
    }

    public ISong getPlaying() {
        return playing;
    }

    public String getPlayingMessage() {
        StringBuilder msg = new StringBuilder("__**Now Playing: ");
        if (playing.getAuthor() != null)
            msg.append(playing.getAuthor()).append(" \u2013 ");
        msg.append(playing.getName()).append("**__\n");
        if (playing.getAlbum() != null)
            msg.append("**From the Album: ").append(playing.getAlbum()).append("**\n");
        if (playing.getUrl() != null)
            msg.append("**URL: ").append(playing.getUrl()).append("**\n");
        if (playing.getArtUrl() != null)
            msg.append("**Artwork: ").append(playing.getArtUrl()).append("**\n");
        return msg.append("*(via ").append(playing.getProvider().getName()).append(")*").toString();
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

}
