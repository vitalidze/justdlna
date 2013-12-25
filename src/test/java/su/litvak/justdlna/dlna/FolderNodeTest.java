package su.litvak.justdlna.dlna;

import org.junit.Test;
import su.litvak.justdlna.model.*;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class FolderNodeTest extends AbstractTest {
    @Test
    public void testThreeLevelStructure() throws IOException {
        FolderNode<VideoFormat> folder = mockDir("Video", VideoFormat.class);
        FolderNode<VideoFormat> subFolder = mockDir("Sub folder", folder);
        FolderNode<VideoFormat> subSubFolder = mockDir("Sub sub folder", subFolder);
        mockFile("video", VideoFormat.FLV, subSubFolder);

        assertEquals(1, folder.getContainers().size());
        assertTrue(folder.getItems().isEmpty());
        assertEquals(1, subFolder.getContainers().size());
        assertTrue(subFolder.getItems().isEmpty());
        assertEquals(0, subSubFolder.getContainers().size());
        assertEquals(1, subSubFolder.getItems().size());
    }

    @Test
    public void testFolderWithMappedSubFolders() throws IOException {
        FolderNode<VideoFormat> folder1 = mockDir("Video 1", VideoFormat.class);
        FolderNode<VideoFormat> folder2 = mockDir("Video 2", VideoFormat.class);
        FolderNode<VideoFormat> folder3 = mockDir("Video 3", VideoFormat.class);

        mockFile("video", VideoFormat.WMV, mockDir("Sub video 1", folder1));
        mockFile("video", VideoFormat.MPEG, folder1);
        mockFile("video", VideoFormat.MKV, folder2);
        mockFile("video", VideoFormat.FLV, folder3);

        folder1.setContainers(Arrays.<ContainerNode>asList(folder2, folder3));

        assertEquals(3, folder1.getContainers().size());
        assertEquals(1, folder1.getItems().size());
    }

    @Test
    public void testViewLog() throws IOException {
        ViewLog.init();
        ViewLog.clear();

        FolderNode<VideoFormat> video = mockDir("Video", VideoFormat.class);
        for (int i = 0; i < 5; i++) {
            mockFile("video " + i, VideoFormat.AVI, video);
        }

        for (ItemNode item : video.getItems()) {
            for (int i = 0; i < 3; i++) {
                ViewLog.log(item.getFile(), item.getParent().getFormatClass());
            }
        }
        assertEquals(1, ViewLog.getLastViewItems(1, VideoFormat.class).size());
        assertEquals(2, ViewLog.getLastViewItems(2, VideoFormat.class).size());
        assertEquals(3, ViewLog.getLastViewItems(3, VideoFormat.class).size());
        assertEquals(4, ViewLog.getLastViewItems(4, VideoFormat.class).size());
        assertEquals(5, ViewLog.getLastViewItems(5, VideoFormat.class).size());
        assertEquals(5, ViewLog.getLastViewItems(6, VideoFormat.class).size());
        assertEquals(5, ViewLog.getLastViewItems(7, VideoFormat.class).size());
        assertEquals(5, ViewLog.getLastViewItems(8, VideoFormat.class).size());
        assertEquals(5, ViewLog.getLastViewItems(9, VideoFormat.class).size());
    }
}
