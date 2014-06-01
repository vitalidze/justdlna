package su.litvak.justdlna.http;

import fi.iki.elonen.NanoHTTPD;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import su.litvak.justdlna.Config;
import su.litvak.justdlna.model.ContainerNode;
import su.litvak.justdlna.model.ItemNode;

import java.util.List;

public class RESTApiHandler implements Handler {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);
    public final static String PREFIX = "/a/";

    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        String uri = session.getUri().substring(PREFIX.length());

        ContainerNode container = null;
        if (uri.isEmpty()) {
            container = Config.get().getContent();
        }

        // TODO move conversion to JSON to container node and item node??
        List<ContainerNode> childContainers = container.getContainers();
        List<ItemNode> items = container.getItems();

        JSONArray list = new JSONArray();
        for (ContainerNode childContainer : childContainers) {
            JSONObject next = new JSONObject();
            next.put("id", childContainer.getId());
            next.put("title", childContainer.getTitle());
            next.put("folder", Boolean.TRUE);
            list.add(next);
        }

        return new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, "application/json", list.toJSONString());
    }
}
