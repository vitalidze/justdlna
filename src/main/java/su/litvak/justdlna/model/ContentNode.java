package su.litvak.justdlna.model;

public abstract class ContentNode {
    final String id;
    private ContainerNode parent;

    ContentNode(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setParent(ContainerNode parent) {
        this.parent = parent;
    }
}
