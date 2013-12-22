package su.litvak.justdlna.dlna;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import su.litvak.justdlna.Config;
import su.litvak.justdlna.model.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class LastAddedNodeTest {
    @Rule public TemporaryFolder tmp = new TemporaryFolder();
    List<File> files = new ArrayList<File>();

    @Before
    public void mockFiles() throws IOException, InterruptedException {
        for (int i = 0; i < 10; i++) {
            File f = mockFile("video " + i + ".mkv");
            files.add(f);
            f.setLastModified(System.currentTimeMillis() - i * 1000);
        }
    }

    @Before
    public void prepareConfiguration() {
        FolderNode<VideoFormat> videos = new FolderNode<VideoFormat>("Video", tmp.getRoot(), VideoFormat.class);
        Config.get().getFolders().clear();
        Config.get().getFolders().add(videos);
    }

    @Test
    public void testVideoSorting() throws IOException {
        LastAddedNode<VideoFormat> lastVideos = new LastAddedNode<VideoFormat>(VideoFormat.class, 5);
        Config.get().getFolders().add(lastVideos);

        assertEquals(5, lastVideos.getItems().size());

        List<File> items = new ArrayList<File>();
        for (ItemNode item : lastVideos.getItems()) {
            items.add(item.getFile());
        }
        assertEquals(files.subList(0, 5), items);
    }

    @Test
    public void testUnderLimit() throws IOException {
        LastAddedNode<VideoFormat> lastVideos = new LastAddedNode<VideoFormat>(VideoFormat.class, files.size() + 1);
        assertEquals(files.size(), lastVideos.getItems().size());
    }

    @Test
    public void testNoContainers() throws IOException {
        LastAddedNode<VideoFormat> lastVideos = new LastAddedNode<VideoFormat>(VideoFormat.class, 5);
        assertEquals(0, lastVideos.getContainers().size());
    }

    private File mockFile(final String name) throws IOException {
        return mockFile(name, this.tmp.getRoot());
    }

    private static File mockFile(final String name, final File parent) throws IOException {
        File f = new File(parent, name);
        touch(f);
        return f;
    }

    private static void touch(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        file.setLastModified(System.currentTimeMillis());
    }
}
