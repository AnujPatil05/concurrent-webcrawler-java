package org.example;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Crawler {
    public static void main(String[] args) {
        // The URL we will start crawling from.
        String startUrl = "https://en.wikipedia.org/wiki/Java_(programming_language)";

        System.out.println("Fetching and parsing: " + startUrl);

        try {
            // 1. Fetch the HTML from the URL using Jsoup.
            // The .get() method handles the network connection and downloads the page.
            Document doc = Jsoup.connect(startUrl).get();

            // 2. Select all the link elements.
            // We use a CSS selector "a[href]" to find all <a> tags that have an href attribute.
            Elements links = doc.select("a[href]");

            System.out.println("\nFound " + links.size() + " links on the page:");

            // 3. Loop through the found links and print their absolute URL.
            for (Element link : links) {
                // The .absUrl("href") method gets the full URL (e.g., "https://en.wikipedia.org/wiki/Object-oriented_programming")
                // instead of just the relative part ("/wiki/Object-oriented_programming").
                String absoluteUrl = link.absUrl("href");
                System.out.println("  -> " + absoluteUrl);
            }

        } catch (IOException e) {
            // This will catch errors like the website being down or having no internet connection.
            System.err.println("An error occurred while fetching the URL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}