package su.litvak.justdlna.http;

import fi.iki.elonen.NanoHTTPD;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;
import org.teleal.cling.support.model.BrowseFlag;
import su.litvak.justdlna.dlna.AbstractTest;
import su.litvak.justdlna.dlna.ContentDirectoryService;
import su.litvak.justdlna.model.*;
import su.litvak.justdlna.util.FileHelper;
import su.litvak.justdlna.util.StreamHelper;

import static org.junit.Assert.*;

public class ServerTest extends AbstractTest {
    @Tested ContentDirectoryService service;
    @Tested Server server;
    @Mocked NanoHTTPD.IHTTPSession session;
    FolderNode<VideoFormat> folder;
    final String TEST_CONTENT = "TST_CONTENT" + System.currentTimeMillis();

    @Before
    public void initContentService() throws Exception {
        folder = mockDir("Video", VideoFormat.class);
        NodesMap.put(ROOT_ID, folder);
        FileHelper.write(TEST_CONTENT, mockFile("Sub file", VideoFormat.MKV, folder));
    }

    @Test
    public void testRetrieving() throws Exception {
        new NonStrictExpectations() {{
            session.getUri(); result = "/" + folder.getItems().get(0).getId();
            session.getMethod(); result = NanoHTTPD.Method.GET;
        }};

        service.browse(ROOT_ID, BrowseFlag.DIRECT_CHILDREN, null, 0, 1, null);
        NanoHTTPD.Response response = server.serve(session);

        assertEquals(TEST_CONTENT, StreamHelper.toString(response.getData()));
    }
}
