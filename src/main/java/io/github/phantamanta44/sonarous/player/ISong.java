package io.github.phantamanta44.sonarous.player;

import java.nio.ByteBuffer;

public interface ISong {

    String getName();
    
    String getAuthor();
    
    String getAlbum();
    
    String getUrl();
    
    String getArtUrl();
    
    ByteBuffer getAudio();
    
}
