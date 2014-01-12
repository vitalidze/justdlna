package su.litvak.justdlna.util;

import java.io.*;

public class RandomAccessFileInputStream extends InputStream {
    private final RandomAccessFile randomAccessFile;
    /**
     * Absolute position in file to finish reading on (exclusive)
     */
    private long limit = -1;

    public RandomAccessFileInputStream(File file) throws FileNotFoundException {
        randomAccessFile = new RandomAccessFile(file, "r");
    }

    @Override
    public int read() throws IOException {
        return randomAccessFile.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return randomAccessFile.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return randomAccessFile.read(b, off, len);
    }

    @Override
    public int available() throws IOException {
        long a = (limit >= 0 ? Math.min(limit, randomAccessFile.length()) : randomAccessFile.length()) - randomAccessFile.getFilePointer();
        return a < 0 ? 0 : (int) a;
    }

    @Override
    public synchronized void reset() throws IOException {
        limit = -1;
        randomAccessFile.seek(0);
    }

    @Override
    public void close() throws IOException {
        randomAccessFile.close();
    }

    public void seek(long pos) throws IOException {
        randomAccessFile.seek(pos);
    }

    public void limit(long limit) {
        this.limit = limit;
    }
}
