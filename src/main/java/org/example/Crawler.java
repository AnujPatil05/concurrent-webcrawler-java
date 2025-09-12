package org.example;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Crawler {

    private final Set<String> visitedLinks = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final int maxDepth;

    public Crawler(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    private int getLinkStatusCode(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode();
        } catch (IOException | InterruptedException e) {
            return 400; // Bad Request
        }
    }

    public void crawl(String url, int depth, ExecutorService executor) {
        if (depth > maxDepth || !visitedLinks.add(url)) {
            return;
        }

        System.out.println("-> Crawling: (" + depth + ") " + url);

        try {
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("a[href]");

            for (Element link : links) {
                String absoluteUrl = link.absUrl("href");

                if (absoluteUrl.isEmpty() || absoluteUrl.startsWith("mailto:")) {
                    continue;
                }

                executor.submit(() -> {
                    int statusCode = getLinkStatusCode(absoluteUrl);
                    if (statusCode != 200) {
                        System.out.println("[" + statusCode + "] BROKEN -> " + absoluteUrl + " (Found on " + url + ")");
                    }
                });

                crawl(absoluteUrl, depth + 1, executor);
            }
        } catch (IOException e) {

        }
    }

    public static void main(String[] args) {
        String startUrl = "https://en.wikipedia.org/wiki/Java_(programming_language)";
        int maxDepth = 2;
        Crawler crawler = new Crawler(maxDepth);
        ExecutorService executor = Executors.newFixedThreadPool(10);

        crawler.crawl(startUrl, 1, executor);

        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("Crawl finished.");
    }
}