package su.litvak.justdlna.util;

import java.io.IOException;
import java.io.InputStream;

public class StreamHelper {
    private StreamHelper() {}

    public static String toString(InputStream is) throws IOException {
        byte[] data = new byte[is.available()];
        is.read(data);
        return new String(data);
    }
}
