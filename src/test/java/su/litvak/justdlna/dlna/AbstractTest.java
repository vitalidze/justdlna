package su.litvak.justdlna.dlna;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import su.litvak.justdlna.model.FolderNode;
import su.litvak.justdlna.model.MediaFormat;

import java.io.File;
import java.io.IOException;

import static su.litvak.justdlna.util.FileHelper.touch;

public abstract class AbstractTest {
    final static String ROOT_ID = "0";

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    <T extends Enum<T> & MediaFormat> FolderNode<T> mockDir(final String name, Class<T> formatClass) {
        return mockDir(name, formatClass, this.tmp.getRoot());
    }

    <T extends Enum<T> & MediaFormat> FolderNode<T> mockDir(final String name, FolderNode<T> parent) {
        return mockDir(name, parent.getFormatClass(), parent.getFolder());
    }

    <T extends Enum<T> & MediaFormat> FolderNode<T> mockDir(final String name, Class<T> formatClass, File parent) {
        File d = new File(parent, name);
        d.mkdirs();
        return new FolderNode<T>(d.getName(), d, formatClass);
    }

    static File mockFile(final String name, MediaFormat format, final FolderNode<?> parent) throws IOException {
        File f = new File(parent.getFolder(), name + '.' + format.getExt());
        touch(f);
        return f;
    }
}
