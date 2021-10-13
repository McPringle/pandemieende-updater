/*
 * MIT License
 *
 * Copyright (C) 2021 Marcus Fihlon and the individual contributors to
 * Pandemieende Updater.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ch.pandemieende.updater;

import edu.umd.cs.findbugs.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoUnit.SECONDS;

@SuppressWarnings({ "java:S106", "java:S2142" })
public final class Downloader {

    private static final String BAG_DOMAIN = "https://www.covid19.admin.ch";
    private static final String DASHBOARD_URL = BAG_DOMAIN.concat("/de/overview");
    private static final Pattern ZIPFILE_REGEX = Pattern.compile("/api/data/[A-Za-z0-9-]+/downloads/sources-csv.zip");

    private final Path downloadFile;

    public Downloader() {
        final var date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        final var home = System.getProperty("user.home");
        downloadFile = Path.of(home, "Downloads", "bag-data_%s.zip".formatted(date));
    }

    public Path download() {
        final var html = getWebsiteContent();
        final var url = getUrl(html);
        return downloadFile(url);
    }

    @Nullable
    private String getWebsiteContent() {
        try {
            final var request = HttpRequest.newBuilder(new URI(DASHBOARD_URL))
                    .GET()
                    .timeout(Duration.of(30, SECONDS))
                    .build();
            final var client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build();
            final var response = client.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.err.printf("Download failed with status code %d%n", response.statusCode());
            }
        } catch (final IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getUrl(@Nullable final String html) {
        if (html != null) {
            final var matcher = ZIPFILE_REGEX.matcher(html);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return null;
    }

    private Path downloadFile(@Nullable final String url) {
        if (url != null) {
            try {
                final var request = HttpRequest.newBuilder(new URI(BAG_DOMAIN.concat(url)))
                        .GET()
                        .timeout(Duration.of(30, SECONDS))
                        .build();
                final var client = HttpClient.newBuilder()
                        .followRedirects(HttpClient.Redirect.ALWAYS)
                        .build();
                final var response = client.send(request,
                        HttpResponse.BodyHandlers.ofFile(downloadFile));
                if (response.statusCode() == 200) {
                    return downloadFile;
                } else {
                    System.err.printf("Download failed with status code %d%n", response.statusCode());
                }
            } catch (final IOException | InterruptedException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
