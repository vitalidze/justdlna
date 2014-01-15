package su.litvak.justdlna.util;

import jnr.posix.FileStat;
import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class FileHelper {
    private final static POSIX POSIX = POSIXFactory.getPOSIX();

    public static void touch(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        file.setLastModified(System.currentTimeMillis());
    }

    public static void access(File file) throws IOException {
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            fis.read();
            fis.close();
        } else {
            file.createNewFile();
        }
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

    public static long getLastAccessTime(File file) {
        FileStat stat = POSIX.stat(file.getAbsolutePath());
        return stat.atime() * 1000;
    }


}
