package su.litvak.justdlna.http;

import fi.iki.elonen.NanoHTTPD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import su.litvak.justdlna.Config;
import su.litvak.justdlna.model.ContainerNode;
import su.litvak.justdlna.model.FolderNode;
import su.litvak.justdlna.model.ItemNode;
import su.litvak.justdlna.model.NodesMap;
import su.litvak.justdlna.util.StreamHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MediaBrowserHandler implements Handler {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);
    public final static String PREFIX = "/m/";
    private final String INDEX_HTML;

    public MediaBrowserHandler() {
        InputStream is = null;
        String s = "";
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("webapp/index.html");
            s = StreamHelper.toString(is);
        } catch (IOException ioex) {
            LOG.error("Error while loading index template", ioex);
        } finally {
            try {
                is.close();
            } catch (IOException ioex) {
                LOG.error("Error while closing resource stream", ioex);
            }
        }
        INDEX_HTML = s;
    }

    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        String uri = session.getUri().substring(PREFIX.length());

        /**
         * Draw list
         */
        if (uri.isEmpty()) {
            String folderId = session.getParms().get("folder");
            ContainerNode container = (ContainerNode) (folderId == null ? null : NodesMap.get(folderId));
            Config.get().getContent();
            if (container == null) {
                container = Config.get().getContent();
            }

            List<ContainerNode> childContainers = container.getContainers();
            List<ItemNode> items = container.getItems();
            StringBuilder fileList = new StringBuilder((childContainers.size() + items.size()) * 64);
            for (ContainerNode containerNode : childContainers) {
                if (fileList.length() > 0) {
                    fileList.append("\r\n");
                }
                fileList.append("<li class=\"table-view-cell\">")
                          .append("<a class=\"push-right\" href=\"?folder=" + containerNode.getId() + "\" data-transition=\"slide-in\">")
                          .append(containerNode.getTitle())
                          .append("</a>")
                          .append("</li>");
                NodesMap.put(containerNode.getId(), containerNode);
            }

            for (ItemNode itemNode : items) {
                if (fileList.length() > 0) {
                    fileList.append("\r\n");
                }
                fileList.append("<li class=\"table-view-cell\">")
                        .append("<a class=\"navigate-right\" href=\"?play=" + itemNode.getId() + "\" data-transition=\"slide-in\">")
                        .append("<span class=\"media-object pull-left icon icon-play\"></span>")
                        .append("<div class=\"media-body\">")
                        .append(itemNode.getFile().getName())
                        .append("</div>")
                        .append("</a>")
                        .append("</li>");
                NodesMap.put(itemNode.getId(), itemNode);
            }

            String result = INDEX_HTML
                    .replace("{{file_list}}", fileList.toString())
                    .replace("{{up_one_level}}", container.getParent() == null ? "" : "<a class=\"icon icon-left-nav pull-left\" href=\"?folder=" + container.getParent().getId() + "\" data-transition=\"slide-out\"></a>");
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, "text/html", result);
        } else {
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
}
