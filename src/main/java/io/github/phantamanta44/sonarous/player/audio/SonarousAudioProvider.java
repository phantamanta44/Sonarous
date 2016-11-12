package io.github.phantamanta44.sonarous.player.audio;

import io.github.phantamanta44.sonarous.util.concurrent.NullaryEventStream;
import sx.blah.discord.handle.audio.IAudioProcessor;
import sx.blah.discord.handle.audio.IAudioProvider;
import sx.blah.discord.handle.audio.impl.AudioManager;
import sx.blah.discord.util.audio.processors.MultiProcessor;

public class SonarousAudioProvider implements IAudioProvider {

    private final NullaryEventStream onEOF = new NullaryEventStream();

    private MultiProcessor processor;
    private byte[] data, next;
    private int opusFrameSize, pos;
    private boolean enabled = true;

    public SonarousAudioProvider() {
        this.processor = new MultiProcessor();
        this.processor.setProvider(this);
    }

    @Override
    public boolean isReady() {
        return enabled && hasNext();
    }

    @Override
    public byte[] provide() {
        System.arraycopy(data, pos, next, 0, opusFrameSize);
        pos += opusFrameSize;
        if (!hasNext())
            onEOF.run();
        return next;
    }

    public boolean hasNext() {
        return data != null && pos + opusFrameSize < data.length;
    }

    public IAudioProcessor getProcessor() {
        return processor;
    }

    public void put(byte[] data, int frameSize) {
        this.data = data;
        this.opusFrameSize = AudioManager.OPUS_FRAME_SIZE * frameSize;
        this.next = new byte[opusFrameSize];
        this.pos = 0;
    }

    public void clear() {
        this.data = null;
        this.opusFrameSize = this.pos = 0;
        this.next = null;
    }

    public void addProcessor(IAudioProcessor processor) {
        this.processor.add(processor);
    }

    public void resetProcessors() {
        processor = new MultiProcessor();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public SonarousAudioProvider onEOF(Runnable runnable) {
        onEOF.addHandler(runnable);
        return this;
    }

}
