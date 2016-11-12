package io.github.phantamanta44.sonarous.bot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.player.MusicPlayer;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerData {

    private static final File DATA_FILE = new File("sonarous_data.json");
    private static final Map<String, ServerData> serverMap = new ConcurrentHashMap<>();

    static {
        JsonParser parser = new JsonParser();
        try (FileReader in = new FileReader(DATA_FILE)) {
            parser.parse(in).getAsJsonObject().entrySet()
                    .forEach(e -> serverMap.put(e.getKey(), new ServerData(e.getValue().getAsJsonObject())));
        } catch(FileNotFoundException ignored) {
            // NO-OP
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeData() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (PrintStream out = new PrintStream(new FileOutputStream(DATA_FILE))) {
            JsonObject dto = new JsonObject();
            serverMap.forEach((id, data) -> dto.add(id, data.serialize()));
            out.println(gson.toJson(dto));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ServerData forServer(String id) {
        ServerData data = serverMap.get(id);
        if (data == null) {
            data = new ServerData();
            serverMap.put(id, data);
        }
        return data;
    }

    private String prefix;
    private MusicPlayer player;

    public ServerData() {
        this.player = new MusicPlayer();
        this.prefix = BotMain.client().getConfigValue("prefix").getAsString();
    }

    public ServerData(JsonObject dto) {
        this.player = new MusicPlayer();
        this.prefix = dto.get("prefix").getAsString();
    }

    private JsonObject serialize() {
        JsonObject dto = new JsonObject();
        dto.addProperty("prefix", prefix);
        return dto;
}

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public MusicPlayer getPlayer() {
        return player;
    }

    public static void unbindAll() {
        serverMap.values().stream().map(ServerData::getPlayer).forEach(MusicPlayer::unbind);
    }

}
