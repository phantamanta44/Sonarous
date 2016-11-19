package io.github.phantamanta44.sonarous.util;

import io.github.phantamanta44.sonarous.util.deferred.Deferred;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;

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

    public static IPromise<IMessage> send(IChannel channel, EmbedBuilder embed) {
        Deferred<IMessage> def = new Deferred<>();
        RequestBuffer.request(() -> {
            try {
                def.resolve(channel.sendMessage(null, embed.build(), false));
            } catch (RateLimitException e) {
                throw e;
            } catch (Throwable e) {
                def.reject(e);
            }
        });
        return def.promise();
    }

}
