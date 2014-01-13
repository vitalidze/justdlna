package su.litvak.justdlna.http;

import fi.iki.elonen.NanoHTTPD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import su.litvak.justdlna.Config;
import su.litvak.justdlna.model.ItemNode;
import su.litvak.justdlna.model.NodesMap;
import su.litvak.justdlna.model.ViewLog;
import su.litvak.justdlna.util.RandomAccessFileInputStream;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

public class Server extends NanoHTTPD {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    public Server() {
        super(Config.get().getHttpPort());
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> header = session.getHeaders();
        Map<String, String> parms = session.getParms();
        String uri = session.getUri();

        if (LOG.isDebugEnabled()) {
            LOG.debug("{} '{}", session.getMethod(), uri);

            Iterator<String> e = header.keySet().iterator();
            while (e.hasNext()) {
                String value = e.next();
                LOG.debug("  HDR: '{}' = '{}'", value, header.get(value));
            }
            e = parms.keySet().iterator();
            while (e.hasNext()) {
                String value = e.next();
                LOG.debug("  PRM: '{}' = '{}'", value, parms.get(value));
            }
        }

        final ItemNode node = (ItemNode) NodesMap.get(uri.replaceFirst("/", ""));
        if (node == null) {
            return createResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Error 404, file not found.");
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
                    Response res = createResponse(Response.Status.RANGE_NOT_SATISFIABLE, NanoHTTPD.MIME_PLAINTEXT, "");
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

                    Response res = createResponse(Response.Status.PARTIAL_CONTENT, node.getFormat().getMime(), fis);
                    res.addHeader("Content-Length", Integer.toString(fis.available()));
                    res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
                    res.addHeader("ETag", etag);
                    ViewLog.log(file, node.getParent().getFormatClass());
                    return res;
                }
            } else {
                if (etag.equals(header.get("if-none-match")))
                    return createResponse(Response.Status.NOT_MODIFIED, node.getFormat().getMime(), "");

                Response res = createResponse(Response.Status.OK, node.getFormat().getMime(), new RandomAccessFileInputStream(file));
                res.addHeader("Content-Length", "" + fileLen);
                res.addHeader("ETag", etag);
                ViewLog.log(file, node.getParent().getFormatClass());
                return res;
            }
        } catch (IOException ioe) {
            return createResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.");
        }
    }

    // Announce that the file server accepts partial content requests
    private Response createResponse(Response.Status status, String mimeType, InputStream message) {
        Response res = new Response(status, mimeType, message);
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }

    private Response createResponse(Response.Status status, String mimeType, String message) {
        Response res = new Response(status, mimeType, message);
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }
}
