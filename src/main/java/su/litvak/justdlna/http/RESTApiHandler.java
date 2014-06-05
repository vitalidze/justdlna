package su.litvak.justdlna.http;

import fi.iki.elonen.NanoHTTPD;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import su.litvak.justdlna.Config;
import su.litvak.justdlna.chromecast.v2.ChromeCast;
import su.litvak.justdlna.chromecast.v2.ChromeCasts;
import su.litvak.justdlna.chromecast.DialServer;
import su.litvak.justdlna.chromecast.Platform;
import su.litvak.justdlna.chromecast.Playback;
import su.litvak.justdlna.model.ContainerNode;
import su.litvak.justdlna.model.ItemNode;
import su.litvak.justdlna.model.NodesMap;

import java.net.InetAddress;
import java.util.List;

public class RESTApiHandler implements Handler {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);
    public final static String PREFIX = "/a/";

    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        String uri = session.getUri().substring(PREFIX.length());

        if (uri.equals("castlist")) {
            JSONArray list = new JSONArray();
            for (ChromeCast chromecast : ChromeCasts.get()) {
                JSONObject next = new JSONObject();
                next.put("name", chromecast.getName());
                next.put("ip_address", chromecast.getIpAddress());
                next.put("port", chromecast.getPort());
                next.put("apps_url", chromecast.getAppsUrl());
                next.put("application", chromecast.getApplication());
                list.add(next);
            }
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, "application/json", list.toJSONString());
        } else if (uri.equals("castplay")) {
            try {
                Playback playback = new Playback(new Platform(), "F6A9FD85", new DialServer(InetAddress.getByName(session.getParms().get("castip"))), null);
                playback.stream("http://192.168.10.4:8192/s/" + session.getParms().get("media"));
            } catch (Exception ex) {
                LOG.error("Unable to stream", ex);
            }
        } else if (uri.equals("browse")) {
            String folderId = session.getParms().get("folder");
            ContainerNode container = (ContainerNode) (folderId == null ? null : NodesMap.get(folderId));
            if (container == null) {
                container = Config.get().getContent();
            }

            JSONArray list = new JSONArray();
            for (ContainerNode childContainer : container.getContainers()) {
                JSONObject next = new JSONObject();
                next.put("id", childContainer.getId());
                next.put("title", childContainer.getTitle());
                next.put("folder", Boolean.TRUE);
                list.add(next);
                NodesMap.put(childContainer.getId(), childContainer);
            }
            for (ItemNode item : container.getItems()) {
                JSONObject next = new JSONObject();
                next.put("id", item.getId());
                next.put("title", item.getFile().getName());
                next.put("folder", Boolean.FALSE);
                next.put("format", item.getFormat().getExt());
                list.add(next);
                NodesMap.put(item.getId(), item);
            }
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, "application/json", list.toJSONString());
        }

        return new NanoHTTPD.Response(NanoHTTPD.Response.Status.NOT_FOUND, "text/html", "No API is available within this path");
    }
}
