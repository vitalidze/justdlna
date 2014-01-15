package su.litvak.justdlna.dlna;

import org.junit.Before;
import org.junit.Test;
import su.litvak.justdlna.model.*;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class LastViewedNodeTest extends AbstractTest {
    @Before
    public void initViewLog() {
        ViewLog.clear();
        ViewLog.init();
    }

    @Test
    public void testUnMappedFolders() throws IOException {
        FolderNode<VideoFormat> video = mockDir("Video", VideoFormat.class);
        FolderNode<VideoFormat> subVideo = mockDir("Sub video", VideoFormat.class);
        video.addContainer(subVideo);
        LastViewedNode<VideoFormat> lastViewed = new LastViewedNode<VideoFormat>(null, Formats.VIDEO.name(), 3);
        video.addContainer(lastViewed);

        ViewLog.log(mockFile("test 1", VideoFormat.MKV, video), VideoFormat.class);
        ViewLog.log(mockFile("test 2", VideoFormat.MKV, subVideo), VideoFormat.class);

        assertEquals(2, lastViewed.getItems().size());
        assertEquals(0, lastViewed.getContainers().size());

        /**
         * Now un-map sub-folder
         */
        video.removeContainer(subVideo);
        assertEquals(1, lastViewed.getItems().size());
    }

    @Test
    public void testDuplicates() throws IOException, InterruptedException {
        FolderNode<VideoFormat> video = mockDir("Video", VideoFormat.class);
        LastViewedNode<VideoFormat> lastViewed = new LastViewedNode<VideoFormat>(null, Formats.VIDEO.name(), 3);
        video.addContainer(lastViewed);

        File file = mockFile("movie 1", VideoFormat.AVI, video);

        ViewLog.log(file, VideoFormat.class);
        Thread.sleep(10);
        ViewLog.log(file, VideoFormat.class);

        assertEquals(1, lastViewed.getItems().size());
    }

    @Test
    public void testOrder() throws IOException, InterruptedException {
        FolderNode<VideoFormat> video = mockDir("Video", VideoFormat.class);
        LastViewedNode<VideoFormat> lastViewed = new LastViewedNode<VideoFormat>(null, Formats.VIDEO.name(), 3);
        video.addContainer(lastViewed);

        File movie1 = mockFile("movie 1", VideoFormat.AVI, video);
        File movie2 = mockFile("movie 2", VideoFormat.AVI, video);
        mockFile("movie 3", VideoFormat.AVI, video);

        ViewLog.log(movie1, VideoFormat.class);
        Thread.sleep(10);
        ViewLog.log(movie2, VideoFormat.class);

        assertEquals(2, lastViewed.getItems().size());
        assertEquals(movie2, lastViewed.getItems().get(0).getFile());
        assertEquals(movie1, lastViewed.getItems().get(1).getFile());
    }
}
