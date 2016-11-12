package io.github.phantamanta44.sonarous.command;

import io.github.phantamanta44.c4a4d4j.CmdCtx;
import io.github.phantamanta44.c4a4d4j.arg.InlineCodeBlock;
import io.github.phantamanta44.commands4a.annot.*;
import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.bot.ServerData;
import io.github.phantamanta44.sonarous.util.ExitCodes;
import io.github.phantamanta44.sonarous.util.RBU;

public class BotCommands {

    @Command(name = "help")
    @Desc("Gets helpful information about the bot.")
    public static void help(CmdCtx ctx) {
        RBU.reply(ctx.getMessage(), "https://github.com/phantamanta44/Sonarous");
    }

    @Command(name = "setprefix", usage = "`prefix`")
    @Desc("Sets the prefix in the given server.")
    @Prereq("perm:manage_server") @Prereq("guild:true")
    public static void setPrefix(CmdCtx ctx, InlineCodeBlock prefix) {
        ServerData.forServer(ctx.getGuild().getID()).setPrefix(prefix.getCode());
        RBU.reply(ctx.getMessage(), "Set server prefix to `%s`.", prefix.getCode());
    }

    @Command(name = "halt", usage = "reason")
    @Desc("Kills the bot.")
    public static void halt(CmdCtx ctx, @Omittable String reason) {
        if (!ctx.getAuthor().getID().equalsIgnoreCase(BotMain.client().getConfigValue("admin").getAsString())) {
            RBU.reply(ctx.getMessage(), "No permission!");
            return;
        }
        if (reason != null) {
            switch (reason.toLowerCase()) {
                case "reboot":
                    RBU.reply(ctx.getMessage(), "Rebooting!").always(ignored -> ExitCodes.exit(ExitCodes.REBOOT));
                    break;
                case "update":
                    RBU.reply(ctx.getMessage(), "Updating!").always(ignored -> ExitCodes.exit(ExitCodes.UPDATE));
                    break;
                default:
                    RBU.reply(ctx.getMessage(), "Unknown exit reason!");
                    break;
            }
        } else {
            RBU.reply(ctx.getMessage(), "Halting!").always(ignored -> ExitCodes.exit(ExitCodes.SUCCESS));
        }
    }

}
