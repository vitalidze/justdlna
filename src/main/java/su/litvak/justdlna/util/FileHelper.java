package su.litvak.justdlna.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileHelper {
    public static void touch(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        file.setLastModified(System.currentTimeMillis());
    }

    public static void write(String s, File file) throws IOException {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(s);
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }
}
