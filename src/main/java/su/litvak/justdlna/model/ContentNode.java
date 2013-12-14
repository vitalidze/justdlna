package su.litvak.justdlna.model;

public abstract class ContentNode {
    final String id;
    final ContainerNode parent;

    ContentNode(ContainerNode parent, String id) {
        this.parent = parent;
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
