package io.github.phantamanta44.sonarous.util.io;


import com.github.fge.lambdas.Throwing;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.util.Maths;
import io.github.phantamanta44.sonarous.util.deferred.Deferred;
import io.github.phantamanta44.sonarous.util.deferred.Deferreds;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.List;

public class PromiseIO {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
    
    public static IPromise<String> requestString(String url, String... headers) {
        if (headers.length % 2 != 0)
            throw new IllegalArgumentException("Headers must come in name-value pairs!");
        Deferred<String> def = new Deferred<>();
        BotMain.client().executorPool().submit(() -> {
            try {
                BotMain.log().info("HTT: {} / {}", url.hashCode(), url);
                GetRequest req = Unirest.get(url).header("User-Agent", USER_AGENT);
                for (int i = 0; i < headers.length; i += 2)
                    req.header(headers[i], headers[i + 1]);
                HttpResponse<String> resp = req.asString();
                int status = resp.getStatus();
                BotMain.log().info("HTT: done: {} (status {})", url.hashCode(), status);
                if (Maths.bounds(status, 400, 600))
                    def.reject(new HttpException(status));
                def.resolve(resp.getBody());
            } catch (Exception e) {
                def.reject(e);
            }
        });
        return def.promise();
    }

    public static IPromise<JsonElement> requestJson(String url, String... headers) {
        return requestString(url, headers).map(new JsonParser()::parse);
    }

    public static IPromise<Document> requestHtml(String url, String... headers) {
        return requestString(url, headers).map(Jsoup::parse);
    }

    public static IPromise<List<String>> readFile(File file) {
        return Deferreds.call(Throwing.supplier(() -> Files.readAllLines(file.toPath()))).promise();
    }

    public static IPromise<byte[]> readBytes(File file) {
        return Deferreds.call(Throwing.supplier(() -> {
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
                return IOUtils.toByteArray(in);
            }
        })).promise();
    }

}