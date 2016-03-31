package io.github.phantamanta44.sonarous.player.queue;

import com.google.gson.JsonObject;
import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.util.SafeJsonWrapper;
import io.github.phantamanta44.sonarous.util.http.HttpException;
import io.github.phantamanta44.sonarous.util.http.HttpUtils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.net.URL;

public class SongSoundcloud extends Song {

	public static final String URL_PAT = "https?://.*soundcloud\\.com/.+";
	private static final String RESOLVE = "https://api.soundcloud.com/resolve?url=%s&client_id=%s";
	
	private final String name, artist, coverUrl, songUrl, streamUrl;
	
	public SongSoundcloud(String url) {
		try {
			if (!url.matches(URL_PAT))
				throw new IllegalArgumentException("Provided URL isn't from SoundCloud!");
			String reqUrl = String.format(RESOLVE, url, getApiKey());
			BotMain.logger.info("Requesting SC data: %s", reqUrl);
			JsonObject resp = HttpUtils.requestJson(reqUrl).getAsJsonObject();
			SafeJsonWrapper data = new SafeJsonWrapper(resp);
			if (!data.getString("kind").equalsIgnoreCase("track"))
				throw new IllegalArgumentException("Provided URL isn't a track!");
			name = data.getString("title");
			artist = data.getJsonObject("user").getString("username");
			coverUrl = data.getString("artwork_url").replaceAll("large.jpg", "t500x500.jpg");
			songUrl = data.getString("permalink_url");
			streamUrl = data.getString("stream_url") + "?client_id=" + getApiKey();
		} catch (HttpException e) {
			if (e.getCode() == 404)
				throw new IllegalArgumentException("Provided URL isn't a track!");
			else
				throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String getApiKey() {
		String key = BotMain.config.get("scApiKey");
		if (key == null)
			throw new UnsupportedOperationException("No SoundCloud api key provided! Populate the config key \"scApiKey\".");
		return key;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getArtist() {
		return artist;
	}

	@Override
	public String getSourceName() {
		return "SoundCloud";
	}

	@Override
	public String getCoverArt() {
		return coverUrl;
	}

	@Override
	public String getUrl() {
		return songUrl;
	}

	@Override
	public AudioInputStream getAudioStream() {
		try {
			return AudioSystem.getAudioInputStream(new URL(streamUrl).openStream());
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
}
