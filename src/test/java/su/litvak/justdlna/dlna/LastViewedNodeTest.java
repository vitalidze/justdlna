package su.litvak.justdlna.dlna;

import org.junit.Before;
import org.junit.Test;
import su.litvak.justdlna.model.*;
import su.litvak.justdlna.util.FileHelper;

import java.io.IOException;

import static org.junit.Assert.*;

public class LastViewedNodeTest extends AbstractTest {

    @Test
    public void testUnMappedFolders() throws IOException {
        FolderNode<VideoFormat> video = mockDir("Video", VideoFormat.class);
        FolderNode<VideoFormat> subVideo = mockDir("Sub video", VideoFormat.class);
        video.addContainer(subVideo);
        LastViewedNode<VideoFormat> lastViewed = new LastViewedNode<VideoFormat>(null, Formats.VIDEO.name(), 3);
        video.addContainer(lastViewed);

        FileHelper.access(mockFile("test 1", VideoFormat.MKV, video));
        FileHelper.access(mockFile("test 2", VideoFormat.MKV, subVideo));

        assertEquals(2, lastViewed.getItems().size());
        assertEquals(0, lastViewed.getContainers().size());

        /**
         * Now un-map sub-folder
         */
        video.removeContainer(subVideo);
        assertEquals(1, lastViewed.getItems().size());

    }
}
