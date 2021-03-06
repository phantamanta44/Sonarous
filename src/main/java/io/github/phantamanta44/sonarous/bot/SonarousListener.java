package io.github.phantamanta44.sonarous.bot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonElement;

import io.github.phantamanta44.c4a4d4j.CmdCtx;
import io.github.phantamanta44.commands4a.exception.InvalidSyntaxException;
import io.github.phantamanta44.commands4a.exception.NoSuchCommandException;
import io.github.phantamanta44.commands4a.exception.PrereqNotMetException;
import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.util.RBU;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

public class SonarousListener {
    
    private final Pattern tagPattern;
    
    public SonarousListener(IUser botUser) {
        tagPattern = Pattern.compile("^(?:" + Pattern.quote(botUser.mention(false)) + "|" + Pattern.quote(botUser.mention(true)) + ")\\s?");
    }

    @EventSubscriber
    public void onMessage(MessageReceivedEvent event) {
        if (event.getMessage().getContent() == null || event.getMessage().getAuthor() == null)
            return;
        
        String cmd = null;
        String prefix = event.getMessage().getChannel().isPrivate()
                ? BotMain.client().getConfigValue("prefix").getAsString()
                : ServerData.forServer(event.getMessage().getGuild().getID()).getPrefix();
        Matcher m;
        if (event.getMessage().getContent().startsWith(prefix))
            cmd = event.getMessage().getContent().substring(prefix.length());
        else if ((m = tagPattern.matcher(event.getMessage().getContent())).find())
            cmd = event.getMessage().getContent().substring(m.group(0).length());
        else
            return;

        boolean cmdExists = true;
        try {
            BotMain.commander().execute(new CmdCtx(event), cmd);
        } catch (PrereqNotMetException e) {
            RBU.reply(event.getMessage(), e.getPrerequisite().getFailMessage());
        } catch (NoSuchCommandException e) {
            JsonElement mcmCfg = BotMain.client().getConfigValue("missingCommandMessage");
            if (mcmCfg != null && mcmCfg.getAsBoolean())
                RBU.reply(event.getMessage(), "No such command `" + e.getCommand() + "`!");
            cmdExists = false;
        } catch (InvalidSyntaxException e) {
            RBU.reply(event.getMessage(), e.getReason());
        }

        if (cmdExists) {
            if (event.getMessage().getChannel().isPrivate())
                BotMain.log().info("CDM: {} ({}#{}): {}",
                        event.getMessage().getAuthor().getID(),
                        event.getMessage().getAuthor().getName(),
                        event.getMessage().getAuthor().getDiscriminator(),
                        event.getMessage().getContent()
                );
            else
                BotMain.log().info("CGD: {}#{} / {} ({}#{}): {}",
                        event.getMessage().getGuild().getName(),
                        event.getMessage().getChannel().getName(),
                        event.getMessage().getAuthor().getID(),
                        event.getMessage().getAuthor().getName(),
                        event.getMessage().getAuthor().getDiscriminator(),
                        event.getMessage().getContent()
                );
        }
    }

}
