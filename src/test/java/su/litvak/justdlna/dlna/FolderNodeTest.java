package su.litvak.justdlna.dlna;

import org.junit.Test;
import su.litvak.justdlna.Config;
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

        for (int i = 1; i < 10; i++) {
            LastViewedNode<VideoFormat> lastViewed = new LastViewedNode<VideoFormat>(null, Formats.VIDEO.name(), i);
            video.addContainer(lastViewed);
            int count = Math.min(5, i);
            assertEquals(count, lastViewed.getItems().size());
            assertEquals(0, lastViewed.getContainers().size());
        }
    }

    @Test
    public void testNonExistentFolder() {
        FolderNode<VideoFormat> video = mockDir("Vids", VideoFormat.class);
        video.getFolder().delete();

        assertTrue(video.getContainers().isEmpty());
        assertTrue(video.getItems().isEmpty());
    }

    @Test
    public void testItemUrl() throws IOException {
        FolderNode<AudioFormat> audio = mockDir("Audios", AudioFormat.class);
        mockFile("test", AudioFormat.MP3, audio);
        ItemNode item = audio.getItems().get(0);
        assertEquals("http://" + Config.get().getIpAddress() + ":" + Config.get().getHttpPort() + "/s/" + item.getId(), item.getItem().getFirstResource().getValue());
    }
}
