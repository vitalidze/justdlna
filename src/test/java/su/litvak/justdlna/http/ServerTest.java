package su.litvak.justdlna.http;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;
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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ServerTest extends AbstractTest {
    @Tested ContentDirectoryService service;
    @Tested Server server;
    @Mocked IHTTPSession session;
    FolderNode<VideoFormat> folder;
    final String TEST_CONTENT = "TST_CONTENT" + System.currentTimeMillis();

    @Before
    public void initContentServiceAndSession() throws Exception {
        folder = mockDir("Video", VideoFormat.class);
        NodesMap.put(ROOT_ID, folder);
        FileHelper.write(TEST_CONTENT, mockFile("Sub file", VideoFormat.MKV, folder));

        new NonStrictExpectations() {{
            session.getUri(); result = "/s/" + folder.getItems().get(0).getId();
            session.getMethod(); result = Method.GET;
        }};
    }

    @Test
    public void testRetrieving() throws Exception {
        service.browse(ROOT_ID, BrowseFlag.DIRECT_CHILDREN, null, 0, 1, null);
        Response response = server.serve(session);

        assertEquals(TEST_CONTENT, StreamHelper.toString(response.getData()));
        assertEquals(TEST_CONTENT.length(), Integer.parseInt(getHeader(response, "Content-Length")));
    }

    @Test
    public void testRangeFromBeginningWithoutUpperBound() throws Exception {
        new NonStrictExpectations() {{
            session.getHeaders(); result = new HashMap<String, String>() {{ put("range", "bytes=0-"); }};
        }};

        service.browse(ROOT_ID, BrowseFlag.DIRECT_CHILDREN, null, 0, 1, null);
        Response response = server.serve(session);

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
        Response response = server.serve(session);

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
        Response response = server.serve(session);

        assertEquals(TEST_CONTENT.substring(4, 9), StreamHelper.toString(response.getData()));
        assertEquals(5, Integer.parseInt(getHeader(response, "Content-Length")));
        assertEquals("bytes 4-8/" + TEST_CONTENT.length(), getHeader(response, "Content-Range"));
    }

    @Test
    public void testIncorrectURL(@Mocked final IHTTPSession session) throws Exception {
        new NonStrictExpectations() {{
            session.getUri(); result = "/s";
            session.getMethod(); result = Method.GET;
        }};

        Response response = server.serve(session);
        assertEquals("Incorrect URL", StreamHelper.toString(response.getData()));
    }

    @Test
    public void testNotExistingHandler(@Mocked final IHTTPSession session) throws Exception {
        new NonStrictExpectations() {{
            session.getUri(); result = "/not_existing/";
            session.getMethod(); result = Method.GET;
        }};

        Response response = server.serve(session);
        assertEquals("Unable to find correct handler", StreamHelper.toString(response.getData()));
    }

    private String getHeader(Response response, String name) throws NoSuchFieldException, IllegalAccessException {
        return getHeaders(response).get(name);
    }

    private Map<String, String> getHeaders(Response response) throws NoSuchFieldException, IllegalAccessException {
        Field field = response.getClass().getDeclaredField("header");
        field.setAccessible(true);
        return (Map<String, String>) field.get(response);
    }
}
