package su.litvak.justdlna.servlet;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import org.eclipse.jetty.http.HttpGenerator;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.AbstractHttpConnection;
import org.eclipse.jetty.server.nio.AbstractNIOConnector;
import org.junit.Before;
import org.junit.Test;
import org.teleal.cling.support.model.BrowseFlag;
import su.litvak.justdlna.dlna.AbstractTest;
import su.litvak.justdlna.dlna.ContentDirectoryService;
import su.litvak.justdlna.model.*;
import su.litvak.justdlna.util.FileHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.StringWriter;

import static mockit.Deencapsulation.*;
import static org.junit.Assert.*;

public class ContentServletTest extends AbstractTest {
    @Tested ContentDirectoryService service;
    @Tested ContentServlet servlet;
    @Mocked HttpServletRequest request;
    @Mocked HttpServletResponse response;
    @Mocked AbstractHttpConnection connection;
    @Mocked AbstractNIOConnector connector;
    @Mocked HttpGenerator generator;
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
        StringWriter writer = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(writer);

        new NonStrictExpectations() {{
            AbstractHttpConnection.getCurrentConnection(); result = connection;
            connection.getConnector(); result = connector;
            connection.getGenerator(); result = generator;
            connector.getUseDirectBuffers(); result = true;
            request.getServletPath(); result = "/" + folder.getItems().get(0).getId();
            request.getMethod(); result = HttpMethods.GET;
            request.getDateHeader(HttpHeaders.IF_UNMODIFIED_SINCE); result = -1;
            response.getBufferSize(); result = 1024;
            response.getOutputStream(); result = new IllegalStateException();
            response.getWriter(); result = printWriter;
        }};

        setField(servlet, "_mimeTypes", new MimeTypes());
        service.browse(ROOT_ID, BrowseFlag.DIRECT_CHILDREN, null, 0, 1, null);
        servlet.doGet(request, response);

        printWriter.flush();
        assertEquals(TEST_CONTENT, writer.toString());
    }
}
