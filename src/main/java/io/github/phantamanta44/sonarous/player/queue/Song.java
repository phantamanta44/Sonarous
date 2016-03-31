package io.github.phantamanta44.sonarous.player.queue;

import javax.sound.sampled.AudioInputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import static io.github.phantamanta44.sonarous.player.queue.SongSoundcloud.URL_PAT;

public abstract class Song {

	public abstract String getName();

	public abstract String getArtist();

	public abstract String getSourceName();

	public abstract String getCoverArt();

	public abstract String getUrl();

	public abstract AudioInputStream getAudioStream();

	/**
	 * Diego Perini's URL regexp pattern (under the MIT license)
	 * https://gist.github.com/dperini/729294
	 */
	private static final Pattern URL_REGEX = Pattern.compile("(?i)^(?:(?:https?|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?$");

	private static final Map<String, Class<? extends Song>> typeMap = new HashMap<>();

	static {
		typeMap.put(URL_PAT, SongSoundcloud.class);
	}

	public static Song getSong(String url) throws URISyntaxException, ClassNotFoundException, IllegalArgumentException {
		if (!URL_REGEX.matcher(url).matches())
			throw new URISyntaxException(url, "Not a real URL!");
		Class<? extends Song> clazz = typeMap.entrySet().stream()
				.filter(t -> url.matches(t.getKey()))
				.map((Function<Map.Entry<String, Class<? extends Song>>, ? extends Class<? extends Song>>)Map.Entry::getValue)
				.findAny().orElse(null);
		if (clazz == null)
			throw new ClassNotFoundException();
		try {
			return clazz.getConstructor(String.class).newInstance(url);
		} catch (NoSuchMethodException|IllegalAccessException e) {
			throw new ClassNotFoundException();
		} catch (InvocationTargetException|InstantiationException e) {
			if (e.getCause() instanceof IllegalArgumentException)
				throw (IllegalArgumentException)e.getCause();
			else if (e.getCause().getCause() instanceof IllegalArgumentException)
				throw (IllegalArgumentException)e.getCause().getCause();
			else
				throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return String.format("%s \u2013 %s", getArtist(), getName());
	}
	
}
