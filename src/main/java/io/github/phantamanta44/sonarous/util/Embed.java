package io.github.phantamanta44.sonarous.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Temporary embeds implementation because d4j doesn't have one yet
 */
public class Embed {

    private List<EmbedField> fields = new LinkedList<>();
    private Map<String, String> rawProps = new HashMap<>();
    private Map<String, JsonElement> jsonProps = new HashMap<>();
    private OptionalInt colour = OptionalInt.empty();

    public Embed withTitle(String title) {
        return put("title", title);
    }

    public Embed withDesc(String desc) {
        return put("description", desc);
    }

    public Embed withUrl(String url) {
        return put("url", url);
    }

    public Embed withColour(Color colour) {
        this.colour = OptionalInt.of(colour.getRGB() & 0xFFFFFF);
        return this;
    }

    public Embed withImage(String url) {
        return put("image", wrapUrl(sanitizeUrl(url)));
    }

    public Embed withThumbnail(String url) {
        return put("thumbnail", wrapUrl(sanitizeUrl(url)));
    }

    public Embed withAuthor(String name, String url, String iconUrl) {
        JsonObject author = new JsonObject();
        author.addProperty("name", name);
        if (url != null)
            author.addProperty("url", url);
        author.addProperty("icon_url", sanitizeUrl(iconUrl));
        return put("author", author);
    }

    public Embed withAuthor(String name, String url) {
        JsonObject author = new JsonObject();
        author.addProperty("name", name);
        author.addProperty("url", url);
        return put("author", author);
    }

    public Embed withAuthor(String name) {
        JsonObject author = new JsonObject();
        author.addProperty("name", name);
        return put("author", author);
    }

    public Embed withFooter(String text, String iconUrl) {
        JsonObject footer = new JsonObject();
        footer.addProperty("text", text);
        footer.addProperty("icon_url", sanitizeUrl(iconUrl));
        return put("footer", footer);
    }

    public Embed withFooter(String text) {
        JsonObject footer = new JsonObject();
        footer.addProperty("text", text);
        return put("footer", footer);
    }

    public Embed withField(String name, String value, boolean inline) {
        fields.add(new EmbedField(name, value, inline));
        return this;
    }

    public Embed withField(String name, String value) {
        return withField(name, value, false);
    }

    private Embed put(String key, String value) {
        rawProps.put(key, value);
        return this;
    }

    private Embed put(String key, JsonElement value) {
        jsonProps.put(key, value);
        return this;
    }

    public String toJson(Gson gson) {
        JsonObject ser = new JsonObject();
        JsonObject embed = new JsonObject();
        embed.addProperty("type", "rich");
        rawProps.entrySet().forEach(e -> embed.addProperty(e.getKey(), e.getValue()));
        jsonProps.entrySet().forEach(e -> embed.add(e.getKey(), e.getValue()));
        JsonArray fieldArr = new JsonArray();
        fields.forEach(f -> fieldArr.add(gson.toJsonTree(f)));
        embed.add("fields", fieldArr);
        if (colour.isPresent())
            embed.addProperty("color", colour.getAsInt());
        ser.add("embed", embed);
        return gson.toJson(ser);
    }

    private static JsonObject wrapUrl(String url, int width, int height) {
        JsonObject dto = wrapUrl(url);
        dto.addProperty("width", width);
        dto.addProperty("height", height);
        return dto;
    }

    private static JsonObject wrapUrl(String url) {
        JsonObject dto = new JsonObject();
        dto.addProperty("url", url);
        return dto;
    }

    private static String sanitizeUrl(String url) {
        return url.replaceAll("http(?!s)://", "https://");
    }

    private static class EmbedField {

        final String name, value;
        final boolean inline;

        EmbedField(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }

    }

}