package su.litvak.justdlna.model;

import java.util.HashMap;
import java.util.Map;

public class NodesMap {
    private final static Map<String, ContentNode> INSTANCE = new HashMap<String, ContentNode>();

    private NodesMap() {
    }

    public static ContentNode get(String id) {
        return INSTANCE.get(id);
    }

    public static void put(String id, ContentNode node) {
        INSTANCE.put(id, node);
    }
}
