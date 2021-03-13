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
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String APPNAME = "Pandemieende Updater";
    private static final String VERSION = loadVersionString();
    private static final String LICENSE = "MIT License - Copyright (C) 2021 Marcus Fihlon and the individual contributors to Pandemieende Updater.";

    public static void main(@NonNull final String... args) {
        System.out.printf("%s %s%n%s%n", APPNAME, VERSION, LICENSE);
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

    private Main() throws InstantiationException {
        throw new InstantiationException("This is the main class and can't be instantiated!");
    }

}
