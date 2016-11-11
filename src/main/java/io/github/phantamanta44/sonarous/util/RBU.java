package io.github.phantamanta44.sonarous.util;

import io.github.phantamanta44.sonarous.util.deferred.Deferred;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RequestBuffer;

/**
 * Request buffer utils
 */
public class RBU {

    public static IPromise<IMessage> reply(IMessage message, String reply) {
        Deferred<IMessage> def = new Deferred<>();
        RequestBuffer.request(() -> {
            try {
                def.resolve(message.getChannel().sendMessage(message.getAuthor().mention() + ": " + reply));
            } catch (Exception e) {
                def.reject(e);
            }
        });
        return def.promise();
    }

    public static IPromise<IMessage> reply(IMessage message, String replyFmt, Object... args) {
        return reply(message, String.format(replyFmt, args));
    }

}
