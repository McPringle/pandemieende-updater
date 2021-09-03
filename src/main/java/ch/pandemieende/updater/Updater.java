package ch.pandemieende.updater;

import edu.umd.cs.findbugs.annotations.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipInputStream;

public final class Updater {

    private @NonNull final String zipFile;

    public Updater(@NonNull final String zipFile) {
        this.zipFile = zipFile;
    }

    public void doUpdate() throws IOException {
        final var file = extractFile("COVID19VaccDosesAdministered.csv");
        System.out.println(file);
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
            for (int len = zipInputStream.read(buffer); len > 0; len = zipInputStream.read(buffer)) {
                fileOutputStream.write(buffer, 0, len);
            }
        }

        return tempFile;
    }

}
