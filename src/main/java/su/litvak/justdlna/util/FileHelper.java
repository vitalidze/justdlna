package su.litvak.justdlna.util;

import java.io.File;
import java.io.IOException;

public class FileHelper {
    public static void touch(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        file.setLastModified(System.currentTimeMillis());
    }
}
