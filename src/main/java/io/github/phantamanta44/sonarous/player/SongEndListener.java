package io.github.phantamanta44.sonarous.player;

import io.github.phantamanta44.sonarous.core.ICTListener;
import io.github.phantamanta44.sonarous.core.context.IEventContext;
import sx.blah.discord.handle.impl.events.AudioStopEvent;

public class SongEndListener implements ICTListener {

	@ICTListener.ListenTo
	public void onSongEnd(AudioStopEvent event, IEventContext ctx) {
		if (Sonarous.hasNext()) {
			Sonarous.next();
			SubscriptionHandler.dispatch();
		}
		else
			Sonarous.stop();
	}

}
