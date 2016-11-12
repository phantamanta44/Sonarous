package io.github.phantamanta44.sonarous.player.song;

public interface ISong {

    ISongProvider getProvider();

    String getName();
    
    String getAuthor();
    
    String getAlbum();
    
    String getUrl();
    
    String getArtUrl();
    
    byte[] getAudio();

    int getFrameSize();
    
}
