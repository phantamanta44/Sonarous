package io.github.phantamanta44.sonarous.player.audio;

import io.github.phantamanta44.sonarous.util.Maths;
import sx.blah.discord.handle.audio.IAudioProcessor;
import sx.blah.discord.handle.audio.IAudioProvider;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AmplificationProcessor implements IAudioProcessor {

    private IAudioProvider source;
    private float amplitude;

    public AmplificationProcessor() {
        this(1F);
    }

    public AmplificationProcessor(float amplitude) {
        this.amplitude = amplitude;
    }

    @Override
    public boolean setProvider(IAudioProvider provider) {
        if (provider.getAudioEncodingType() != AudioEncodingType.PCM)
            return false;
        this.source = provider;
        return true;
    }

    @Override
    public boolean isReady() {
        return source.isReady();
    }

    @Override
    public byte[] provide() {
        byte[] data = source.provide();
        if (data == null || data.length == 0)
            return new byte[0];
        ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);
        for (int i = 0; i < buf.capacity(); i += 2) {
            short sample = buf.getShort(i);
            buf.putShort(i, (short)(sample * amplitude));
        }
        return buf.array();
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = Maths.clamp(amplitude, 0F, 1F);
    }

    public float getAmplitude() {
        return amplitude;
    }

}
