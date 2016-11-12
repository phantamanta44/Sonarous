package io.github.phantamanta44.sonarous.command;

import io.github.phantamanta44.c4a4d4j.CmdCtx;
import io.github.phantamanta44.c4a4d4j.arg.InlineCodeBlock;
import io.github.phantamanta44.commands4a.annot.Command;
import io.github.phantamanta44.commands4a.annot.Desc;
import io.github.phantamanta44.commands4a.annot.Prereq;
import io.github.phantamanta44.sonarous.player.ServerData;
import io.github.phantamanta44.sonarous.util.RBU;
import sx.blah.discord.handle.obj.Permissions;

public class BotCommands {

    @Command(name = "help", usage = "")
    @Desc("Gets helpful information about the bot.")
    public static void help(CmdCtx ctx) {
        RBU.reply(ctx.getMessage(), "https://github.com/phantamanta44/Sonarous");
    }

    @Command(name = "setprefix", usage = "`prefix`")
    @Desc("Sets the prefix in the given server.")
    @Prereq("perm:manage_server") @Prereq("guild:true")
    public static void setPrefix(CmdCtx ctx, InlineCodeBlock prefix) {
        ServerData.forServer(ctx.getGuild().getID()).setPrefix(prefix.getCode());
        RBU.reply(ctx.getMessage(), "**Set server prefix to** `%s`**.**", prefix.getCode());
    }

}
