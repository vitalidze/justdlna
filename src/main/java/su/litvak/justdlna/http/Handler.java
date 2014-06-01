package su.litvak.justdlna.http;

import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;

public interface Handler {
    Response serve(IHTTPSession session);
}
