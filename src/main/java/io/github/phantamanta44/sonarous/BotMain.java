package io.github.phantamanta44.sonarous;

import io.github.phantamanta44.c4a4d4j.C4A4D4J;
import io.github.phantamanta44.c4a4d4j.CmdCtx;
import io.github.phantamanta44.commands4a.CommandEngineProvider;
import io.github.phantamanta44.commands4a.command.CommandEngine;
import io.github.phantamanta44.sonarous.bot.Discord;
import io.github.phantamanta44.sonarous.bot.ServerData;
import io.github.phantamanta44.sonarous.bot.SonarousListener;
import io.github.phantamanta44.sonarous.util.ExitCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.Discord4J;

public class BotMain {

    private static Discord client;
    private static CommandEngine<CmdCtx> commander;
    private static Logger log;

	public static void main(String[] args) {
        log = LoggerFactory.getLogger("Sonarous");
        Discord4J.disableChannelWarnings();
        client = new Discord();
		if (!client.readConfig())
            ExitCodes.exit(ExitCodes.LOGIN_FAIL);
        client.init().done(readyEvent -> {
            commander = CommandEngineProvider.getEngine(C4A4D4J.DESCRIPTOR);
            commander.scan("io.github.phantamanta44.sonarous.command");
            client.api().getDispatcher().registerListener(new SonarousListener(client.api().getOurUser()));
        }).fail(e -> {
            e.printStackTrace();
            ExitCodes.exit(ExitCodes.LOGIN_FAIL);
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ServerData.writeData();
            ServerData.unbindAll();
        }));
	}

	public static Discord client() {
        return client;
    }

    public static CommandEngine<CmdCtx> commander() {
        return commander;
    }

    public static Logger log() {
        return log;
    }
	
}
