package io.github.phantamanta44.sonarous.player;

import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.core.ICTListener;
import io.github.phantamanta44.sonarous.core.context.IEventContext;
import sx.blah.discord.handle.impl.events.VoiceDisconnectedEvent;
import sx.blah.discord.handle.impl.events.VoicePingEvent;

public class KeepAliveListener implements ICTListener {

	@ICTListener.ListenTo
	public void onHeartBeat(VoicePingEvent event, IEventContext ctx) {
		// NO-OP
	}

	@ICTListener.ListenTo
	public void onDisconnect(VoiceDisconnectedEvent event, IEventContext ctx) {
		switch (event.getReason()) {
			case UNKNOWN:
				BotMain.logger.warn("Disconnected from voice channel for unknown reason.");
				break;
			case TIMEOUT:
				BotMain.logger.warn("Voice timed out!");
				break;
			case MISSED_PINGS:
				BotMain.logger.warn("Voice missing pings!");
				break;
			case RECONNECTING:
				BotMain.logger.warn("Voice reconnecting...");
				break;
		}
	}

}
