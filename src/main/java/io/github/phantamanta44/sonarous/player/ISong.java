package io.github.phantamanta44.sonarous.player;

public interface ISong {

    ISongProvider getProvider();

    String getName();
    
    String getAuthor();
    
    String getAlbum();
    
    String getUrl();
    
    String getArtUrl();
    
    byte[] getAudio();

    int getFrameSize();

    default long getLength() {
        return getAudio().length / (getFrameSize() / 8);
    }
    
}
