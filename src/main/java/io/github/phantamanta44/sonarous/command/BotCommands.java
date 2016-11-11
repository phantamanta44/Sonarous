package io.github.phantamanta44.sonarous.command;

import io.github.phantamanta44.c4a4d4j.CmdCtx;
import io.github.phantamanta44.commands4a.annot.Command;
import io.github.phantamanta44.commands4a.annot.Desc;
import io.github.phantamanta44.sonarous.util.RBU;

public class BotCommands {

    @Command(name = "help", usage = "")
    @Desc("Gets helpful information about the bot.")
    public static void help(CmdCtx ctx) {
        RBU.reply(ctx.getMessage(), "https://github.com/phantamanta44/Sonarous");
    }

}
