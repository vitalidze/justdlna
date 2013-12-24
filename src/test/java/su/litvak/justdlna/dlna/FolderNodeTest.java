package su.litvak.justdlna.dlna;

import org.junit.Test;
import su.litvak.justdlna.model.FolderNode;
import su.litvak.justdlna.model.VideoFormat;

import java.io.IOException;

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
}
