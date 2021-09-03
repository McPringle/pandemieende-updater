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

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

public final class Updater {

    private @NonNull final String zipFile;
    private @NonNull final Map<LocalDate, Data> dataStore = new HashMap<>();

    public Updater(@NonNull final String zipFile) {
        this.zipFile = zipFile;
    }

    public void doUpdate() throws CsvValidationException, IOException {
        updateAdministeredVaccineDoses();
        updateVaccinatedPersons();
        toJSON();
    }

    private void toJSON() {
        final var lines = dataStore.values().stream()
                .sorted()
                .limit(5)
                .map(Data::toJSON)
                .collect(Collectors.joining(",\n"));
        final var history = Arrays.stream(lines.split("\n"))
                .map("    "::concat)
                .collect(Collectors.joining("\n"))
                .trim();
        final var json = """
                {
                  "lastUpdate" : "%s",
                  "history" : [
                    %s
                  ]
                }""".formatted(LocalDate.now(), history);
        System.out.println(json);
    }

    private void updateAdministeredVaccineDoses() throws IOException, CsvValidationException {
        final var file = extractFile("COVID19VaccDosesAdministered.csv");
        try (var reader = Files.newBufferedReader(file.toPath())) {
            parseData(reader).forEach(line -> {
                final var statusDate = LocalDate.parse(line[0]);
                final var administeredVaccineDoses = Integer.parseInt(line[4]);
                final var data = getData(statusDate);
                data.setAdministeredVaccineDoses(administeredVaccineDoses);
            });
        }
    }

    private void updateVaccinatedPersons() throws IOException, CsvValidationException {
        final var file = extractFile("COVID19VaccPersons_v2.csv");
        try (var reader = Files.newBufferedReader(file.toPath())) {
            parseData(reader).stream()
                    .filter(line -> "COVID19FullyVaccPersons".equals(line[9]))
                    .forEach(line -> {
                final var statusDate = LocalDate.parse(line[0]);
                final var vaccinatedPersons = Integer.parseInt(line[4]);
                final var vaccinationRate = Double.parseDouble(line[7]);
                final var data = getData(statusDate);
                data.setVaccinatedPersons(vaccinatedPersons);
                data.setVaccinationRate(vaccinationRate);
            });
        }
    }

    private ArrayList<String[]> parseData(@NonNull final Reader reader) throws CsvValidationException, IOException {
        final var list = new ArrayList<String[]>();
        try (var csvReader = new CSVReader(reader)) {
            for (var line = csvReader.readNext(); line != null; line = csvReader.readNext()) {
                if ("CHFL".equals(line[1])) {
                    list.add(line);
                }
            }
        }
        return list;
    }

    private File extractFile(@NonNull final String filename) throws IOException {
        File file = null;

        try (var zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
            var zipEntry = zipInputStream.getNextEntry();
            while (file == null && zipEntry != null) {
                if (zipEntry.getName().endsWith(filename)) {
                    file = createTemporaryFile(zipInputStream);
                } else {
                    zipEntry = zipInputStream.getNextEntry();
                }
            }
            zipInputStream.closeEntry();
        }

        if (file != null) {
            return file;
        }

        throw new FileNotFoundException(filename);
    }

    private File createTemporaryFile(@NonNull final ZipInputStream zipInputStream) throws IOException {
        final var tempFile = File.createTempFile("pandemieende_", ".csv");
        tempFile.deleteOnExit();

        var buffer = new byte[1024];
        try (var fileOutputStream = new FileOutputStream(tempFile)) {
            for (var len = zipInputStream.read(buffer); len > 0; len = zipInputStream.read(buffer)) {
                fileOutputStream.write(buffer, 0, len);
            }
        }

        return tempFile;
    }

    private @NonNull Data getData(@NonNull final LocalDate statusDate) {
        var data = dataStore.get(statusDate);

        if (data == null) {
            data = new Data(statusDate);
            dataStore.put(statusDate, data);
        }

        return data;
    }

}
