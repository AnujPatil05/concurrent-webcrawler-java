# Concurrent Web Crawler in Java

A simple, multi-threaded web crawler built in Java to efficiently find broken links on a website.

## Key Features

* **Concurrent Crawling:** Uses a Java `ExecutorService` and a fixed thread pool to check multiple links in parallel, resulting increase in speed.
* **Recursive Depth Control:** Can crawl recursively to a user-defined depth.
* **Duplicate URL Prevention:** Keeps track of visited URLs in a thread-safe set to avoid getting stuck in crawl loops.
* **Broken Link Reporting:** Identifies and reports links that return non-200 status codes.

## Tech Stack

* **Language:** Java
* **Concurrency:** Java Executor Framework (`ExecutorService`, `ThreadPoolExecutor`)
* **HTML Parsing:** Jsoup
* **HTTP Requests:** Java `HttpClient`

## How to Run

1.  Clone the repository:
    ```bash
    git clone [https://github.com/AnujPatil05/concurrent-webcrawler-java.git]
    ```
2.  Build the project into a runnable JAR file using Maven:
    ```bash
    mvn clean package
    ```
3.  Run the crawler from the command line, providing a starting URL and a max depth:
    ```bash
    java -jar target/crawler-1.0-SNAPSHOT.jar <startUrl> <maxDepth>
    ```
    **Example:**
    ```bash
    java -jar target/crawler-1.0-SNAPSHOT.jar https://en.wikipedia.org/wiki/Java_programming_language 2
    ```

