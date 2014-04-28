package su.litvak.justdlna.util;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;
import org.junit.Test;
import su.litvak.justdlna.dlna.AbstractTest;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import static org.junit.Assert.*;

public class RandomAccessFileInputStreamTest extends AbstractTest {
    File file;
    final String string = "TEST_STRING" + System.nanoTime();

    @Before
    public void createFile() throws IOException {
        file = tmp.newFile();
        FileHelper.write(string, file);
    }

    @Test
    public void testStraightRead() throws IOException {
        RandomAccessFileInputStream rafis = new RandomAccessFileInputStream(file);
        assertEquals(string, StreamHelper.toString(rafis));
    }

    @Test
    public void testPartialRead() throws IOException {
        RandomAccessFileInputStream rafis = new RandomAccessFileInputStream(file);
        rafis.seek(5);
        assertEquals(string.substring(5), StreamHelper.toString(rafis));
    }

    @Test
    public void testStraightReadWithLimit() throws IOException {
        RandomAccessFileInputStream rafis = new RandomAccessFileInputStream(file);
        rafis.limit(4);
        assertEquals(string.substring(0, 4), StreamHelper.toString(rafis));
    }

    @Test
    public void testPartialReadWithLimit() throws IOException {
        RandomAccessFileInputStream rafis = new RandomAccessFileInputStream(file);
        rafis.seek(3);
        rafis.limit(8);
        assertEquals(string.substring(3, 8), StreamHelper.toString(rafis));
    }

    @Test
    public void testIncorrectSeekAndLimit() throws IOException {
        RandomAccessFileInputStream rafis = new RandomAccessFileInputStream(file);
        rafis.seek(2);
        rafis.limit(1);
        assertTrue(StreamHelper.toString(rafis).isEmpty());
    }

    @Test
    public void testIncorrectLimit() throws IOException {
        RandomAccessFileInputStream rafis = new RandomAccessFileInputStream(file);
        rafis.limit(string.length() + 1);
        assertEquals(string, StreamHelper.toString(rafis));
    }

    @Test
    public void testAvailableBigFileSize(final @Mocked RandomAccessFile file) throws IOException {
        new NonStrictExpectations() {{
            file.length(); result = 2341471881l;
        }};

        RandomAccessFileInputStream rafis = new RandomAccessFileInputStream(file);
        assertTrue(rafis.available() > 0);
    }
}
