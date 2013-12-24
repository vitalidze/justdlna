package su.litvak.justdlna.model;

public abstract class ContentNode {
    private String id;
    private ContainerNode parent;

    ContentNode(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public final void setParent(ContainerNode parent) {
        this.parent = parent;
    }

    public ContainerNode getParent() {
        return parent;
    }
}
