package io.github.phantamanta44.sonarous.util;

import sx.blah.discord.api.internal.json.objects.EmbedObject;

public class D4JUtils {

    public static String stringify(EmbedObject embed) {
        StringBuilder sb = new StringBuilder();
        if (embed.author != null) {
            if (isTruthy(embed.author.name))
                sb.append("**__").append(embed.author.name).append("__**\n");
        }
        if (isTruthy(embed.title))
            sb.append("**\u300a ").append(embed.title).append(" \u300b**\n");
        if (isTruthy(embed.description))
            sb.append("*").append(embed.description).append("*\n");
        if (embed.fields.length > 0) {
            sb.append("\n");
            for (EmbedObject.EmbedFieldObject field : embed.fields)
                sb.append("**__").append(field.name).append("__**\n").append(field.value).append("\n");
            sb.append("\n");
        }
        if (embed.footer != null) {
            if (isTruthy(embed.footer.text))
                sb.append("*").append(embed.footer.text).append("*");
        }
        return sb.toString();
    }

    public static boolean isTruthy(String string) {
        return string != null && !string.isEmpty();
    }

}
