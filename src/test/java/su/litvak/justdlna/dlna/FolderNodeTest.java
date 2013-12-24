package su.litvak.justdlna.dlna;

import org.junit.Test;
import su.litvak.justdlna.model.ContainerNode;
import su.litvak.justdlna.model.FolderNode;
import su.litvak.justdlna.model.VideoFormat;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

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
}
