package io.github.phantamanta44.sonarous.player;

import com.google.gson.JsonElement;
import io.github.phantamanta44.c4a4d4j.CmdCtx;
import io.github.phantamanta44.commands4a.exception.InvalidSyntaxException;
import io.github.phantamanta44.commands4a.exception.NoSuchCommandException;
import io.github.phantamanta44.commands4a.exception.PrereqNotMetException;
import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.Discord;
import io.github.phantamanta44.sonarous.util.RBU;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageTokenizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SonarousListener {

    @EventSubscriber
    public void onMessage(MessageReceivedEvent event) {
        if (event.getMessage().getContent() == null || event.getMessage().getAuthor() == null)
            return;
        String prefix = BotMain.client().getConfigValue("prefix").getAsString();
        IUser botUser = BotMain.client().api().getOurUser();
        Pattern tagPattern = Pattern.compile("^(?:" + Pattern.quote(botUser.mention(false)) + "|" + Pattern.quote(botUser.mention(true)) + ")\\s?");
        Matcher m = tagPattern.matcher(event.getMessage().getContent());
        String cmd = null;
        if (event.getMessage().getContent().startsWith(prefix)) {
            cmd = event.getMessage().getContent().substring(prefix.length());
        } else if (m.find()) {
            cmd = event.getMessage().getContent().substring(m.group(0).length());
        } else {
            return;
        }

        try {
            BotMain.commander().execute(new CmdCtx(event), cmd);
        } catch (PrereqNotMetException e) {
            RBU.reply(event.getMessage(), e.getPrerequisite().getFailMessage());
        } catch (NoSuchCommandException e) {
            JsonElement mcmCfg = BotMain.client().getConfigValue("missingCommandMessage");
            if (mcmCfg != null && mcmCfg.getAsBoolean())
                RBU.reply(event.getMessage(), "No such command `" + e.getCommand() + "`!");
        } catch (InvalidSyntaxException e) {
            RBU.reply(event.getMessage(), "Invalid parameters! " + e.getReason());
        }
    }

}
