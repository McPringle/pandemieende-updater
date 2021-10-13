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

import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@SuppressWarnings({ "java:S106", "java:S4042" })
public final class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String VERSION = loadVersionString();
    private static final String LICENSE = "MIT License - Copyright (C) 2021 Marcus Fihlon and the individual contributors to Pandemieende Updater.";

    public static void main(@NonNull final String... args) {
        final var options = buildOptions();
        try {
            final var cmd = parseCommandLine(options, args);
            if (cmd.hasOption("a")) {
                System.out.println("Starting auto-update...");
                final var downloadFile = new Downloader().download();
                System.out.printf("  - BAG data downloaded to file: %s%n", downloadFile);
                new Updater(downloadFile.toString()).doUpdate();
                System.out.println("  - BAG data updated.");
                if (!downloadFile.toFile().delete()) {
                    System.err.printf("  - Deleting temporary download file failed: %s%n", downloadFile);
                    System.exit(1);
                } else {
                    System.out.printf("  - Temporary file '%s' deleted.%n", downloadFile);
                }
                System.out.println("Done.");
            }
            if (cmd.hasOption("d")) {
                final var downloadFile = new Downloader().download();
                System.out.printf("BAG data downloaded to file: %s%n", downloadFile);
            }
            if (cmd.hasOption("u")) {
                new Updater(cmd.getOptionValue("u")).doUpdate();
                System.out.println("BAG data updated.");
            }
            if (cmd.hasOption("h") || cmd.getOptions().length <= options.getRequiredOptions().size()) {
                printHelp(options);
            }
            if (cmd.hasOption("v")) {
                System.out.printf("Pandemieende Updater %s%n%s%n", VERSION, LICENSE); //NOSONAR
            }
        } catch (final ParseException e) {
            LOGGER.error(e.getMessage());
            printHelp(options);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static String loadVersionString() {
        final var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("version.txt");
        assert stream != null : "Version file is missing!";
        try (Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8)) {
            return scanner.nextLine();
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "<UNKNOWN VERSION>";
        }
    }

    private static CommandLine parseCommandLine(final @NonNull Options options, final String... args) throws ParseException {
        return new DefaultParser().parse(options, args);
    }

    private static Options buildOptions() {
        final var options = new Options();
        options.addOption("a", "auto", false, "download and update the pandemic data");
        options.addOption("d", "download", false, "download the pandemic data");
        options.addOption("h", "help", false, "print this message");
        options.addOption("u", "update", true, "update the pandemic data");
        options.getOption("u").setArgName("filename");
        options.addOption("v", "version", false, "print version information");
        return options;
    }

    private static void printHelp(@NonNull final Options options) {
        final var formatter = new HelpFormatter();
        formatter.printHelp(
                "updater",
                "Automating the update process for the \"pandemieende.ch\" website.\n ",
                options,
                "\n" + LICENSE,
                true);
    }

    private Main() throws InstantiationException {
        throw new InstantiationException("This is the main class and can't be instantiated!");
    }

}
