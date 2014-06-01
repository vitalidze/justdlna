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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server extends NanoHTTPD {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);
    private final Pattern URL_PREFIX = Pattern.compile("(/\\w+/).*");
    private Map<String, Handler> handlers;

    public Server() {
        super(Config.get().getHttpPort());

        handlers = new HashMap<String, Handler>();
        handlers.put(MediaStreamHandler.PREFIX, new MediaStreamHandler());
        handlers.put(MediaBrowserHandler.PREFIX, new MediaBrowserHandler());
    }

    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        Map<String, String> parms = session.getParms();
        Map<String, String> header = session.getHeaders();
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

        Matcher m = URL_PREFIX.matcher(uri);
        if (!m.matches()) {
            return new Response(Response.Status.BAD_REQUEST, "text/plain", "Incorrect URL");
        }

        String prefix = m.group(1);
        Handler handler = handlers.get(prefix);
        if (handler == null) {
            return new Response(Response.Status.NOT_FOUND, "text/plain", "Unable to find correct handler");
        }
        return handler.serve(session);
    }
}
