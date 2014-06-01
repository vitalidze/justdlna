package su.litvak.justdlna.http;

import fi.iki.elonen.NanoHTTPD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import su.litvak.justdlna.model.ItemNode;
import su.litvak.justdlna.model.NodesMap;
import su.litvak.justdlna.model.ViewLog;
import su.litvak.justdlna.util.RandomAccessFileInputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class MediaStreamHandler implements Handler {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);
    public final static String PREFIX = "/s/";

    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        String uri = session.getUri();
        Map<String, String> header = session.getHeaders();

        final ItemNode node = (ItemNode) NodesMap.get(uri.replaceFirst(PREFIX, ""));
        if (node == null) {
            return createResponse(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Error 404, file not found.");
        }

        File file = node.getFile();
        try {
            String etag = Integer.toHexString((file.getAbsolutePath() + file.lastModified() + "" + file.length()).hashCode());

            // Support (simple) skipping:
            long startFrom = 0;
            long endAt = -1;
            String range = header.get("range");
            if (range != null) {
                if (range.startsWith("bytes=")) {
                    range = range.substring("bytes=".length());
                    int minus = range.indexOf('-');
                    try {
                        if (minus > 0) {
                            startFrom = Long.parseLong(range.substring(0, minus));
                            endAt = Long.parseLong(range.substring(minus + 1));
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            // Change return code and add Content-Range header when skipping is requested
            long fileLen = file.length();
            if (range != null && startFrom >= 0) {
                if (startFrom >= fileLen) {
                    NanoHTTPD.Response res = createResponse(NanoHTTPD.Response.Status.RANGE_NOT_SATISFIABLE, NanoHTTPD.MIME_PLAINTEXT, "");
                    res.addHeader("Content-Range", "bytes 0-0/" + fileLen);
                    res.addHeader("ETag", etag);
                    return res;
                } else {
                    if (endAt < 0) {
                        endAt = fileLen - 1;
                    }

                    RandomAccessFileInputStream fis = new RandomAccessFileInputStream(file);
                    fis.seek(startFrom);
                    fis.limit(endAt + 1);

                    NanoHTTPD.Response res = createResponse(NanoHTTPD.Response.Status.PARTIAL_CONTENT, node.getFormat().getMime(), fis);
                    res.addHeader("Content-Length", Integer.toString(fis.available()));
                    res.addHeader("File-Size", Long.toString(fileLen));
                    res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
                    res.addHeader("ETag", etag);
                    dumpHeaders(res);
                    ViewLog.log(file, node.getParent().getFormatClass());
                    return res;
                }
            } else {
                if (etag.equals(header.get("if-none-match")))
                    return createResponse(NanoHTTPD.Response.Status.NOT_MODIFIED, node.getFormat().getMime(), "");

                NanoHTTPD.Response res = createResponse(NanoHTTPD.Response.Status.OK, node.getFormat().getMime(), new RandomAccessFileInputStream(file));
                res.addHeader("Content-Length", "" + fileLen);
                res.addHeader("File-Size", Long.toString(fileLen));
                res.addHeader("ETag", etag);
                dumpHeaders(res);
                ViewLog.log(file, node.getParent().getFormatClass());
                return res;
            }
        } catch (IOException ioe) {
            return createResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.");
        }
    }

    // Announce that the file server accepts partial content requests
    private NanoHTTPD.Response createResponse(NanoHTTPD.Response.Status status, String mimeType, InputStream message) {
        NanoHTTPD.Response res = new NanoHTTPD.Response(status, mimeType, message);
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }

    private NanoHTTPD.Response createResponse(NanoHTTPD.Response.Status status, String mimeType, String message) {
        NanoHTTPD.Response res = new NanoHTTPD.Response(status, mimeType, message);
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }

    private void dumpHeaders(NanoHTTPD.Response response) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("RESPONSE HEADERS:");
            for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
                LOG.debug("{} = {}", entry.getKey(), entry.getValue());
            }
        }
    }
}
