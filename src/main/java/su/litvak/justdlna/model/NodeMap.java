package su.litvak.justdlna.model;

import java.util.HashMap;

public class NodeMap extends HashMap<String, ContentNode> {
    private final static NodeMap MAP = new NodeMap();

    private NodeMap() {
    }

    public static NodeMap get() {
        return MAP;
    }
}
