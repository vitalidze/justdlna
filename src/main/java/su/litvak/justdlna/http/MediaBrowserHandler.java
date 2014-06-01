package su.litvak.justdlna.http;

import fi.iki.elonen.NanoHTTPD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class MediaBrowserHandler implements Handler {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);
    public final static String PREFIX = "/m/";

    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        String uri = session.getUri().substring(PREFIX.length());

        if (uri.isEmpty()) {
            uri = "index.html";
        }

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("webapp/" + uri);
        if (is == null) {
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "Requested resource not found");
        }
        String mime = "text/plain";
        if (uri.endsWith("js")) {
            mime = "application/javascript";
        } else if (uri.endsWith("html")) {
            mime = "text/html";
        } else if (uri.endsWith("css")) {
            mime = "text/css";
        }

        return new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, mime, is);
    }
}
