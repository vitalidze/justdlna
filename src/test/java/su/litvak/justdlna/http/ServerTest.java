package su.litvak.justdlna.http;

import fi.iki.elonen.NanoHTTPD;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;
import org.fourthline.cling.support.model.BrowseFlag;
import su.litvak.justdlna.dlna.AbstractTest;
import su.litvak.justdlna.dlna.ContentDirectoryService;
import su.litvak.justdlna.model.*;
import su.litvak.justdlna.util.FileHelper;
import su.litvak.justdlna.util.StreamHelper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ServerTest extends AbstractTest {
    @Tested ContentDirectoryService service;
    @Tested Server server;
    @Mocked NanoHTTPD.IHTTPSession session;
    FolderNode<VideoFormat> folder;
    final String TEST_CONTENT = "TST_CONTENT" + System.currentTimeMillis();

    @Before
    public void initContentServiceAndSession() throws Exception {
        folder = mockDir("Video", VideoFormat.class);
        NodesMap.put(ROOT_ID, folder);
        FileHelper.write(TEST_CONTENT, mockFile("Sub file", VideoFormat.MKV, folder));

        new NonStrictExpectations() {{
            session.getUri(); result = "/" + folder.getItems().get(0).getId();
            session.getMethod(); result = NanoHTTPD.Method.GET;
        }};
    }

    @Test
    public void testRetrieving() throws Exception {
        service.browse(ROOT_ID, BrowseFlag.DIRECT_CHILDREN, null, 0, 1, null);
        NanoHTTPD.Response response = server.serve(session);

        assertEquals(TEST_CONTENT, StreamHelper.toString(response.getData()));
        assertEquals(TEST_CONTENT.length(), Integer.parseInt(getHeader(response, "Content-Length")));
    }

    @Test
    public void testRangeFromBeginningWithoutUpperBound() throws Exception {
        new NonStrictExpectations() {{
            session.getHeaders(); result = new HashMap<String, String>() {{ put("range", "bytes=0-"); }};
        }};

        service.browse(ROOT_ID, BrowseFlag.DIRECT_CHILDREN, null, 0, 1, null);
        NanoHTTPD.Response response = server.serve(session);

        assertEquals(TEST_CONTENT, StreamHelper.toString(response.getData()));
        assertEquals(TEST_CONTENT.length(), Integer.parseInt(getHeader(response, "Content-Length")));
        assertEquals("bytes 0-" + (TEST_CONTENT.length() - 1) + "/" + TEST_CONTENT.length(), getHeader(response, "Content-Range"));
    }

    @Test
    public void testRangeFromMiddleWithoutUpperBound() throws Exception {
        new NonStrictExpectations() {{
            session.getHeaders(); result = new HashMap<String, String>() {{ put("range", "bytes=4-"); }};
        }};

        service.browse(ROOT_ID, BrowseFlag.DIRECT_CHILDREN, null, 0, 1, null);
        NanoHTTPD.Response response = server.serve(session);

        assertEquals(TEST_CONTENT.substring(4), StreamHelper.toString(response.getData()));
        assertEquals(TEST_CONTENT.length() - 4, Integer.parseInt(getHeader(response, "Content-Length")));
        assertEquals("bytes 4-" + (TEST_CONTENT.length() - 1) + "/" + TEST_CONTENT.length(), getHeader(response, "Content-Range"));
    }

    @Test
    public void testRangeMiddle() throws Exception {
        new NonStrictExpectations() {{
            session.getHeaders(); result = new HashMap<String, String>() {{ put("range", "bytes=4-8"); }};
        }};

        service.browse(ROOT_ID, BrowseFlag.DIRECT_CHILDREN, null, 0, 1, null);
        NanoHTTPD.Response response = server.serve(session);

        assertEquals(TEST_CONTENT.substring(4, 9), StreamHelper.toString(response.getData()));
        assertEquals(5, Integer.parseInt(getHeader(response, "Content-Length")));
        assertEquals("bytes 4-8/" + TEST_CONTENT.length(), getHeader(response, "Content-Range"));
    }

    private String getHeader(NanoHTTPD.Response response, String name) throws NoSuchFieldException, IllegalAccessException {
        return getHeaders(response).get(name);
    }

    private Map<String, String> getHeaders(NanoHTTPD.Response response) throws NoSuchFieldException, IllegalAccessException {
        Field field = response.getClass().getDeclaredField("header");
        field.setAccessible(true);
        return (Map<String, String>) field.get(response);
    }
}
