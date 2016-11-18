package io.github.phantamanta44.sonarous.player.song;

import sx.blah.discord.handle.audio.impl.AudioManager;

public interface ISong {

    ISongProvider getProvider();

    String getName();
    
    String getAuthor();
    
    String getAlbum();
    
    String getUrl();
    
    String getArtUrl();
    
    byte[] getAudio();

    int getChannelCount();

    int getFrameSize();

    default float getLength() {
        return (float)getAudio().length / (getFrameSize() * AudioManager.OPUS_FRAME_SIZE * 50);
    }
    
}
