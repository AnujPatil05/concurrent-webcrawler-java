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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Crawler {

    private static int getLinkStatusCode(String url) {
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

    public static void main(String[] args) {
        String startUrl = "https://en.wikipedia.org/wiki/Java_(programming_language)";

        ExecutorService executor = Executors.newFixedThreadPool(10);

        System.out.println("Fetching and parsing: " + startUrl);

        try {
            Document doc = Jsoup.connect(startUrl).get();
            Elements links = doc.select("a[href]");
            System.out.println("\nFound " + links.size() + " links. Checking status concurrently...");

            for (Element link : links) {
                String absoluteUrl = link.absUrl("href");

                if (absoluteUrl.isEmpty() || absoluteUrl.startsWith("mailto:")) {
                    continue;
                }

                Runnable task = () -> {
                    int statusCode = getLinkStatusCode(absoluteUrl);
                    System.out.println("[" + statusCode + "] " + absoluteUrl);
                };

                executor.submit(task);
            }

        } catch (IOException e) {
            System.err.println("An error occurred while fetching the URL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                    System.err.println("Executor did not terminate in the specified time.");
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}