package su.litvak.justdlna.http;

import fi.iki.elonen.NanoHTTPD;
import org.codehaus.jackson.JsonNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import su.litvak.justdlna.Config;
import su.litvak.justdlna.chromecast.Chromecasts;
import su.litvak.justdlna.chromecast.DialServer;
import su.litvak.justdlna.model.ContainerNode;
import su.litvak.justdlna.model.ItemNode;
import su.litvak.justdlna.model.NodesMap;

import java.util.List;

public class RESTApiHandler implements Handler {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);
    public final static String PREFIX = "/a/";

    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        String uri = session.getUri().substring(PREFIX.length());

        if (uri.equals("castlist")) {
            JSONArray list = new JSONArray();
            for (DialServer chromecast : Chromecasts.get()) {
                JSONObject next = new JSONObject();
                next.put("name", chromecast.getFriendlyName());
                next.put("ip_address", chromecast.getIpAddress().getHostAddress());
                next.put("port", chromecast.getPort());
                next.put("apps_url", chromecast.getAppsUrl());
                next.put("location", chromecast.getLocation());
                next.put("manufacturer", chromecast.getManufacturer());
                next.put("model_name", chromecast.getModelName());
                list.add(next);
            }
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, "application/json", list.toJSONString());
        } else if (uri.startsWith("browse")) {
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
