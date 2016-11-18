package io.github.phantamanta44.sonarous.util;

import io.github.phantamanta44.sonarous.util.deferred.Deferred;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;
import org.apache.http.entity.StringEntity;
import sx.blah.discord.api.internal.DiscordClientImpl;
import sx.blah.discord.api.internal.DiscordEndpoints;
import sx.blah.discord.api.internal.DiscordUtils;
import sx.blah.discord.api.internal.json.objects.MessageObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;

import java.util.EnumSet;

/**
 * Request buffer utils
 */
public class RBU {

    public static IPromise<IMessage> send(IChannel channel, String message) {
        Deferred<IMessage> def = new Deferred<>();
        RequestBuffer.request(() -> {
            try {
                def.resolve(channel.sendMessage(message));
            } catch (RateLimitException e) {
                throw e;
            } catch (Exception e) {
                def.reject(e);
            }
        });
        return def.promise();
    }

    public static IPromise<IMessage> send(IChannel channel, String messageFmt, Object... args) {
        return send(channel, String.format(messageFmt, args));
    }

    public static IPromise<IMessage> reply(IMessage message, String reply) {
        return send(message.getChannel(), "%s: %s", message.getAuthor().mention(), reply);
    }

    public static IPromise<IMessage> reply(IMessage message, String replyFmt, Object... args) {
        return reply(message, String.format(replyFmt, args));
    }

    public static IPromise<Void> join(IVoiceChannel channel) {
        Deferred<Void> def = new Deferred<>();
        RequestBuffer.request(() -> {
            try {
                channel.join();
                def.resolve(null);
            } catch (Throwable e) {
                def.reject(e);
            }
        });
        return def.promise();
    }

    public static IPromise<IMessage> send(IChannel channel, Embed embed) {
        Deferred<IMessage> def = new Deferred<>();
        RequestBuffer.request(() -> {
            try {
                DiscordUtils.checkPermissions(channel.getClient(), channel, EnumSet.of(Permissions.SEND_MESSAGES));
                MessageObject resp = DiscordUtils.GSON.fromJson(
                        ((DiscordClientImpl)channel.getClient()).REQUESTS.POST.makeRequest(
                                DiscordEndpoints.CHANNELS + channel.getID() + "/messages",
                                new StringEntity(embed.toJson(DiscordUtils.GSON), "UTF-8")
                        ),
                        MessageObject.class
                );
                if (resp != null && resp.id != null)
                    def.resolve(DiscordUtils.getMessageFromJSON(channel, resp));
                else
                    def.reject(new DiscordException("Message was unable to be sent."));
            } catch (RateLimitException e) {
                throw e;
            } catch (Exception e) {
                def.reject(e);
            }
        });
        return def.promise();
    }

}
