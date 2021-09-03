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
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

public final class Updater {

    private @NonNull final String zipFile;
    private @NonNull final Map<LocalDate, Data> dataStore = new HashMap<>();

    public Updater(@NonNull final String zipFile) {
        this.zipFile = zipFile;
    }

    public void doUpdate() throws CsvValidationException, IOException {
        updateAdministeredVaccineDoses();

        dataStore.values().stream()
                .sorted()
                .forEach(System.out::println);
    }

    private void updateAdministeredVaccineDoses() throws IOException, CsvValidationException {
        final var file = extractFile("COVID19VaccDosesAdministered.csv");
        try (var reader = Files.newBufferedReader(file.toPath())) {
            parseAdministeredVaccineDoses(reader).forEach(line -> {
                final var statusDate = LocalDate.parse(line[0]);
                final var administeredVaccineDoses = Integer.parseInt(line[4]);
                final var data = getData(statusDate);
                data.setAdministeredVaccineDoses(administeredVaccineDoses);
            });
        }
    }

    private ArrayList<String[]> parseAdministeredVaccineDoses(@NonNull final Reader reader) throws CsvValidationException, IOException {
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
